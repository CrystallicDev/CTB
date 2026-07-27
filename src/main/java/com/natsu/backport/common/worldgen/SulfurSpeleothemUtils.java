package com.natsu.backport.common.worldgen;

import java.util.Random;
import java.util.function.Consumer;

import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

/** The 26.2 SpeleothemUtils, on the sulfur spike blocks. */
public final class SulfurSpeleothemUtils {

	private SulfurSpeleothemUtils() {
	}

	static boolean isEmptyOrWater(LevelAccessor level, BlockPos pos) {
		return level.isStateAtPosition(pos, SulfurSpeleothemUtils::isEmptyOrWater);
	}

	static void buildBaseToTipColumn(Direction direction, int totalLength, boolean mergedTip,
			Consumer<BlockState> consumer, Block pointedBlock) {
		if (totalLength >= 3) {
			consumer.accept(createPointedBlock(direction, DripstoneThickness.BASE, pointedBlock));
			for (int i = 0; i < totalLength - 3; i++) {
				consumer.accept(createPointedBlock(direction, DripstoneThickness.MIDDLE, pointedBlock));
			}
		}
		if (totalLength >= 2) {
			consumer.accept(createPointedBlock(direction, DripstoneThickness.FRUSTUM, pointedBlock));
		}
		if (totalLength >= 1) {
			consumer.accept(createPointedBlock(direction,
					mergedTip ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP, pointedBlock));
		}
	}

	static void growSpeleothem(LevelAccessor level, BlockPos startPos, Direction tipDirection, int height,
			boolean mergedTip, Block baseBlock, Block pointedBlock) {
		if (!isBase(level.getBlockState(startPos.relative(tipDirection.getOpposite())), baseBlock)) {
			return;
		}
		BlockPos.MutableBlockPos pos = startPos.mutable();
		buildBaseToTipColumn(tipDirection, height, mergedTip, state -> {
			if (state.is(pointedBlock)) {
				state = state.setValue(BlockStateProperties.WATERLOGGED, level.isWaterAt(pos));
			}
			level.setBlock(pos, state, 2);
			pos.move(tipDirection);
		}, pointedBlock);
	}

	static boolean placeBaseBlockIfPossible(LevelAccessor level, BlockPos pos, Block baseBlock) {
		BlockState state = level.getBlockState(pos);
		if (state.is(CTBTags.Blocks.SULFUR_SPIKE_REPLACEABLE)) {
			level.setBlock(pos, baseBlock.defaultBlockState(), 2);
			return true;
		}
		return false;
	}

	private static BlockState createPointedBlock(Direction direction, DripstoneThickness thickness,
			Block pointedBlock) {
		return pointedBlock.defaultBlockState()
				.setValue(BlockStateProperties.VERTICAL_DIRECTION, direction)
				.setValue(BlockStateProperties.DRIPSTONE_THICKNESS, thickness);
	}

	static boolean isBase(BlockState state, Block baseBlock) {
		return state.is(baseBlock) || state.is(CTBTags.Blocks.SULFUR_SPIKE_REPLACEABLE);
	}

	static boolean isEmptyOrWater(BlockState state) {
		return state.isAir() || state.is(Blocks.WATER);
	}

	static boolean isNeitherEmptyNorWater(BlockState state) {
		return !state.isAir() && !state.is(Blocks.WATER);
	}

	static void createPatchOfBaseBlocks(WorldGenLevel level, Random random, BlockPos pos, Block baseBlock,
			float chanceOfDirectionalSpread, float chanceOfSpreadRadius2, float chanceOfSpreadRadius3) {
		placeBaseBlockIfPossible(level, pos, baseBlock);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			if (random.nextFloat() > chanceOfDirectionalSpread) {
				continue;
			}
			BlockPos pos1 = pos.relative(direction);
			placeBaseBlockIfPossible(level, pos1, baseBlock);
			if (random.nextFloat() > chanceOfSpreadRadius2) {
				continue;
			}
			BlockPos pos2 = pos1.relative(Direction.getRandom(random));
			placeBaseBlockIfPossible(level, pos2, baseBlock);
			if (random.nextFloat() > chanceOfSpreadRadius3) {
				continue;
			}
			BlockPos pos3 = pos2.relative(Direction.getRandom(random));
			placeBaseBlockIfPossible(level, pos3, baseBlock);
		}
	}
}
