package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.SulphurCube;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * Port of the 26.2 SulfurCubeInnerLayer : inside the shell, either the yellow
 * core or the swallowed block, sharing the exact same pose as the outer pass.
 */
public class SulphurCubeInnerLayer extends RenderLayer<SulphurCube, SulphurCubeModel> {

	private static final ResourceLocation INNER =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_inner.png");
	private static final ResourceLocation INNER_SMALL =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_inner_small.png");

	private final SulphurCubeModel innerModel;
	private final SulphurCubeModel smallInnerModel;

	public SulphurCubeInnerLayer(RenderLayerParent<SulphurCube, SulphurCubeModel> parent) {
		super(parent);
		this.innerModel = new SulphurCubeModel(SulphurCubeModel.inner().bakeRoot());
		this.smallInnerModel = new SulphurCubeModel(SulphurCubeModel.smallInner().bakeRoot());
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, SulphurCube cube,
			float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
			float netHeadYaw, float headPitch) {
		if (cube.isInvisible()) {
			return;
		}
		boolean small = cube.getSize() <= 1;
		if (cube.hasBodyItem()) {
			ItemStack body = cube.getBodyItem();
			poseStack.pushPose();
			// the vanilla layer flips out of model space and centers the block
			poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
			if (small) {
				poseStack.scale(0.5F, 0.5F, 0.5F);
			}
			if (body.getItem() instanceof BlockItem blockItem) {
				poseStack.translate(-0.5, -0.518, -0.5);
				Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
						blockItem.getBlock().defaultBlockState(), poseStack, buffers, packedLight,
						OverlayTexture.NO_OVERLAY);
			} else {
				poseStack.translate(0.0, -0.518 + 0.35, 0.0);
				poseStack.scale(0.8F, 0.8F, 0.8F);
				Minecraft.getInstance().getItemRenderer().renderStatic(body,
						net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.GROUND,
						packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers, cube.getId());
			}
			poseStack.popPose();
		} else {
			SulphurCubeModel model = small ? this.smallInnerModel : this.innerModel;
			ResourceLocation texture = small ? INNER_SMALL : INNER;
			VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(texture));
			model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
