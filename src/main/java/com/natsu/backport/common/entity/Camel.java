package com.natsu.backport.common.entity;

import java.util.EnumSet;
import java.util.Random;

import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
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
 * The 1.20 camel on the 1.18.2 horse : two seats, a dash on the jump bar,
 * sits down when idling. The 26.2 brain is rebuilt with goals.
 */
public class Camel extends AbstractHorse implements IAnimatable {

	public static final float BABY_SCALE = 0.6F;
	public static final int DASH_COOLDOWN_TICKS = 55;
	private static final float RUNNING_SPEED_BONUS = 0.1F;
	private static final float DASH_VERTICAL_MOMENTUM = 1.4285F;
	private static final float DASH_HORIZONTAL_MOMENTUM = 22.2222F;
	public static final int SITDOWN_DURATION_TICKS = 40;
	public static final int STANDUP_DURATION_TICKS = 52;
	private static final float SITTING_HEIGHT_DIFFERENCE = 1.43F;

	/** abs = the game time of the last pose change, negative while sitting. */
	private static final EntityDataAccessor<Integer> LAST_POSE_CHANGE_TICK =
			SynchedEntityData.defineId(Camel.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DASH =
			SynchedEntityData.defineId(Camel.class, EntityDataSerializers.BOOLEAN);

	private static final EntityDimensions SITTING_DIMENSIONS =
			EntityDimensions.scalable(1.7F, 2.375F - SITTING_HEIGHT_DIFFERENCE);

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	private int dashCooldown = 0;

	public Camel(EntityType<? extends Camel> type, Level level) {
		super(type, level);
		this.maxUpStep = 1.5F;
		this.moveControl = new CamelMoveControl(this);
		this.lookControl = new CamelLookControl(this);
		GroundPathNavigation navigation = (GroundPathNavigation) this.getNavigation();
		navigation.setCanFloat(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createBaseHorseAttributes()
				.add(Attributes.MAX_HEALTH, 32.0)
				.add(Attributes.MOVEMENT_SPEED, 0.09F)
				.add(Attributes.JUMP_STRENGTH, 0.42F);
	}

	@Override
	protected void randomizeAttributes() {
		// the camel stats are fixed, no horse rolls
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DASH, false);
		this.entityData.define(LAST_POSE_CHANGE_TICK, 0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new CamelPanicGoal(4.0));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
		this.goalSelector.addGoal(3, new TemptGoal(this, 2.5, Ingredient.of(CTBTags.Items.CAMEL_FOOD), false));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 2.5));
		this.goalSelector.addGoal(5, new RandomSittingGoal());
		this.goalSelector.addGoal(6, new CamelStrollGoal(2.0));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.resetLastPoseChangeTick((int) tag.getLong("LastPoseTick"));
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
			MobSpawnType spawnType, SpawnGroupData groupData, CompoundTag tag) {
		this.resetLastPoseChangeTickToFullStand((int) level.getLevel().getGameTime());
		return super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
	}

	public static boolean checkCamelSpawnRules(EntityType<? extends Camel> type, LevelAccessor level,
			MobSpawnType spawnType, BlockPos pos, Random random) {
		return level.getBlockState(pos.below()).is(CTBTags.Blocks.CAMELS_SPAWNABLE_ON)
				&& level.getRawBrightness(pos, 0) > 8;
	}

	// --- sitting ---

	public boolean isCamelSitting() {
		return this.entityData.get(LAST_POSE_CHANGE_TICK) < 0;
	}

	public boolean isCamelVisuallySitting() {
		return this.getPoseTime() < 0L != this.isCamelSitting();
	}

	public boolean isInPoseTransition() {
		return this.getPoseTime() < (this.isCamelSitting() ? SITDOWN_DURATION_TICKS : STANDUP_DURATION_TICKS);
	}

	public boolean isVisuallySittingDown() {
		return this.isCamelSitting() && this.getPoseTime() < SITDOWN_DURATION_TICKS && this.getPoseTime() >= 0L;
	}

	public long getPoseTime() {
		return (int) this.level.getGameTime() - Math.abs(this.entityData.get(LAST_POSE_CHANGE_TICK));
	}

	public void sitDown() {
		if (this.isCamelSitting()) {
			return;
		}
		this.playSound(this.getSitDownSound(), 1.0F, 1.0F);
		this.resetLastPoseChangeTick(-(int) this.level.getGameTime());
		this.refreshDimensions();
	}

	public void standUp() {
		if (!this.isCamelSitting()) {
			return;
		}
		this.playSound(this.getStandUpSound(), 1.0F, 1.0F);
		this.resetLastPoseChangeTick((int) this.level.getGameTime());
		this.refreshDimensions();
	}

	public void standUpInstantly() {
		this.resetLastPoseChangeTickToFullStand((int) this.level.getGameTime());
		this.refreshDimensions();
	}

	public void resetLastPoseChangeTick(int tick) {
		this.entityData.set(LAST_POSE_CHANGE_TICK, tick);
	}

	private void resetLastPoseChangeTickToFullStand(int now) {
		this.resetLastPoseChangeTick(Math.max(0, now - STANDUP_DURATION_TICKS - 1));
	}

	public boolean refuseToMove() {
		return this.isCamelSitting() || this.isInPoseTransition();
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		EntityDimensions dimensions = this.isCamelSitting() && !this.isInPoseTransition()
				? SITTING_DIMENSIONS : super.getDimensions(pose);
		return this.isBaby() ? dimensions.scale(BABY_SCALE) : dimensions;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height - 0.1F;
	}

	// --- ticking ---

	@Override
	public void tick() {
		super.tick();
		if (this.isDashing() && this.dashCooldown < 50
				&& (this.onGround || this.isInWater() || this.isPassenger())) {
			this.setDashing(false);
		}
		if (this.dashCooldown > 0) {
			this.dashCooldown--;
			if (this.dashCooldown == 0) {
				this.level.playSound(null, this.blockPosition(), this.getDashReadySound(),
						SoundSource.NEUTRAL, 1.0F, 1.0F);
			}
		}
		if (this.isCamelSitting() && this.isInWater()) {
			this.standUpInstantly();
		}
		if (!this.level.isClientSide) {
			this.updateSprintBonus();
		}
	}

	private static final java.util.UUID SPRINT_BONUS_ID =
			java.util.UUID.fromString("2a3b5c8e-9d41-4e76-b380-5c1f6e2a9d47");

	/** The vanilla running speed bonus while the rider sprints and the dash is ready. */
	private void updateSprintBonus() {
		boolean sprinting = this.getControllingPassenger() instanceof Player player
				&& player.isSprinting() && this.dashCooldown == 0;
		net.minecraft.world.entity.ai.attributes.AttributeInstance speed =
				this.getAttribute(Attributes.MOVEMENT_SPEED);
		boolean has = speed.getModifier(SPRINT_BONUS_ID) != null;
		if (sprinting && !has) {
			speed.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
					SPRINT_BONUS_ID, "Camel sprint bonus", RUNNING_SPEED_BONUS,
					net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION));
		} else if (!sprinting && has) {
			speed.removeModifier(SPRINT_BONUS_ID);
		}
	}

	@Override
	public void travel(Vec3 input) {
		if (this.isAlive()) {
			LivingEntity rider = this.getControllingPassenger() instanceof LivingEntity living ? living : null;
			if (rider != null && rider.zza > 0.0F && this.isCamelSitting() && !this.isInPoseTransition()) {
				this.standUp();
			}
			if (this.refuseToMove() && this.onGround) {
				this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
				input = input.multiply(0.0, 1.0, 0.0);
			}
		}
		super.travel(input);
	}

	// --- dash ---

	public boolean isDashing() {
		return this.entityData.get(DASH);
	}

	public void setDashing(boolean dashing) {
		this.entityData.set(DASH, dashing);
	}

	public int getDashCooldown() {
		return this.dashCooldown;
	}

	@Override
	public void onPlayerJump(int jumpPower) {
		if (!this.isSaddled() || this.dashCooldown > 0 || !this.onGround || this.refuseToMove()) {
			return;
		}
		if (jumpPower < 0) {
			jumpPower = 0;
		}
		// the 1.18.2 horse jump executes inside travel with no hook : dash here instead
		float amount = jumpPower >= 90 ? 1.0F : 0.4F + 0.4F * jumpPower / 90.0F;
		this.executeDash(amount);
	}

	private void executeDash(float amount) {
		double jumpStrength = this.getCustomJump();
		this.setDeltaMovement(this.getDeltaMovement().add(
				this.getLookAngle().multiply(1.0, 0.0, 1.0).normalize()
						.scale(DASH_HORIZONTAL_MOMENTUM * amount
								* this.getAttributeValue(Attributes.MOVEMENT_SPEED)
								* this.getBlockSpeedFactor())
						.add(0.0, DASH_VERTICAL_MOMENTUM * amount * jumpStrength, 0.0)));
		this.dashCooldown = DASH_COOLDOWN_TICKS;
		this.setDashing(true);
		this.hasImpulse = true;
	}

	@Override
	public void handleStartJump(int jumpScale) {
		this.playSound(this.getDashingSound(), 1.0F, 1.0F);
		this.setDashing(true);
	}

	@Override
	public void handleStopJump() {
	}

	@Override
	public void onSyncedDataUpdated(net.minecraft.network.syncher.EntityDataAccessor<?> accessor) {
		// the jump runs client side only : arm the cooldown wherever DASH flips,
		// otherwise the server clears the flag on the very next tick
		if (!this.firstTick && DASH.equals(accessor) && this.entityData.get(DASH)) {
			if (this.dashCooldown == 0) {
				this.dashCooldown = DASH_COOLDOWN_TICKS;
			}
		}
		if (!this.firstTick && LAST_POSE_CHANGE_TICK.equals(accessor)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(accessor);
	}

	public boolean canSprint() {
		return true;
	}

	// --- interactions ---

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isSecondaryUseActive() && !this.isBaby()) {
			this.openInventory(player);
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		InteractionResult used = stack.interactLivingEntity(player, this, hand);
		if (used.consumesAction()) {
			return used;
		}
		if (this.isFood(stack)) {
			return this.fedFood(player, stack);
		}
		// the 1.20 direct saddling, no inventory trip needed
		if (stack.is(net.minecraft.world.item.Items.SADDLE) && !this.isSaddled() && !this.isBaby()) {
			if (!this.level.isClientSide) {
				this.equipSaddle(SoundSource.NEUTRAL);
				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		if (this.getPassengers().size() < 2 && !this.isBaby()) {
			this.doPlayerRide(player);
		}
		return InteractionResult.sidedSuccess(this.level.isClientSide);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(CTBTags.Items.CAMEL_FOOD);
	}

	@Override
	protected boolean handleEating(Player player, ItemStack stack) {
		if (!this.isFood(stack)) {
			return false;
		}
		boolean couldHeal = this.getHealth() < this.getMaxHealth();
		if (couldHeal) {
			this.heal(2.0F);
		}
		boolean couldSetInLove = this.isTamed() && this.getAge() == 0 && this.canFallInLove();
		if (couldSetInLove) {
			this.setInLove(player);
		}
		boolean couldAgeUp = this.isBaby();
		if (couldAgeUp) {
			this.level.addParticle(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
					this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
			if (!this.level.isClientSide) {
				this.ageUp(10);
			}
		}
		if (couldHeal || couldSetInLove || couldAgeUp) {
			if (!this.isSilent()) {
				this.playSound(this.getEatingSound(), 1.0F,
						1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isTamed() {
		return true;
	}

	@Override
	public boolean canMate(Animal partner) {
		return partner != this && partner instanceof Camel other && this.canParent() && other.canParent();
	}

	@Override
	public Camel getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return CTBEntities.CAMEL.get().create(level);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		this.standUpInstantly();
		return super.hurt(source, amount);
	}

	// --- passengers ---

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	@Override
	public void positionRider(Entity passenger) {
		if (!this.hasPassenger(passenger)) {
			return;
		}
		int index = Math.max(this.getPassengers().indexOf(passenger), 0);
		boolean driver = index == 0;
		float offset = 0.5F;
		float scale = this.isBaby() ? BABY_SCALE : 1.0F;
		if (this.getPassengers().size() > 1 && !driver) {
			offset = -0.7F;
		}
		if (!driver && passenger instanceof Animal) {
			offset += 0.2F;
		}
		double height = this.getBodyAnchorAnimationYOffset(driver, 0.0F) + passenger.getMyRidingOffset();
		Vec3 seat = new Vec3(0.0, height, offset * scale)
				.yRot(-this.getYRot() * ((float) Math.PI / 180.0F));
		passenger.setPos(this.getX() + seat.x, this.getY() + seat.y, this.getZ() + seat.z);
		if (passenger instanceof LivingEntity living) {
			living.yBodyRot = this.yBodyRot;
		}
	}

	/** The vanilla two part sit and stand ease for the rider anchor. */
	public double getBodyAnchorAnimationYOffset(boolean isFront, float partialTicks) {
		double baseSitOffset = this.getDimensions(Pose.STANDING).height
				- (this.isBaby() ? 0.09375 : 0.375);
		float scale = this.isBaby() ? BABY_SCALE : 1.0F;
		float sittingHeightDifference = scale * SITTING_HEIGHT_DIFFERENCE;
		float verticalDrop = sittingHeightDifference - scale * 0.2F;
		float bottomPoint = sittingHeightDifference - verticalDrop;
		boolean isInTransition = this.isInPoseTransition();
		boolean isSitting = this.isCamelSitting();
		if (isInTransition) {
			int animationDuration = isSitting ? SITDOWN_DURATION_TICKS : STANDUP_DURATION_TICKS;
			int halfPoint;
			float flexPointOffset;
			if (isSitting) {
				halfPoint = 28;
				flexPointOffset = isFront ? 0.5F : 0.1F;
			} else {
				halfPoint = isFront ? 24 : 32;
				flexPointOffset = isFront ? 0.6F : 0.35F;
			}
			float poseTime = Mth.clamp((float) this.getPoseTime() + partialTicks, 0.0F, animationDuration);
			boolean isFirstPart = poseTime < halfPoint;
			float part = isFirstPart ? poseTime / halfPoint
					: (poseTime - halfPoint) / (animationDuration - halfPoint);
			float flexPoint = sittingHeightDifference - flexPointOffset * verticalDrop;
			baseSitOffset += isSitting
					? Mth.lerp(part, isFirstPart ? sittingHeightDifference : flexPoint,
							isFirstPart ? flexPoint : bottomPoint)
					: Mth.lerp(part, isFirstPart ? bottomPoint - sittingHeightDifference : bottomPoint - flexPoint,
							isFirstPart ? bottomPoint - flexPoint : 0.0F);
		}
		if (isSitting && !isInTransition) {
			baseSitOffset += bottomPoint;
		}
		return baseSitOffset;
	}

	@Override
	public int getMaxHeadYRot() {
		return 30;
	}

	// --- sounds ---

	protected SoundEvent getDashingSound() {
		return CTBSounds.CAMEL_DASH.get();
	}

	protected SoundEvent getDashReadySound() {
		return CTBSounds.CAMEL_DASH_READY.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return CTBSounds.CAMEL_AMBIENT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return CTBSounds.CAMEL_DEATH.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return CTBSounds.CAMEL_HURT.get();
	}

	@Override
	protected SoundEvent getEatingSound() {
		return CTBSounds.CAMEL_EAT.get();
	}

	protected SoundEvent getStandUpSound() {
		return CTBSounds.CAMEL_STAND.get();
	}

	protected SoundEvent getSitDownSound() {
		return CTBSounds.CAMEL_SIT.get();
	}

	protected SoundEvent getSaddleSoundEvent() {
		return CTBSounds.CAMEL_SADDLE.get();
	}

	protected SoundEvent getStepSound() {
		return CTBSounds.CAMEL_STEP.get();
	}

	protected SoundEvent getSandStepSound() {
		return CTBSounds.CAMEL_STEP_SAND.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (state.is(CTBTags.Blocks.CAMEL_SAND_STEP_SOUND_BLOCKS)) {
			this.playSound(this.getSandStepSound(), 1.0F, 1.0F);
		} else {
			this.playSound(this.getStepSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void equipSaddle(SoundSource source) {
		this.inventory.setItem(0, new ItemStack(net.minecraft.world.item.Items.SADDLE));
		if (source != null) {
			this.level.playSound(null, this, this.getSaddleSoundEvent(), source, 0.5F, 1.0F);
		}
	}

	// --- geckolib ---

	private boolean inStandUpTransition() {
		return !this.isCamelVisuallySitting() && this.isInPoseTransition() && this.getPoseTime() >= 0L;
	}

	@Override
	public void registerControllers(AnimationData data) {
		// one controller and every animation keys every bone : geckolib bones
		// are shared across entities, an unkeyed bone inherits the pose of
		// whichever camel rendered just before
		data.addAnimationController(new AnimationController<>(this, "main", 4, event -> {
			if (this.isCamelVisuallySitting()) {
				// HOLD, not PLAY_ONCE : a finished PLAY_ONCE snaps back to the
				// bind pose and the re-set restarts it, blinking sat and stood
				if (this.isVisuallySittingDown()) {
					event.getController().setAnimation(new AnimationBuilder()
							.addAnimation("special.sit", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
				} else {
					event.getController().setAnimation(new AnimationBuilder()
							.addAnimation("special.sit_pose", ILoopType.EDefaultLoopTypes.LOOP));
				}
				return PlayState.CONTINUE;
			}
			if (this.isDashing()) {
				event.getController().setAnimationSpeed(1.0);
				event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("moove.dash", ILoopType.EDefaultLoopTypes.LOOP));
				return PlayState.CONTINUE;
			}
			if (this.inStandUpTransition()) {
				event.getController().setAnimationSpeed(1.0);
				event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("special.standup", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
				return PlayState.CONTINUE;
			}
			if (event.isMoving()) {
				// the walk cycle keeps up with the actual speed, sprint included
				double speed = this.getDeltaMovement().horizontalDistance();
				event.getController().setAnimationSpeed(Mth.clamp(speed / 0.09, 0.6, 3.0));
				event.getController().setAnimation(new AnimationBuilder()
						.addAnimation("moove.walk", ILoopType.EDefaultLoopTypes.LOOP));
				return PlayState.CONTINUE;
			}
			event.getController().setAnimationSpeed(1.0);
			event.getController().setAnimation(new AnimationBuilder()
					.addAnimation("moove.idle", ILoopType.EDefaultLoopTypes.LOOP));
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	// --- controls and goals ---

	private class CamelMoveControl extends MoveControl {

		CamelMoveControl(Camel camel) {
			super(camel);
		}

		@Override
		public void tick() {
			if (this.operation == MoveControl.Operation.MOVE_TO && !Camel.this.isLeashed()
					&& Camel.this.isCamelSitting() && !Camel.this.isInPoseTransition()) {
				Camel.this.standUp();
			}
			super.tick();
		}
	}

	private class CamelLookControl extends LookControl {

		CamelLookControl(Camel camel) {
			super(camel);
		}

		@Override
		public void tick() {
			if (Camel.this.getControllingPassenger() == null) {
				super.tick();
			}
		}
	}

	@Override
	protected BodyRotationControl createBodyControl() {
		return new BodyRotationControl(this) {
			@Override
			public void clientTick() {
				if (!Camel.this.refuseToMove()) {
					super.clientTick();
				}
			}
		};
	}

	private class CamelPanicGoal extends PanicGoal {

		CamelPanicGoal(double speedModifier) {
			super(Camel.this, speedModifier);
		}

		@Override
		public void start() {
			Camel.this.standUpInstantly();
			super.start();
		}
	}

	private class CamelStrollGoal extends WaterAvoidingRandomStrollGoal {

		CamelStrollGoal(double speedModifier) {
			super(Camel.this, speedModifier);
		}

		@Override
		public boolean canUse() {
			return !Camel.this.refuseToMove() && super.canUse();
		}
	}

	/** The vanilla RandomSitting behavior : sits for a while when nothing happens. */
	private class RandomSittingGoal extends Goal {

		private int sittingTicksLeft;

		RandomSittingGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
		}

		@Override
		public boolean canUse() {
			return !Camel.this.isVehicle() && !Camel.this.isInWater() && !Camel.this.isLeashed()
					&& Camel.this.onGround && !Camel.this.isCamelSitting()
					&& Camel.this.getNavigation().isDone()
					&& Camel.this.random.nextInt(reducedTickDelay(400)) == 0;
		}

		@Override
		public boolean canContinueToUse() {
			return this.sittingTicksLeft > 0 && !Camel.this.isVehicle() && !Camel.this.isInWater();
		}

		@Override
		public void start() {
			this.sittingTicksLeft = 300 + Camel.this.random.nextInt(600);
			// a leftover move order makes the move control stand us right back up
			Camel.this.getNavigation().stop();
			Camel.this.sitDown();
		}

		@Override
		public void stop() {
			if (Camel.this.isCamelSitting()) {
				Camel.this.standUp();
			}
		}

		@Override
		public void tick() {
			this.sittingTicksLeft--;
		}
	}
}
