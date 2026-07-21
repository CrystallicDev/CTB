package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CTBTags {

	public static class Blocks {
		public static final TagKey<Block> PALE_OAK_LOGS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CTBackport.MODID, "pale_oak_logs"));
		public static final TagKey<Block> CHERRY_LOGS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CTBackport.MODID, "cherry_logs"));
		public static final TagKey<Block> BAMBOO_LOGS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CTBackport.MODID, "bamboo_logs"));
	}

	public static class Items {
		public static final TagKey<net.minecraft.world.item.Item> NAUTILUS_FOOD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(CTBackport.MODID, "nautilus_food"));
		public static final TagKey<net.minecraft.world.item.Item> NAUTILUS_TAMING_ITEMS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(CTBackport.MODID, "nautilus_taming_items"));
		public static final TagKey<net.minecraft.world.item.Item> NAUTILUS_BUCKET_FOOD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(CTBackport.MODID, "nautilus_bucket_food"));
	}

}
