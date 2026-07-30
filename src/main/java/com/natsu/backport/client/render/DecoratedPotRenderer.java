package com.natsu.backport.client.render;

import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.DecoratedPotBlock;
import com.natsu.backport.common.block.entity.DecoratedPotBlockEntity;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/** Straight port of the vanilla DecoratedPotRenderer, same boxes, same poses. */
public class DecoratedPotRenderer implements BlockEntityRenderer<DecoratedPotBlockEntity> {

	private static final ResourceLocation BASE = texture("decorated_pot_base");
	private static final ResourceLocation SIDE = texture("decorated_pot_side");
	private static final Map<Item, ResourceLocation> PATTERNS = Map.ofEntries(
			Map.entry(CTBItems.ANGLER_POTTERY_SHERD.get(), texture("angler_pottery_pattern")),
			Map.entry(CTBItems.ARCHER_POTTERY_SHERD.get(), texture("archer_pottery_pattern")),
			Map.entry(CTBItems.ARMS_UP_POTTERY_SHERD.get(), texture("arms_up_pottery_pattern")),
			Map.entry(CTBItems.BLADE_POTTERY_SHERD.get(), texture("blade_pottery_pattern")),
			Map.entry(CTBItems.BREWER_POTTERY_SHERD.get(), texture("brewer_pottery_pattern")),
			Map.entry(CTBItems.BURN_POTTERY_SHERD.get(), texture("burn_pottery_pattern")),
			Map.entry(CTBItems.DANGER_POTTERY_SHERD.get(), texture("danger_pottery_pattern")),
			Map.entry(CTBItems.EXPLORER_POTTERY_SHERD.get(), texture("explorer_pottery_pattern")),
			Map.entry(CTBItems.FRIEND_POTTERY_SHERD.get(), texture("friend_pottery_pattern")),
			Map.entry(CTBItems.HEART_POTTERY_SHERD.get(), texture("heart_pottery_pattern")),
			Map.entry(CTBItems.HEARTBREAK_POTTERY_SHERD.get(), texture("heartbreak_pottery_pattern")),
			Map.entry(CTBItems.HOWL_POTTERY_SHERD.get(), texture("howl_pottery_pattern")),
			Map.entry(CTBItems.MINER_POTTERY_SHERD.get(), texture("miner_pottery_pattern")),
			Map.entry(CTBItems.MOURNER_POTTERY_SHERD.get(), texture("mourner_pottery_pattern")),
			Map.entry(CTBItems.PLENTY_POTTERY_SHERD.get(), texture("plenty_pottery_pattern")),
			Map.entry(CTBItems.PRIZE_POTTERY_SHERD.get(), texture("prize_pottery_pattern")),
			Map.entry(CTBItems.SHEAF_POTTERY_SHERD.get(), texture("sheaf_pottery_pattern")),
			Map.entry(CTBItems.SHELTER_POTTERY_SHERD.get(), texture("shelter_pottery_pattern")),
			Map.entry(CTBItems.SKULL_POTTERY_SHERD.get(), texture("skull_pottery_pattern")),
			Map.entry(CTBItems.SNORT_POTTERY_SHERD.get(), texture("snort_pottery_pattern")),
			Map.entry(CTBItems.FLOW_POTTERY_SHERD.get(), texture("flow_pottery_pattern")),
			Map.entry(CTBItems.GUSTER_POTTERY_SHERD.get(), texture("guster_pottery_pattern")),
			Map.entry(CTBItems.SCRAPE_POTTERY_SHERD.get(), texture("scrape_pottery_pattern")));

	private final ModelPart neck;
	private final ModelPart top;
	private final ModelPart bottom;
	private final ModelPart frontSide;
	private final ModelPart backSide;
	private final ModelPart leftSide;
	private final ModelPart rightSide;

	private static ResourceLocation texture(String name) {
		return new ResourceLocation(CTBackport.MODID, "textures/entity/decorated_pot/" + name + ".png");
	}

