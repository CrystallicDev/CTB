package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.client.ClientVariantCache;
import com.natsu.backport.server.events.AnimalVariants;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

/** The 1.21.5 pig : new 64x64 mesh for the variant textures, fluffy when cold. */
public class VariantPigRenderer extends PigRenderer {

	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation(CTBackport.MODID, "textures/entity/pig/temperate_pig.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/pig/warm_pig.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/pig/cold_pig.png"),
	};

	private final PigModel<Pig> normalModel = new PigModel<>(VariantAnimalModels.bakePig());
	private final PigModel<Pig> coldModel = new PigModel<>(VariantAnimalModels.bakeColdPig());

	public VariantPigRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(Pig entity, float entityYaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		this.model = ClientVariantCache.get(entity.getId()) == AnimalVariants.COLD ? this.coldModel : this.normalModel;
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(Pig entity) {
		byte variant = ClientVariantCache.get(entity.getId());
		return TEXTURES[variant >= 0 && variant < 3 ? variant : 0];
	}
}
