package com.natsu.backport.common.entity;

import java.util.EnumSet;
import java.util.Random;

import com.natsu.backport.common.item.HarnessItem;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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
 * The 1.21.6 happy ghast : a tamed sky mount. The harness lives in a synched
 * stack (1.18.2 has no body slot), the flight control is folded into travel,
 * and standing players freeze it into a solid platform through the boat style
 * canBeCollidedWith.
 */
public class HappyGhast extends Animal implements IAnimatable {

	public static final float BABY_SCALE = 0.2375F;
	public static final int MAX_PASSENGERS = 4;
	private static final int MAX_STILL_TIMEOUT = 10;

	private static final EntityDataAccessor<ItemStack> DATA_HARNESS =
			SynchedEntityData.defineId(HappyGhast.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> STAYS_STILL =
			SynchedEntityData.defineId(HappyGhast.class, EntityDataSerializers.BOOLEAN);

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	private int serverStillTimeout;

	public HappyGhast(EntityType<? extends HappyGhast> type, Level level) {
		super(type, level);
		this.moveControl = new GhastFloatMoveControl();
		this.lookControl = new GhastLookControl();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Animal.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0)
				.add(Attributes.FLYING_SPEED, 0.05)
				.add(Attributes.MOVEMENT_SPEED, 0.05)
				.add(Attributes.FOLLOW_RANGE, 16.0);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_HARNESS, ItemStack.EMPTY);
		this.entityData.define(STAYS_STILL, false);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(3, new GhastFloatGoal());
		this.goalSelector.addGoal(4, new TemptFloatGoal());
		this.goalSelector.addGoal(5, new RandomFloatAroundGoal());
	}

	// --- harness ---

	public ItemStack getHarness() {
		return this.entityData.get(DATA_HARNESS);
	}

	public boolean isWearingHarness() {
		return !this.getHarness().isEmpty();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("still_timeout", this.serverStillTimeout);
		if (this.isWearingHarness()) {
			tag.put("harness", this.getHarness().save(new CompoundTag()));
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setServerStillTimeout(tag.getInt("still_timeout"));
		this.entityData.set(DATA_HARNESS,
				tag.contains("harness") ? ItemStack.of(tag.getCompound("harness")) : ItemStack.EMPTY);
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (this.isBaby()) {
			return super.mobInteract(player, hand);
		}
		if (stack.getItem() instanceof HarnessItem && !this.isWearingHarness()) {
			if (!this.level.isClientSide) {
				ItemStack harness = stack.copy();
				harness.setCount(1);
				this.entityData.set(DATA_HARNESS, harness);
				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}
				this.playSound(CTBSounds.HARNESS_EQUIP.get(), 0.5F, 1.0F);
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		if (stack.is(Items.SHEARS) && this.isWearingHarness()) {
			if (!this.level.isClientSide) {
				this.spawnAtLocation(this.getHarness());
				this.entityData.set(DATA_HARNESS, ItemStack.EMPTY);
				this.playSound(CTBSounds.HARNESS_UNEQUIP.get(), 0.5F, 1.0F);
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				this.ejectPassengers();
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		if (this.isFood(stack)) {
			return super.mobInteract(player, hand);
		}
		if (this.isWearingHarness() && !player.isSecondaryUseActive()) {
			if (!this.level.isClientSide) {
				player.startRiding(this);
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		return super.mobInteract(player, hand);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(CTBTags.Items.HAPPY_GHAST_FOOD);
	}

	@Override
	public boolean canFallInLove() {
		return false;
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return com.natsu.backport.common.registry.CTBEntities.HAPPY_GHAST.get().create(level);
	}

	// --- still platform ---

	private void setServerStillTimeout(int timeout) {
		this.serverStillTimeout = timeout;
		this.entityData.set(STAYS_STILL, timeout > 0);
	}

	public boolean staysStill() {
		return this.entityData.get(STAYS_STILL);
	}

	public boolean isOnStillTimeout() {
		return this.staysStill() || this.serverStillTimeout > 0;
	}

	private boolean scanPlayerAboveGhast() {
		AABB box = this.getBoundingBox();
		AABB detection = new AABB(box.minX - 1.0, box.maxY - 1.0E-5F, box.minZ - 1.0,
				box.maxX + 1.0, box.maxY + box.getYsize() / 2.0, box.maxZ + 1.0);
		for (Player player : this.level.players()) {
			if (!player.isSpectator() && !(player.getRootVehicle() instanceof HappyGhast)
					&& detection.contains(player.getRootVehicle().position())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		// the boat trick : a collidable entity is a platform you can stand on
		return !this.isBaby() && this.isAlive() && this.isOnStillTimeout();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level.isClientSide) {
			return;
		}
		if (this.serverStillTimeout > 0) {
			if (this.tickCount > 60) {
				this.serverStillTimeout--;
			}
			this.setServerStillTimeout(this.serverStillTimeout);
		}
		if (this.scanPlayerAboveGhast()) {
			this.setServerStillTimeout(MAX_STILL_TIMEOUT);
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.continuousHeal();
	}

	/** Regenerates slowly, fast in the rain or up in the clouds. */
	private void continuousHeal() {
		if (this.level.isClientSide || !this.isAlive() || this.deathTime != 0
				|| this.getHealth() >= this.getMaxHealth()) {
			return;
		}
		boolean fast = this.getY() >= 192.0 || this.level.isRainingAt(this.blockPosition());
		if (this.tickCount % (fast ? 20 : 600) == 0) {
			this.heal(1.0F);
		}
	}

	// --- riding and flight ---

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengers().size() < MAX_PASSENGERS;
	}

	@Override
	protected void addPassenger(Entity passenger) {
		if (!this.isVehicle()) {
			this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
					CTBSounds.HARNESS_GOGGLES_DOWN.get(), this.getSoundSource(), 1.0F, 1.0F);
		}
		super.addPassenger(passenger);
		if (!this.level.isClientSide) {
			if (!this.scanPlayerAboveGhast()) {
				this.setServerStillTimeout(0);
			} else if (this.serverStillTimeout > MAX_STILL_TIMEOUT) {
				this.setServerStillTimeout(MAX_STILL_TIMEOUT);
			}
		}
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		if (!this.level.isClientSide) {
			this.setServerStillTimeout(MAX_STILL_TIMEOUT);
		}
		if (!this.isVehicle()) {
			this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
					CTBSounds.HARNESS_GOGGLES_UP.get(), this.getSoundSource(), 1.0F, 1.0F);
		}
	}

	@Override
	public Entity getControllingPassenger() {
		if (this.isWearingHarness() && !this.isOnStillTimeout()
				&& this.getFirstPassenger() instanceof Player player) {
			return player;
		}
		return null;
	}

	@Override
	public void positionRider(Entity passenger) {
		if (!this.hasPassenger(passenger)) {
			return;
		}
		int index = Math.max(this.getPassengers().indexOf(passenger), 0);
		// four seats on the back, driver front then clockwise
		double[][] seats = { { 0.0, 1.6 }, { -1.6, 0.0 }, { 0.0, -1.6 }, { 1.6, 0.0 } };
		double[] seat = seats[Math.min(index, 3)];
		Vec3 offset = new Vec3(seat[0], this.getPassengersRidingOffset(), seat[1])
				.yRot(-this.getYRot() * ((float) Math.PI / 180.0F));
		passenger.setPos(this.getX() + offset.x, this.getY() + offset.y + passenger.getMyRidingOffset(),
				this.getZ() + offset.z);
	}

	@Override
	public double getPassengersRidingOffset() {
		return this.getBbHeight();
	}

	@Override
	public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
		return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
	}

	@Override
	public void travel(Vec3 input) {
		if (this.getControllingPassenger() instanceof Player controller) {
			if (this.isControlledByLocalInstance()) {
				// yaw eases toward the rider, pitch follows half the look
				float wantedYRot = controller.getYRot();
				float yRot = this.getYRot() + Mth.wrapDegrees(wantedYRot - this.getYRot()) * 0.08F;
				this.setRot(yRot, controller.getXRot() * 0.5F);
				this.yBodyRot = this.yHeadRot = this.getYRot();
				this.yRotO = this.yHeadRot;

				float strafe = controller.xxa;
				float forward = 0.0F;
				float up = 0.0F;
				if (controller.zza != 0.0F) {
					float cos = Mth.cos(controller.getXRot() * ((float) Math.PI / 180.0F));
					float sin = -Mth.sin(controller.getXRot() * ((float) Math.PI / 180.0F));
					if (controller.zza < 0.0F) {
						cos *= -0.5F;
						sin *= -0.5F;
					}
					forward = cos;
					up = sin;
				}
				Vec3 ridden = new Vec3(strafe, up, forward)
						.scale(3.9F * this.getAttributeValue(Attributes.FLYING_SPEED));
				this.flyingTravel(ridden, 1.0F);
			} else {
				this.setDeltaMovement(Vec3.ZERO);
			}
			return;
		}
		if (this.isOnStillTimeout()) {
			this.setDeltaMovement(this.getDeltaMovement().scale(0.7));
			this.move(MoverType.SELF, this.getDeltaMovement());
			return;
		}
		this.flyingTravel(input, (float) this.getAttributeValue(Attributes.FLYING_SPEED) * 5.0F / 3.0F);
	}

	/** The ghast float physics : direct impulse and air friction. */
	private void flyingTravel(Vec3 input, float speed) {
		if (this.isInWater()) {
			this.moveRelative(0.02F, input);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
		} else {
			this.moveRelative(speed, input);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
		}
		this.calculateEntityAnimation(this, false);
	}

	@Override
	public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	protected void checkFallDamage(double ya, boolean onGround, net.minecraft.world.level.block.state.BlockState state,
			BlockPos pos) {
	}

	@Override
	public boolean onClimbable() {
		return false;
	}

	// --- dimensions, sounds ---

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		EntityDimensions dimensions = super.getDimensions(pose);
		return this.isBaby() ? dimensions.scale(BABY_SCALE / 1.0F) : dimensions;
	}

	@Override
	public float getVoicePitch() {
		return 1.0F;
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.NEUTRAL;
	}

	@Override
	public int getAmbientSoundInterval() {
		return this.isVehicle() ? super.getAmbientSoundInterval() * 6 : super.getAmbientSoundInterval();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isBaby() ? CTBSounds.GHASTLING_AMBIENT.get() : CTBSounds.HAPPY_GHAST_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isBaby() ? CTBSounds.GHASTLING_HURT.get() : CTBSounds.HAPPY_GHAST_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isBaby() ? CTBSounds.GHASTLING_DEATH.get() : CTBSounds.HAPPY_GHAST_DEATH.get();
	}

	@Override
	protected float getSoundVolume() {
		return this.isBaby() ? 1.0F : 4.0F;
	}

	@Override
	protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
	}

	// --- controls and goals ---

	private class GhastFloatMoveControl extends MoveControl {

		private int floatDuration;

		GhastFloatMoveControl() {
			super(HappyGhast.this);
		}

		@Override
		public void tick() {
			if (HappyGhast.this.isOnStillTimeout()) {
				return;
			}
			if (this.operation == MoveControl.Operation.MOVE_TO) {
				if (this.floatDuration-- <= 0) {
					this.floatDuration += HappyGhast.this.random.nextInt(5) + 2;
					Vec3 toWanted = new Vec3(this.wantedX - HappyGhast.this.getX(),
							this.wantedY - HappyGhast.this.getY(), this.wantedZ - HappyGhast.this.getZ());
					double distance = toWanted.length();
					toWanted = toWanted.normalize();
					if (this.canReach(toWanted, Mth.ceil(distance))) {
						HappyGhast.this.setDeltaMovement(
								HappyGhast.this.getDeltaMovement().add(toWanted.scale(0.1)));
					} else {
						this.operation = MoveControl.Operation.WAIT;
					}
				}
			}
		}

		private boolean canReach(Vec3 direction, int steps) {
			AABB box = HappyGhast.this.getBoundingBox();
			for (int i = 1; i < steps; i++) {
				box = box.move(direction);
				if (!HappyGhast.this.level.noCollision(HappyGhast.this, box)) {
					return false;
				}
			}
			return true;
		}
	}

	private class GhastLookControl extends LookControl {

		GhastLookControl() {
			super(HappyGhast.this);
		}

		@Override
		public void tick() {
			if (HappyGhast.this.isOnStillTimeout()) {
				float closeAngle = Mth.wrapDegrees(HappyGhast.this.getYRot()) * 0.5F;
				HappyGhast.this.setYRot(HappyGhast.this.getYRot() - closeAngle * 0.1F);
				HappyGhast.this.setYHeadRot(HappyGhast.this.getYRot());
				return;
			}
			// face the travel direction like a ghast
			Vec3 motion = HappyGhast.this.getDeltaMovement();
			if (motion.horizontalDistanceSqr() > 1.0E-6) {
				HappyGhast.this.setYRot(-((float) Mth.atan2(motion.x, motion.z)) * (180.0F / (float) Math.PI));
				HappyGhast.this.yBodyRot = HappyGhast.this.yHeadRot = HappyGhast.this.getYRot();
			}
		}
	}

	@Override
	protected BodyRotationControl createBodyControl() {
		return new BodyRotationControl(this) {
			@Override
			public void clientTick() {
				if (HappyGhast.this.isVehicle()) {
					HappyGhast.this.yBodyRot = HappyGhast.this.yHeadRot = HappyGhast.this.getYRot();
				}
				super.clientTick();
			}
		};
	}

	private class GhastFloatGoal extends FloatGoal {

		GhastFloatGoal() {
			super(HappyGhast.this);
		}

		@Override
		public boolean canUse() {
			return !HappyGhast.this.isOnStillTimeout() && super.canUse();
		}
	}

	/** Drifts toward players holding snacks or harnesses. */
	private class TemptFloatGoal extends Goal {

		private Player temptingPlayer;

		TemptFloatGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			if (HappyGhast.this.isOnStillTimeout() || HappyGhast.this.isVehicle()) {
				return false;
			}
			this.temptingPlayer = HappyGhast.this.level.getNearestPlayer(HappyGhast.this, 16.0);
			return this.temptingPlayer != null && this.isTempting(this.temptingPlayer);
		}

		private boolean isTempting(Player player) {
			return player.getMainHandItem().is(CTBTags.Items.HAPPY_GHAST_TEMPT_ITEMS)
					|| player.getOffhandItem().is(CTBTags.Items.HAPPY_GHAST_TEMPT_ITEMS);
		}

		@Override
		public boolean canContinueToUse() {
			return this.canUse();
		}

		@Override
		public void tick() {
			HappyGhast.this.getLookControl().setLookAt(this.temptingPlayer);
			if (HappyGhast.this.distanceToSqr(this.temptingPlayer) > 9.0) {
				HappyGhast.this.moveControl.setWantedPosition(this.temptingPlayer.getX(),
						this.temptingPlayer.getEyeY() + 1.5, this.temptingPlayer.getZ(), 1.0);
			}
		}
	}

	/** The vanilla ghast wander, biased to stay near the ground. */
	private class RandomFloatAroundGoal extends Goal {

		RandomFloatAroundGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			if (HappyGhast.this.isOnStillTimeout() || HappyGhast.this.isVehicle()) {
				return false;
			}
			MoveControl control = HappyGhast.this.getMoveControl();
			if (!control.hasWanted()) {
				return true;
			}
			double dx = control.getWantedX() - HappyGhast.this.getX();
			double dy = control.getWantedY() - HappyGhast.this.getY();
			double dz = control.getWantedZ() - HappyGhast.this.getZ();
			double distSqr = dx * dx + dy * dy + dz * dz;
			return distSqr < 1.0 || distSqr > 3600.0;
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}

		@Override
		public void start() {
			Random random = HappyGhast.this.random;
			double x = HappyGhast.this.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
			double y = HappyGhast.this.getY() + (random.nextFloat() * 2.0F - 1.0F) * 8.0F;
			double z = HappyGhast.this.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
			// stay a handful of blocks above the surface
			BlockPos surface = HappyGhast.this.level.getHeightmapPos(
					net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
					new BlockPos(x, y, z));
			y = Mth.clamp(y, surface.getY() + 2.0, surface.getY() + 12.0);
			HappyGhast.this.getMoveControl().setWantedPosition(x, y, z, 1.0);
		}
	}

	// --- geckolib ---

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "main", 4, event -> {
			event.getController().setAnimation(new AnimationBuilder()
					.addAnimation("moove.float", ILoopType.EDefaultLoopTypes.LOOP));
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
