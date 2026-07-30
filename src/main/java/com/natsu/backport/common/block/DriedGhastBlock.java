package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.entity.HappyGhast;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The 1.21.6 dried ghast : soak it in water and a ghastling wakes up. */
public class DriedGhastBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

	public static final int MAX_HYDRATION_LEVEL = 3;
	public static final IntegerProperty HYDRATION_LEVEL = IntegerProperty.create("hydration", 0, MAX_HYDRATION_LEVEL);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final int HYDRATION_TICK_DELAY = 5000;
	private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0);

	public DriedGhastBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(HYDRATION_LEVEL, 0)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, HYDRATION_LEVEL, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState,
			LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		if (level instanceof ServerLevel server) {
			this.rescheduleTick(server, pos);
		}
		return super.updateShape(state, direction, neighbourState, level, pos, neighbourPos);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (level instanceof ServerLevel server) {
			this.rescheduleTick(server, pos);
		}
	}

	private void rescheduleTick(ServerLevel level, BlockPos pos) {
		if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
			level.scheduleTick(pos, this, HYDRATION_TICK_DELAY + level.random.nextInt(600));
		}
	}

	public int getHydrationLevel(BlockState state) {
		return state.getValue(HYDRATION_LEVEL);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (state.getValue(WATERLOGGED)) {
			if (this.getHydrationLevel(state) < MAX_HYDRATION_LEVEL) {
				level.playSound(null, pos, CTBSounds.DRIED_GHAST_TRANSITION.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				level.setBlock(pos, state.setValue(HYDRATION_LEVEL, this.getHydrationLevel(state) + 1), 2);
			} else {
				this.spawnGhastling(level, pos, state);
				return;
			}
		} else if (this.getHydrationLevel(state) > 0) {
			// drying back out
			level.setBlock(pos, state.setValue(HYDRATION_LEVEL, this.getHydrationLevel(state) - 1), 2);
		}
		this.rescheduleTick(level, pos);
	}

	private void spawnGhastling(ServerLevel level, BlockPos pos, BlockState state) {
		level.removeBlock(pos, false);
		HappyGhast ghastling = CTBEntities.HAPPY_GHAST.get().create(level);
		if (ghastling != null) {
			Vec3 spawn = Vec3.atBottomCenterOf(pos);
			float rotation = state.getValue(FACING).toYRot();
			ghastling.setBaby(true);
			ghastling.moveTo(spawn.x(), spawn.y(), spawn.z(), rotation, 0.0F);
			ghastling.setYHeadRot(rotation);
			level.addFreshEntity(ghastling);
			level.playSound(null, pos, CTBSounds.GHASTLING_SPAWN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		if (!state.getValue(WATERLOGGED)) {
			if (random.nextInt(40) == 0 && level.getBlockState(pos.below()).is(
					net.minecraft.world.level.block.Blocks.SOUL_SAND)) {
				level.playLocalSound(x, y, z, CTBSounds.DRIED_GHAST_AMBIENT.get(), SoundSource.BLOCKS,
						1.0F, 1.0F, false);
			}
			if (random.nextInt(6) == 0) {
				level.addParticle(ParticleTypes.SMOKE, x, y + 0.3, z, 0.0, 0.02, 0.0);
			}
		} else if (random.nextInt(40) == 0) {
			level.playLocalSound(x, y, z, CTBSounds.DRIED_GHAST_AMBIENT_WATER.get(), SoundSource.BLOCKS,
					1.0F, 1.0F, false);
		}
	}
}
