package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Tall dry grass, bonemeal spreads short dry grass around it. */
public class TallDryGrassBlock extends DryVegetationBlock implements BonemealableBlock {

	private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public TallDryGrassBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
		return level instanceof LevelReader reader && SpringBushBlock
				.findSpreadableNeighbourPos(reader, pos, CTBBlocks.SHORT_DRY_GRASS.get().defaultBlockState()).isPresent();
	}

	@Override
	public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
		SpringBushBlock.findSpreadableNeighbourPos(level, pos, CTBBlocks.SHORT_DRY_GRASS.get().defaultBlockState())
				.ifPresent(target -> level.setBlockAndUpdate(target, CTBBlocks.SHORT_DRY_GRASS.get().defaultBlockState()));
	}
}
