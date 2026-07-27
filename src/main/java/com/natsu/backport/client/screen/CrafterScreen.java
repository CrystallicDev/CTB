package com.natsu.backport.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.inventory.CrafterMenu;
import com.natsu.backport.common.network.CTBNetwork;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

/** The 1.21 crafter screen : click an empty grid slot to toggle it. */
public class CrafterScreen extends AbstractContainerScreen<CrafterMenu> {

	private static final ResourceLocation CONTAINER_LOCATION =
			new ResourceLocation(CTBackport.MODID, "textures/gui/container/crafter.png");
	private static final ResourceLocation DISABLED_SLOT =
			new ResourceLocation(CTBackport.MODID, "textures/gui/sprites/crafter/disabled_slot.png");
	private static final ResourceLocation POWERED_REDSTONE =
			new ResourceLocation(CTBackport.MODID, "textures/gui/sprites/crafter/powered_redstone.png");
	private static final ResourceLocation UNPOWERED_REDSTONE =
			new ResourceLocation(CTBackport.MODID, "textures/gui/sprites/crafter/unpowered_redstone.png");
	private static final Component DISABLED_SLOT_TOOLTIP = new TranslatableComponent("gui.togglable_slot");

	private final Player player;

	public CrafterScreen(CrafterMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.player = inventory.player;
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	protected void slotClicked(Slot slot, int slotId, int buttonNum, ClickType clickType) {
		if (slot instanceof CrafterMenu.CrafterSlot && !slot.hasItem() && !this.player.isSpectator()) {
			switch (clickType) {
				case PICKUP -> {
					if (this.menu.isSlotDisabled(slotId)) {
						this.updateSlotState(slotId, true);
					} else if (this.menu.getCarried().isEmpty()) {
						this.updateSlotState(slotId, false);
					}
				}
				case SWAP -> {
					if (this.menu.isSlotDisabled(slotId)
							&& !this.player.getInventory().getItem(buttonNum).isEmpty()) {
						this.updateSlotState(slotId, true);
					}
				}
				default -> {
				}
			}
		}
		super.slotClicked(slot, slotId, buttonNum, clickType);
	}

	private void updateSlotState(int slotId, boolean enabled) {
		this.menu.setSlotState(slotId, enabled);
		CTBNetwork.CHANNEL.sendToServer(
				new CTBNetwork.CrafterSlotStatePacket(this.menu.containerId, slotId, enabled));
		this.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.4F, enabled ? 1.0F : 0.75F);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		if (this.hoveredSlot instanceof CrafterMenu.CrafterSlot && !this.hoveredSlot.hasItem()
				&& !this.menu.isSlotDisabled(this.hoveredSlot.index) && this.menu.getCarried().isEmpty()
				&& !this.player.isSpectator()) {
			this.renderTooltip(poseStack, DISABLED_SLOT_TOOLTIP, mouseX, mouseY);
		} else {
			this.renderTooltip(poseStack, mouseX, mouseY);
		}
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, CONTAINER_LOCATION);
		int x0 = (this.width - this.imageWidth) / 2;
		int y0 = (this.height - this.imageHeight) / 2;
		blit(poseStack, x0, y0, 0, 0, this.imageWidth, this.imageHeight);

		// the disabled slot overlays
		for (int i = 0; i < CrafterMenu.SLOT_COUNT; i++) {
			if (this.menu.isSlotDisabled(i)) {
				Slot slot = this.menu.getSlot(i);
				RenderSystem.setShaderTexture(0, DISABLED_SLOT);
				blit(poseStack, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, 0, 0, 18, 18, 18, 18);
			}
		}

		// the redstone state indicator over the arrow
		RenderSystem.setShaderTexture(0, this.menu.isPowered() ? POWERED_REDSTONE : UNPOWERED_REDSTONE);
		blit(poseStack, this.width / 2 + 9, this.height / 2 - 48, 0, 0, 16, 16, 16, 16);
	}
}
