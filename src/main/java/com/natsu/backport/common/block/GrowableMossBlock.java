package com.natsu.backport.common.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MossBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class GrowableMossBlock extends MossBlock {

	private final Holder<ConfiguredFeature<VegetationPatchConfiguration, ?>> boneMealFeature;
	
	public GrowableMossBlock(Properties props, Holder<ConfiguredFeature<VegetationPatchConfiguration, ?>> feature) {
		super(props);
		this.boneMealFeature = feature;
	}
	
	@Override
	public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos pos, BlockState state,
			boolean p_153800_) {
		return blockGetter.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
		if (boneMealFeature != null) {
			boneMealFeature.value().place(level, level.getChunkSource().getGenerator(), random,
					pos.above());
		}
	}

}
