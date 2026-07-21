package com.natsu.backport.common.entity.model;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.AbstractNautilus;
import com.natsu.backport.common.entity.ZombieNautilus;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class NautilusModel extends AnimatedGeoModel<AbstractNautilus> {

	private static final ResourceLocation MODEL = new ResourceLocation(CTBackport.MODID, "geo/nautilus.geo.json");
	private static final ResourceLocation ANIMATION = new ResourceLocation(CTBackport.MODID, "animations/nautilus.animation.json");
	private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/nautilus.png");
	private static final ResourceLocation TEXTURE_ZOMBIE = new ResourceLocation(CTBackport.MODID, "textures/entity/nautilus_zombie.png");
	private static final ResourceLocation TEXTURE_ZOMBIE_CORAL = new ResourceLocation(CTBackport.MODID, "textures/entity/nautilus_zombie_coral.png");

	@Override
	public ResourceLocation getModelLocation(AbstractNautilus entity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractNautilus entity) {
		if (entity instanceof ZombieNautilus zombie) {
			return zombie.getVariant() == 1 ? TEXTURE_ZOMBIE_CORAL : TEXTURE_ZOMBIE;
		}
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(AbstractNautilus entity) {
		return ANIMATION;
	}
}
