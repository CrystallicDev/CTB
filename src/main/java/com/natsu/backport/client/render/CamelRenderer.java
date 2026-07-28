package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Camel;
import com.natsu.backport.common.entity.CamelHusk;
import com.natsu.backport.common.entity.model.CamelModel;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

@OnlyIn(value = Dist.CLIENT)
public class CamelRenderer extends GeoEntityRenderer<Camel> {

	public CamelRenderer(EntityRendererProvider.Context context) {
		super(context, new CamelModel());
		this.shadowRadius = 0.7F;
		this.addLayer(new SaddleLayer(this));
	}

	/** The vanilla saddle pass : same model, the saddle texture painted over. */
	private static class SaddleLayer extends AbstractLayerGeo<Camel> {

		private static final ResourceLocation SADDLE =
				new ResourceLocation(CTBackport.MODID, "textures/entity/camel_saddle.png");
		private static final ResourceLocation SADDLE_HUSK =
				new ResourceLocation(CTBackport.MODID, "textures/entity/camel_husk_saddle.png");

		SaddleLayer(GeoEntityRenderer<Camel> renderer) {
			super(renderer, renderer::getTextureLocation, e -> renderer.getGeoModelProvider().getModelLocation(e));
		}

		@Override
		public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Camel camel,
				float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
				float headPitch) {
			if (camel.isInvisible() || !camel.isSaddled()) {
				return;
			}
			ResourceLocation texture = camel instanceof CamelHusk ? SADDLE_HUSK : SADDLE;
			reRenderCurrentModelInRenderer(camel, partialTick, poseStack, bufferSource, packedLight,
					RenderType.entityCutoutNoCull(texture));
		}
	}
}
