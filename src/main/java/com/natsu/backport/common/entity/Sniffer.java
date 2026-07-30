package com.natsu.backport.common.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * The 1.20 sniffer : an ancient beast that sniffs the ground and digs up the
 * torchflower and pitcher seeds. The 26.2 brain timeline is rebuilt as a
 * single dig goal running the vanilla state timings.
 */
public class Sniffer extends Animal implements IAnimatable {

	public enum State {
		IDLING, FEELING_HAPPY, SCENTING, SNIFFING, SEARCHING, DIGGING, RISING
	}

	public static final ResourceLocation DIGGING_LOOT =
			new ResourceLocation("ctbackport", "gameplay/sniffer_digging");
	private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
	private static final int SNIFFING_COOLDOWN_TICKS = 9600;
	private static final EntityDataAccessor<Integer> DATA_STATE =
			SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_DROP_SEED_AT_TICK =
			SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.INT);

	private static final EntityDimensions DIGGING_DIMENSIONS = EntityDimensions.scalable(1.9F, 1.75F - 0.4F);

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	private final Deque<BlockPos> exploredPositions = new ArrayDeque<>();
	private int sniffCooldown;

	public Sniffer(EntityType<? extends Sniffer> type, Level level) {
		super(type, level);
		this.getNavigation().setCanFloat(true);
		this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
		this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
		this.sniffCooldown = 400 + this.random.nextInt(1200);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Animal.createMobAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.1F)
				.add(Attributes.MAX_HEALTH, 14.0);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_STATE, State.IDLING.ordinal());
		this.entityData.define(DATA_DROP_SEED_AT_TICK, 0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new SnifferPanicGoal(2.0));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(CTBTags.Items.SNIFFER_FOOD), false));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
		this.goalSelector.addGoal(5, new SnifferDigGoal());
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
	}

	public State getState() {
		return State.values()[this.entityData.get(DATA_STATE)];
	}

	private void setState(State state) {
		this.entityData.set(DATA_STATE, state.ordinal());
	}

	public Sniffer transitionTo(State state) {
		switch (state) {
			case SCENTING -> this.playSound(CTBSounds.SNIFFER_SCENTING.get(), 1.0F, this.isBaby() ? 1.3F : 1.0F);
			case SNIFFING -> this.playSound(CTBSounds.SNIFFER_SNIFFING.get(), 1.0F, 1.0F);
			case DIGGING -> {
				this.entityData.set(DATA_DROP_SEED_AT_TICK, this.tickCount + DIGGING_DROP_SEED_OFFSET_TICKS);
				this.playSound(CTBSounds.SNIFFER_DIGGING.get(), 1.0F, 1.0F);
			}
			case RISING -> this.playSound(CTBSounds.SNIFFER_DIGGING_STOP.get(), 1.0F, 1.0F);
			case FEELING_HAPPY -> this.playSound(CTBSounds.SNIFFER_HAPPY.get(), 1.0F, 1.0F);
			default -> {
			}
		}
		this.setState(state);
		return this;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
		if (DATA_STATE.equals(accessor)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(accessor);
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		EntityDimensions dimensions = this.getState() == State.DIGGING ? DIGGING_DIMENSIONS
				: super.getDimensions(pose);
		return this.isBaby() ? dimensions.scale(0.5F) : dimensions;
	}

	public boolean canSniff() {
		return !this.isInWater() && !this.isInLove() && this.onGround && !this.isPassenger()
				&& !this.isLeashed() && !this.isBaby() && !this.isVehicle();
	}

	private BlockPos getHeadBlock() {
		Vec3 position = this.position().add(this.getForward().scale(2.25));
		return new BlockPos(position.x(), this.getY() + 0.2F, position.z());
	}

	Optional<BlockPos> calculateDigPosition() {
		for (int i = 0; i < 5; i++) {
			Vec3 target = LandRandomPos.getPos(this, 10 + 2 * i, 3);
			if (target == null) {
				continue;
			}
			BlockPos pos = new BlockPos(target).below();
			if (this.level.getWorldBorder().isWithinBounds(pos) && this.canDig(pos)) {
				return Optional.of(pos);
			}
		}
		return Optional.empty();
	}

	boolean canDigHere() {
		return this.canDig(this.getHeadBlock().below());
	}

	private boolean canDig(BlockPos position) {
		if (!this.level.getBlockState(position).is(CTBTags.Blocks.SNIFFER_DIGGABLE_BLOCK)
				|| this.exploredPositions.contains(position)) {
			return false;
		}
		Path path = this.getNavigation().createPath(position, 1);
		return path != null && path.canReach();
	}

	private void dropSeed() {
		if (!(this.level instanceof ServerLevel server)
				|| this.entityData.get(DATA_DROP_SEED_AT_TICK) != this.tickCount) {
			return;
		}
		BlockPos head = this.getHeadBlock();
		LootContext context = new LootContext.Builder(server)
				.withParameter(LootContextParams.THIS_ENTITY, this)
				.withParameter(LootContextParams.ORIGIN, this.position())
				.withRandom(this.random)
				.create(LootContextParamSets.GIFT);
		for (ItemStack stack : server.getServer().getLootTables().get(DIGGING_LOOT).getRandomItems(context)) {
			ItemEntity entity = new ItemEntity(this.level, head.getX(), head.getY(), head.getZ(), stack);
			entity.setDefaultPickUpDelay();
			server.addFreshEntity(entity);
		}
		this.playSound(CTBSounds.SNIFFER_DROP_SEED.get(), 1.0F, 1.0F);
	}

	private void emitDiggingParticles(int diggingTicks) {
		if (diggingTicks % 5 < 3) {
			BlockPos head = this.getHeadBlock();
			BlockState below = this.level.getBlockState(head.below());
			if (below.getRenderShape() != RenderShape.INVISIBLE) {
				for (int i = 0; i < 30; i++) {
					Vec3 center = Vec3.atCenterOf(head).add(0.0, -0.65F, 0.0);
					this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, below),
							center.x, center.y, center.z, 0.0, 0.0, 0.0);
				}
				if (this.tickCount % 10 == 0) {
					this.level.playLocalSound(this.getX(), this.getY(), this.getZ(),
							below.getSoundType().getHitSound(), this.getSoundSource(), 0.5F, 0.5F, false);
				}
			}
		}
	}

	private void storeExploredPosition(BlockPos position) {
		this.exploredPositions.addFirst(position);
		while (this.exploredPositions.size() > 20) {
			this.exploredPositions.removeLast();
		}
	}

	@Override
	public void tick() {
		if (this.getState() == State.DIGGING) {
			this.emitDiggingParticles(this.tickCount);
			this.dropSeed();
		}
		super.tick();
	}

	@Override
	public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
		ItemEntity egg = new ItemEntity(level, this.getX(), this.getY(), this.getZ(),
				new ItemStack(CTBItems.SNIFFER_EGG_ITEM.get()));
		egg.setDefaultPickUpDelay();
		this.setAge(6000);
		partner.setAge(6000);
		this.resetLove();
		partner.resetLove();
		level.broadcastEntityEvent(this, (byte) 18);
		if (level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
			level.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(level,
					this.getX(), this.getY(), this.getZ(), this.random.nextInt(7) + 1));
		}
		this.playSound(CTBSounds.SNIFFER_EGG_PLOP.get(), 1.0F,
				(this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
		level.addFreshEntity(egg);
	}

	@Override
	public void die(DamageSource source) {
		this.transitionTo(State.IDLING);
		super.die(source);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(CTBTags.Items.SNIFFER_FOOD);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return com.natsu.backport.common.registry.CTBEntities.SNIFFER.get().create(level);
	}

	@Override
	public boolean canMate(Animal partner) {
		if (!(partner instanceof Sniffer other) || this.getState() != State.IDLING
				|| other.getState() != State.IDLING) {
			return false;
		}
		return super.canMate(partner);
	}

	@Override
	public int getMaxHeadYRot() {
		return 50;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return CTBSounds.SNIFFER_IDLE.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return CTBSounds.SNIFFER_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return CTBSounds.SNIFFER_DEATH.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(CTBSounds.SNIFFER_STEP.get(), 0.15F, 1.0F);
	}

	@Override
	public float getVoicePitch() {
		return this.isBaby() ? super.getVoicePitch() * 1.3F : super.getVoicePitch();
	}

	// --- goals ---

	private class SnifferPanicGoal extends PanicGoal {

		SnifferPanicGoal(double speedModifier) {
			super(Sniffer.this, speedModifier);
		}

		@Override
		public void start() {
			Sniffer.this.transitionTo(State.IDLING);
			super.start();
		}
	}

	/** The full vanilla dig timeline : scent, sniff, search, dig, rise, be happy. */
	private class SnifferDigGoal extends Goal {

		private State stage;
		private int stageTicks;
		private int stageDuration;
		private BlockPos digPos;

		SnifferDigGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
		}

		@Override
		public boolean canUse() {
			if (!Sniffer.this.canSniff()) {
				return false;
			}
			if (Sniffer.this.sniffCooldown > 0) {
				Sniffer.this.sniffCooldown--;
				return false;
			}
			return Sniffer.this.random.nextInt(reducedTickDelay(100)) == 0;
		}

		@Override
		public boolean canContinueToUse() {
			return this.stage != null && Sniffer.this.canSniff();
		}

		@Override
		public void start() {
			this.enter(State.SCENTING, 40 + Sniffer.this.random.nextInt(41));
		}

		private void enter(State state, int duration) {
			this.stage = state;
			this.stageTicks = 0;
			this.stageDuration = duration;
			Sniffer.this.transitionTo(state);
		}

		@Override
		public void tick() {
			this.stageTicks++;
			switch (this.stage) {
				case SCENTING -> {
					if (this.stageTicks >= this.stageDuration) {
						this.enter(State.SNIFFING, 40 + Sniffer.this.random.nextInt(41));
					}
				}
				case SNIFFING -> {
					if (this.stageTicks >= this.stageDuration) {
						Optional<BlockPos> target = Sniffer.this.calculateDigPosition();
						if (target.isEmpty()) {
							this.finish(600);
							return;
						}
						this.digPos = target.get();
						this.enter(State.SEARCHING, 600);
						Sniffer.this.getNavigation().moveTo(this.digPos.getX(), this.digPos.getY() + 1,
								this.digPos.getZ(), 1.15);
					}
				}
				case SEARCHING -> {
					boolean close = Sniffer.this.blockPosition().distSqr(this.digPos.above()) < 6.25
							|| Sniffer.this.canDigHere();
					if (close && Sniffer.this.canDigHere()) {
						Sniffer.this.getNavigation().stop();
						this.enter(State.DIGGING, 160 + Sniffer.this.random.nextInt(21));
					} else if (Sniffer.this.getNavigation().isDone() || this.stageTicks >= this.stageDuration) {
						this.finish(600);
					}
				}
				case DIGGING -> {
					if (this.stageTicks >= this.stageDuration) {
						Sniffer.this.storeExploredPosition(Sniffer.this.getOnPos());
						this.enter(State.RISING, 40);
					}
				}
				case RISING -> {
					if (this.stageTicks >= this.stageDuration) {
						this.enter(State.FEELING_HAPPY, 40 + Sniffer.this.random.nextInt(61));
					}
				}
				case FEELING_HAPPY -> {
					if (this.stageTicks >= this.stageDuration) {
						this.finish(SNIFFING_COOLDOWN_TICKS);
					}
				}
				default -> this.finish(600);
			}
		}

		private void finish(int cooldown) {
			Sniffer.this.sniffCooldown = cooldown;
			this.stage = null;
		}

		@Override
		public void stop() {
			Sniffer.this.transitionTo(State.IDLING);
			Sniffer.this.getNavigation().stop();
			this.stage = null;
			this.digPos = null;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}
	}

	// --- geckolib ---

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "main", 4, event -> {
			switch (this.getState()) {
				case SCENTING -> event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("special.sniffsniff", ILoopType.EDefaultLoopTypes.LOOP));
				case SNIFFING -> event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("special.longsniff", ILoopType.EDefaultLoopTypes.LOOP));
				case SEARCHING -> event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("moove.sniff_search", ILoopType.EDefaultLoopTypes.LOOP));
				case DIGGING -> event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("special.dig", ILoopType.EDefaultLoopTypes.LOOP));
				case RISING, FEELING_HAPPY -> event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("special.happy", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
				default -> {
					if (event.isMoving()) {
						event.getController().setAnimation(new AnimationBuilder()
								.addAnimation("moove.walk", ILoopType.EDefaultLoopTypes.LOOP));
					} else {
						event.getController().setAnimation(new AnimationBuilder()
								.addAnimation("special.none", ILoopType.EDefaultLoopTypes.LOOP));
					}
				}
			}
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

}
