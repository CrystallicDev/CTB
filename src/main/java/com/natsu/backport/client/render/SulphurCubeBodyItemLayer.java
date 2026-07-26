package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.natsu.backport.common.entity.SulphurCube;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * The swallowed block where the core was. The layer pose is the 1.18.2 model
 * space, flipped and lifted by the dispatcher, so a 180 degree x rotation puts
 * the block upright again and the offsets land it inside the shell.
 */
public class SulphurCubeBodyItemLayer extends RenderLayer<SulphurCube, SulphurCubeModel> {

	public SulphurCubeBodyItemLayer(RenderLayerParent<SulphurCube, SulphurCubeModel> parent) {
		super(parent);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, SulphurCube cube,
			float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
			float netHeadYaw, float headPitch) {
		if (cube.isInvisible() || !cube.hasBodyItem()) {
			return;
		}
		ItemStack body = cube.getBodyItem();
		boolean small = cube.getSize() <= 1;
		poseStack.pushPose();
		poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		if (small) {
			poseStack.scale(0.5F, 0.5F, 0.5F);
			poseStack.translate(-0.5, -2.874, -0.5);
		} else {
			poseStack.translate(-0.5, -1.4375, -0.5);
		}
		if (body.getItem() instanceof BlockItem blockItem) {
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
					blockItem.getBlock().defaultBlockState(), poseStack, buffers, packedLight,
					OverlayTexture.NO_OVERLAY);
		} else {
			poseStack.translate(0.5, 0.5, 0.5);
			poseStack.scale(0.8F, 0.8F, 0.8F);
			Minecraft.getInstance().getItemRenderer().renderStatic(body, ItemTransforms.TransformType.GROUND,
					packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers, cube.getId());
		}
		poseStack.popPose();
	}
}
