package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Bogged;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class BoggedRenderer extends SkeletonRenderer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/bogged/bogged.png");
	private static final ResourceLocation OVERLAY = new ResourceLocation(CTBackport.MODID, "textures/entity/bogged/bogged_overlay.png");

	public BoggedRenderer(EntityRendererProvider.Context context) {
		super(context);
		// the mushroom coat, same inflated geometry as the stray overcoat
		SkeletonModel<AbstractSkeleton> overlayModel = new SkeletonModel<>(context.bakeLayer(ModelLayers.STRAY_OUTER_LAYER));
		this.addLayer(new RenderLayer<>(this) {
			@Override
			public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractSkeleton entity,
					float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
				if (entity instanceof Bogged bogged && bogged.isSheared()) {
					return;
				}
				coloredCutoutModelCopyLayerRender(this.getParentModel(), overlayModel, OVERLAY, poseStack, buffer, packedLight,
						entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, 1.0F, 1.0F, 1.0F);
			}
		});
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractSkeleton entity) {
		return TEXTURE;
	}
}
