package com.natsu.backport.server.events;

import java.util.Set;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBVegetationFeatures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Adds the Spring to Life vegetation to the vanilla biomes, same lists as 1.21.5. */
@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class SpringVegetation {

	private static final Set<String> BUSH_BIOMES = Set.of(
			"birch_forest", "forest", "frozen_river", "old_growth_birch_forest", "plains",
			"river", "windswept_forest", "windswept_gravelly_hills", "windswept_hills");
	private static final Set<String> DRY_GRASS_BADLANDS_BIOMES = Set.of(
			"badlands", "eroded_badlands", "wooded_badlands");
	private static final Set<String> WILDFLOWERS_BIRCH_BIOMES = Set.of(
			"birch_forest", "old_growth_birch_forest");
	private static final Set<String> FIREFLY_NEAR_WATER_BIOMES = Set.of(
			"badlands", "bamboo_jungle", "beach", "birch_forest", "cold_ocean", "dark_forest",
			"deep_cold_ocean", "deep_frozen_ocean", "deep_lukewarm_ocean", "deep_ocean",
			"eroded_badlands", "flower_forest", "forest", "frozen_ocean", "frozen_river",
			"ice_spikes", "jungle", "lukewarm_ocean", "mushroom_fields", "ocean",
			"old_growth_birch_forest", "old_growth_pine_taiga", "old_growth_spruce_taiga",
			"plains", "river", "savanna", "savanna_plateau", "snowy_beach", "snowy_plains",
			"snowy_taiga", "sparse_jungle", "stony_shore", "sunflower_plains", "taiga",
			"warm_ocean", "windswept_forest", "windswept_gravelly_hills", "windswept_hills",
			"windswept_savanna", "wooded_badlands");

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onBiomeLoad(BiomeLoadingEvent event) {
		ResourceLocation name = event.getName();
		if (name == null) {
			return;
		}
		String path = name.getPath();
		boolean vanilla = name.getNamespace().equals("minecraft");
		boolean paleGarden = name.getNamespace().equals(CTBackport.MODID) && path.contains("pale_garden");

		if (vanilla && BUSH_BIOMES.contains(path)) {
			add(event, CTBVegetationFeatures.PATCH_BUSH);
		}
		if (vanilla && path.equals("desert")) {
			add(event, CTBVegetationFeatures.PATCH_DRY_GRASS_DESERT);
		}
		if (vanilla && DRY_GRASS_BADLANDS_BIOMES.contains(path)) {
			add(event, CTBVegetationFeatures.PATCH_DRY_GRASS_BADLANDS);
		}
		if (vanilla && (path.equals("dark_forest") || path.equals("forest"))) {
			add(event, CTBVegetationFeatures.PATCH_LEAF_LITTER);
		}
		if (vanilla && WILDFLOWERS_BIRCH_BIOMES.contains(path)) {
			add(event, CTBVegetationFeatures.WILDFLOWERS_BIRCH_FOREST);
		}
		if (vanilla && path.equals("meadow")) {
			add(event, CTBVegetationFeatures.WILDFLOWERS_MEADOW);
		}
		if ((vanilla && FIREFLY_NEAR_WATER_BIOMES.contains(path)) || paleGarden) {
			add(event, CTBVegetationFeatures.PATCH_FIREFLY_BUSH_NEAR_WATER);
		}
		if (vanilla && path.equals("swamp")) {
			add(event, CTBVegetationFeatures.PATCH_FIREFLY_BUSH_NEAR_WATER_SWAMP);
			add(event, CTBVegetationFeatures.PATCH_FIREFLY_BUSH_SWAMP);
		}
		if (vanilla) {
			for (net.minecraft.core.Holder<net.minecraft.world.level.levelgen.placement.PlacedFeature> fallen
					: com.natsu.backport.common.registry.CTBSpringExtras.FALLEN_BY_BIOME
							.getOrDefault(path, java.util.List.of())) {
				add(event, fallen);
			}
		}
		// archaeology digs near the desert pyramids and the ocean ruins
		if (vanilla && (path.equals("desert") || path.contains("ocean"))) {
			event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES,
					com.natsu.backport.common.registry.CTBSpringExtras.ARCHAEOLOGY_SITES);
		}
		// the dried ghasts sleeping in the soul sand valley
		if (vanilla && path.equals("soul_sand_valley")) {
			event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES,
					com.natsu.backport.common.registry.CTBSpringExtras.DRIED_GHAST_PATCH);
		}
		// the 1.21.5 cactus tops some columns with a flower : swap the vanilla patches for ours
		if (vanilla && CACTUS_BIOMES.contains(path)) {
			event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION)
					.removeIf(holder -> holder.unwrapKey()
							.map(key -> key.location().getNamespace().equals("minecraft")
									&& key.location().getPath().startsWith("patch_cactus"))
							.orElse(false));
			add(event, path.equals("desert")
					? com.natsu.backport.common.registry.CTBSpringExtras.PATCH_CACTUS_DESERT
					: com.natsu.backport.common.registry.CTBSpringExtras.PATCH_CACTUS_DECORATED);
		}
	}

	private static final Set<String> CACTUS_BIOMES = Set.of(
			"desert", "badlands", "eroded_badlands", "wooded_badlands");

	private static void add(BiomeLoadingEvent event, net.minecraft.core.Holder<net.minecraft.world.level.levelgen.placement.PlacedFeature> feature) {
		event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, feature);
	}
}
