package com.natsu.backport.common.block;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.entity.TrialSpawnerBlockEntity;
import com.natsu.backport.common.block.entity.trialspawner.TrialSpawnerState;
import com.natsu.backport.common.registry.CTBBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class TrialSpawnerBlock extends BaseEntityBlock {

	public static final EnumProperty<TrialSpawnerState> STATE = EnumProperty.create("trial_spawner_state", TrialSpawnerState.class);
	public static final BooleanProperty OMINOUS = BooleanProperty.create("ominous");

	public TrialSpawnerBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(STATE, TrialSpawnerState.INACTIVE).setValue(OMINOUS, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STATE, OMINOUS);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TrialSpawnerBlockEntity(pos, state);
	}

	// spawn eggs only know about the vanilla spawner, so the override lives here
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		if (held.getItem() instanceof SpawnEggItem egg) {
			if (!level.isClientSide && level.getBlockEntity(pos) instanceof TrialSpawnerBlockEntity spawner) {
				EntityType<?> type = egg.getType(held.getTag());
				spawner.getTrialSpawner().overrideEntityToSpawn(type, level);
				spawner.setChanged();
				level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
				if (!player.getAbilities().instabuild) {
					held.shrink(1);
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level instanceof ServerLevel serverLevel
				? createTickerHelper(type, CTBBlockEntities.TRIAL_SPAWNER.get(),
						(innerLevel, pos, blockState, entity) -> entity.getTrialSpawner()
								.tickServer(serverLevel, pos, blockState.getValue(OMINOUS)))
				: createTickerHelper(type, CTBBlockEntities.TRIAL_SPAWNER.get(),
						(innerLevel, pos, blockState, entity) -> entity.getTrialSpawner()
								.tickClient(innerLevel, pos, blockState.getValue(OMINOUS)));
	}
}
