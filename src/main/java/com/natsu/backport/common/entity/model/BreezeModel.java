package com.natsu.backport.common.entity.model;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Breeze;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BreezeModel extends AnimatedGeoModel<Breeze> {

	@Override
	public void setCustomAnimations(Breeze breeze, int instanceId, AnimationEvent animationEvent) {
		super.setCustomAnimations(breeze, instanceId, animationEvent);
		IBone rods = this.getAnimationProcessor().getBone("rods");
		if (rods != null) {
			float partialTick = animationEvent == null ? 0.0F : (float) animationEvent.getPartialTick();
			rods.setRotationY(Mth.lerp(partialTick, breeze.rodsAngleO, breeze.rodsAngle));
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
