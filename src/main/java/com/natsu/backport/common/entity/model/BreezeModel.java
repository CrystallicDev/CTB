package com.natsu.backport.common.entity.model;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Breeze;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BreezeModel extends AnimatedGeoModel<Breeze> {

	// full turn every ~2s, whatever animation is playing
	private static final float RODS_SPIN_SPEED = 0.15F;

	@Override
	public void setCustomAnimations(Breeze breeze, int instanceId, AnimationEvent animationEvent) {
		super.setCustomAnimations(breeze, instanceId, animationEvent);
		IBone rods = this.getAnimationProcessor().getBone("rods");
		if (rods != null) {
			float partialTick = animationEvent == null ? 0.0F : (float) animationEvent.getPartialTick();
			rods.setRotationY((breeze.tickCount + partialTick) * RODS_SPIN_SPEED);
		}
	}

    @Override
    public ResourceLocation getModelLocation(Breeze entity) {
        return new ResourceLocation(CTBackport.MODID, "geo/breeze.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Breeze entity) {
        return new ResourceLocation(CTBackport.MODID, "textures/entity/breeze/breeze.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Breeze entity) {
        return new ResourceLocation(CTBackport.MODID, "animations/breeze.animation.json");
    }
}
