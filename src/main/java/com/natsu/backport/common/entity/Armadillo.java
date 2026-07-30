package com.natsu.backport.common.entity;

import java.util.List;
import java.util.Random;

import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * The 1.20.5 armadillo : rolls into a ball when threatened, sheds scutes over
 * time and gives one up to a brush. The 26.x brain is rebuilt with goals and
 * a plain scare scan.
 */
public class Armadillo extends Animal implements IAnimatable {

	public enum State {
		IDLE, ROLLING, SCARED, UNROLLING
	}

	public static final float BABY_SCALE = 0.6F;
	private static final int SCARE_CHECK_INTERVAL = 80;
	private static final int ROLLING_DURATION = 10;
	private static final int UNROLLING_DURATION = 30;
	private static final byte EVENT_PEEK = 64;

	private static final EntityDataAccessor<Integer> DATA_STATE =
			SynchedEntityData.defineId(Armadillo.class, EntityDataSerializers.INT);

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	private int scuteTime;
	private int stateTicks;
	private int peekTicks;

	public Armadillo(EntityType<? extends Armadillo> type, Level level) {
		super(type, level);
		this.scuteTime = this.pickNextScuteDropTime();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Animal.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 12.0)
				.add(Attributes.MOVEMENT_SPEED, 0.14);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_STATE, State.IDLE.ordinal());
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new ArmadilloPanicGoal(2.0));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
		this.goalSelector.addGoal(3, new ArmadilloTemptGoal());
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
		this.goalSelector.addGoal(5, new ArmadilloStrollGoal(1.0));
		this.goalSelector.addGoal(6, new ArmadilloLookGoal());
	}

	public State getState() {
		return State.values()[this.entityData.get(DATA_STATE)];
	}

	private void switchToState(State state) {
		this.entityData.set(DATA_STATE, state.ordinal());
		this.stateTicks = 0;
	}

	public boolean isScared() {
		return this.getState() != State.IDLE;
	}

	// --- rolling ---

	public void rollUp() {
		if (this.isScared()) {
			return;
		}
		this.getNavigation().stop();
		this.resetLove();
		this.playSound(CTBSounds.ARMADILLO_ROLL.get(), 1.0F, 1.0F);
		this.switchToState(State.ROLLING);
	}

	public void rollOut() {
		if (!this.isScared()) {
			return;
		}
		this.playSound(CTBSounds.ARMADILLO_UNROLL_FINISH.get(), 1.0F, 1.0F);
		this.switchToState(State.IDLE);
	}

	public boolean canStayRolledUp() {
		return !this.isInWater() && !this.isLeashed() && !this.isPassenger() && !this.isVehicle();
	}

	public boolean isScaredBy(LivingEntity entity) {
		if (!this.getBoundingBox().inflate(7.0, 2.0, 7.0).intersects(entity.getBoundingBox())) {
			return false;
		}
		if (entity instanceof Monster && entity.getMobType() == net.minecraft.world.entity.MobType.UNDEAD) {
			return true;
		}
		if (this.getLastHurtByMob() == entity) {
			return true;
		}
		if (entity instanceof Player player) {
			return !player.isSpectator() && (player.isSprinting() || player.isPassenger());
		}
		return false;
	}

	private boolean anyThreatNearby() {
		List<LivingEntity> nearby = this.level.getEntitiesOfClass(LivingEntity.class,
				this.getBoundingBox().inflate(7.0, 2.0, 7.0), e -> e != this);
		return nearby.stream().anyMatch(this::isScaredBy);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.level.isClientSide) {
			if (this.peekTicks > 0) {
				this.peekTicks--;
			}
			return;
		}
		this.stateTicks++;
		switch (this.getState()) {
			case IDLE -> {
				if (this.tickCount % SCARE_CHECK_INTERVAL == 0 && this.canStayRolledUp()
						&& this.anyThreatNearby()) {
					this.rollUp();
				}
			}
			case ROLLING -> {
				if (this.stateTicks >= ROLLING_DURATION) {
					this.switchToState(State.SCARED);
				}
			}
			case SCARED -> {
				if (!this.canStayRolledUp()) {
					this.rollOut();
				} else if (this.tickCount % SCARE_CHECK_INTERVAL == 0 && !this.anyThreatNearby()) {
					this.playSound(CTBSounds.ARMADILLO_UNROLL_START.get(), 1.0F, 1.0F);
					this.switchToState(State.UNROLLING);
				} else if (this.random.nextInt(80) == 0) {
					this.playSound(CTBSounds.ARMADILLO_PEEK.get(), 1.0F, 1.0F);
					this.level.broadcastEntityEvent(this, EVENT_PEEK);
				}
			}
			case UNROLLING -> {
				if (this.anyThreatNearby()) {
					this.switchToState(State.SCARED);
				} else if (this.stateTicks >= UNROLLING_DURATION) {
					this.rollOut();
				}
			}
		}
		// sheds a scute every five to ten minutes
		if (this.isAlive() && !this.isBaby() && --this.scuteTime <= 0) {
			this.playSound(CTBSounds.ARMADILLO_SCUTE_DROP.get(), 1.0F,
					(this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.spawnAtLocation(CTBItems.ARMADILLO_SCUTE.get());
			this.scuteTime = this.pickNextScuteDropTime();
		}
	}

	private int pickNextScuteDropTime() {
		return this.random.nextInt(6000) + 6000;
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == EVENT_PEEK) {
			this.peekTicks = 30;
		} else {
			super.handleEntityEvent(id);
		}
	}

	// --- combat, interactions ---

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isScared()) {
			amount = (amount - 1.0F) / 2.0F;
		}
		boolean hurt = super.hurt(source, amount);
		if (hurt && !this.level.isClientSide && !this.isDeadOrDying()) {
			if (source.getEntity() instanceof LivingEntity) {
				if (this.canStayRolledUp()) {
					this.rollUp();
				}
			} else if (source == DamageSource.DROWN || source.isFire()) {
				this.rollOut();
			}
		}
		return hurt;
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.getItem() instanceof com.natsu.backport.common.item.BrushItem && !this.isBaby()) {
			if (!this.level.isClientSide) {
				this.spawnAtLocation(CTBItems.ARMADILLO_SCUTE.get());
				this.playSound(CTBSounds.ARMADILLO_BRUSH.get(), 1.0F, 1.0F);
				stack.hurtAndBreak(16, player, p -> p.broadcastBreakEvent(hand));
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		if (this.isScared()) {
			return InteractionResult.FAIL;
		}
		return super.mobInteract(player, hand);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(CTBTags.Items.ARMADILLO_FOOD);
	}

	@Override
	public boolean canFallInLove() {
		return super.canFallInLove() && !this.isScared();
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return com.natsu.backport.common.registry.CTBEntities.ARMADILLO.get().create(level);
	}

	public static boolean checkArmadilloSpawnRules(EntityType<? extends Armadillo> type, LevelAccessor level,
			MobSpawnType spawnType, BlockPos pos, Random random) {
		return level.getBlockState(pos.below()).is(CTBTags.Blocks.ARMADILLO_SPAWNABLE_ON)
				&& level.getRawBrightness(pos, 0) > 8;
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
			MobSpawnType spawnType, SpawnGroupData groupData, CompoundTag tag) {
		return super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("state", this.entityData.get(DATA_STATE));
		tag.putInt("scute_time", this.scuteTime);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.entityData.set(DATA_STATE, tag.getInt("state") % State.values().length);
		if (tag.contains("scute_time")) {
			this.scuteTime = tag.getInt("scute_time");
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		EntityDimensions dimensions = super.getDimensions(pose);
		return this.isBaby() ? dimensions.scale(BABY_SCALE) : dimensions;
	}

	// --- sounds ---

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isScared() ? null : CTBSounds.ARMADILLO_AMBIENT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return CTBSounds.ARMADILLO_DEATH.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isScared() ? CTBSounds.ARMADILLO_HURT_REDUCED.get() : CTBSounds.ARMADILLO_HURT.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(CTBSounds.ARMADILLO_STEP.get(), 0.15F, 1.0F);
	}

	@Override
	public int getMaxHeadYRot() {
		return 32;
	}

	// --- goals ---

	private class ArmadilloPanicGoal extends PanicGoal {

		ArmadilloPanicGoal(double speedModifier) {
			super(Armadillo.this, speedModifier);
		}

		@Override
		public boolean canUse() {
			return !Armadillo.this.isScared() && super.canUse();
		}

		@Override
		public void start() {
			Armadillo.this.rollOut();
			super.start();
		}
	}

	private class ArmadilloTemptGoal extends TemptGoal {

		ArmadilloTemptGoal() {
			super(Armadillo.this, 1.25, Ingredient.of(CTBTags.Items.ARMADILLO_FOOD), false);
		}

		@Override
		public boolean canUse() {
			return !Armadillo.this.isScared() && super.canUse();
		}
	}

	private class ArmadilloStrollGoal extends WaterAvoidingRandomStrollGoal {

		ArmadilloStrollGoal(double speedModifier) {
			super(Armadillo.this, speedModifier);
		}

		@Override
		public boolean canUse() {
			return !Armadillo.this.isScared() && super.canUse();
		}
	}

	private class ArmadilloLookGoal extends LookAtPlayerGoal {

		ArmadilloLookGoal() {
			super(Armadillo.this, Player.class, 6.0F);
		}

		@Override
		public boolean canUse() {
			return !Armadillo.this.isScared() && super.canUse();
		}
	}

	@Override
	public void travel(net.minecraft.world.phys.Vec3 input) {
		if (this.isScared()) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
			input = input.multiply(0.0, 1.0, 0.0);
		}
		super.travel(input);
	}

	// --- geckolib ---

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "main", 4, event -> {
			switch (this.getState()) {
				case ROLLING -> event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("special.roll_up", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
				case SCARED -> {
					if (this.peekTicks > 0) {
						event.getController().setAnimation(new AnimationBuilder()
								.addAnimation("special.peek", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
					} else {
						event.getController().setAnimation(new AnimationBuilder()
								.addAnimation("special.roll_up", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
					}
				}
				case UNROLLING -> event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("special.roll_out", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
				default -> {
					if (this.animationSpeed > 0.02F) {
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
