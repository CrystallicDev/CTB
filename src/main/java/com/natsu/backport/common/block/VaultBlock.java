package com.natsu.backport.common.block;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.entity.vault.VaultBlockEntity;
import com.natsu.backport.common.block.entity.vault.VaultState;
import com.natsu.backport.common.registry.CTBBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class VaultBlock extends BaseEntityBlock {

	public static final EnumProperty<VaultState> STATE = EnumProperty.create("vault_state", VaultState.class);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OMINOUS = BooleanProperty.create("ominous");

	public VaultBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH).setValue(STATE, VaultState.INACTIVE).setValue(OMINOUS, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, STATE, OMINOUS);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		if (!held.isEmpty() && state.getValue(STATE) == VaultState.ACTIVE) {
			if (level instanceof ServerLevel serverLevel) {
				if (!(serverLevel.getBlockEntity(pos) instanceof VaultBlockEntity vault)) {
					return InteractionResult.PASS;
				}

				VaultBlockEntity.Server.tryInsertKey(serverLevel, pos, state, vault.getConfig(),
						vault.getServerData(), vault.getSharedData(), player, held);
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new VaultBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level instanceof ServerLevel serverLevel
				? createTickerHelper(type, CTBBlockEntities.VAULT.get(),
						(innerLevel, pos, blockState, entity) -> VaultBlockEntity.Server.tick(
								serverLevel, pos, blockState, entity.getConfig(), entity.getServerData(), entity.getSharedData()))
				: createTickerHelper(type, CTBBlockEntities.VAULT.get(),
						(innerLevel, pos, blockState, entity) -> VaultBlockEntity.Client.tick(
								innerLevel, pos, blockState, entity.getClientData(), entity.getSharedData()));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
