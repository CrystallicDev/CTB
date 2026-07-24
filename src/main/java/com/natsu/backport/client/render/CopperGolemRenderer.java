package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.entity.model.CopperGolemModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CopperGolemRenderer extends GeoEntityRenderer<CopperGolem> {

	public CopperGolemRenderer(EntityRendererProvider.Context context) {
		super(context, new CopperGolemModel());
		this.shadowRadius = 0.35F;
		this.addLayer(new CopperGolemGlowLayer(this));
	}

	@Override
	public RenderType getRenderType(CopperGolem golem, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
			com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, int packedLight, net.minecraft.resources.ResourceLocation texture) {
		return RenderType.entityCutoutNoCull(texture);
	}

	@Override
	public void render(CopperGolem golem, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(golem, entityYaw, partialTick, poseStack, buffer, packedLight);

		// the glowing eyes, same emissive trick as the creaking
		// (rendered by a second pass of the model through the eyes render type)

		ItemStack carried = golem.getMainHandItem();
		if (!carried.isEmpty()) {
			poseStack.pushPose();
			float bodyYaw = Mth.rotLerp(partialTick, golem.yBodyRotO, golem.yBodyRot);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - bodyYaw));
			// held out in both hands ahead of the body
			poseStack.translate(0.0, 0.55, -0.45);
			poseStack.scale(0.55F, 0.55F, 0.55F);
			Minecraft.getInstance().getItemRenderer().renderStatic(carried, ItemTransforms.TransformType.GROUND,
					packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, golem.getId());
			poseStack.popPose();
		}
	}
}
