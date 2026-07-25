package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.client.ClientVariantCache;
import com.natsu.backport.server.events.AnimalVariants;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;

/** The vanilla chicken with the climate textures, crested when cold. */
public class VariantChickenRenderer extends ChickenRenderer {

	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation(CTBackport.MODID, "textures/entity/chicken/temperate_chicken.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/chicken/warm_chicken.png"),
			new ResourceLocation(CTBackport.MODID, "textures/entity/chicken/cold_chicken.png"),
	};

	private final ChickenModel<Chicken> normalModel;
	private final ChickenModel<Chicken> coldModel = new ChickenModel<>(VariantAnimalModels.bakeColdChicken());

	public VariantChickenRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.normalModel = new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN));
	}

	@Override
	public void render(Chicken entity, float entityYaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		this.model = ClientVariantCache.get(entity.getId()) == AnimalVariants.COLD ? this.coldModel : this.normalModel;
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(Chicken entity) {
		byte variant = ClientVariantCache.get(entity.getId());
		return TEXTURES[variant >= 0 && variant < 3 ? variant : 0];
	}
}
