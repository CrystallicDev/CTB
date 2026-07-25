package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CopperChestBlock;
import com.natsu.backport.common.block.entity.CopperChestBlockEntity;
import com.natsu.backport.common.registry.CTBBlocks;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

/** The vanilla ChestRenderer with a texture per oxidation stage. */
public class CopperChestRenderer implements BlockEntityRenderer<CopperChestBlockEntity> {

	private final ModelPart lid;
	private final ModelPart bottom;
	private final ModelPart lock;
	private final ModelPart doubleLeftLid;
	private final ModelPart doubleLeftBottom;
	private final ModelPart doubleLeftLock;
	private final ModelPart doubleRightLid;
	private final ModelPart doubleRightBottom;
	private final ModelPart doubleRightLock;

	public CopperChestRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart single = context.bakeLayer(ModelLayers.CHEST);
		this.bottom = single.getChild("bottom");
		this.lid = single.getChild("lid");
		this.lock = single.getChild("lock");
		ModelPart left = context.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
		this.doubleLeftBottom = left.getChild("bottom");
		this.doubleLeftLid = left.getChild("lid");
		this.doubleLeftLock = left.getChild("lock");
		ModelPart right = context.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
		this.doubleRightBottom = right.getChild("bottom");
		this.doubleRightLid = right.getChild("lid");
		this.doubleRightLock = right.getChild("lock");
	}

	public static ResourceLocation getChestTexture(Block block, ChestType type) {
		WeatheringCopper.WeatherState stage = block instanceof CopperChestBlock chest
				? chest.getWeatherState() : WeatheringCopper.WeatherState.UNAFFECTED;
		String base = switch (stage) {
			case UNAFFECTED -> "copper";
			case EXPOSED -> "copper_exposed";
			case WEATHERED -> "copper_weathered";
			case OXIDIZED -> "copper_oxidized";
		};
		String suffix = switch (type) {
			case LEFT -> "_left";
			case RIGHT -> "_right";
			default -> "";
		};
		return new ResourceLocation(CTBackport.MODID, "textures/entity/chest/" + base + suffix + ".png");
	}

	@Override
	public void render(CopperChestBlockEntity chest, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
			int packedLight, int packedOverlay) {
		Level level = chest.getLevel();
		boolean inLevel = level != null;
		BlockState state = inLevel ? chest.getBlockState()
				: CTBBlocks.COPPER_CHEST.get().defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
		ChestType type = state.hasProperty(ChestBlock.TYPE) ? state.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
		if (!(state.getBlock() instanceof CopperChestBlock block)) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(0.5, 0.5, 0.5);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(ChestBlock.FACING).toYRot()));
		poseStack.translate(-0.5, -0.5, -0.5);

		DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combined = inLevel
				? block.combine(state, level, chest.getBlockPos(), true)
				: DoubleBlockCombiner.Combiner::acceptNone;
		float openness = combined.<Float2FloatFunction>apply(ChestBlock.opennessCombiner(chest)).get(partialTick);
		openness = 1.0F - openness;
		openness = 1.0F - openness * openness * openness;
		int light = combined.<Int2IntFunction>apply(new BrightnessCombiner<>()).applyAsInt(packedLight);

		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(getChestTexture(block, type)));
		if (type == ChestType.LEFT) {
			this.renderParts(poseStack, consumer, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, openness, light, packedOverlay);
		} else if (type == ChestType.RIGHT) {
			this.renderParts(poseStack, consumer, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, openness, light, packedOverlay);
		} else {
			this.renderParts(poseStack, consumer, this.lid, this.lock, this.bottom, openness, light, packedOverlay);
		}
		poseStack.popPose();
	}

	private void renderParts(PoseStack poseStack, VertexConsumer consumer, ModelPart lid, ModelPart lock,
			ModelPart bottom, float openness, int light, int overlay) {
		lid.xRot = -(openness * ((float) Math.PI / 2F));
		lock.xRot = lid.xRot;
		lid.render(poseStack, consumer, light, overlay);
		lock.render(poseStack, consumer, light, overlay);
		bottom.render(poseStack, consumer, light, overlay);
	}
}
