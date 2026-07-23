package com.natsu.backport.client.render;

import com.natsu.backport.CTBackport;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class ParchedRenderer extends SkeletonRenderer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/parched/parched.png");

	public ParchedRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
		return TEXTURE;
	}
}
