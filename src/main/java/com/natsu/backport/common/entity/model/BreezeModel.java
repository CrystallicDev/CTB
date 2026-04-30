package com.natsu.backport.common.entity.model;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Breeze;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BreezeModel extends AnimatedGeoModel<Breeze> {

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
