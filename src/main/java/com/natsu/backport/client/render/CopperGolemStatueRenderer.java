package com.natsu.backport.client.render;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.entity.CopperGolemStatueBlockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class CopperGolemStatueRenderer extends GeoBlockRenderer<CopperGolemStatueBlockEntity> {

	public CopperGolemStatueRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new StatueModel());
	}

	public static class StatueModel extends AnimatedGeoModel<CopperGolemStatueBlockEntity> {

		// a COPY of the golem geo : geckolib caches bones by geo location, and
		// sharing them with the living golem cross contaminates the poses at
		// every animation transition (the snapshot captures the foreign pose)
		private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/copper_golem_statue.geo.json");
		private static final ResourceLocation ANIMATION = new ResourceLocation(CTBackport.MODID, "animations/copper_golem.animation.json");
		private static final ResourceLocation[] TEXTURES = {
				new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem.png"),
				new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_exposed.png"),
				new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_weathered.png"),
				new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_oxidized.png"),
		};

		@Override
		public ResourceLocation getModelLocation(CopperGolemStatueBlockEntity statue) {
			return MODEL;
		}

		@Override
		public ResourceLocation getTextureLocation(CopperGolemStatueBlockEntity statue) {
			return TEXTURES[net.minecraft.util.Mth.clamp(statue.getWeatherLevel(), 0, 3)];
		}

		@Override
		public ResourceLocation getAnimationFileLocation(CopperGolemStatueBlockEntity statue) {
			return ANIMATION;
		}

		@Override
		public void setCustomAnimations(CopperGolemStatueBlockEntity statue, int instanceId,
				software.bernie.geckolib3.core.event.predicate.AnimationEvent animationEvent) {
			super.setCustomAnimations(statue, instanceId, animationEvent);
			boolean sitting = statue.getBlockState().hasProperty(com.natsu.backport.common.block.CopperGolemStatueBlock.POSE)
					&& statue.getBlockState().getValue(com.natsu.backport.common.block.CopperGolemStatueBlock.POSE)
							== com.natsu.backport.common.block.CopperGolemStatueBlock.Pose.SITTING;
			hide("sit_seat", !sitting);
			hide("sit_back", !sitting);
			hide("antenna", !statue.hasAntenna());
		}

		private void hide(String bone, boolean hidden) {
			software.bernie.geckolib3.core.processor.IBone b = this.getAnimationProcessor().getBone(bone);
			if (b != null) {
				b.setHidden(hidden);
			}
		}
	}
}
