package com.natsu.backport.client.render;

import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.DecoratedPotBlock;
import com.natsu.backport.common.block.entity.DecoratedPotBlockEntity;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/** The base pot is a regular block model, this only overlays the sherd patterns. */
public class DecoratedPotRenderer implements BlockEntityRenderer<DecoratedPotBlockEntity> {

	private static final Map<Item, ResourceLocation> PATTERNS = Map.of(
			CTBItems.FLOW_POTTERY_SHERD.get(), pattern("flow_pottery_pattern"),
			CTBItems.GUSTER_POTTERY_SHERD.get(), pattern("guster_pottery_pattern"),
			CTBItems.SCRAPE_POTTERY_SHERD.get(), pattern("scrape_pottery_pattern"));

	private static ResourceLocation pattern(String name) {
		return new ResourceLocation(CTBackport.MODID, "textures/entity/decorated_pot/" + name + ".png");
	}

	public DecoratedPotRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(DecoratedPotBlockEntity pot, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		List<Item> sherds = pot.getSherds();
		if (sherds.stream().noneMatch(PATTERNS::containsKey)) {
			return;
		}

		Direction facing = pot.getBlockState().getValue(DecoratedPotBlock.FACING);
		// nbt order is back, left, right, front
		Direction[] sides = {facing.getOpposite(), facing.getCounterClockWise(), facing.getClockWise(), facing};
		for (int i = 0; i < Math.min(4, sherds.size()); i++) {
			ResourceLocation texture = PATTERNS.get(sherds.get(i));
			if (texture != null) {
				drawFace(poseStack, buffer.getBuffer(RenderType.entityCutout(texture)), sides[i], packedLight, packedOverlay);
			}
		}
	}

	private static void drawFace(PoseStack poseStack, VertexConsumer consumer, Direction side, int light, int overlay) {
		float min = 1.0F / 16.0F - 0.004F;
		float max = 15.0F / 16.0F + 0.004F;
		float top = 1.0F + 0.004F;
		float bottom = -0.004F;

		poseStack.pushPose();
		Matrix4f pose = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		float nx = side.getStepX();
		float nz = side.getStepZ();

		// quad corners, counter clockwise seen from outside
		float[][] corners = switch (side) {
			case NORTH -> new float[][] {{max, bottom, min}, {min, bottom, min}, {min, top, min}, {max, top, min}};
			case SOUTH -> new float[][] {{min, bottom, max}, {max, bottom, max}, {max, top, max}, {min, top, max}};
			case WEST -> new float[][] {{min, bottom, min}, {min, bottom, max}, {min, top, max}, {min, top, min}};
			default -> new float[][] {{max, bottom, max}, {max, bottom, min}, {max, top, min}, {max, top, max}};
		};
		// vanilla maps the 14 wide face onto texture columns 1..15
		float u0 = 1.0F / 16.0F;
		float u1 = 15.0F / 16.0F;
		float[][] uvs = {{u1, 1.0F}, {u0, 1.0F}, {u0, 0.0F}, {u1, 0.0F}};

		for (int i = 0; i < 4; i++) {
			consumer.vertex(pose, corners[i][0], corners[i][1], corners[i][2])
					.color(255, 255, 255, 255)
					.uv(uvs[i][0], uvs[i][1])
					.overlayCoords(overlay)
					.uv2(light)
					.normal(normal, nx, 0.0F, nz)
					.endVertex();
		}
		poseStack.popPose();
	}
}
