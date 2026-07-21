package com.natsu.backport.common.entity;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

public class ZombieNautilus extends AbstractNautilus {

	// 0 = plain zombie, 1 = coral grown, warmer waters
	private static final EntityDataAccessor<Byte> DATA_VARIANT = SynchedEntityData.defineId(ZombieNautilus.class, EntityDataSerializers.BYTE);

	public ZombieNautilus(EntityType<? extends ZombieNautilus> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return AbstractNautilus.createAttributes().add(Attributes.MOVEMENT_SPEED, 1.1);
	}

	@Override
	@Nullable
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return null;
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_VARIANT, (byte) 0);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putByte("Variant", this.getVariant());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setVariant(tag.getByte("Variant"));
	}

	public byte getVariant() {
		return this.entityData.get(DATA_VARIANT);
	}

	public void setVariant(byte variant) {
		this.entityData.set(DATA_VARIANT, variant);
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
			@Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
		Holder<Biome> biome = level.getBiome(this.blockPosition());
		this.setVariant((byte) (biome.value().getBaseTemperature() >= 0.5F ? 1 : 0));
		return super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
	}
}
