package com.natsu.backport.common.entity;

import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/** Floats above a player during an ominous trial, then drops its item on their head. */
public class OminousItemSpawner extends Entity {

	private static final int SPAWN_ITEM_DELAY_MIN = 60;
	private static final int SPAWN_ITEM_DELAY_MAX = 120;
	private static final int TICKS_BEFORE_ABOUT_TO_SPAWN_SOUND = 36;
	private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(OminousItemSpawner.class, EntityDataSerializers.ITEM_STACK);

	public long spawnItemAfterTicks;

	public OminousItemSpawner(EntityType<? extends OminousItemSpawner> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	public static OminousItemSpawner create(Level level, ItemStack item) {
		OminousItemSpawner spawner = new OminousItemSpawner(CTBEntities.OMINOUS_ITEM_SPAWNER.get(), level);
		spawner.spawnItemAfterTicks = SPAWN_ITEM_DELAY_MIN + level.getRandom().nextInt(SPAWN_ITEM_DELAY_MAX - SPAWN_ITEM_DELAY_MIN + 1);
		spawner.setItem(item);
		return spawner;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level instanceof ServerLevel serverLevel) {
			this.tickServer(serverLevel);
		} else {
			this.tickClient();
		}
	}

	private void tickServer(ServerLevel level) {
		if (this.tickCount == this.spawnItemAfterTicks - TICKS_BEFORE_ABOUT_TO_SPAWN_SOUND) {
			level.playSound(null, this.blockPosition(), CTBSounds.TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
		}

		if (this.tickCount >= this.spawnItemAfterTicks) {
			this.spawnItem(level);
			this.discard();
		}
	}

	private void tickClient() {
		if (this.level.getGameTime() % 5L == 0L) {
			this.addParticles();
		}
	}

	private void spawnItem(ServerLevel level) {
		ItemStack item = this.getItem();
		if (item.isEmpty()) {
			return;
		}

		Entity spawned;
		if (item.getItem() == net.minecraft.world.item.Items.FIRE_CHARGE) {
			net.minecraft.world.entity.projectile.SmallFireball fireball = new net.minecraft.world.entity.projectile.SmallFireball(
					level, this.getX(), this.getY(), this.getZ(), 0.0, -1.0, 0.0);
			level.addFreshEntity(fireball);
			spawned = fireball;
		} else if (item.getItem() instanceof com.natsu.backport.common.item.WindChargeItem) {
			WindChargeEntity charge = new WindChargeEntity(com.natsu.backport.common.registry.CTBEntities.WIND_CHARGE_ENTITY.get(), level);
			charge.setPos(this.getX(), this.getY(), this.getZ());
			charge.shoot(0.0, -1.0, 0.0, 1.0F, 1.0F);
			level.addFreshEntity(charge);
			spawned = charge;
		} else if (item.getItem() instanceof ThrowablePotionItem) {
			ThrownPotion potion = new ThrownPotion(level, this.getX(), this.getY(), this.getZ());
			potion.setItem(item);
			potion.setOwner(this);
			potion.shoot(0.0, -1.0, 0.0, 0.5F, 1.0F);
			level.addFreshEntity(potion);
			spawned = potion;
		} else if (item.getItem() instanceof ArrowItem arrowItem) {
			AbstractArrow arrow = arrowItem.createArrow(level, item, null);
			arrow.setPos(this.getX(), this.getY(), this.getZ());
			arrow.pickup = AbstractArrow.Pickup.ALLOWED;
			arrow.shoot(0.0, -1.0, 0.0, 1.0F, 6.0F);
			level.addFreshEntity(arrow);
			spawned = arrow;
		} else {
			spawned = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), item);
			level.addFreshEntity(spawned);
		}

		level.playSound(null, this.blockPosition(), CTBSounds.TRIAL_SPAWNER_SPAWN_ITEM.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
		addSpawnItemParticles(level, this.blockPosition());
		level.gameEvent(spawned, GameEvent.ENTITY_PLACE, this.blockPosition());
		this.setItem(ItemStack.EMPTY);
	}

	private static void addSpawnItemParticles(ServerLevel level, BlockPos pos) {
		for (int i = 0; i < 20; i++) {
			double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5);
			double y = pos.getY() + 0.5 + (level.random.nextDouble() - 0.5);
			double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5);
			level.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
		}
	}

	private void addParticles() {
		Vec3 center = this.position();
		int count = 1 + this.random.nextInt(3);
		for (int i = 0; i < count; i++) {
			Vec3 from = new Vec3(
					this.getX() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()),
					this.getY() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()),
					this.getZ() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()));
			Vec3 dir = center.vectorTo(from);
			this.level.addParticle(CTBParticles.OMINOUS_SPAWNING.get(), center.x(), center.y(), center.z(), dir.x(), dir.y(), dir.z());
		}
	}

	public ItemStack getItem() {
		return this.entityData.get(DATA_ITEM);
	}

	public void setItem(ItemStack stack) {
		this.entityData.set(DATA_ITEM, stack);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.setItem(ItemStack.of(tag.getCompound("item")));
		this.spawnItemAfterTicks = tag.getLong("spawn_item_after_ticks");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		if (!this.getItem().isEmpty()) {
			tag.put("item", this.getItem().save(new CompoundTag()));
		}
		tag.putLong("spawn_item_after_ticks", this.spawnItemAfterTicks);
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean isIgnoringBlockTriggers() {
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
