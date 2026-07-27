package com.natsu.backport.common.block;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.entity.ShelfBlockEntity;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Port of the 1.21.9 ShelfBlock : three display slots swapped by clicking the
 * front face ; powered shelves link into rows of up to three and swap their
 * whole row against the hotbar.
 */
public class ShelfBlock extends BaseEntityBlock implements SideChainPartBlock, SimpleWaterloggedBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<CTBSideChainPart> SIDE_CHAIN_PART =
			EnumProperty.create("side_chain", CTBSideChainPart.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final Map<Direction, VoxelShape> SHAPES = makeShapes();

	public ShelfBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(POWERED, false)
				.setValue(SIDE_CHAIN_PART, CTBSideChainPart.UNCONNECTED)
				.setValue(WATERLOGGED, false));
	}

	/** The three vanilla boxes, authored for north, rotated by hand. */
	private static Map<Direction, VoxelShape> makeShapes() {
		VoxelShape north = Shapes.or(
				Block.box(0.0, 12.0, 11.0, 16.0, 16.0, 13.0),
				Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0),
				Block.box(0.0, 0.0, 11.0, 16.0, 4.0, 13.0));
		VoxelShape south = Shapes.or(
				Block.box(0.0, 12.0, 3.0, 16.0, 16.0, 5.0),
				Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0),
				Block.box(0.0, 0.0, 3.0, 16.0, 4.0, 5.0));
		VoxelShape west = Shapes.or(
				Block.box(11.0, 12.0, 0.0, 13.0, 16.0, 16.0),
				Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.box(11.0, 0.0, 0.0, 13.0, 4.0, 16.0));
		VoxelShape east = Shapes.or(
				Block.box(3.0, 12.0, 0.0, 5.0, 16.0, 16.0),
				Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0),
				Block.box(3.0, 0.0, 0.0, 5.0, 4.0, 16.0));
		return Map.of(Direction.NORTH, north, Direction.SOUTH, south, Direction.WEST, west, Direction.EAST, east);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, SIDE_CHAIN_PART, WATERLOGGED);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ShelfBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
				.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	// --- the redstone side ---

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!level.isClientSide) {
			boolean signal = level.hasNeighborSignal(pos);
			if (state.getValue(POWERED) != signal) {
				BlockState newState = state.setValue(POWERED, signal);
				if (!signal) {
					newState = newState.setValue(SIDE_CHAIN_PART, CTBSideChainPart.UNCONNECTED);
				}
				level.setBlock(pos, newState, Block.UPDATE_ALL);
				this.playSound(level, pos, signal ? CTBSounds.SHELF_ACTIVATE.get() : CTBSounds.SHELF_DEACTIVATE.get());
				level.gameEvent(null, signal ? GameEvent.BLOCK_PRESS : GameEvent.BLOCK_UNPRESS, pos);
			}
		}
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (state.getValue(POWERED)) {
			this.updateSelfAndNeighborsOnPoweringUp(level, pos, state, oldState);
		} else {
			this.updateNeighborsAfterPoweringDown(level, pos, state);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf) {
				Containers.dropContents(level, pos, shelf);
				level.updateNeighbourForOutputSignal(pos, this);
			}
			this.updateNeighborsAfterPoweringDown(level, pos, state);
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	// --- the side chain contract ---

	@Override
	public CTBSideChainPart getSideChainPart(BlockState state) {
		return state.getValue(SIDE_CHAIN_PART);
	}

	@Override
	public BlockState setSideChainPart(BlockState state, CTBSideChainPart newPart) {
		return state.setValue(SIDE_CHAIN_PART, newPart);
	}

	@Override
	public Direction getFacing(BlockState state) {
		return state.getValue(FACING);
	}

	@Override
	public boolean isConnectable(BlockState state) {
		return state.getBlock() instanceof ShelfBlock && state.hasProperty(POWERED) && state.getValue(POWERED);
	}

	@Override
	public int getMaxChainLength() {
		return 3;
	}

	// --- interaction ---

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hitResult) {
		if (!(level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf) || hand == InteractionHand.OFF_HAND) {
			return InteractionResult.PASS;
		}
		OptionalInt hitSlot = getHitSlot(hitResult, state.getValue(FACING));
		if (hitSlot.isEmpty()) {
			return InteractionResult.PASS;
		}
		ItemStack held = player.getItemInHand(hand);
		Inventory inventory = player.getInventory();
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		if (!state.getValue(POWERED)) {
			boolean itemRemoved = swapSingleItem(held, player, shelf, hitSlot.getAsInt(), inventory);
			if (itemRemoved) {
				this.playSound(level, pos, held.isEmpty()
						? CTBSounds.SHELF_TAKE_ITEM.get() : CTBSounds.SHELF_SINGLE_SWAP.get());
			} else {
				if (held.isEmpty()) {
					return InteractionResult.PASS;
				}
				this.playSound(level, pos, CTBSounds.SHELF_PLACE_ITEM.get());
			}
			return InteractionResult.CONSUME;
		}
		boolean anySwapped = this.swapHotbar(level, pos, inventory);
		if (!anySwapped) {
			return InteractionResult.CONSUME;
		}
		this.playSound(level, pos, CTBSounds.SHELF_MULTI_SWAP.get());
		return InteractionResult.CONSUME;
	}

	private static boolean swapSingleItem(ItemStack held, Player player, ShelfBlockEntity shelf, int hitSlot, Inventory inventory) {
		ItemStack removed = shelf.swapItemNoUpdate(hitSlot, held);
		ItemStack newInventoryItem = player.getAbilities().instabuild && removed.isEmpty() ? held.copy() : removed;
		inventory.setItem(inventory.selected, newInventoryItem);
		inventory.setChanged();
		shelf.setChanged();
		return !removed.isEmpty();
	}

	private boolean swapHotbar(Level level, BlockPos pos, Inventory inventory) {
		List<BlockPos> connectedBlocks = this.getAllBlocksConnectedTo(level, pos);
		if (connectedBlocks.isEmpty()) {
			return false;
		}
		boolean anySwapped = false;
		for (int shelfPartIndex = 0; shelfPartIndex < connectedBlocks.size(); shelfPartIndex++) {
			if (!(level.getBlockEntity(connectedBlocks.get(shelfPartIndex)) instanceof ShelfBlockEntity shelfPart)) {
				continue;
			}
			for (int slot = 0; slot < shelfPart.getContainerSize(); slot++) {
				int inventorySlot = 9 - (connectedBlocks.size() - shelfPartIndex) * shelfPart.getContainerSize() + slot;
				if (inventorySlot >= 0 && inventorySlot <= inventory.getContainerSize()) {
					ItemStack placed = inventory.removeItemNoUpdate(inventorySlot);
					ItemStack removed = shelfPart.swapItemNoUpdate(slot, placed);
					if (!placed.isEmpty() || !removed.isEmpty()) {
						inventory.setItem(inventorySlot, removed);
						anySwapped = true;
					}
				}
			}
			inventory.setChanged();
			shelfPart.setChanged();
		}
		return anySwapped;
	}

	/** The vanilla SelectableSlotContainer hit test : one row, three columns. */
	public static OptionalInt getHitSlot(BlockHitResult hitResult, Direction blockFacing) {
		return getRelativeHitCoordinatesForBlockFace(hitResult, blockFacing)
				.map(hit -> OptionalInt.of(getSection(hit.x)))
				.orElseGet(OptionalInt::empty);
	}

	private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult hitResult, Direction blockFacing) {
		Direction hitDirection = hitResult.getDirection();
		if (blockFacing != hitDirection) {
			return Optional.empty();
		}
		BlockPos hitBlockPos = hitResult.getBlockPos().relative(hitDirection);
		Vec3 relative = hitResult.getLocation().subtract(hitBlockPos.getX(), hitBlockPos.getY(), hitBlockPos.getZ());
		return switch (hitDirection) {
			case NORTH -> Optional.of(new Vec2((float) (1.0 - relative.x), (float) relative.y));
			case SOUTH -> Optional.of(new Vec2((float) relative.x, (float) relative.y));
			case WEST -> Optional.of(new Vec2((float) relative.z, (float) relative.y));
			case EAST -> Optional.of(new Vec2((float) (1.0 - relative.z), (float) relative.y));
			default -> Optional.empty();
		};
	}

	private static int getSection(float relativeCoordinate) {
		float targetedPixel = relativeCoordinate * 16.0F;
		return Mth.clamp(Mth.floor(targetedPixel / (16.0F / 3)), 0, 2);
	}

	private void playSound(LevelAccessor level, BlockPos pos, SoundEvent sound) {
		level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
	}

	// --- comparator, one bit per filled slot ---

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf) {
			int slot1 = shelf.getItem(0).isEmpty() ? 0 : 1;
			int slot2 = shelf.getItem(1).isEmpty() ? 0 : 1;
			int slot3 = shelf.getItem(2).isEmpty() ? 0 : 1;
			return slot1 | slot2 << 1 | slot3 << 2;
		}
		return 0;
	}

}
