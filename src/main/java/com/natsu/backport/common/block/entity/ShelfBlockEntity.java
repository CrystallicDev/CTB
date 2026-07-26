package com.natsu.backport.common.block.entity;

import com.natsu.backport.common.registry.CTBBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** The 1.21.9 shelf : three displayed stacks, synced to the client for rendering. */
public class ShelfBlockEntity extends BlockEntity implements Container {

	public static final int MAX_ITEMS = 3;

	private final NonNullList<ItemStack> items = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);

	public ShelfBlockEntity(BlockPos pos, BlockState state) {
		super(CTBBlockEntities.SHELF.get(), pos, state);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.items.clear();
		ContainerHelper.loadAllItems(tag, this.items);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		ContainerHelper.saveAllItems(tag, this.items, true);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, this.items, true);
		return tag;
	}

	public ItemStack swapItemNoUpdate(int slot, ItemStack heldStack) {
		ItemStack retrieved = this.removeItemNoUpdate(slot);
		this.items.set(slot, heldStack);
		return retrieved;
	}

	/** Changes must reach the renderers, vanilla resyncs the whole block. */
	@Override
	public void setChanged() {
		super.setChanged();
		if (this.level != null && !this.level.isClientSide) {
			this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}

	@Override
	public int getContainerSize() {
		return MAX_ITEMS;
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.items.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
		if (!result.isEmpty()) {
			this.setChanged();
		}
		return result;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(this.items, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.items.set(slot, stack);
		this.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return this.level != null && this.level.getBlockEntity(this.worldPosition) == this
				&& player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5,
						this.worldPosition.getZ() + 0.5) <= 64.0;
	}

	@Override
	public void clearContent() {
		this.items.clear();
	}
}
