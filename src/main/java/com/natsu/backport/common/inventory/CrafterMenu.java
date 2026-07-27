package com.natsu.backport.common.inventory;

import com.natsu.backport.common.block.entity.CrafterBlockEntity;
import com.natsu.backport.common.registry.CTBMenus;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/** The 1.21 crafter menu : a 3x3 grid with togglable slots and a preview result. */
public class CrafterMenu extends AbstractContainerMenu implements ContainerListener {

	public static final int SLOT_COUNT = 9;
	private final ResultContainer resultContainer = new ResultContainer();
	private final ContainerData containerData;
	private final Player player;
	private final Container container;

	public CrafterMenu(int containerId, Inventory inventory) {
		super(CTBMenus.CRAFTER.get(), containerId);
		this.player = inventory.player;
		this.containerData = new SimpleContainerData(CrafterBlockEntity.NUM_DATA);
		this.container = new net.minecraft.world.SimpleContainer(SLOT_COUNT);
		this.addSlots(inventory);
	}

	public CrafterMenu(int containerId, Inventory inventory, Container container, ContainerData containerData) {
		super(CTBMenus.CRAFTER.get(), containerId);
		this.player = inventory.player;
		this.containerData = containerData;
		this.container = container;
		checkContainerSize(container, SLOT_COUNT);
		container.startOpen(inventory.player);
		this.addSlots(inventory);
		this.addSlotListener(this);
	}

	private void addSlots(Inventory inventory) {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				this.addSlot(new CrafterSlot(this.container, x + y * 3, 26 + x * 18, 17 + y * 18, this));
			}
		}
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
		}
		this.addSlot(new ResultSlot(this.resultContainer, 0, 134, 35));
		this.addDataSlots(this.containerData);
		this.refreshRecipeResult();
	}

	public void setSlotState(int slotId, boolean enabled) {
		if (this.container instanceof CrafterBlockEntity crafter) {
			crafter.setSlotState(slotId, enabled);
		} else {
			this.containerData.set(slotId, enabled ? 0 : 1);
		}
		this.broadcastChanges();
	}

	public boolean isSlotDisabled(int slotId) {
		return slotId > -1 && slotId < SLOT_COUNT && this.containerData.get(slotId) == 1;
	}

	public boolean isPowered() {
		return this.containerData.get(CrafterBlockEntity.DATA_TRIGGERED) == 1;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		ItemStack clicked = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			clicked = stack.copy();
			if (slotIndex < SLOT_COUNT ? !this.moveItemStackTo(stack, 9, 45, true)
					: !this.moveItemStackTo(stack, 0, SLOT_COUNT, false)) {
				return ItemStack.EMPTY;
			}
			if (stack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (stack.getCount() == clicked.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, stack);
		}
		return clicked;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	private void refreshRecipeResult() {
		if (!(this.player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		Level level = serverPlayer.getLevel();
		CraftingContainer grid = new CraftingContainer(CrafterBlockEntity.NoopMenu.INSTANCE, 3, 3);
		for (int i = 0; i < SLOT_COUNT; i++) {
			grid.setItem(i, this.container.getItem(i).copy());
		}
		ItemStack result = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level)
				.map(recipe -> recipe.assemble(grid)).orElse(ItemStack.EMPTY);
		this.resultContainer.setItem(0, result);
	}

	public Container getContainer() {
		return this.container;
	}

	@Override
	public void slotChanged(AbstractContainerMenu menu, int slotIndex, ItemStack stack) {
		this.refreshRecipeResult();
	}

	@Override
	public void dataChanged(AbstractContainerMenu menu, int id, int value) {
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.container.stopOpen(player);
	}

	/** A crafter grid slot, locked while its state is disabled. */
	public static class CrafterSlot extends Slot {

		private final CrafterMenu menu;

		public CrafterSlot(Container container, int slot, int x, int y, CrafterMenu menu) {
			super(container, slot, x, y);
			this.menu = menu;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return !this.menu.isSlotDisabled(this.getSlotIndex()) && super.mayPlace(stack);
		}

		@Override
		public void setChanged() {
			super.setChanged();
			this.menu.slotsChanged(this.container);
		}
	}

	/** The preview slot : look, do not touch. */
	private static class ResultSlot extends Slot {

		ResultSlot(Container container, int slot, int x, int y) {
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}

		@Override
		public boolean mayPickup(Player player) {
			return false;
		}
	}
}
