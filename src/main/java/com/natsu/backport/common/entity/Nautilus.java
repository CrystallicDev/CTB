package com.natsu.backport.common.entity;

import javax.annotation.Nullable;

import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public class Nautilus extends AbstractNautilus {

	public Nautilus(EntityType<? extends Nautilus> type, Level level) {
		super(type, level);
	}

	@Override
	@Nullable
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		Nautilus baby = CTBEntities.NAUTILUS.get().create(level);
		if (baby != null && this.isTame()) {
			baby.setOwnerUUID(this.getOwnerUUID());
			baby.setTame(true);
		}
		return baby;
	}

	@Override
	protected net.minecraft.sounds.SoundEvent getAmbientSound() {
		return this.isUnderWater() ? CTBSounds.NAUTILUS_AMBIENT.get() : CTBSounds.NAUTILUS_AMBIENT_LAND.get();
	}

	@Override
	protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
		return this.isUnderWater() ? CTBSounds.NAUTILUS_HURT.get() : CTBSounds.NAUTILUS_HURT_LAND.get();
	}

	@Override
	protected net.minecraft.sounds.SoundEvent getDeathSound() {
		return this.isUnderWater() ? CTBSounds.NAUTILUS_DEATH.get() : CTBSounds.NAUTILUS_DEATH_LAND.get();
	}

	@Override
	protected net.minecraft.sounds.SoundEvent getDashSound() {
		return this.isUnderWater() ? CTBSounds.NAUTILUS_DASH.get() : CTBSounds.NAUTILUS_DASH_LAND.get();
	}

	@Override
	protected net.minecraft.sounds.SoundEvent getDashReadySound() {
		return this.isUnderWater() ? CTBSounds.NAUTILUS_DASH_READY.get() : CTBSounds.NAUTILUS_DASH_READY_LAND.get();
	}

	@Override
	protected void playEatingSound() {
		this.playSound(CTBSounds.NAUTILUS_EAT.get(), 1.0F, 1.0F);
	}

	public static boolean checkNautilusSpawnRules(EntityType<? extends AbstractNautilus> type, LevelAccessor level,
			MobSpawnType spawnType, BlockPos pos, Random random) {
		int seaLevel = level.getSeaLevel();
		return pos.getY() >= seaLevel - 25 && pos.getY() <= seaLevel
				&& level.getFluidState(pos.below()).is(FluidTags.WATER)
				&& level.getBlockState(pos.above()).is(Blocks.WATER);
	}
}
