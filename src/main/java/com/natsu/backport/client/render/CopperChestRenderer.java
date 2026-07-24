package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CopperChestBlock;
import com.natsu.backport.common.block.entity.CopperChestBlockEntity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CopperChestRenderer implements BlockEntityRenderer<CopperChestBlockEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(CTBackport.MODID, "textures/entity/chest/copper.png");

	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;

	public CopperChestRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart root = context.bakeLayer(ModelLayers.CHEST);
		this.bottom = root.getChild("bottom");
		this.lid = root.getChild("lid");
		this.lock = root.getChild("lock");
	}

	@Override
	public void render(CopperChestBlockEntity chest, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Direction facing = chest.getBlockState().hasProperty(CopperChestBlock.FACING)
				? chest.getBlockState().getValue(CopperChestBlock.FACING) : Direction.SOUTH;

		poseStack.pushPose();
		poseStack.translate(0.5, 0.5, 0.5);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
		poseStack.translate(-0.5, -0.5, -0.5);

		float openness = chest.getOpenNess(partialTick);
		openness = 1.0F - openness;
		openness = 1.0F - openness * openness * openness;
		float lidAngle = -(openness * ((float) Math.PI / 2F));

		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
		this.lid.xRot = lidAngle;
		this.lock.xRot = lidAngle;
		this.lid.render(poseStack, consumer, packedLight, packedOverlay);
		this.lock.render(poseStack, consumer, packedLight, packedOverlay);
		this.bottom.render(poseStack, consumer, packedLight, packedOverlay);
		poseStack.popPose();
	}
}
