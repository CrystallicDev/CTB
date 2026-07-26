package com.natsu.backport.common.entity;

import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

/**
 * The 26.2 Sulfur Cube aligned on vanilla : a peaceful cube that swallows an
 * item and turns into a pushable ball whose physics, sounds, explosiveness and
 * heat come from the matching archetype.
 */
public class SulphurCube extends Slime implements Bucketable {

	private static final EntityDataAccessor<ItemStack> DATA_BODY_ITEM =
			SynchedEntityData.defineId(SulphurCube.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> DATA_FROM_BUCKET =
			SynchedEntityData.defineId(SulphurCube.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> DATA_MAX_FUSE =
			SynchedEntityData.defineId(SulphurCube.class, EntityDataSerializers.INT);

	public static final int ADULT_SIZE = 2;
	public static final int BABY_SIZE = 1;
	public static final int DEFAULT_GROW_UP_TIME = 24000;
	private static final double PUSH_DISTANCE_THRESHOLD = 1.3;
	private static final double MAX_PLAYER_PUSH_SPEED = 0.5;
	private static final float PLAYER_PUSH_SPEED_SCALE = 0.3F;
	private static final float VEHICLE_PUSH_SPEED_SCALE = 0.16F;
	private static final float VERTICAL_PUSH_MULTIPLIER = 0.3F;

	/** absMoveTo wipes xOld for remote players, sample positions ourselves. */
	private static final WeakHashMap<Player, Vec3> LAST_SAMPLED_POS = new WeakHashMap<>();

	private int growUpTime = 0;
	private int fuse = -1;
	private int pushSoundCooldown = 0;
	private SulfurCubeArchetype archetype = SulfurCubeArchetype.REGULAR;

	public SulphurCube(EntityType<? extends SulphurCube> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 6.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 0.0D)
				.add(Attributes.FOLLOW_RANGE, 16.0D);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_BODY_ITEM, ItemStack.EMPTY);
		this.entityData.define(DATA_FROM_BUCKET, false);
		this.entityData.define(DATA_MAX_FUSE, -1);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (this.hasBodyItem()) {
			tag.put("BodyItem", this.getBodyItem().save(new CompoundTag()));
		}
		tag.putBoolean("FromBucket", this.fromBucket());
		tag.putInt("GrowUpTime", this.growUpTime);
		tag.putInt("Fuse", this.fuse);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("BodyItem")) {
			this.setBodyItem(ItemStack.of(tag.getCompound("BodyItem")));
		} else if (tag.contains("HeldBlock")) {
			// the pre alignment cubes carried a block state
			var state = net.minecraft.nbt.NbtUtils.readBlockState(tag.getCompound("HeldBlock"));
			this.setBodyItem(new ItemStack(state.getBlock().asItem()));
		}
		this.setFromBucket(tag.getBoolean("FromBucket"));
		this.growUpTime = tag.getInt("GrowUpTime");
		this.fuse = tag.contains("Fuse") ? tag.getInt("Fuse") : -1;
	}

	// --- body item and archetype ---

	public ItemStack getBodyItem() {
		return this.entityData.get(DATA_BODY_ITEM);
	}

	public void setBodyItem(ItemStack stack) {
		this.entityData.set(DATA_BODY_ITEM, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
		this.archetype = stack.isEmpty() ? SulfurCubeArchetype.REGULAR : SulfurCubeArchetype.byItem(stack);
	}

	public boolean hasBodyItem() {
		return !this.getBodyItem().isEmpty();
	}

	public SulfurCubeArchetype getArchetype() {
		return this.archetype;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
		super.onSyncedDataUpdated(accessor);
		if (DATA_BODY_ITEM.equals(accessor)) {
			ItemStack stack = this.getBodyItem();
			this.archetype = stack.isEmpty() ? SulfurCubeArchetype.REGULAR : SulfurCubeArchetype.byItem(stack);
		}
	}

	public boolean isAdult() {
		return this.getSize() >= ADULT_SIZE;
	}

	@Override
	protected net.minecraft.core.particles.ParticleOptions getParticleType() {
		return com.natsu.backport.common.registry.CTBParticles.SULFUR_CUBE_GOO.get();
	}

	// --- goals : inert while loaded ---

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.removeAllGoals();
	}

	@Override
	public boolean isPushable() {
		return !this.hasBodyItem();
	}

	@Override
	protected void jumpFromGround() {
		if (this.hasBodyItem()) {
			return;
		}
		super.jumpFromGround();
	}

	@Override
	protected boolean isDealsDamage() {
		return false;
	}

	@Override
	public void refreshDimensions() {
		super.refreshDimensions();
		this.maxUpStep = this.hasBodyItem() ? 0.0F : 0.6F;
	}

	// --- explosive fuse ---

	public boolean isPrimed() {
		return this.fuse >= 0 && this.entityData.get(DATA_MAX_FUSE) >= 0;
	}

	public boolean canExplode() {
		return this.archetype.explosion != null && this.hasBodyItem() && this.isAlive() && !this.isPrimed();
	}

	public boolean primeTime(boolean imminent) {
		if (!this.canExplode() || this.level.isClientSide) {
			return false;
		}
		int fuseTime = this.archetype.explosion.fuse();
		if (imminent) {
			fuseTime = fuseTime / 8 + this.random.nextInt(Math.max(1, fuseTime / 8));
		}
		this.setInvulnerable(true);
		this.fuse = fuseTime;
		this.entityData.set(DATA_MAX_FUSE, fuseTime);
		this.playSound(SoundEvents.TNT_PRIMED, 1.0F, 1.0F);
		return true;
	}

	private void tickFuse() {
		if (this.fuse > 0) {
			this.fuse--;
		}
		if (this.archetype.explosion == null || !this.hasBodyItem()) {
			return;
		}
		if (this.fuse == 0 && this.isPrimed()) {
			this.dead = true;
			if (!this.level.isClientSide) {
				boolean grief = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
				this.level.explode(this, this.getX(), this.getY(0.0625), this.getZ(),
						this.archetype.explosion.power(), this.archetype.explosion.causesFire(),
						grief ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE);
			}
			this.discard();
		}
	}

	private void primeWhenOnPoweredPosition() {
		if (!this.level.isClientSide && this.canExplode()
				&& this.level.getBestNeighborSignal(this.blockPosition()) > 0) {
			this.primeTime(false);
		}
	}

	// --- ticking : fuse, contact damage, growth ---

	@Override
	public void tick() {
		this.tickFuse();
		this.primeWhenOnPoweredPosition();
		super.tick();
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.pushSoundCooldown > 0) {
			this.pushSoundCooldown--;
		}
		if (!this.level.isClientSide && this.isAlive() && !this.isAdult() && this.growUpTime > 0) {
			this.growUpTime--;
			if (this.growUpTime <= 0) {
				this.setSize(ADULT_SIZE, false);
				this.level.broadcastEntityEvent(this, (byte) 18);
			}
		}
		if (!this.level.isClientSide && this.hasBodyItem() && this.archetype.contactDamage > 0.0F
				&& this.tickCount % 10 == 0) {
			for (LivingEntity touched : this.level.getEntitiesOfClass(LivingEntity.class,
					this.getBoundingBox().inflate(0.1), e -> e != this && e.isAlive())) {
				touched.hurt(sulfurCubeHot(this), this.archetype.contactDamage);
				touched.setSecondsOnFire(1);
			}
		}
	}

	/** The ball : no walking, manual bounce, friction and drag per archetype. */
	@Override
	public void travel(Vec3 input) {
		if (!this.hasBodyItem()) {
			super.travel(input);
			return;
		}
		Vec3 before = this.getDeltaMovement();
		this.move(MoverType.SELF, before);
		Vec3 velocity = this.getDeltaMovement();
		double vx = velocity.x;
		double vy = velocity.y;
		double vz = velocity.z;

		// bounce on impact
		if (this.verticalCollision && before.y < -0.1 && this.archetype.bounce > 0.01F) {
			vy = -before.y * this.archetype.bounce;
			if (before.y < -0.3) {
				this.playSound(CTBSounds.SULFUR_CUBE_BOUNCE.get(), 1.0F, 1.0F);
			}
		}
		if (this.horizontalCollision && this.archetype.bounce > 0.01F) {
			if (Math.abs(before.x) > 0.05 && Math.abs(vx) < 1.0E-4) {
				vx = -before.x * this.archetype.bounce;
			}
			if (Math.abs(before.z) > 0.05 && Math.abs(vz) < 1.0E-4) {
				vz = -before.z * this.archetype.bounce;
			}
		}

		// gravity or buoyancy
		if (this.isInWater() && this.archetype.buoyant) {
			vy = Math.min(vy + 0.04, 0.12);
		} else if (!this.isNoGravity()) {
			vy -= 0.08;
			vy *= 0.98;
		}

		// friction on the ground, drag in the air
		if (this.onGround) {
			double frictionFactor = Mth.clamp(0.98 - this.archetype.friction * 0.38, 0.05, 0.98);
			vx *= frictionFactor;
			vz *= frictionFactor;
		} else {
			double dragFactor = Mth.clamp(0.99 - this.archetype.drag * 0.05, 0.5, 0.995);
			vx *= dragFactor;
			vz *= dragFactor;
		}
		this.setDeltaMovement(vx, vy, vz);
	}

	// --- pushing the ball around ---

	@Override
	public void playerTouch(Player player) {
		super.playerTouch(player);
		this.playerPush(player);
	}

	private void playerPush(Player player) {
		if (!this.hasBodyItem() || this.level.isClientSide) {
			return;
		}
		Entity pusher = player.isPassenger() ? player.getRootVehicle() : player;
		Vec3 cubeToPusher = this.position().subtract(pusher.position());
		double cubeTop = this.getY() + this.getBbHeight();
		double pusherTop = pusher.getY() + pusher.getBbHeight();
		if (cubeToPusher.horizontalDistance() >= PUSH_DISTANCE_THRESHOLD
				|| pusher.getY() > cubeTop || pusherTop <= this.getY()) {
			return;
		}
		Vec3 pushDirection = new Vec3(cubeToPusher.x, 0.0, cubeToPusher.z).normalize().scale(this.massFactor());
		float pushSpeedScale = player.isPassenger() ? VEHICLE_PUSH_SPEED_SCALE : PLAYER_PUSH_SPEED_SCALE;
		double playerSpeed = sampleSpeed(player) * 2.0 * pushSpeedScale;
		playerSpeed = Mth.clamp(playerSpeed, 0.0, MAX_PLAYER_PUSH_SPEED);
		Vec3 pushVelocity = new Vec3(pushDirection.x,
				this.onGround ? VERTICAL_PUSH_MULTIPLIER : 0.0, pushDirection.z).scale(playerSpeed);
		if (pushVelocity.lengthSqr() < 1.0E-6) {
			return;
		}
		this.setDeltaMovement(this.getDeltaMovement().add(pushVelocity));
		this.hurtMarked = true;
		float threshold = this.archetype.pushSoundImpulseThreshold;
		if (pushVelocity.lengthSqr() > threshold * threshold && this.pushSoundCooldown <= 0) {
			this.pushSoundCooldown = (int) (this.archetype.pushSoundCooldown * 20.0F);
			this.playSound(this.archetype.pushSound.get(), 1.0F, 1.0F);
		}
	}

	private static double sampleSpeed(Player player) {
		Vec3 now = player.position();
		Vec3 previous = LAST_SAMPLED_POS.put(player, now);
		if (previous == null || previous.distanceToSqr(now) > 25.0) {
			return 0.0;
		}
		return previous.distanceTo(now);
	}

	// --- getting hit : the ball flies where the attacker looks ---

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.hasBodyItem() && !source.isBypassInvul()) {
			if (this.canExplode()) {
				Entity direct = source.getDirectEntity();
				if (source.isFire() || direct instanceof AbstractArrow arrow && arrow.isOnFire()) {
					this.primeTime(false);
				} else if (source.isExplosion()) {
					this.primeTime(true);
				}
			}
			if (source.getEntity() instanceof LivingEntity attacker
					&& !source.isFire() && !source.isExplosion() && !source.isMagic()) {
				this.hitAsBall(attacker, Math.max(amount, 1.0F));
				SoundEvent hurtSound = this.getHurtSound(source);
				if (hurtSound != null) {
					this.playSound(hurtSound, this.getSoundVolume(), this.getVoicePitch());
				}
				this.level.broadcastEntityEvent(this, (byte) 2);
				return false;
			}
		}
		return super.hurt(source, amount);
	}

	/** The negated knockback resistance the vanilla attributes encode. */
	private float massFactor() {
		return Math.max(0.0F, 1.0F + this.archetype.speed);
	}

	private void hitAsBall(LivingEntity attacker, float damage) {
		Vec3 look = attacker.getLookAngle().normalize();
		float powerMultiplier = Mth.sqrt(damage) * this.massFactor();
		float horizontal = this.archetype.knockbackHorizontal * powerMultiplier;
		float vertical = this.archetype.knockbackVertical * powerMultiplier;
		// looking down drives the ball flat, looking up pops it in the air
		vertical += (float) Math.max(0.0, -look.y) * 0.5F * horizontal;
		Vec3 flat = new Vec3(look.x, 0.0, look.z).normalize();
		this.setDeltaMovement(this.getDeltaMovement()
				.add(flat.x * horizontal, vertical, flat.z * horizontal));
		this.hurtMarked = true;
		this.playSound(this.archetype.hitSound.get(), 1.0F, 1.0F);
	}

	// --- interactions ---

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (this.isPrimed()) {
			return InteractionResult.PASS;
		}
		if (this.canExplode() && (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE))) {
			this.primeTime(false);
			if (stack.is(Items.FLINT_AND_STEEL)) {
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			} else if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		if (stack.is(Items.SHEARS) && this.hasBodyItem()) {
			if (!this.level.isClientSide) {
				this.spawnAtLocation(this.getBodyItem());
				this.setBodyItem(ItemStack.EMPTY);
				this.playSound(CTBSounds.SULFUR_CUBE_EJECT.get(), 1.0F, 1.0F);
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		if (this.isAdult() && SulfurCubeArchetype.isSwallowable(stack)) {
			boolean worked = this.equipItem(stack);
			if (worked && !this.level.isClientSide && !player.getAbilities().instabuild) {
				stack.shrink(1);
			}
			return worked ? InteractionResult.sidedSuccess(this.level.isClientSide) : InteractionResult.PASS;
		}
		if (stack.is(Items.BUCKET) && this.isAlive() && !this.hasBodyItem()) {
			this.playSound(this.getPickupSound(), 1.0F, 1.0F);
			ItemStack bucket = this.getBucketItemStack();
			this.saveToBucketTag(bucket);
			ItemStack result = ItemUtils.createFilledResult(stack, player, bucket, false);
			player.setItemInHand(hand, result);
			if (!this.level.isClientSide) {
				this.discard();
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		return super.mobInteract(player, hand);
	}

	public boolean equipItem(ItemStack heldItem) {
		if (!this.isAdult()) {
			return false;
		}
		if (this.hasBodyItem()) {
			if (heldItem.is(this.getBodyItem().getItem())) {
				return false;
			}
			if (!this.level.isClientSide) {
				this.spawnAtLocation(this.getBodyItem());
			}
		}
		if (!this.level.isClientSide) {
			ItemStack single = heldItem.copy();
			single.setCount(1);
			this.setBodyItem(single);
		}
		this.playSound(CTBSounds.SULFUR_CUBE_ABSORB.get(), 1.0F, 1.0F);
		return true;
	}

	/** Loose items on the floor get vacuumed too, like vanilla. */
	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		if (this.isAdult() && !this.hasBodyItem() && SulfurCubeArchetype.isSwallowable(itemEntity.getItem())) {
			ItemStack stack = itemEntity.getItem();
			this.setBodyItem(stack.split(1));
			this.playSound(CTBSounds.SULFUR_CUBE_ABSORB.get(), 1.0F, 1.0F);
			if (stack.isEmpty()) {
				itemEntity.discard();
			}
		}
	}

	@Override
	public boolean wantsToPickUp(ItemStack stack) {
		return this.isAdult() && !this.hasBodyItem() && SulfurCubeArchetype.isSwallowable(stack);
	}

	@Override
	public boolean canPickUpLoot() {
		return this.isAdult() && !this.hasBodyItem();
	}

	// --- sizes, growth, split ---

	public int getGrowUpTime() {
		return this.growUpTime;
	}

	public void setGrowUpTime(int growUpTime) {
		this.growUpTime = growUpTime;
	}

	@Override
	public void setSize(int size, boolean resetHealth) {
		super.setSize(size <= 1 ? BABY_SIZE : ADULT_SIZE, resetHealth);
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
			MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
		SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
		if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.SPAWNER
				|| reason == MobSpawnType.CHUNK_GENERATION || reason == MobSpawnType.SPAWN_EGG) {
			this.setSize(ADULT_SIZE, true);
			this.growUpTime = 0;
		}
		return result;
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		int size = this.getSize();
		if (!this.level.isClientSide && size > 1 && this.isDeadOrDying() && !this.isPrimed()) {
			if (this.hasBodyItem()) {
				this.spawnAtLocation(this.getBodyItem());
				this.setBodyItem(ItemStack.EMPTY);
			}
			this.spawnBabies(size);
			this.setSize(BABY_SIZE, false);
		}
		super.remove(reason);
	}

	private void spawnBabies(int parentSize) {
		float halfWidth = parentSize / 4.0F;
		for (int j = 0; j < 2; j++) {
			float xOff = (j % 2 - 0.5F) * halfWidth;
			float zOff = (j / 2 - 0.5F) * halfWidth;
			if (this.getType().create(this.level) instanceof SulphurCube child) {
				if (this.isPersistenceRequired()) {
					child.setPersistenceRequired();
				}
				child.setCustomName(this.getCustomName());
				child.setNoAi(this.isNoAi());
				child.setInvulnerable(this.isInvulnerable());
				child.setSize(BABY_SIZE, true);
				child.setGrowUpTime(DEFAULT_GROW_UP_TIME);
				child.moveTo(this.getX() + xOff, this.getY() + 0.5, this.getZ() + zOff,
						this.random.nextFloat() * 360.0F, 0.0F);
				this.level.addFreshEntity(child);
			}
		}
	}

	// --- sounds ---

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isTiny() ? CTBSounds.SULFUR_CUBE_SMALL_HURT.get() : CTBSounds.SULFUR_CUBE_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isTiny() ? CTBSounds.SULFUR_CUBE_SMALL_DEATH.get() : CTBSounds.SULFUR_CUBE_DEATH.get();
	}

	@Override
	protected SoundEvent getSquishSound() {
		if (this.isTiny()) {
			return CTBSounds.SULFUR_CUBE_SMALL_SQUISH.get();
		}
		return this.hasBodyItem() ? CTBSounds.SULFUR_CUBE_BOUNCE.get() : CTBSounds.SULFUR_CUBE_SQUISH.get();
	}

	@Override
	protected SoundEvent getJumpSound() {
		return this.isTiny() ? CTBSounds.SULFUR_CUBE_SMALL_JUMP.get() : CTBSounds.SULFUR_CUBE_JUMP.get();
	}

	// --- bucket ---

	@Override
	public boolean fromBucket() {
		return this.entityData.get(DATA_FROM_BUCKET);
	}

	@Override
	public void setFromBucket(boolean fromBucket) {
		this.entityData.set(DATA_FROM_BUCKET, fromBucket);
	}

	@Override
	public void saveToBucketTag(ItemStack stack) {
		Bucketable.saveDefaultDataToBucketTag(this, stack);
		CompoundTag tag = stack.getOrCreateTag();
		tag.putInt("Size", this.getSize() - 1);
		tag.putInt("GrowUpTime", this.growUpTime);
	}

	@Override
	public void loadFromBucketTag(CompoundTag tag) {
		Bucketable.loadDefaultDataFromBucketTag(this, tag);
		if (tag.contains("Size")) {
			this.setSize(tag.getInt("Size") + 1, true);
		}
		if (tag.contains("GrowUpTime")) {
			this.growUpTime = tag.getInt("GrowUpTime");
		}
	}

	@Override
	public ItemStack getBucketItemStack() {
		return new ItemStack(CTBItems.SULPHUR_CUBE_BUCKET.get());
	}

	@Override
	public SoundEvent getPickupSound() {
		return SoundEvents.BUCKET_FILL_FISH;
	}

	@Override
	public boolean requiresCustomPersistence() {
		return super.requiresCustomPersistence() || this.fromBucket() || this.hasBodyItem();
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return !this.fromBucket() && !this.hasCustomName() && !this.hasBodyItem();
	}

	@Override
	public boolean canFreeze() {
		return !this.hasBodyItem() && super.canFreeze();
	}

	/** The hot archetype death line needs its own source. */
	public static DamageSource sulfurCubeHot(SulphurCube cube) {
		return new EntityDamageSource("sulfurCubeHot", cube).setIsFire();
	}
}
