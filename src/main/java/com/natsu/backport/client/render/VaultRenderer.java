package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.natsu.backport.common.block.entity.vault.VaultBlockEntity;
import com.natsu.backport.common.block.entity.vault.VaultClientData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity> {

	public VaultRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(VaultBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		ItemStack displayItem = blockEntity.getSharedData().getDisplayItem();
		if (displayItem.isEmpty() || blockEntity.getLevel() == null) {
			return;
		}

		VaultClientData clientData = blockEntity.getClientData();
		poseStack.pushPose();
		poseStack.translate(0.5, 0.4, 0.5);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.rotLerp(partialTick, clientData.previousSpin(), clientData.currentSpin())));
		Minecraft.getInstance().getItemRenderer().renderStatic(displayItem, ItemTransforms.TransformType.GROUND,
				packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
		poseStack.popPose();
	}
}
