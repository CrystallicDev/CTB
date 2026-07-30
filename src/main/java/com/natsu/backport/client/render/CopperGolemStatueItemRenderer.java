package com.natsu.backport.client.render;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.CopperGolemStatueItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class CopperGolemStatueItemRenderer extends GeoItemRenderer<CopperGolemStatueItem> {

	public static final Lazy<CopperGolemStatueItemRenderer> INSTANCE = Lazy.of(CopperGolemStatueItemRenderer::new);

	private CopperGolemStatueItemRenderer() {
		super(new StatueItemModel());
	}

	private static class StatueItemModel extends AnimatedGeoModel<CopperGolemStatueItem> {

		// the statue copy of the geo, never the living golem one (shared bones
		// cross contaminate transition snapshots)
		private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/copper_golem_statue.geo.json");
		private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem.png");
		private static final ResourceLocation ANIMATION = new ResourceLocation(CTBackport.MODID, "animations/copper_golem.animation.json");

		@Override
		public ResourceLocation getModelLocation(CopperGolemStatueItem item) {
			return MODEL;
		}

		@Override
		public ResourceLocation getTextureLocation(CopperGolemStatueItem item) {
			return TEXTURE;
		}

		@Override
		public ResourceLocation getAnimationFileLocation(CopperGolemStatueItem item) {
			return ANIMATION;
		}

		@Override
		public void setCustomAnimations(CopperGolemStatueItem item, int instanceId,
				software.bernie.geckolib3.core.event.predicate.AnimationEvent animationEvent) {
			super.setCustomAnimations(item, instanceId, animationEvent);
			hide("sit_seat", true);
			hide("sit_back", true);
		}

		private void hide(String bone, boolean hidden) {
			software.bernie.geckolib3.core.processor.IBone b = this.getAnimationProcessor().getBone(bone);
			if (b != null) {
				b.setHidden(hidden);
			}
		}
	}
}
