package com.natsu.backport.common.block;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The vanilla SegmentableBlock helpers : four quadrants filled counter clockwise. */
final class SegmentedShapes {

	private SegmentedShapes() {
	}

	static Map<BlockState, VoxelShape> build(Block block, EnumProperty<Direction> facing, IntegerProperty amount, double height) {
		Map<Direction, VoxelShape> quadrants = Map.of(
				Direction.NORTH, Block.box(0.0, 0.0, 0.0, 8.0, height, 8.0),
				Direction.EAST, Block.box(8.0, 0.0, 0.0, 16.0, height, 8.0),
				Direction.SOUTH, Block.box(8.0, 0.0, 8.0, 16.0, height, 16.0),
				Direction.WEST, Block.box(0.0, 0.0, 8.0, 8.0, height, 16.0));
		ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
		for (BlockState state : block.getStateDefinition().getPossibleStates()) {
			VoxelShape shape = Shapes.empty();
			Direction direction = state.getValue(facing);
			for (int i = 0; i < state.getValue(amount); i++) {
				shape = Shapes.or(shape, quadrants.get(direction));
				direction = direction.getCounterClockWise();
			}
			builder.put(state, shape);
		}
		return builder.build();
	}

	static boolean canBeReplaced(BlockState state, BlockPlaceContext context, IntegerProperty amount) {
		return !context.isSecondaryUseActive() && context.getItemInHand().is(state.getBlock().asItem())
				&& state.getValue(amount) < 4;
	}

	static BlockState getStateForPlacement(BlockPlaceContext context, Block block, IntegerProperty amount, EnumProperty<Direction> facing) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		return state.is(block)
				? state.setValue(amount, Math.min(4, state.getValue(amount) + 1))
				: block.defaultBlockState().setValue(facing, context.getHorizontalDirection().getOpposite());
	}
}
