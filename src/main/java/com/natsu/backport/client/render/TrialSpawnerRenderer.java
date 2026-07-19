package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.natsu.backport.common.block.entity.TrialSpawnerBlockEntity;
import com.natsu.backport.common.block.entity.trialspawner.TrialSpawner;
import com.natsu.backport.common.block.entity.trialspawner.TrialSpawnerStateData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {

	public TrialSpawnerRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(TrialSpawnerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (blockEntity.getLevel() == null) {
			return;
		}

		TrialSpawner spawner = blockEntity.getTrialSpawner();
		TrialSpawnerStateData data = spawner.getStateData();
		Entity entity = data.getOrCreateDisplayEntity(spawner, blockEntity.getLevel(), blockEntity.getState());
		if (entity == null) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(0.5, 0.0, 0.5);
		float scale = 0.53125F;
		float maxSize = Math.max(entity.getBbWidth(), entity.getBbHeight());
		if (maxSize > 1.0) {
			scale /= maxSize;
		}

		poseStack.translate(0.0, 0.4F, 0.0);
		poseStack.mulPose(Vector3f.YP.rotationDegrees((float) Mth.lerp(partialTick, data.getOSpin(), data.getSpin()) * 10.0F));
		poseStack.translate(0.0, -0.2F, 0.0);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(-30.0F));
		poseStack.scale(scale, scale, scale);
		Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0.0, 0.0, 0.0, 0.0F, partialTick, poseStack, buffer, packedLight);
		poseStack.popPose();
	}
}
