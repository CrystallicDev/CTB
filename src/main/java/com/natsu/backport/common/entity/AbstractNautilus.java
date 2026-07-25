package com.natsu.backport.common.entity;

import javax.annotation.Nullable;

import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public abstract class AbstractNautilus extends TamableAnimal implements PlayerRideableJumping, Saddleable, IAnimatable {

	private static final EntityDataAccessor<Boolean> DASH = SynchedEntityData.defineId(AbstractNautilus.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(AbstractNautilus.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<ItemStack> ARMOR = SynchedEntityData.defineId(AbstractNautilus.class, EntityDataSerializers.ITEM_STACK);
	private static final java.util.UUID ARMOR_MODIFIER_UUID = java.util.UUID.fromString("21b17c9e-6c37-4d43-8be1-2ff4c1f34e21");

	private static final int EFFECT_DURATION = 60;
	private static final int EFFECT_REFRESH_RATE = 40;
	private static final int DASH_COOLDOWN_TICKS = 40;
	private static final float DASH_MOMENTUM_IN_WATER = 1.2F;
	private static final float DASH_MOMENTUM_ON_LAND = 0.5F;
	private static final int TOTAL_AIR_SUPPLY = 300;

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	protected final net.minecraft.world.SimpleContainer inventory = new net.minecraft.world.SimpleContainer(2);
	private int dashCooldown = 0;
	protected float playerJumpPendingScale;

	protected AbstractNautilus(EntityType<? extends AbstractNautilus> type, Level level) {
		super(type, level);
		this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.011F, 0.0F, true);
		this.lookControl = new SmoothSwimmingLookControl(this, 10);
		this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		this.inventory.addListener(container -> this.inventoryChanged());
	}

	private void inventoryChanged() {
		if (this.level.isClientSide) {
			return;
		}
		this.entityData.set(SADDLED, this.inventory.getItem(0).is(Items.SADDLE));
		ItemStack armor = this.inventory.getItem(1);
		this.entityData.set(ARMOR, armor.copy());
		net.minecraft.world.entity.ai.attributes.AttributeInstance attribute = this.getAttribute(Attributes.ARMOR);
		if (attribute != null) {
			attribute.removeModifier(ARMOR_MODIFIER_UUID);
			if (armor.getItem() instanceof com.natsu.backport.common.item.NautilusArmorItem armorItem) {
				attribute.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
						ARMOR_MODIFIER_UUID, "Nautilus armor", armorItem.getProtection(),
						net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION));
			}
		}
	}

	public net.minecraft.world.SimpleContainer getInventory() {
		return this.inventory;
	}

	public ItemStack getArmor() {
		return this.entityData.get(ARMOR);
	}

	public void openInventory(Player player) {
		if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
			net.minecraftforge.network.NetworkHooks.openGui(serverPlayer,
					new net.minecraft.world.SimpleMenuProvider((id, playerInventory, unused) ->
							new com.natsu.backport.common.inventory.NautilusInventoryMenu(id, playerInventory,
									this.inventory, this), this.getDisplayName()),
					buffer -> buffer.writeVarInt(this.getId()));
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Animal.createLivingAttributes()
				.add(Attributes.ATTACK_KNOCKBACK)
				.add(Attributes.MAX_HEALTH, 15.0)
				.add(Attributes.MOVEMENT_SPEED, 1.0)
				.add(Attributes.ATTACK_DAMAGE, 3.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
				.add(Attributes.FOLLOW_RANGE, 16.0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, Ingredient.of(Items.PUFFERFISH, Items.PUFFERFISH_BUCKET), false));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0, 10));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DASH, false);
		this.entityData.define(SADDLED, false);
		this.entityData.define(ARMOR, ItemStack.EMPTY);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("SaddleItem", this.isSaddled());
		if (!this.inventory.getItem(1).isEmpty()) {
			tag.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.inventory.setItem(0, tag.getBoolean("SaddleItem") ? new ItemStack(Items.SADDLE) : ItemStack.EMPTY);
		this.inventory.setItem(1, tag.contains("ArmorItem") ? ItemStack.of(tag.getCompound("ArmorItem")) : ItemStack.EMPTY);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return !this.isTame() && !this.isBaby() ? stack.is(CTBTags.Items.NAUTILUS_TAMING_ITEMS) : stack.is(CTBTags.Items.NAUTILUS_FOOD);
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new WaterBoundPathNavigation(this, level);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader level) {
		return 0.0F;
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader level) {
		return level.isUnobstructed(this);
	}

	@Override
	public int getMaxAirSupply() {
		return TOTAL_AIR_SUPPLY;
	}

	// dries out on land like the vanilla nautilus
	@Override
	public void baseTick() {
		int air = this.getAirSupply();
		super.baseTick();
		if (!this.isNoAi() && !this.level.isClientSide) {
			if (this.isAlive() && !this.isInWater()) {
				this.setAirSupply(air - 1);
				if (this.getAirSupply() <= -20) {
					this.setAirSupply(0);
					this.hurt(DamageSource.DRY_OUT, 2.0F);
				}
			} else {
				this.setAirSupply(this.getMaxAirSupply());
			}
		}
	}

	// --- riding ---

	@Override
	public boolean isSaddleable() {
		return this.isAlive() && !this.isBaby() && this.isTame();
	}

	@Override
	public void equipSaddle(@Nullable SoundSource source) {
		this.inventory.setItem(0, new ItemStack(Items.SADDLE));
		if (source != null) {
			this.level.playSound(null, this, this.isUnderWater()
					? com.natsu.backport.common.registry.CTBSounds.NAUTILUS_SADDLE_UNDERWATER_EQUIP.get()
					: com.natsu.backport.common.registry.CTBSounds.NAUTILUS_SADDLE_EQUIP.get(), source, 0.5F, 1.0F);
		}
	}

	@Override
	public boolean isSaddled() {
		return this.entityData.get(SADDLED);
	}

	@Override
	protected void dropEquipment() {
		super.dropEquipment();
		for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
			ItemStack stack = this.inventory.getItem(slot);
			if (!stack.isEmpty()) {
				this.spawnAtLocation(stack);
				this.inventory.setItem(slot, ItemStack.EMPTY);
			}
		}
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return !this.isVehicle();
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.isSaddled() && this.getFirstPassenger() instanceof Player player ? player : null;
	}

	@Override
	public void travel(Vec3 input) {
		if (this.isAlive() && this.isVehicle() && this.getControllingPassenger() instanceof Player controller) {
			// steering, the pitch drives the vertical component like vanilla
			this.setYRot(this.getYRot() + Mth.wrapDegrees(controller.getYRot() - this.getYRot()) * 0.5F);
			this.setXRot(controller.getXRot() * 0.5F);
			this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

			float strafe = controller.xxa;
			float forward = 0.0F;
			float up = 0.0F;
			if (controller.zza != 0.0F) {
				float forwardLook = Mth.cos(controller.getXRot() * ((float) Math.PI / 180F));
				float upLook = -Mth.sin(controller.getXRot() * ((float) Math.PI / 180F));
				if (controller.zza < 0.0F) {
					forwardLook *= -0.5F;
					upLook *= -0.5F;
				}
				up = upLook;
				forward = forwardLook;
			}

			if (this.playerJumpPendingScale > 0.0F && !this.isDashing()) {
				this.executeRidersJump(this.playerJumpPendingScale, controller);
			}
			this.playerJumpPendingScale = 0.0F;

			float speed = (this.isInWater() ? 0.0325F : 0.02F) * (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
			if (this.isInWater()) {
				this.moveRelative(speed, new Vec3(strafe, up, forward));
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
			} else {
				this.flyingSpeed = speed * 0.1F;
				super.travel(new Vec3(strafe, up, forward));
			}
		} else if (this.isInWater()) {
			this.moveRelative(this.getSpeed(), input);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
		} else {
			super.travel(input);
		}
	}

	protected void doPlayerRide(Player player) {
		if (!this.level.isClientSide) {
			player.startRiding(this);
		}
	}

	// --- dash ---

	public boolean isDashing() {
		return this.entityData.get(DASH);
	}

	public void setDashing(boolean dashing) {
		this.entityData.set(DASH, dashing);
	}

	@Override
	public boolean canJump() {
		return this.isSaddled();
	}

	@Override
	public void onPlayerJump(int jumpAmount) {
		if (this.isSaddled() && this.dashCooldown <= 0) {
			if (jumpAmount >= 90) {
				this.playerJumpPendingScale = 1.0F;
			} else {
				this.playerJumpPendingScale = 0.4F + 0.4F * jumpAmount / 90.0F;
			}
		}
	}

	protected void executeRidersJump(float scale, Player controller) {
		Vec3 boost = controller.getLookAngle()
				.scale((this.isInWater() ? DASH_MOMENTUM_IN_WATER : DASH_MOMENTUM_ON_LAND) * scale * this.getAttributeValue(Attributes.MOVEMENT_SPEED));
		this.setDeltaMovement(this.getDeltaMovement().add(boost));
		this.dashCooldown = DASH_COOLDOWN_TICKS;
		this.setDashing(true);
	}

	@Override
	public void handleStartJump(int jumpScale) {
		SoundEvent dash = this.getDashSound();
		if (dash != null) {
			this.playSound(dash, 1.0F, 1.0F);
		}
		this.setDashing(true);
	}

	@Override
	public void handleStopJump() {
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
		if (!this.firstTick && DASH.equals(accessor)) {
			this.dashCooldown = this.dashCooldown == 0 ? DASH_COOLDOWN_TICKS : this.dashCooldown;
		}
		super.onSyncedDataUpdated(accessor);
	}

	// --- ticking ---

	@Override
	public void tick() {
		super.tick();
		if (!this.level.isClientSide) {
			this.applyEffects();
		}

		if (this.isDashing() && this.dashCooldown < DASH_COOLDOWN_TICKS - 5) {
			this.setDashing(false);
		}

		if (this.dashCooldown > 0) {
			this.dashCooldown--;
			if (this.dashCooldown == 0) {
				SoundEvent ready = this.getDashReadySound();
				if (ready != null) {
					this.playSound(ready, 1.0F, 1.0F);
				}
			}
		}

		if (this.level.isClientSide && this.isInWater()) {
			this.spawnBubbles();
		}
	}

	private void applyEffects() {
		if (this.getFirstPassenger() instanceof Player player) {
			boolean hasEffect = player.hasEffect(CTBEffects.BREATH_OF_THE_NAUTILUS.get());
			if (!hasEffect || this.level.getGameTime() % EFFECT_REFRESH_RATE == 0L) {
				player.addEffect(new MobEffectInstance(CTBEffects.BREATH_OF_THE_NAUTILUS.get(), EFFECT_DURATION, 0, true, true, true));
			}
		}
	}

	private void spawnBubbles() {
		double speed = this.getDeltaMovement().length();
		double bubbleProbability = Mth.clamp(speed * 2.0, 0.15F, 1.0);
		if (this.random.nextFloat() < bubbleProbability) {
			float xRot = Mth.clamp(this.getXRot(), -10.0F, 10.0F);
			Vec3 mouthDir = this.calculateViewVector(xRot, this.getYRot());
			double spread = this.random.nextDouble() * 0.8 * (1.0 + speed);
			this.level.addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE,
					this.getX() - mouthDir.x * 1.1, this.getY() - mouthDir.y + 0.25, this.getZ() - mouthDir.z * 1.1,
					(this.random.nextFloat() - 0.5) * spread,
					(this.random.nextFloat() - 0.5) * spread,
					(this.random.nextFloat() - 0.5) * spread);
		}
	}

	// --- interaction ---

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		if (this.isBaby()) {
			return super.mobInteract(player, hand);
		}

		if (this.isTame() && player.isSecondaryUseActive()) {
			if (!this.level.isClientSide) {
				this.openInventory(player);
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		if (!held.isEmpty() && this.isTame()
				&& held.getItem() instanceof com.natsu.backport.common.item.NautilusArmorItem
				&& this.inventory.getItem(1).isEmpty()) {
			if (!this.level.isClientSide) {
				ItemStack single = held.copy();
				single.setCount(1);
				this.inventory.setItem(1, single);
				if (!player.getAbilities().instabuild) {
					held.shrink(1);
				}
				this.level.playSound(null, this, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.NEUTRAL, 0.5F, 1.0F);
			}
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		if (!held.isEmpty()) {
			if (!this.level.isClientSide && !this.isTame() && this.isFood(held)) {
				this.usePlayerItem(player, hand, held);
				this.tryToTame(player);
				return InteractionResult.SUCCESS;
			}

			if (this.isFood(held) && this.getHealth() < this.getMaxHealth()) {
				this.usePlayerItem(player, hand, held);
				this.heal(2.0F);
				this.playEatingSound();
				return InteractionResult.sidedSuccess(this.level.isClientSide);
			}

			InteractionResult result = held.interactLivingEntity(player, this, hand);
			if (result.consumesAction()) {
				return result;
			}
		}

		if (this.isTame() && !player.isSecondaryUseActive() && !this.isFood(held)) {
			this.doPlayerRide(player);
			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		return super.mobInteract(player, hand);
	}

	@Override
	protected void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
		if (stack.is(CTBTags.Items.NAUTILUS_BUCKET_FOOD)) {
			if (!player.getAbilities().instabuild) {
				player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
			}
		} else {
			super.usePlayerItem(player, hand, stack);
		}
	}

	private void tryToTame(Player player) {
		if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
			this.tame(player);
			this.navigation.stop();
			this.level.broadcastEntityEvent(this, (byte) 7);
		} else {
			this.level.broadcastEntityEvent(this, (byte) 6);
		}

		this.playEatingSound();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		boolean wasHurt = super.hurt(source, amount);
		if (wasHurt && source.getEntity() instanceof LivingEntity attacker && !(attacker instanceof Player p && this.isOwnedBy(p))) {
			this.setTarget(attacker);
		}
		return wasHurt;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return effect.getEffect() != MobEffects.POISON && super.canBeAffected(effect);
	}

	@Override
	public boolean removeWhenFarAway(double distSqr) {
		return !this.isTame();
	}

	@Override
	protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
	}

	protected void playEatingSound() {
		this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
	}

	@Nullable
	protected SoundEvent getDashSound() {
		return null;
	}

	@Nullable
	protected SoundEvent getDashReadySound() {
		return null;
	}

	protected boolean isMobControlled() {
		return this.getFirstPassenger() instanceof Mob;
	}

	// --- geckolib ---

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("moove.swim", ILoopType.EDefaultLoopTypes.LOOP));
		// the jet pulse reads from the animation, so its speed follows the actual motion
		double speed = this.getDeltaMovement().horizontalDistance() + Math.abs(this.getDeltaMovement().y) * 0.5;
		event.getController().setAnimationSpeed(Mth.clamp(0.6 + speed * 10.0, 0.6, 2.5));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
