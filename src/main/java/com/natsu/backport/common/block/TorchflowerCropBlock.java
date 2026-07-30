package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The 1.20 torchflower crop : two crop stages then the flower itself. */
public class TorchflowerCropBlock extends CropBlock {

	public static final int MAX_AGE = 1;
	public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
	private static final VoxelShape[] SHAPES = {
			Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0),
			Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0) };

	public TorchflowerCropBlock(Properties properties) {
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
		return com.natsu.backport.common.registry.CTBItems.TORCHFLOWER_SEEDS.get();
	}

	@Override
	public BlockState getStateForAge(int age) {
		return age == MAX_AGE + 1 ? CTBBlocks.TORCHFLOWER.get().defaultBlockState()
				: this.defaultBlockState().setValue(AGE, age);
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
		int newAge = Math.min(state.getValue(AGE) + increment, MAX_AGE + 1);
		level.setBlock(pos, this.getStateForAge(newAge), 2);
	}

	@Override
	public void growCrops(net.minecraft.world.level.Level level, BlockPos pos, BlockState state) {
		int newAge = Math.min(state.getValue(AGE) + this.getBonemealAgeIncrease(level), MAX_AGE + 1);
		level.setBlock(pos, this.getStateForAge(newAge), 2);
	}

	@Override
	protected int getBonemealAgeIncrease(net.minecraft.world.level.Level level) {
		return Mth.nextInt(level.random, 1, 2);
	}
}
