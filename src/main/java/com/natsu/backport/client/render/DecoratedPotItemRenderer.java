package com.natsu.backport.client.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

public class DecoratedPotItemRenderer extends BlockEntityWithoutLevelRenderer {

	public static final Lazy<DecoratedPotItemRenderer> INSTANCE = Lazy.of(DecoratedPotItemRenderer::new);

	private final DecoratedPotRenderer potRenderer = new DecoratedPotRenderer(null);

	private DecoratedPotItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		this.potRenderer.renderPot(poseStack, buffer, packedLight, packedOverlay, Direction.SOUTH, readSherds(stack));
	}

	private static List<Item> readSherds(ItemStack stack) {
		List<Item> sherds = new ArrayList<>();
		CompoundTag beTag = stack.getTagElement("BlockEntityTag");
		if (beTag != null && beTag.contains("sherds")) {
			for (Tag entry : beTag.getList("sherds", Tag.TAG_STRING)) {
				ResourceLocation id = ResourceLocation.tryParse(entry.getAsString());
				Item item = id != null ? ForgeRegistries.ITEMS.getValue(id) : null;
				sherds.add(item != null && item != Items.AIR ? item : Items.BRICK);
			}
		}
		return sherds;
	}
}
