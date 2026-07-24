package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.entity.model.CopperGolemModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.RenderUtils;

public class CopperGolemRenderer extends GeoEntityRenderer<CopperGolem> {

	private CopperGolem currentGolem;

	public CopperGolemRenderer(EntityRendererProvider.Context context) {
		super(context, new CopperGolemModel());
		this.shadowRadius = 0.35F;
		this.addLayer(new CopperGolemGlowLayer(this));
	}

	@Override
	public RenderType getRenderType(CopperGolem golem, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
			VertexConsumer vertexConsumer, int packedLight, net.minecraft.resources.ResourceLocation texture) {
		return RenderType.entityCutoutNoCull(texture);
	}

	@Override
	public void render(CopperGolem golem, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		this.currentGolem = golem;
		super.render(golem, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("right_arm") && this.currentGolem != null) {
			ItemStack carried = this.currentGolem.getMainHandItem();
			if (!carried.isEmpty()) {
				// follow the animated arm into its local space, then sit the
				// stack between the raised hands
				poseStack.pushPose();
				RenderUtils.translate(bone, poseStack);
				RenderUtils.moveToPivot(bone, poseStack);
				RenderUtils.rotate(bone, poseStack);
				RenderUtils.moveBackFromPivot(bone, poseStack);
				poseStack.translate(0.14, -0.4, 0.0);
				poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
				Minecraft.getInstance().getItemRenderer().renderStatic(carried, ItemTransforms.TransformType.GROUND,
						packedLight, OverlayTexture.NO_OVERLAY, poseStack, this.rtb, this.currentGolem.getId());
				poseStack.popPose();
				// the item render swaps the active buffer, grab ours back
				buffer = this.rtb.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(this.currentGolem)));
			}
		}
		super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
