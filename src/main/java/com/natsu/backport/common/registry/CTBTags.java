package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CTBTags {

	public static class Blocks {
		public static final TagKey<Block> SULFUR_SPIKE_REPLACEABLE = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CTBackport.MODID, "sulfur_spike_replaceable_blocks"));
		public static final TagKey<Block> PALE_OAK_LOGS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CTBackport.MODID, "pale_oak_logs"));
		public static final TagKey<Block> CHERRY_LOGS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CTBackport.MODID, "cherry_logs"));
		public static final TagKey<Block> BAMBOO_LOGS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CTBackport.MODID, "bamboo_logs"));
	}

	public static class Items {
		public static final java.util.Map<com.natsu.backport.common.entity.SulfurCubeArchetype, TagKey<net.minecraft.world.item.Item>> SULFUR_CUBE_ARCHETYPES = buildArchetypeTags();

		private static java.util.Map<com.natsu.backport.common.entity.SulfurCubeArchetype, TagKey<net.minecraft.world.item.Item>> buildArchetypeTags() {
			java.util.EnumMap<com.natsu.backport.common.entity.SulfurCubeArchetype, TagKey<net.minecraft.world.item.Item>> map =
					new java.util.EnumMap<>(com.natsu.backport.common.entity.SulfurCubeArchetype.class);
			for (com.natsu.backport.common.entity.SulfurCubeArchetype archetype : com.natsu.backport.common.entity.SulfurCubeArchetype.values()) {
				map.put(archetype, TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(CTBackport.MODID,
						"sulfur_cube_archetype_" + archetype.name().toLowerCase(java.util.Locale.ROOT))));
			}
			return map;
		}

		public static final TagKey<net.minecraft.world.item.Item> SULFUR_CUBE_FOOD =
				TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(CTBackport.MODID, "sulfur_cube_food"));
		public static final TagKey<net.minecraft.world.item.Item> NAUTILUS_FOOD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(CTBackport.MODID, "nautilus_food"));
		public static final TagKey<net.minecraft.world.item.Item> NAUTILUS_TAMING_ITEMS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(CTBackport.MODID, "nautilus_taming_items"));
		public static final TagKey<net.minecraft.world.item.Item> NAUTILUS_BUCKET_FOOD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(CTBackport.MODID, "nautilus_bucket_food"));
	}

}
