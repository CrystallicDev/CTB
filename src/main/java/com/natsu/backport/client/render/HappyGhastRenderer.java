package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.HappyGhast;
import com.natsu.backport.common.item.HarnessItem;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

@OnlyIn(value = Dist.CLIENT)
public class HappyGhastRenderer extends GeoEntityRenderer<HappyGhast> {

	public HappyGhastRenderer(EntityRendererProvider.Context context) {
		super(context, new HappyGhastModel());
		this.shadowRadius = 2.0F;
		this.addLayer(new HarnessLayer(this));
	}

	@Override
	public RenderType getRenderType(HappyGhast animatable, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight,
			ResourceLocation texture) {
		return RenderType.entityCutoutNoCull(texture);
	}

	@Override
	public void render(HappyGhast ghast, float entityYaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		// the model is authored at one block, the entity is four wide
		float scale = 4.0F * (ghast.isBaby() ? HappyGhast.BABY_SCALE : 1.0F);
		poseStack.scale(scale, scale, scale);
		super.render(ghast, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	/** The colored harness painted over the body when equipped. */
	private static class HarnessLayer extends AbstractLayerGeo<HappyGhast> {

		HarnessLayer(GeoEntityRenderer<HappyGhast> renderer) {
			super(renderer, renderer::getTextureLocation, e -> renderer.getGeoModelProvider().getModelLocation(e));
		}

		@Override
		public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, HappyGhast ghast,
				float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
				float headPitch) {
			if (ghast.isInvisible() || !(ghast.getHarness().getItem() instanceof HarnessItem harness)) {
				return;
			}
			ResourceLocation texture = new ResourceLocation(CTBackport.MODID,
					"textures/entity/happy_ghast_harness_" + harness.getColor().getName() + ".png");
			reRenderCurrentModelInRenderer(ghast, partialTick, poseStack, bufferSource, packedLight,
					RenderType.entityCutoutNoCull(texture));
		}
	}

	public static class HappyGhastModel extends AnimatedGeoModel<HappyGhast> {

		private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/happy_ghast.geo.json");
		private static final ResourceLocation ANIMATION = new ResourceLocation(CTBackport.MODID, "animations/happy_ghast.animation.json");
		private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/happy_ghast.png");
		private static final ResourceLocation TEXTURE_BABY = new ResourceLocation(CTBackport.MODID, "textures/entity/happy_ghast_baby.png");

		@Override
		public ResourceLocation getModelLocation(HappyGhast entity) {
			return MODEL;
		}

		@Override
		public ResourceLocation getTextureLocation(HappyGhast entity) {
			return entity.isBaby() ? TEXTURE_BABY : TEXTURE;
		}

		@Override
		public ResourceLocation getAnimationFileLocation(HappyGhast entity) {
			return ANIMATION;
		}
	}
}
