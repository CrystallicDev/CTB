package com.natsu.backport.common.worldgen;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * The 26.2 sulfur_spike_cluster feature, the dripstone cluster shape grown out
 * of sulfur. Config values inlined from the vanilla worldgen JSON.
 */
public class SulfurSpikeClusterFeature extends Feature<NoneFeatureConfiguration> {

	private static final int FLOOR_TO_CEILING_SEARCH_RANGE = 12;
	private static final IntProvider HEIGHT = UniformInt.of(1, 4);
	private static final IntProvider RADIUS = UniformInt.of(2, 8);
	private static final int MAX_STALAGMITE_STALACTITE_HEIGHT_DIFF = 1;
	private static final int HEIGHT_DEVIATION = 3;
	private static final IntProvider LAYER_THICKNESS = UniformInt.of(2, 4);
	private static final UniformFloat DENSITY = UniformFloat.of(0.3F, 0.7F);
	private static final float WETNESS = 0.0F;
	private static final float CHANCE_AT_MAX_DISTANCE_FROM_CENTER = 0.1F;
	private static final int MAX_DISTANCE_FROM_EDGE_AFFECTING_CHANCE = 3;
	private static final int MAX_DISTANCE_FROM_CENTER_AFFECTING_HEIGHT_BIAS = 8;

