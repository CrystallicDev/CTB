package com.natsu.backport.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class CopperDoorBlock extends DoorBlock {

	public CopperDoorBlock(BlockBehaviour.Properties props) {
		super(props);
	}

	/**
	 * Vanilla 1.18 requires both halves to be the exact same block, so a door
	 * whose lower half just oxidized (or got waxed/scraped) pops off. Modern
	 * versions instead make the other half adopt the neighbour's block, which
	 * is what this does.
	 */
	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		DoubleBlockHalf half = state.getValue(HALF);
		if (dir.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (dir == Direction.UP)) {
			return neighbor.getBlock() instanceof DoorBlock && neighbor.getValue(HALF) != half
					? neighbor.setValue(HALF, half)
					: Blocks.AIR.defaultBlockState();
		}
		return half == DoubleBlockHalf.LOWER && dir == Direction.DOWN && !state.canSurvive(level, pos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, dir, neighbor, level, pos, neighborPos);
	}
}
