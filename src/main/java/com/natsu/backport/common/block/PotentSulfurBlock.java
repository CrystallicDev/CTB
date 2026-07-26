package com.natsu.backport.common.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.entity.PotentSulfurBlockEntity;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Port of the 26.2 PotentSulfurBlock : dry on land, noxious under water, and a
 * geyser when lava or magma sits underneath — continuous over lava, periodic
 * dormant and erupting cycles over magma.
 */
public class PotentSulfurBlock extends BaseEntityBlock {

	public static final int ALLOWED_WATER_BLOCKS_ABOVE = 4;
	public static final EnumProperty<CTBPotentSulfurState> STATE =
			EnumProperty.create("potent_sulfur_state", CTBPotentSulfurState.class);

	public PotentSulfurBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(STATE, CTBPotentSulfurState.DRY));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STATE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PotentSulfurBlockEntity(pos, state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return validBlockState(state, level, pos);
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return validBlockState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
	}

	private static BlockState validBlockState(BlockState state, LevelReader level, BlockPos pos) {
		if (!isWaterSource(level.getFluidState(pos.above()))) {
			return state.setValue(STATE, CTBPotentSulfurState.DRY);
		}
		BlockState below = level.getBlockState(pos.below());
		if (below.is(Blocks.LAVA) && isSourceIfFluid(below)) {
			return state.setValue(STATE, CTBPotentSulfurState.CONTINUOUS);
		}
		if (below.is(Blocks.MAGMA_BLOCK) && isSourceIfFluid(below)) {
			boolean isGeyser = state.getValue(STATE) == CTBPotentSulfurState.ERUPTING
					|| state.getValue(STATE) == CTBPotentSulfurState.DORMANT;
			if (!isGeyser && level.getBlockEntity(pos) instanceof PotentSulfurBlockEntity potentSulfur) {
				potentSulfur.resetCountdown();
			}
			if (state.getValue(STATE) == CTBPotentSulfurState.ERUPTING) {
				return state;
			}
			return state.setValue(STATE, CTBPotentSulfurState.DORMANT);
		}
		return state.setValue(STATE, CTBPotentSulfurState.WET);
	}

	static boolean isWaterSource(FluidState fluid) {
		return fluid.isSource() && fluid.getType() == Fluids.WATER;
	}

	private static boolean isSourceIfFluid(BlockState below) {
		FluidState fluid = below.getFluidState();
		return fluid.isEmpty() || fluid.isSource();
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (state.getValue(STATE) == CTBPotentSulfurState.ERUPTING
				|| state.getValue(STATE) == CTBPotentSulfurState.CONTINUOUS) {
			level.blockEvent(pos, this, 0, 0);
			level.playSound(null, pos, state.getValue(STATE) == CTBPotentSulfurState.CONTINUOUS
					? CTBSounds.GEYSER_CONTINUOUS_START.get() : CTBSounds.GEYSER_ERUPTION_START.get(),
					SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (state.getValue(STATE) == CTBPotentSulfurState.DRY
				|| !isWaterSource(level.getFluidState(pos.above()))) {
			return;
		}
		spawnBubbleParticlesAt(level, random, pos.getX(), pos.getY() + 1, pos.getZ());
		spawnBubbleParticlesAt(level, random, pos.getX(), pos.getY() + 1, pos.getZ());
		if (random.nextInt(10) == 0) {
			level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(),
					CTBSounds.NOXIOUS_GAS.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
		}
	}

	private static void spawnBubbleParticlesAt(Level level, Random random, double x, double y, double z) {
		level.addAlwaysVisibleParticle(CTBParticles.SULFUR_BUBBLES.get(),
				x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(), 0.0, 0.0, 0.0);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int b0, int b1) {
		if (level.getBlockEntity(pos) instanceof PotentSulfurBlockEntity potentSulfur) {
			potentSulfur.eruptionTick = level.getGameTime();
		}
		return true;
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		boolean client = level.isClientSide;
		BlockEntityTicker<PotentSulfurBlockEntity> ticker = switch (state.getValue(STATE)) {
			case DRY -> null;
			case WET -> client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER
					: PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER;
			case DORMANT -> client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER
					: PotentSulfurBlockEntity.andThen(PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER,
							PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER);
			case ERUPTING -> client
					? PotentSulfurBlockEntity.andThen(
							PotentSulfurBlockEntity.clientGeyserPlumeTicker(CTBSounds.GEYSER_ERUPTION_ACTIVE.get()),
							PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER)
					: PotentSulfurBlockEntity.andThen(PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER,
							PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER);
			case CONTINUOUS -> client
					? PotentSulfurBlockEntity.andThen(
							PotentSulfurBlockEntity.clientGeyserPlumeTicker(CTBSounds.GEYSER_CONTINUOUS_ACTIVE.get()),
							PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER)
					: PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER;
		};
		if (ticker == null) {
			return null;
		}
		return createTickerHelper(type, CTBBlockEntities.POTENT_SULFUR.get(), ticker);
	}
}
