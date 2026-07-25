package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.client.ClientVariantCache;
import com.natsu.backport.server.events.AnimalVariants;

import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

/** The 1.21.5 cow : new 64x64 mesh for the variant textures, fluffy when cold. */
public class VariantCowRenderer extends CowRenderer {

	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation(CTBackport.MODID, "textures/entity/cow/temperate_cow.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/cow/warm_cow.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/cow/cold_cow.png"),
	};

	private final CowModel<Cow> normalModel = new CowModel<>(VariantAnimalModels.bakeCow());
	private final CowModel<Cow> coldModel = new CowModel<>(VariantAnimalModels.bakeColdCow());

	public VariantCowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(Cow entity, float entityYaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		this.model = ClientVariantCache.get(entity.getId()) == AnimalVariants.COLD ? this.coldModel : this.normalModel;
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(Cow entity) {
		byte variant = ClientVariantCache.get(entity.getId());
		return TEXTURES[variant >= 0 && variant < 3 ? variant : 0];
	}
}