	public DecoratedPotRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart base = createBaseLayer().bakeRoot();
		this.neck = base.getChild("neck");
		this.top = base.getChild("top");
		this.bottom = base.getChild("bottom");
		ModelPart sides = createSidesLayer().bakeRoot();
		this.frontSide = sides.getChild("front");
		this.backSide = sides.getChild("back");
		this.leftSide = sides.getChild("left");
		this.rightSide = sides.getChild("right");
	}

	public static LayerDefinition createBaseLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		CubeDeformation grow = new CubeDeformation(0.2F);
		CubeDeformation shrink = new CubeDeformation(-0.1F);
		root.addOrReplaceChild("neck", CubeListBuilder.create()
					.texOffs(0, 0).addBox(4.0F, 17.0F, 4.0F, 8.0F, 3.0F, 8.0F, shrink)
					.texOffs(0, 5).addBox(5.0F, 20.0F, 5.0F, 6.0F, 1.0F, 6.0F, grow),
				PartPose.offsetAndRotation(0.0F, 37.0F, 16.0F, (float) Math.PI, 0.0F, 0.0F));
		CubeListBuilder lid = CubeListBuilder.create().texOffs(-14, 13).addBox(0.0F, 0.0F, 0.0F, 14.0F, 0.0F, 14.0F);
		root.addOrReplaceChild("top", lid, PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0F, 0.0F, 0.0F));
		root.addOrReplaceChild("bottom", lid, PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 32, 32);
	}

	public static LayerDefinition createSidesLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		// 1.18.2 has no per face visibility, the inward duplicate of the flat box gets culled anyway
		CubeListBuilder side = CubeListBuilder.create().texOffs(1, 0).addBox(0.0F, 0.0F, 0.0F, 14.0F, 16.0F, 0.0F);
		root.addOrReplaceChild("back", side, PartPose.offsetAndRotation(15.0F, 16.0F, 1.0F, 0.0F, 0.0F, (float) Math.PI));
		root.addOrReplaceChild("left", side, PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0F, (float) (-Math.PI / 2), (float) Math.PI));
		root.addOrReplaceChild("right", side, PartPose.offsetAndRotation(15.0F, 16.0F, 15.0F, 0.0F, (float) (Math.PI / 2), (float) Math.PI));
		root.addOrReplaceChild("front", side, PartPose.offsetAndRotation(1.0F, 16.0F, 15.0F, (float) Math.PI, 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 16, 16);
	}

	@Override
	public void render(DecoratedPotBlockEntity pot, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Direction direction = pot.getBlockState().getValue(DecoratedPotBlock.FACING);
		renderPot(poseStack, buffer, packedLight, packedOverlay, direction, pot.getSherds());
	}

	/** Shared with the item renderer. Sherd order is back, left, right, front. */
	public void renderPot(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, Direction direction, List<Item> sherds) {
		poseStack.pushPose();
		poseStack.translate(0.5, 0.0, 0.5);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - direction.toYRot()));
		poseStack.translate(-0.5, 0.0, -0.5);

		VertexConsumer base = buffer.getBuffer(RenderType.entitySolid(BASE));
		this.neck.render(poseStack, base, packedLight, packedOverlay);
		this.top.render(poseStack, base, packedLight, packedOverlay);
		this.bottom.render(poseStack, base, packedLight, packedOverlay);

		renderSide(this.backSide, poseStack, buffer, packedLight, packedOverlay, sherd(sherds, 0));
		renderSide(this.leftSide, poseStack, buffer, packedLight, packedOverlay, sherd(sherds, 1));
		renderSide(this.rightSide, poseStack, buffer, packedLight, packedOverlay, sherd(sherds, 2));
		renderSide(this.frontSide, poseStack, buffer, packedLight, packedOverlay, sherd(sherds, 3));
		poseStack.popPose();
	}

	private static Item sherd(List<Item> sherds, int index) {
		return index < sherds.size() ? sherds.get(index) : net.minecraft.world.item.Items.BRICK;
	}

	private static void renderSide(ModelPart part, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, Item sherd) {
		ResourceLocation texture = PATTERNS.getOrDefault(sherd, SIDE);
		part.render(poseStack, buffer.getBuffer(RenderType.entitySolid(texture)), light, overlay);
	}
}
