package com.natsu.backport.common.block.entity;

import com.natsu.backport.common.block.CrafterBlock;
import com.natsu.backport.common.inventory.CrafterMenu;
import com.natsu.backport.common.registry.CTBBlockEntities;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** The 1.21 crafter container : nine slots, each disableable while empty. */
public class CrafterBlockEntity extends RandomizableContainerBlockEntity {

	public static final int CONTAINER_SIZE = 9;
	public static final int DATA_TRIGGERED = 9;
	public static final int NUM_DATA = 10;

	private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	private int craftingTicksRemaining = 0;

	protected final ContainerData containerData = new ContainerData() {
		private final int[] slotStates = new int[CONTAINER_SIZE];
		private int triggered;

		@Override
		public int get(int dataId) {
			return dataId == DATA_TRIGGERED ? this.triggered : this.slotStates[dataId];
		}

		@Override
		public void set(int dataId, int value) {
			if (dataId == DATA_TRIGGERED) {
				this.triggered = value;
			} else {
				this.slotStates[dataId] = value;
			}
		}

		@Override
		public int getCount() {
			return NUM_DATA;
		}
	};

	public CrafterBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(CTBBlockEntities.CRAFTER.get(), worldPosition, blockState);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.crafter");
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new CrafterMenu(containerId, inventory, this, this.containerData);
	}

	public void setSlotState(int slotId, boolean enabled) {
		if (!this.slotCanBeDisabled(slotId)) {
			return;
		}
		this.containerData.set(slotId, enabled ? 0 : 1);
		this.setChanged();
	}

	public boolean isSlotDisabled(int slotId) {
		return slotId >= 0 && slotId < CONTAINER_SIZE && this.containerData.get(slotId) == 1;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (this.containerData.get(slot) == 1) {
			return false;
		}
		ItemStack slotStack = this.items.get(slot);
		int currentStackSize = slotStack.getCount();
		if (currentStackSize >= slotStack.getMaxStackSize()) {
			return false;
		}
		if (slotStack.isEmpty()) {
			return true;
		}
		return !this.smallerStackExist(currentStackSize, slotStack, slot);
	}

	private boolean smallerStackExist(int baseSize, ItemStack baseItem, int baseSlot) {
		for (int i = baseSlot + 1; i < CONTAINER_SIZE; i++) {
			if (this.isSlotDisabled(i)) {
				continue;
			}
			ItemStack slotStack = this.getItem(i);
			if (slotStack.isEmpty()
					|| slotStack.getCount() < baseSize && ItemStack.isSameItemSameTags(slotStack, baseItem)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.craftingTicksRemaining = tag.getInt("crafting_ticks_remaining");
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(tag)) {
			ContainerHelper.loadAllItems(tag, this.items);
		}
		for (int i = 0; i < CONTAINER_SIZE; i++) {
			this.containerData.set(i, 0);
		}
		for (int slot : tag.getIntArray("disabled_slots")) {
			if (this.slotCanBeDisabled(slot)) {
				this.containerData.set(slot, 1);
			}
		}
		this.containerData.set(DATA_TRIGGERED, tag.getInt("triggered"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
		if (!this.trySaveLootTable(tag)) {
			ContainerHelper.saveAllItems(tag, this.items);
		}
		IntArrayList disabledSlots = new IntArrayList();
		for (int i = 0; i < CONTAINER_SIZE; i++) {
			if (this.isSlotDisabled(i)) {
				disabledSlots.add(i);
			}
		}
		tag.putIntArray("disabled_slots", disabledSlots.toIntArray());
		tag.putInt("triggered", this.containerData.get(DATA_TRIGGERED));
	}

	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (this.isSlotDisabled(slot)) {
			this.setSlotState(slot, true);
		}
		super.setItem(slot, stack);
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	public NonNullList<ItemStack> getItemList() {
		return this.items;
	}

	/** A snapshot 3x3 crafting grid for recipe matching, the 1.18.2 substitute for CraftingInput. */
	public CraftingContainer asCraftingContainer() {
		CraftingContainer container = new CraftingContainer(NoopMenu.INSTANCE, 3, 3);
		for (int i = 0; i < CONTAINER_SIZE; i++) {
			container.setItem(i, this.items.get(i).copy());
		}
		return container;
	}

	public void setTriggered(boolean value) {
		this.containerData.set(DATA_TRIGGERED, value ? 1 : 0);
	}

	public boolean isTriggered() {
		return this.containerData.get(DATA_TRIGGERED) == 1;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CrafterBlockEntity entity) {
		int remaining = entity.craftingTicksRemaining - 1;
		if (remaining < 0) {
			return;
		}
		entity.craftingTicksRemaining = remaining;
		if (remaining == 0) {
			level.setBlock(pos, state.setValue(CrafterBlock.CRAFTING, false), 3);
		}
	}

	public void setCraftingTicksRemaining(int ticks) {
		this.craftingTicksRemaining = ticks;
	}

	public int getRedstoneSignal() {
		int count = 0;
		for (int i = 0; i < this.getContainerSize(); i++) {
			if (!this.getItem(i).isEmpty() || this.isSlotDisabled(i)) {
				count++;
			}
		}
		return count;
	}

	private boolean slotCanBeDisabled(int slotId) {
		return slotId > -1 && slotId < CONTAINER_SIZE && this.items.get(slotId).isEmpty();
	}

	/** The dummy menu behind the snapshot crafting grids. */
	public static final class NoopMenu extends AbstractContainerMenu {

		public static final NoopMenu INSTANCE = new NoopMenu();

		private NoopMenu() {
			super(null, -1);
		}

		@Override
		public boolean stillValid(Player player) {
			return false;
		}

		@Override
		public ItemStack quickMoveStack(Player player, int index) {
			return ItemStack.EMPTY;
		}
	}
}
