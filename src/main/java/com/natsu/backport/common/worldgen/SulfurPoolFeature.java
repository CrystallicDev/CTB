package com.natsu.backport.common.worldgen;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.natsu.backport.common.block.CTBPotentSulfurState;
import com.natsu.backport.common.block.PotentSulfurBlock;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * The 26.2 sulfur_pool : a lake of water walled in sulfur, then the block
 * under the water surface below the origin turned into a wet potent sulfur —
 * the natural geysers of the caves.
 */
public class SulfurPoolFeature extends Feature<NoneFeatureConfiguration> {

	private static final LakeFeature.Configuration LAKE_CONFIG = new LakeFeature.Configuration(
			BlockStateProvider.simple(Blocks.WATER.defaultBlockState()),
			BlockStateProvider.simple(CTBBlocks.SULFUR.get().defaultBlockState()));

	public SulfurPoolFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		Random random = context.random();
		BlockPos origin = context.origin();
		boolean placed = ((LakeFeature) Feature.LAKE).place(new FeaturePlaceContext<>(Optional.empty(), level,
				context.chunkGenerator(), random, origin, LAKE_CONFIG));
		if (!placed) {
			return false;
		}
		// the vanilla environment_scan : down four blocks for a solid block with water above
		BlockPos.MutableBlockPos pos = origin.mutable();
		for (int i = 0; i <= 4; i++) {
			BlockState state = level.getBlockState(pos);
			if (state.getMaterial().isSolidBlocking()
					&& level.getFluidState(pos.above()).is(FluidTags.WATER)) {
				level.setBlock(pos, CTBBlocks.POTENT_SULFUR.get().defaultBlockState()
						.setValue(PotentSulfurBlock.STATE, CTBPotentSulfurState.WET), 2);
				break;
			}
			pos.move(Direction.DOWN);
		}
		return true;
	}
}
