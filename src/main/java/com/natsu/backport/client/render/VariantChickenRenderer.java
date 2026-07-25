package com.natsu.backport.client.render;

import com.natsu.backport.CTBackport;
import com.natsu.backport.client.ClientVariantCache;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;

public class VariantChickenRenderer extends ChickenRenderer {

	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation(CTBackport.MODID, "textures/entity/chicken/temperate_chicken.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/chicken/warm_chicken.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/chicken/cold_chicken.png"),
	};

	public VariantChickenRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(Chicken entity) {
		byte variant = ClientVariantCache.get(entity.getId());
		return TEXTURES[variant >= 0 && variant < 3 ? variant : 0];
	}
}
