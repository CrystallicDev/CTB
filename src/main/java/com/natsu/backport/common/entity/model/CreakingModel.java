package com.natsu.backport.common.entity.model;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Creaking;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CreakingModel extends AnimatedGeoModel<Creaking>{

	@Override
	public ResourceLocation getAnimationFileLocation(Creaking animatable) {
		return new ResourceLocation(CTBackport.MODID, "animations/creaking/creaking_anim.json");
	}

	@Override
	public ResourceLocation getModelLocation(Creaking object) {
		return new ResourceLocation(CTBackport.MODID, "geo/creaking.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Creaking object) {
		return new ResourceLocation(CTBackport.MODID, "textures/entity/creaking/creaking.png");
	}

}
