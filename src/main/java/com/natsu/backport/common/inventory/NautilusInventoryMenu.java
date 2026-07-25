package com.natsu.backport.common.inventory;

import com.natsu.backport.common.entity.AbstractNautilus;
import com.natsu.backport.common.item.NautilusArmorItem;
import com.natsu.backport.common.registry.CTBMenus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Saddle and armor slots, the horse layout. */
public class NautilusInventoryMenu extends AbstractContainerMenu {

	private final Container container;
	private final AbstractNautilus nautilus;

	public NautilusInventoryMenu(int id, Inventory playerInventory, Container container, AbstractNautilus nautilus) {
		super(CTBMenus.NAUTILUS_INVENTORY.get(), id);
		this.container = container;
		this.nautilus = nautilus;
		container.startOpen(playerInventory.player);

		this.addSlot(new Slot(container, 0, 8, 18) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(Items.SADDLE) && !this.hasItem() && nautilus.isSaddleable();
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});
		this.addSlot(new Slot(container, 1, 8, 36) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof NautilusArmorItem;
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
		}
	}

	public static NautilusInventoryMenu fromNetwork(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
		AbstractNautilus nautilus = playerInventory.player.level.getEntity(buffer.readVarInt()) instanceof AbstractNautilus found
				? found : null;
		return new NautilusInventoryMenu(id, playerInventory,
				nautilus != null ? nautilus.getInventory() : new SimpleContainer(2), nautilus);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.nautilus != null && this.nautilus.isAlive()
				&& this.nautilus.distanceTo(player) < 8.0F && this.container.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack moved = slot.getItem();
			result = moved.copy();
			if (index < 2) {
				if (!this.moveItemStackTo(moved, 2, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(1).mayPlace(moved) && !this.getSlot(1).hasItem()) {
				if (!this.moveItemStackTo(moved, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(0).mayPlace(moved)) {
				if (!this.moveItemStackTo(moved, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				return ItemStack.EMPTY;
			}
			if (moved.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return result;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.container.stopOpen(player);
	}
}
