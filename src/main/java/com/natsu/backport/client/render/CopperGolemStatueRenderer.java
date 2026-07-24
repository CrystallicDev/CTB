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

		private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/copper_golem.geo.json");
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
	}
}
