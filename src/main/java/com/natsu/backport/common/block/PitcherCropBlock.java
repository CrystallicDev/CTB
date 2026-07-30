package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The 1.20 pitcher pod crop, single block until it matures into the two tall
 * pitcher plant (the vanilla two tall growth stages are folded into the final
 * conversion, a 1.18.2 simplification).
 */
public class PitcherCropBlock extends CropBlock {

	public static final int MAX_AGE = 3;
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	private static final VoxelShape[] SHAPES = {
			Block.box(5.0, 0.0, 5.0, 11.0, 3.0, 11.0),
			Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0),
			Block.box(3.0, 0.0, 3.0, 13.0, 11.0, 13.0),
			Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0) };

	public PitcherCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	@Override
	public int getMaxAge() {
		return MAX_AGE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(AGE)];
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return com.natsu.backport.common.registry.CTBItems.PITCHER_POD.get();
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (level.getRawBrightness(pos, 0) >= 9
				&& net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state,
						random.nextInt((int) (25.0F / getGrowthSpeed(this, level, pos)) + 1) == 0)) {
			this.grow(level, state, pos, 1);
			net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
		}
	}

	private void grow(ServerLevel level, BlockState state, BlockPos pos, int increment) {
		int newAge = state.getValue(AGE) + increment;
		if (newAge > MAX_AGE) {
			this.becomePlant(level, pos);
		} else {
			level.setBlock(pos, state.setValue(AGE, newAge), 2);
		}
	}

	private void becomePlant(Level level, BlockPos pos) {
		if (level.getBlockState(pos.above()).isAir() || level.getBlockState(pos.above()).is(Blocks.AIR)) {
			level.setBlock(pos, CTBBlocks.PITCHER_PLANT.get().defaultBlockState()
					.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
			level.setBlock(pos.above(), CTBBlocks.PITCHER_PLANT.get().defaultBlockState()
					.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
		}
	}

	@Override
	public void growCrops(Level level, BlockPos pos, BlockState state) {
		int newAge = state.getValue(AGE) + this.getBonemealAgeIncrease(level);
		if (newAge > MAX_AGE) {
			this.becomePlant(level, pos);
		} else {
			level.setBlock(pos, state.setValue(AGE, Math.min(newAge, MAX_AGE)), 2);
		}
	}

	@Override
	protected int getBonemealAgeIncrease(Level level) {
		return Mth.nextInt(level.random, 1, 2);
	}
}
