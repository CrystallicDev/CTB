package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.entity.model.CopperGolemModel;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

/** Fullbright pass with the per oxidation eyes texture. */
@OnlyIn(value = Dist.CLIENT)
public class CopperGolemGlowLayer extends AbstractLayerGeo<CopperGolem> {

	public CopperGolemGlowLayer(GeoEntityRenderer<CopperGolem> renderer) {
		super(renderer, renderer::getTextureLocation, e -> renderer.getGeoModelProvider().getModelLocation(e));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CopperGolem golem,
			float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
			float headPitch) {
		reRenderCurrentModelInRenderer(golem, partialTick, poseStack, bufferSource, packedLight,
				RenderType.eyes(CopperGolemModel.EYES[golem.getWeatherLevel()]));
	}
}
