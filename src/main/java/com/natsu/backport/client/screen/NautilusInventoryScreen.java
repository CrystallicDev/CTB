package com.natsu.backport.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.common.inventory.NautilusInventoryMenu;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** The horse GUI with just the saddle and armor slots. */
public class NautilusInventoryScreen extends AbstractContainerScreen<NautilusInventoryMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/horse.png");

	public NautilusInventoryScreen(NautilusInventoryMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
		// the saddle and armor slot frames from the horse sheet
		this.blit(poseStack, x + 7, y + 17, 18, this.imageHeight + 54, 18, 18);
		this.blit(poseStack, x + 7, y + 35, 0, this.imageHeight + 54, 18, 18);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}
}
