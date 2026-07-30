package com.natsu.backport.common.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.natsu.backport.common.worldgen.FallenTreeFeature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

/**
 * The rest of Spring to Life : fallen trees and the flowered cactus. Vanilla
 * folds the fallen trees into each biome's tree selector ; here they are
 * standalone placed features whose rarity equals the vanilla selector chance
 * times the 1.18.2 tree count of the biome.
 */
public final class CTBSpringExtras {

	private enum FallenTree {
		OAK(Blocks.OAK_LOG, 4, 7, true, Blocks.OAK_SAPLING),
		BIRCH(Blocks.BIRCH_LOG, 5, 8, false, Blocks.BIRCH_SAPLING),
		SUPER_BIRCH(Blocks.BIRCH_LOG, 5, 15, false, Blocks.BIRCH_SAPLING),
		SPRUCE(Blocks.SPRUCE_LOG, 6, 10, false, Blocks.SPRUCE_SAPLING),
		JUNGLE(Blocks.JUNGLE_LOG, 4, 11, true, Blocks.JUNGLE_SAPLING);

		final Block log;
		final int minLength;
		final int maxLength;
		final boolean vines;
		final Block sapling;

		FallenTree(Block log, int minLength, int maxLength, boolean vines, Block sapling) {
			this.log = log;
			this.minLength = minLength;
			this.maxLength = maxLength;
			this.vines = vines;
			this.sapling = sapling;
		}
	}

	private record Placement(String biome, FallenTree type, int rarity) {
	}

	// vanilla selector chance x the 1.18.2 per chunk tree count of the biome
	private static final List<Placement> TABLE = List.of(
			new Placement("plains", FallenTree.OAK, 1600),
			new Placement("sunflower_plains", FallenTree.OAK, 1600),
			new Placement("forest", FallenTree.OAK, 8),
			new Placement("forest", FallenTree.BIRCH, 40),
			new Placement("flower_forest", FallenTree.BIRCH, 190),
			new Placement("birch_forest", FallenTree.BIRCH, 8),
			new Placement("old_growth_birch_forest", FallenTree.BIRCH, 8),
			new Placement("old_growth_birch_forest", FallenTree.SUPER_BIRCH, 16),
			new Placement("taiga", FallenTree.SPRUCE, 8),
			new Placement("old_growth_pine_taiga", FallenTree.SPRUCE, 8),
			new Placement("old_growth_spruce_taiga", FallenTree.SPRUCE, 8),
			new Placement("snowy_plains", FallenTree.SPRUCE, 800),
			new Placement("snowy_taiga", FallenTree.SPRUCE, 800),
			new Placement("jungle", FallenTree.JUNGLE, 2),
			new Placement("sparse_jungle", FallenTree.JUNGLE, 38),
			new Placement("savanna", FallenTree.OAK, 73),
			new Placement("savanna_plateau", FallenTree.OAK, 73),
			new Placement("windswept_hills", FallenTree.SPRUCE, 1200),
			new Placement("windswept_hills", FallenTree.OAK, 800),
			new Placement("windswept_gravelly_hills", FallenTree.SPRUCE, 1200),
			new Placement("windswept_gravelly_hills", FallenTree.OAK, 800),
			new Placement("windswept_forest", FallenTree.SPRUCE, 39),
			new Placement("windswept_forest", FallenTree.OAK, 26),
			new Placement("wooded_badlands", FallenTree.OAK, 16),
			new Placement("dark_forest", FallenTree.OAK, 5),
			new Placement("dark_forest", FallenTree.BIRCH, 25));

	/** Biome path to the fallen tree placed features it gets, filled by register(). */
	public static final Map<String, List<Holder<PlacedFeature>>> FALLEN_BY_BIOME = new HashMap<>();

	public static Holder<PlacedFeature> ARCHAEOLOGY_SITES;
	public static Holder<PlacedFeature> DRIED_GHAST_PATCH;
	public static Holder<PlacedFeature> PATCH_CACTUS_DESERT;
	public static Holder<PlacedFeature> PATCH_CACTUS_DECORATED;

	private CTBSpringExtras() {
	}

