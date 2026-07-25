package com.natsu.backport.client.render;

import com.natsu.backport.CTBackport;
import com.natsu.backport.client.ClientVariantCache;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

public class VariantPigRenderer extends PigRenderer {

	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation(CTBackport.MODID, "textures/entity/pig/temperate_pig.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/pig/warm_pig.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/pig/cold_pig.png"),
	};

	public VariantPigRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(Pig entity) {
		byte variant = ClientVariantCache.get(entity.getId());
		return TEXTURES[variant >= 0 && variant < 3 ? variant : 0];
	}
}
