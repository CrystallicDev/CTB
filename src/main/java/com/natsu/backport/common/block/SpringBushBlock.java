package com.natsu.backport.common.block;

import java.util.Optional;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The 1.21.5 bush : a leafy plant that spreads to a neighbour on bonemeal. */
public class SpringBushBlock extends BushBlock implements BonemealableBlock {

	private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);

	public SpringBushBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	static Optional<BlockPos> findSpreadableNeighbourPos(LevelReader level, BlockPos pos, BlockState placeState) {
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos target = pos.relative(direction);
			if (level.getBlockState(target).isAir() && placeState.canSurvive(level, target)) {
				return Optional.of(target);
			}
		}
		return Optional.empty();
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
		return level instanceof LevelReader reader
				&& findSpreadableNeighbourPos(reader, pos, this.defaultBlockState()).isPresent();
	}

	@Override
	public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
		findSpreadableNeighbourPos(level, pos, this.defaultBlockState())
				.ifPresent(target -> level.setBlockAndUpdate(target, this.defaultBlockState()));
	}
}