	public static void register() {
		Map<FallenTree, Holder<ConfiguredFeature<FallenTreeFeature.Config, ?>>> configured = new HashMap<>();
		Map<String, Holder<PlacedFeature>> placed = new HashMap<>();
		for (Placement entry : TABLE) {
			String key = entry.type.name().toLowerCase() + "_" + entry.rarity;
			Holder<PlacedFeature> feature = placed.computeIfAbsent(key, k -> {
				Holder<ConfiguredFeature<FallenTreeFeature.Config, ?>> tree = configured.computeIfAbsent(
						entry.type, t -> FeatureUtils.register(
								"ctbackport:fallen_" + t.name().toLowerCase() + "_tree",
								CTBFeatures.FALLEN_TREE.get(),
								new FallenTreeFeature.Config(t.log.defaultBlockState(), t.minLength,
										t.maxLength, t.vines, 0.1F)));
				return PlacementUtils.register("ctbackport:fallen_" + k + "_tree", tree,
						RarityFilter.onAverageOnceEvery(entry.rarity), InSquarePlacement.spread(),
						SurfaceWaterDepthFilter.forMaxDepth(0),
						HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR),
						BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(
								entry.type.sapling.defaultBlockState(), BlockPos.ZERO)),
						BiomeFilter.biome());
			});
			FALLEN_BY_BIOME.computeIfAbsent(entry.biome, b -> new java.util.ArrayList<>()).add(feature);
		}

		ARCHAEOLOGY_SITES = PlacementUtils.register("ctbackport:archaeology_sites",
				FeatureUtils.register("ctbackport:archaeology_sites",
						CTBFeatures.ARCHAEOLOGY_SITE.get(),
						net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.INSTANCE));
		DRIED_GHAST_PATCH = PlacementUtils.register("ctbackport:dried_ghast",
				FeatureUtils.register("ctbackport:dried_ghast",
						CTBFeatures.DRIED_GHAST_FEATURE.get(),
						net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.INSTANCE),
				RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), BiomeFilter.biome());

		// the cherry grove petal carpet, all facings and amounts evenly mixed
		SimpleWeightedRandomList.Builder<BlockState> petals = SimpleWeightedRandomList.builder();
		for (int amount = 1; amount <= 4; amount++) {
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				petals.add(CTBBlocks.PINK_PETALS.get().defaultBlockState()
						.setValue(com.natsu.backport.common.block.FlowerBedBlock.FACING, direction)
						.setValue(com.natsu.backport.common.block.FlowerBedBlock.AMOUNT, amount), 1);
			}
		}
		PlacementUtils.register("ctbackport:flower_cherry",
				FeatureUtils.register("ctbackport:flower_cherry", Feature.SIMPLE_BLOCK,
						new net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration(
								new net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider(petals.build()))),
				net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement.of(-0.8, 5, 10),
				InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING),
				BiomeFilter.biome());

		// the 1.21.5 cactus column : one to three cactus, one in four topped by a flower
		BlockState cactus = Blocks.CACTUS.defaultBlockState();
		Holder<PlacedFeature> column = PlacementUtils.register("ctbackport:cactus_column",
				FeatureUtils.register("ctbackport:cactus_column", Feature.BLOCK_COLUMN,
						new BlockColumnConfiguration(List.of(
								BlockColumnConfiguration.layer(BiasedToBottomInt.of(1, 3),
										BlockStateProvider.simple(cactus)),
								BlockColumnConfiguration.layer(
										new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder()
												.add(ConstantInt.of(0), 3).add(ConstantInt.of(1), 1).build()),
										BlockStateProvider.simple(CTBBlocks.CACTUS_FLOWER.get()))),
								Direction.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE, false)),
				BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE,
						BlockPredicate.wouldSurvive(cactus, BlockPos.ZERO))));
		Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> patch = FeatureUtils.register(
				"ctbackport:patch_cactus", Feature.RANDOM_PATCH, new RandomPatchConfiguration(10, 7, 3, column));
		PATCH_CACTUS_DESERT = PlacementUtils.register("ctbackport:patch_cactus_desert", patch,
				RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome());
		PATCH_CACTUS_DECORATED = PlacementUtils.register("ctbackport:patch_cactus_decorated", patch,
				RarityFilter.onAverageOnceEvery(13), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome());
	}
}