	public SulfurSpikeClusterFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos origin = context.origin();
		Random random = context.random();
		if (!SulfurSpeleothemUtils.isEmptyOrWater(level, origin)) {
			return false;
		}
		int height = HEIGHT.sample(random);
		float wetness = WETNESS;
		float density = DENSITY.sample(random);
		int xRadius = RADIUS.sample(random);
		int zRadius = RADIUS.sample(random);
		for (int dx = -xRadius; dx <= xRadius; dx++) {
			for (int dz = -zRadius; dz <= zRadius; dz++) {
				double chance = getChanceOfSpeleothem(xRadius, zRadius, dx, dz);
				BlockPos pos = origin.offset(dx, 0, dz);
				placeColumn(level, random, pos, dx, dz, wetness, chance, height, density);
			}
		}
		return true;
	}

	private void placeColumn(WorldGenLevel level, Random random, BlockPos pos, int dx, int dz, float chanceOfWater,
			double chanceOfSpeleothem, int clusterHeight, float density) {
		Optional<Column> baseColumn = Column.scan(level, pos, FLOOR_TO_CEILING_SEARCH_RANGE,
				SulfurSpeleothemUtils::isEmptyOrWater, SulfurSpeleothemUtils::isNeitherEmptyNorWater);
		if (baseColumn.isEmpty()) {
			return;
		}
		OptionalInt ceiling = baseColumn.get().getCeiling();
		OptionalInt baseFloor = baseColumn.get().getFloor();
		if (ceiling.isEmpty() && baseFloor.isEmpty()) {
			return;
		}
		Column column;
		if (random.nextFloat() < chanceOfWater && baseFloor.isPresent()
				&& canPlacePool(level, pos.atY(baseFloor.getAsInt()))) {
			int baseFloorY = baseFloor.getAsInt();
			column = baseColumn.get().withFloor(OptionalInt.of(baseFloorY - 1));
			level.setBlock(pos.atY(baseFloorY), Blocks.WATER.defaultBlockState(), 2);
		} else {
			column = baseColumn.get();
		}
		OptionalInt floor = column.getFloor();
		Block base = CTBBlocks.SULFUR.get();
		Block pointed = CTBBlocks.SULFUR_SPIKE.get();

		int stalactiteHeight;
		if (ceiling.isPresent() && random.nextDouble() < chanceOfSpeleothem
				&& !isLava(level, pos.atY(ceiling.getAsInt()))) {
			replaceBlocksWithBaseBlocks(level, pos.atY(ceiling.getAsInt()), LAYER_THICKNESS.sample(random),
					Direction.UP, base);
			int maxHeightForThisColumn = floor.isPresent()
					? Math.min(clusterHeight, ceiling.getAsInt() - floor.getAsInt()) : clusterHeight;
			stalactiteHeight = getSpeleothemHeight(random, dx, dz, density, maxHeightForThisColumn);
		} else {
			stalactiteHeight = 0;
		}
		int stalagmiteHeight;
		if (floor.isPresent() && random.nextDouble() < chanceOfSpeleothem
				&& !isLava(level, pos.atY(floor.getAsInt()))) {
			replaceBlocksWithBaseBlocks(level, pos.atY(floor.getAsInt()), LAYER_THICKNESS.sample(random),
					Direction.DOWN, base);
			stalagmiteHeight = ceiling.isPresent()
					? Math.max(0, stalactiteHeight + Mth.randomBetweenInclusive(random,
							-MAX_STALAGMITE_STALACTITE_HEIGHT_DIFF, MAX_STALAGMITE_STALACTITE_HEIGHT_DIFF))
					: getSpeleothemHeight(random, dx, dz, density, clusterHeight);
		} else {
			stalagmiteHeight = 0;
		}

		int actualStalactiteHeight;
		int actualStalagmiteHeight;
		if (ceiling.isPresent() && floor.isPresent()
				&& ceiling.getAsInt() - stalactiteHeight <= floor.getAsInt() + stalagmiteHeight) {
			int floorY = floor.getAsInt();
			int ceilingY = ceiling.getAsInt();
			int lowestStalactiteBottom = Math.max(ceilingY - stalactiteHeight, floorY + 1);
			int highestStalagmiteTop = Math.min(floorY + stalagmiteHeight, ceilingY - 1);
			int actualStalactiteBottom = Mth.randomBetweenInclusive(random, lowestStalactiteBottom,
					highestStalagmiteTop + 1);
			actualStalactiteHeight = ceilingY - actualStalactiteBottom;
			actualStalagmiteHeight = actualStalactiteBottom - 1 - floorY;
		} else {
			actualStalactiteHeight = stalactiteHeight;
			actualStalagmiteHeight = stalagmiteHeight;
		}
		boolean mergeTips = random.nextBoolean() && actualStalactiteHeight > 0 && actualStalagmiteHeight > 0
				&& column.getHeight().isPresent()
				&& actualStalactiteHeight + actualStalagmiteHeight == column.getHeight().getAsInt();
		if (ceiling.isPresent()) {
			SulfurSpeleothemUtils.growSpeleothem(level, pos.atY(ceiling.getAsInt() - 1), Direction.DOWN,
					actualStalactiteHeight, mergeTips, base, pointed);
		}
		if (floor.isPresent()) {
			SulfurSpeleothemUtils.growSpeleothem(level, pos.atY(floor.getAsInt() + 1), Direction.UP,
					actualStalagmiteHeight, mergeTips, base, pointed);
		}
	}

	private boolean isLava(LevelReader level, BlockPos pos) {
		return level.getBlockState(pos).is(Blocks.LAVA);
	}

	private int getSpeleothemHeight(Random random, int dx, int dz, float density, int maxHeight) {
		if (random.nextFloat() > density) {
			return 0;
		}
		int distanceFromCenter = Math.abs(dx) + Math.abs(dz);
		float heightMean = (float) Mth.clampedMap(distanceFromCenter, 0.0,
				MAX_DISTANCE_FROM_CENTER_AFFECTING_HEIGHT_BIAS, maxHeight / 2.0, 0.0);
		return (int) ClampedNormalFloat.sample(random, heightMean, HEIGHT_DEVIATION, 0.0F, maxHeight);
	}

	private boolean canPlacePool(WorldGenLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.is(Blocks.WATER) || state.is(CTBBlocks.SULFUR.get()) || state.is(CTBBlocks.SULFUR_SPIKE.get())) {
			return false;
		}
		if (level.getBlockState(pos.above()).getFluidState().is(FluidTags.WATER)) {
			return false;
		}
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			if (!canBeAdjacentToWater(level, pos.relative(direction))) {
				return false;
			}
		}
		return canBeAdjacentToWater(level, pos.below());
	}

	private boolean canBeAdjacentToWater(WorldGenLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.getFluidState().is(FluidTags.WATER);
	}

	private void replaceBlocksWithBaseBlocks(WorldGenLevel level, BlockPos firstPos, int maxCount, Direction direction,
			Block base) {
		BlockPos.MutableBlockPos pos = firstPos.mutable();
		for (int i = 0; i < maxCount; i++) {
			if (!SulfurSpeleothemUtils.placeBaseBlockIfPossible(level, pos, base)) {
				return;
			}
			pos.move(direction);
		}
	}

	private double getChanceOfSpeleothem(int xRadius, int zRadius, int dx, int dz) {
		int distanceFromEdge = Math.min(xRadius - Math.abs(dx), zRadius - Math.abs(dz));
		return Mth.clampedMap(distanceFromEdge, 0.0F, MAX_DISTANCE_FROM_EDGE_AFFECTING_CHANCE,
				CHANCE_AT_MAX_DISTANCE_FROM_CENTER, 1.0F);
	}
}
