package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The 1.20 sniffer egg : hatches in about a day, twice as fast on moss. */
public class SnifferEggBlock extends Block {

	public static final int MAX_HATCH_LEVEL = 2;
	public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
	private static final int REGULAR_HATCH_TIME_TICKS = 24000;
	private static final int BOOSTED_HATCH_TIME_TICKS = 12000;
	private static final int RANDOM_HATCH_OFFSET_TICKS = 300;
	private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 2.0, 15.0, 16.0, 14.0);

	public SnifferEggBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HATCH);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	public int getHatchLevel(BlockState state) {
		return state.getValue(HATCH);
	}

	private boolean isReadyToHatch(BlockState state) {
		return this.getHatchLevel(state) == MAX_HATCH_LEVEL;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (!this.isReadyToHatch(state)) {
			level.playSound(null, pos, CTBSounds.SNIFFER_EGG_CRACK.get(), SoundSource.BLOCKS, 0.7F,
					0.9F + random.nextFloat() * 0.2F);
			level.setBlock(pos, state.setValue(HATCH, this.getHatchLevel(state) + 1), 2);
			this.rescheduleTick(level, pos);
			return;
		}
		level.playSound(null, pos, CTBSounds.SNIFFER_EGG_HATCH.get(), SoundSource.BLOCKS, 0.7F,
				0.9F + random.nextFloat() * 0.2F);
		level.destroyBlock(pos, false);
		Sniffer(level, pos);
	}

	private static void Sniffer(ServerLevel level, BlockPos pos) {
		com.natsu.backport.common.entity.Sniffer sniffer = CTBEntities.SNIFFER.get().create(level);
		if (sniffer != null) {
			sniffer.setBaby(true);
			sniffer.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
			level.addFreshEntity((Entity) sniffer);
		}
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!level.isClientSide) {
			if (hatchBoost(level, pos)) {
				level.levelEvent(3009, pos, 0);
			}
			this.rescheduleTick((ServerLevel) level, pos);
		}
	}

	private void rescheduleTick(ServerLevel level, BlockPos pos) {
		int total = hatchBoost(level, pos) ? BOOSTED_HATCH_TIME_TICKS : REGULAR_HATCH_TIME_TICKS;
		int third = total / 3;
		level.scheduleTick(pos, this, third + level.random.nextInt(RANDOM_HATCH_OFFSET_TICKS));
	}

	public static boolean hatchBoost(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos.below()).is(Blocks.MOSS_BLOCK);
	}
}
