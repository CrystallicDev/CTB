package com.natsu.backport.common.block;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.entity.CopperChestBlockEntity;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Port of the 1.21.11 CopperChestBlock : a real ChestBlock, so double chests
 * and the vanilla container plumbing come for free. This base class is the
 * waxed variant, the oxidizing one is WeatheringCopperChestBlock.
 */
public class CopperChestBlock extends ChestBlock {

	/** The golem ritual turns the copper base block into the matching chest. */
	private static final Map<Block, Supplier<Block>> COPPER_TO_COPPER_CHEST_MAPPING = Map.of(
			Blocks.COPPER_BLOCK, () -> CTBBlocks.COPPER_CHEST.get(),
			Blocks.EXPOSED_COPPER, () -> CTBBlocks.EXPOSED_COPPER_CHEST.get(),
			Blocks.WEATHERED_COPPER, () -> CTBBlocks.WEATHERED_COPPER_CHEST.get(),
			Blocks.OXIDIZED_COPPER, () -> CTBBlocks.OXIDIZED_COPPER_CHEST.get(),
			Blocks.WAXED_COPPER_BLOCK, () -> CTBBlocks.COPPER_CHEST.get(),
			Blocks.WAXED_EXPOSED_COPPER, () -> CTBBlocks.EXPOSED_COPPER_CHEST.get(),
			Blocks.WAXED_WEATHERED_COPPER, () -> CTBBlocks.WEATHERED_COPPER_CHEST.get(),
			Blocks.WAXED_OXIDIZED_COPPER, () -> CTBBlocks.OXIDIZED_COPPER_CHEST.get());

	private final WeatheringCopper.WeatherState weatherState;

	public CopperChestBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
		super(properties, () -> CTBBlockEntities.COPPER_CHEST.get());
		this.weatherState = weatherState;
	}

	public WeatheringCopper.WeatherState getWeatherState() {
		return this.weatherState;
	}

	public boolean isWaxed() {
		return true;
	}

	public boolean chestCanConnectTo(BlockState state) {
		return state.getBlock() instanceof CopperChestBlock && state.hasProperty(TYPE);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CopperChestBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		// the vanilla chest placement with the copper connect rule
		ChestType type = ChestType.SINGLE;
		Direction facing = context.getHorizontalDirection().getOpposite();
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		boolean sneaking = context.isSecondaryUseActive();
		Direction clickedFace = context.getClickedFace();
		if (clickedFace.getAxis().isHorizontal() && sneaking) {
			Direction partner = this.copperPartnerFacing(context, clickedFace.getOpposite());
			if (partner != null && partner.getAxis() != clickedFace.getAxis()) {
				facing = partner;
				type = partner.getCounterClockWise() == clickedFace.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
			}
		}
		if (type == ChestType.SINGLE && !sneaking) {
			if (facing == this.copperPartnerFacing(context, facing.getClockWise())) {
				type = ChestType.LEFT;
			} else if (facing == this.copperPartnerFacing(context, facing.getCounterClockWise())) {
				type = ChestType.RIGHT;
			}
		}
		BlockState state = this.defaultBlockState().setValue(FACING, facing).setValue(TYPE, type)
				.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
		return getLeastOxidizedChestOfConnectedBlocks(state, context.getLevel(), context.getClickedPos());
	}

	@Nullable
	private Direction copperPartnerFacing(BlockPlaceContext context, Direction direction) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
		return this.chestCanConnectTo(state) && state.getValue(TYPE) == ChestType.SINGLE ? state.getValue(FACING) : null;
	}

	/** A forming pair settles on the least oxidized of the two, unwaxing on mismatch. */
	private static BlockState getLeastOxidizedChestOfConnectedBlocks(BlockState state, Level level, BlockPos pos) {
		if (state.getValue(TYPE) == ChestType.SINGLE) {
			return state;
		}
		BlockState connectedState = level.getBlockState(pos.relative(getConnectedDirection(state)));
		if (state.getBlock() instanceof CopperChestBlock chest
				&& connectedState.getBlock() instanceof CopperChestBlock connectedChest) {
			BlockState updated = state;
			BlockState connectedPredicted = connectedState;
			if (chest.isWaxed() != connectedChest.isWaxed()) {
				updated = unwaxBlock(chest, state).orElse(updated);
				connectedPredicted = unwaxBlock(connectedChest, connectedState).orElse(connectedPredicted);
			}
			Block least = chest.weatherState.ordinal() <= connectedChest.weatherState.ordinal()
					? updated.getBlock() : connectedPredicted.getBlock();
			return least.withPropertiesOf(updated);
		}
		return state;
	}

	private static Optional<BlockState> unwaxBlock(CopperChestBlock chest, BlockState state) {
		return !chest.isWaxed() ? Optional.of(state)
				: Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock()))
						.map(block -> block.withPropertiesOf(state));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		BlockState updated = this.updateShapeConnectAware(state, direction, neighborState, level, pos);
		if (this.chestCanConnectTo(neighborState)
				&& updated.getValue(TYPE) != ChestType.SINGLE
				&& getConnectedDirection(updated) == direction) {
			// both halves stay the same block through oxidation, scraping and waxing
			return neighborState.getBlock().withPropertiesOf(updated);
		}
		return updated;
	}

	private BlockState updateShapeConnectAware(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		if (this.chestCanConnectTo(neighborState) && direction.getAxis().isHorizontal()) {
			ChestType neighborType = neighborState.getValue(TYPE);
			if (state.getValue(TYPE) == ChestType.SINGLE && neighborType != ChestType.SINGLE
					&& state.getValue(FACING) == neighborState.getValue(FACING)
					&& getConnectedDirection(neighborState) == direction.getOpposite()) {
				return state.setValue(TYPE, neighborType.getOpposite());
			}
		} else if (getConnectedDirection(state) == direction) {
			return state.setValue(TYPE, ChestType.SINGLE);
		}
		return state;
	}

	public static BlockState getFromCopperBlock(Block copperBlock, Direction facing) {
		Block chest = COPPER_TO_COPPER_CHEST_MAPPING
				.getOrDefault(copperBlock, () -> CTBBlocks.COPPER_CHEST.get()).get();
		return chest.defaultBlockState().setValue(FACING, facing);
	}
}
