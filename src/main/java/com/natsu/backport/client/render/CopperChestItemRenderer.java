package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.util.Lazy;

/** Renders the copper chest items with the chest model and their stage texture. */
public class CopperChestItemRenderer extends BlockEntityWithoutLevelRenderer {

	public static final Lazy<CopperChestItemRenderer> INSTANCE = Lazy.of(CopperChestItemRenderer::new);

	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;

	private CopperChestItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		ModelPart root = ChestRenderer.createSingleBodyLayer().bakeRoot();
		this.bottom = root.getChild("bottom");
		this.lid = root.getChild("lid");
		this.lock = root.getChild("lock");
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (!(stack.getItem() instanceof BlockItem blockItem)) {
			return;
		}
		poseStack.pushPose();
		// same orientation as a placed chest facing south
		poseStack.translate(0.5, 0.5, 0.5);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		poseStack.translate(-0.5, -0.5, -0.5);
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(
				CopperChestRenderer.getChestTexture(blockItem.getBlock(), ChestType.SINGLE)));
		this.lid.xRot = 0.0F;
		this.lock.xRot = 0.0F;
		this.lid.render(poseStack, consumer, packedLight, packedOverlay);
		this.lock.render(poseStack, consumer, packedLight, packedOverlay);
		this.bottom.render(poseStack, consumer, packedLight, packedOverlay);
		poseStack.popPose();
	}
}
