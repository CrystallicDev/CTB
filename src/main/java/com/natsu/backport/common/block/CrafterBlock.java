package com.natsu.backport.common.block;

import java.util.Optional;
import java.util.Random;

import com.natsu.backport.common.block.entity.CrafterBlockEntity;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** The 1.21 crafter : a redstone triggered 3x3 auto crafting table. */
public class CrafterBlock extends BaseEntityBlock {

	public static final BooleanProperty CRAFTING = BooleanProperty.create("crafting");
	public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
	public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;
	private static final int MAX_CRAFTING_TICKS = 6;
	private static final int CRAFTING_TICK_DELAY = 4;

	public CrafterBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(ORIENTATION, FrontAndTop.NORTH_UP)
				.setValue(TRIGGERED, false)
				.setValue(CRAFTING, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter ? crafter.getRedstoneSignal() : 0;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
			boolean isMoving) {
		boolean shouldTrigger = level.hasNeighborSignal(pos);
		boolean isTriggered = state.getValue(TRIGGERED);
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (shouldTrigger && !isTriggered) {
			level.scheduleTick(pos, this, CRAFTING_TICK_DELAY);
			level.setBlock(pos, state.setValue(TRIGGERED, true), 2);
			this.setBlockEntityTriggered(blockEntity, true);
		} else if (!shouldTrigger && isTriggered) {
			level.setBlock(pos, state.setValue(TRIGGERED, false).setValue(CRAFTING, false), 2);
			this.setBlockEntityTriggered(blockEntity, false);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		this.dispenseFrom(state, level, pos);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return level.isClientSide ? null
				: createTickerHelper(type, CTBBlockEntities.CRAFTER.get(), CrafterBlockEntity::serverTick);
	}

	private void setBlockEntityTriggered(BlockEntity blockEntity, boolean triggered) {
		if (blockEntity instanceof CrafterBlockEntity crafter) {
			crafter.setTriggered(triggered);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		CrafterBlockEntity crafter = new CrafterBlockEntity(pos, state);
		crafter.setTriggered(state.hasProperty(TRIGGERED) && state.getValue(TRIGGERED));
		return crafter;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction front = context.getNearestLookingDirection().getOpposite();
		Direction top = switch (front) {
			case DOWN -> context.getHorizontalDirection().getOpposite();
			case UP -> context.getHorizontalDirection();
			default -> Direction.UP;
		};
		return this.defaultBlockState()
				.setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(front, top))
				.setValue(TRIGGERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity by, ItemStack stack) {
		if (state.getValue(TRIGGERED)) {
			level.scheduleTick(pos, this, CRAFTING_TICK_DELAY);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter) {
				Containers.dropContents(level, pos, crafter);
				level.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter) {
			player.openMenu(crafter);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	protected void dispenseFrom(BlockState state, ServerLevel level, BlockPos pos) {
		if (!(level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter)) {
			return;
		}
		CraftingContainer grid = crafter.asCraftingContainer();
		Optional<CraftingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level);
		if (recipe.isEmpty()) {
			level.playSound(null, pos, CTBSounds.CRAFTER_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			return;
		}
		ItemStack results = recipe.get().assemble(grid);
		if (results.isEmpty()) {
			level.playSound(null, pos, CTBSounds.CRAFTER_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			return;
		}
		crafter.setCraftingTicksRemaining(MAX_CRAFTING_TICKS);
		level.setBlock(pos, state.setValue(CRAFTING, true), 2);
		this.dispenseItem(level, pos, crafter, results, state);
		for (ItemStack remainingItem : recipe.get().getRemainingItems(grid)) {
			if (!remainingItem.isEmpty()) {
				this.dispenseItem(level, pos, crafter, remainingItem, state);
			}
		}
		crafter.getItemList().forEach(stack -> {
			if (!stack.isEmpty()) {
				stack.shrink(1);
			}
		});
		crafter.setChanged();
	}

	private void dispenseItem(ServerLevel level, BlockPos pos, CrafterBlockEntity crafter, ItemStack results,
			BlockState state) {
		Direction direction = state.getValue(ORIENTATION).front();
		Container into = HopperBlockEntity.getContainerAt(level, pos.relative(direction));
		ItemStack remaining = results.copy();
		if (into != null && (into instanceof CrafterBlockEntity || results.getCount() > into.getMaxStackSize())) {
			while (!remaining.isEmpty()) {
				ItemStack single = remaining.copy();
				single.setCount(1);
				if (!HopperBlockEntity.addItem(crafter, into, single, direction.getOpposite()).isEmpty()) {
					break;
				}
				remaining.shrink(1);
			}
		} else if (into != null) {
			while (!remaining.isEmpty()) {
				int oldSize = remaining.getCount();
				remaining = HopperBlockEntity.addItem(crafter, into, remaining, direction.getOpposite());
				if (oldSize == remaining.getCount()) {
					break;
				}
			}
		}
		if (!remaining.isEmpty()) {
			Vec3 spawn = Vec3.atCenterOf(pos).add(
					direction.getStepX() * 0.7, direction.getStepY() * 0.7, direction.getStepZ() * 0.7);
			net.minecraft.core.dispenser.DefaultDispenseItemBehavior.spawnItem(level, remaining, 6, direction,
					new net.minecraft.core.PositionImpl(spawn.x, spawn.y, spawn.z));
			level.playSound(null, pos, CTBSounds.CRAFTER_CRAFT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			level.levelEvent(2000, pos, direction.get3DDataValue());
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(ORIENTATION, rotation.rotation().rotate(state.getValue(ORIENTATION)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(ORIENTATION, mirror.rotation().rotate(state.getValue(ORIENTATION)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION, TRIGGERED, CRAFTING);
	}
}
