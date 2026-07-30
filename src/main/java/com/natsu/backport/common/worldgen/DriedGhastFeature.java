package com.natsu.backport.common.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.natsu.backport.common.block.DriedGhastBlock;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * The 26.x dried ghasts of the soul sand valley : one sleeping on the soul
 * sand or on the bones of a fossil. Vanilla ties them to the nether fossil
 * structure pieces ; this scans the column for a resting spot instead.
 */
public class DriedGhastFeature extends Feature<NoneFeatureConfiguration> {

	public DriedGhastFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		Random random = context.random();
		BlockPos origin = context.origin();
		List<BlockPos> spots = new ArrayList<>();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(origin.getX(), 30, origin.getZ());
		for (int y = 30; y <= 110; y++) {
			pos.setY(y);
			if (!level.getBlockState(pos).isAir()) {
				continue;
			}
			var below = level.getBlockState(pos.below());
			if (below.is(Blocks.SOUL_SAND) || below.is(Blocks.SOUL_SOIL) || below.is(Blocks.BONE_BLOCK)) {
				spots.add(pos.immutable());
			}
		}
		if (spots.isEmpty()) {
			return false;
		}
		BlockPos target = spots.get(random.nextInt(spots.size()));
		level.setBlock(target, CTBBlocks.DRIED_GHAST.get().defaultBlockState()
				.setValue(DriedGhastBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)), 2);
		return true;
	}
}
