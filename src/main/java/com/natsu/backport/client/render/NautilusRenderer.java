package com.natsu.backport.client.render;

import com.natsu.backport.common.entity.AbstractNautilus;
import com.natsu.backport.common.entity.model.NautilusModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NautilusRenderer extends GeoEntityRenderer<AbstractNautilus> {

	public NautilusRenderer(EntityRendererProvider.Context context) {
		super(context, new NautilusModel());
		this.shadowRadius = 0.7F;
		this.addLayer(new NautilusEquipmentLayer(this));
	}

	@Override
	public net.minecraft.client.renderer.RenderType getRenderType(AbstractNautilus animatable, float partialTick,
			com.mojang.blaze3d.vertex.PoseStack poseStack,
			net.minecraft.client.renderer.MultiBufferSource bufferSource,
			com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight,
			net.minecraft.resources.ResourceLocation texture) {
		// no cull : the one sided mouth faces read from both sides, Creaking style
		return net.minecraft.client.renderer.RenderType.entityCutoutNoCull(texture);
	}

	@Override
	public void render(AbstractNautilus nautilus, float entityYaw, float partialTick,
			com.mojang.blaze3d.vertex.PoseStack poseStack,
			net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight) {
		if (nautilus.isBaby()) {
			poseStack.scale(0.45F, 0.45F, 0.45F);
		}
		super.render(nautilus, entityYaw, partialTick, poseStack, buffer, packedLight);
	}
}
