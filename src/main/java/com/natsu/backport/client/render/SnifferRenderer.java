package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Sniffer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@OnlyIn(value = Dist.CLIENT)
public class SnifferRenderer extends GeoEntityRenderer<Sniffer> {

	public SnifferRenderer(EntityRendererProvider.Context context) {
		super(context, new SnifferModel());
		this.shadowRadius = 1.1F;
	}

	@Override
	public RenderType getRenderType(Sniffer animatable, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight,
			ResourceLocation texture) {
		return RenderType.entityCutoutNoCull(texture);
	}

	@Override
	public void render(Sniffer sniffer, float entityYaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		if (sniffer.isBaby()) {
			poseStack.scale(0.5F, 0.5F, 0.5F);
		}
		super.render(sniffer, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	public static class SnifferModel extends AnimatedGeoModel<Sniffer> {

		private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/sniffer.geo.json");
		private static final ResourceLocation ANIMATION = new ResourceLocation(CTBackport.MODID, "animations/sniffer.animation.json");
		private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/sniffer.png");

		@Override
		public ResourceLocation getModelLocation(Sniffer entity) {
			return MODEL;
		}

		@Override
		public ResourceLocation getTextureLocation(Sniffer entity) {
			return TEXTURE;
		}

		@Override
		public ResourceLocation getAnimationFileLocation(Sniffer entity) {
			return ANIMATION;
		}
	}
}
