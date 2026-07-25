package com.natsu.backport.client.render;

import com.natsu.backport.CTBackport;
import com.natsu.backport.client.ClientVariantCache;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

public class VariantCowRenderer extends CowRenderer {

	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation(CTBackport.MODID, "textures/entity/cow/temperate_cow.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/cow/warm_cow.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/cow/cold_cow.png"),
	};

	public VariantCowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(Cow entity) {
		byte variant = ClientVariantCache.get(entity.getId());
		return TEXTURES[variant >= 0 && variant < 3 ? variant : 0];
	}
}
