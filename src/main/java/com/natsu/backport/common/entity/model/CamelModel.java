package com.natsu.backport.common.entity.model;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Camel;
import com.natsu.backport.common.entity.CamelHusk;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CamelModel extends AnimatedGeoModel<Camel> {

	private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/camel.geo.json");
	private static final ResourceLocation ANIMATION = new ResourceLocation(CTBackport.MODID, "animations/camel.animation.json");
	private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/camel.png");
	private static final ResourceLocation TEXTURE_BABY = new ResourceLocation(CTBackport.MODID, "textures/entity/camel_baby.png");
	private static final ResourceLocation TEXTURE_HUSK = new ResourceLocation(CTBackport.MODID, "textures/entity/camel_husk.png");

	@Override
	public ResourceLocation getModelLocation(Camel entity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(Camel entity) {
		if (entity instanceof CamelHusk) {
			return TEXTURE_HUSK;
		}
		return entity.isBaby() ? TEXTURE_BABY : TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Camel entity) {
		return ANIMATION;
	}
}
