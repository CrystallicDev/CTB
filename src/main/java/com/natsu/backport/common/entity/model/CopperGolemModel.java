package com.natsu.backport.common.entity.model;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.CopperGolem;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CopperGolemModel extends AnimatedGeoModel<CopperGolem> {

	private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/copper_golem.geo.json");
	private static final ResourceLocation ANIMATION = new ResourceLocation(CTBackport.MODID, "animations/copper_golem.animation.json");
	public static final ResourceLocation[] TEXTURES = {
			new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_exposed.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_weathered.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_oxidized.png"),
	};
	public static final ResourceLocation[] EYES = {
			new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_eyes.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_eyes_exposed.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_eyes_weathered.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/copper_golem_eyes_oxidized.png"),
	};

	@Override
	public ResourceLocation getModelLocation(CopperGolem entity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(CopperGolem entity) {
		return TEXTURES[entity.getWeatherLevel()];
	}

	@Override
	public ResourceLocation getAnimationFileLocation(CopperGolem entity) {
		return ANIMATION;
	}
}
