package com.natsu.backport.common.block.entity;

import com.natsu.backport.common.block.TrialSpawnerBlock;
import com.natsu.backport.common.block.entity.trialspawner.PlayerDetector;
import com.natsu.backport.common.block.entity.trialspawner.TrialSpawner;
import com.natsu.backport.common.block.entity.trialspawner.TrialSpawnerState;
import com.natsu.backport.common.registry.CTBBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrialSpawnerBlockEntity extends BlockEntity implements TrialSpawner.StateAccessor {

	private final TrialSpawner trialSpawner = new TrialSpawner(this,
			PlayerDetector.NO_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);

	public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
		super(CTBBlockEntities.TRIAL_SPAWNER.get(), pos, state);
	}

	public TrialSpawner getTrialSpawner() {
		return this.trialSpawner;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.trialSpawner.load(tag);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		this.trialSpawner.save(tag);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	public TrialSpawnerState getState() {
		return this.getBlockState().hasProperty(TrialSpawnerBlock.STATE)
				? this.getBlockState().getValue(TrialSpawnerBlock.STATE)
				: TrialSpawnerState.INACTIVE;
	}

	@Override
	public void setState(Level level, TrialSpawnerState state) {
		this.setChanged();
		level.setBlock(this.worldPosition, this.getBlockState().setValue(TrialSpawnerBlock.STATE, state), Block.UPDATE_ALL);
	}

	@Override
	public void markUpdated() {
		this.setChanged();
		if (this.level != null) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
		}
	}
}
