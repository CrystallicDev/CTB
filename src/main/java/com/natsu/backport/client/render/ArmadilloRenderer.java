package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Armadillo;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@OnlyIn(value = Dist.CLIENT)
public class ArmadilloRenderer extends GeoEntityRenderer<Armadillo> {

	public ArmadilloRenderer(EntityRendererProvider.Context context) {
		super(context, new ArmadilloModel());
		this.shadowRadius = 0.4F;
	}

	@Override
	public RenderType getRenderType(Armadillo animatable, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight,
			ResourceLocation texture) {
		return RenderType.entityCutoutNoCull(texture);
	}

	@Override
	public void render(Armadillo armadillo, float entityYaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		if (armadillo.isBaby()) {
			poseStack.scale(Armadillo.BABY_SCALE, Armadillo.BABY_SCALE, Armadillo.BABY_SCALE);
		}
		super.render(armadillo, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	public static class ArmadilloModel extends AnimatedGeoModel<Armadillo> {

		private static final String[] BODY_BONES =
				{ "body", "tail", "head", "head_cube", "right_ear", "right_ear_cube", "left_ear",
						"left_ear_cube", "right_hind_leg", "left_hind_leg", "right_front_leg", "left_front_leg" };
		private static final String[] HEAD_BONES = { "head", "head_cube", "right_ear", "right_ear_cube",
				"left_ear", "left_ear_cube" };

		@Override
		public void setCustomAnimations(Armadillo armadillo, int uniqueID,
				software.bernie.geckolib3.core.event.predicate.AnimationEvent event) {
			super.setCustomAnimations(armadillo, uniqueID, event);
			// the ball cube swaps in for the body while fully rolled up
			boolean balled = armadillo.getState() == Armadillo.State.SCARED;
			boolean peeking = balled && armadillo.isPeeking();
			this.getAnimationProcessor().getBone("cube").setHidden(!balled);
			for (String bone : BODY_BONES) {
				this.getAnimationProcessor().getBone(bone).setHidden(balled);
			}
			if (peeking) {
				for (String bone : HEAD_BONES) {
					this.getAnimationProcessor().getBone(bone).setHidden(false);
				}
			}
		}

		private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/armadillo.geo.json");
		private static final ResourceLocation ANIMATION = new ResourceLocation(CTBackport.MODID, "animations/armadillo.animation.json");
		private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/armadillo.png");

		@Override
		public ResourceLocation getModelLocation(Armadillo entity) {
			return MODEL;
		}

		@Override
		public ResourceLocation getTextureLocation(Armadillo entity) {
			return TEXTURE;
		}

		@Override
		public ResourceLocation getAnimationFileLocation(Armadillo entity) {
			return ANIMATION;
		}
	}
}
