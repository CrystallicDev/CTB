package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.natsu.backport.common.entity.OminousItemSpawner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class OminousItemSpawnerRenderer extends EntityRenderer<OminousItemSpawner> {

	public OminousItemSpawnerRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(OminousItemSpawner entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		ItemStack item = entity.getItem();
		if (item.isEmpty()) {
			return;
		}

		poseStack.pushPose();
		float bob = (float) Math.sin((entity.tickCount + partialTick) / 10.0F) * 0.05F;
		poseStack.translate(0.0, 0.1 + bob, 0.0);
		poseStack.mulPose(Vector3f.YP.rotationDegrees((entity.tickCount + partialTick) * 4.0F));
		Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemTransforms.TransformType.GROUND,
				packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.getId());
		poseStack.popPose();
		super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(OminousItemSpawner entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
