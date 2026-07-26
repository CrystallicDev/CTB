package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.natsu.backport.common.block.ShelfBlock;
import com.natsu.backport.common.block.entity.ShelfBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

/** Draws the three shelf stacks standing against the back panel. */
public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity> {

	public ShelfRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(ShelfBlockEntity shelf, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
			int packedLight, int packedOverlay) {
		Direction facing = shelf.getBlockState().hasProperty(ShelfBlock.FACING)
				? shelf.getBlockState().getValue(ShelfBlock.FACING) : Direction.NORTH;

		poseStack.pushPose();
		poseStack.translate(0.5, 0.0, 0.5);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
		// local frame : the front faces minus Z, slots left to right for the viewer
		for (int slot = 0; slot < ShelfBlockEntity.MAX_ITEMS; slot++) {
			ItemStack stack = shelf.getItem(slot);
			if (stack.isEmpty()) {
				continue;
			}
			poseStack.pushPose();
			poseStack.translate(0.3125 - slot * 0.3125, 0.40, 0.25);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			poseStack.scale(0.35F, 0.35F, 0.35F);
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED,
					packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, (int) shelf.getBlockPos().asLong() + slot);
			poseStack.popPose();
		}
		poseStack.popPose();
	}
}
