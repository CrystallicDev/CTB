package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Creaking;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

/**
 * Fullbright pass over the model with an explicit glow texture (eyes and mouth
 * cracks only, everything else transparent). Simpler and safer than geckolib's
 * runtime-generated glowmasks.
 */
@OnlyIn(value = Dist.CLIENT)
public class CreakingGlowLayer extends AbstractLayerGeo<Creaking> {

	private static final ResourceLocation GLOW_TEXTURE =
			new ResourceLocation(CTBackport.MODID, "textures/entity/creaking/creaking_glow.png");

	public CreakingGlowLayer(GeoEntityRenderer<Creaking> renderer) {
		super(renderer, renderer::getTextureLocation, e -> renderer.getGeoModelProvider().getModelLocation(e));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Creaking creaking,
			float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
			float headPitch) {
		reRenderCurrentModelInRenderer(creaking, partialTick, poseStack, bufferSource, packedLight,
				RenderType.eyes(GLOW_TEXTURE));
	}
}
