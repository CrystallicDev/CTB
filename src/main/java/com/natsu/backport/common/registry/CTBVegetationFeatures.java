package com.natsu.backport.common.registry;

import java.util.List;

import com.natsu.backport.common.block.FlowerBedBlock;
import com.natsu.backport.common.block.LeafLitterBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.material.Fluids;

/**
 * The 1.21.5 vegetation features rebuilt in code, same configs and placements
 * as the vanilla worldgen JSONs. Registered at common setup, wired into the
 * vanilla biomes by SpringVegetation.
 */
public final class CTBVegetationFeatures {

	public static Holder<PlacedFeature> PATCH_BUSH;
	public static Holder<PlacedFeature> PATCH_DRY_GRASS_DESERT;
	public static Holder<PlacedFeature> PATCH_DRY_GRASS_BADLANDS;
	public static Holder<PlacedFeature> PATCH_LEAF_LITTER;
	public static Holder<PlacedFeature> WILDFLOWERS_BIRCH_FOREST;
	public static Holder<PlacedFeature> WILDFLOWERS_MEADOW;
	public static Holder<PlacedFeature> PATCH_FIREFLY_BUSH_NEAR_WATER;
	public static Holder<PlacedFeature> PATCH_FIREFLY_BUSH_NEAR_WATER_SWAMP;
	public static Holder<PlacedFeature> PATCH_FIREFLY_BUSH_SWAMP;

	private CTBVegetationFeatures() {
	}

	public static void register() {
		Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> bush = patch("patch_bush", 24, 5, 3,
				BlockStateProvider.simple(CTBBlocks.BUSH.get()));
		Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> dryGrass = patch("patch_dry_grass", 64, 7, 3,
				new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
						.add(CTBBlocks.SHORT_DRY_GRASS.get().defaultBlockState(), 1)
						.add(CTBBlocks.TALL_DRY_GRASS.get().defaultBlockState(), 1)
						.build()));
		Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> leafLitter = patch("patch_leaf_litter", 32, 7, 3,
				segmented(CTBBlocks.LEAF_LITTER.get(), LeafLitterBlock.FACING, LeafLitterBlock.AMOUNT, 3));
		Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> wildflowersBirch = patch("wildflowers_birch_forest", 64, 6, 2,
				segmented(CTBBlocks.WILDFLOWERS.get(), FlowerBedBlock.FACING, FlowerBedBlock.AMOUNT, 4));
		Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> wildflowersMeadow = patch("wildflowers_meadow", 8, 6, 2,
				segmented(CTBBlocks.WILDFLOWERS.get(), FlowerBedBlock.FACING, FlowerBedBlock.AMOUNT, 4));
		Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> fireflyBush = patch("patch_firefly_bush", 20, 4, 3,
				BlockStateProvider.simple(CTBBlocks.FIREFLY_BUSH.get()));

		// the vanilla sugar cane style water adjacency test, under the feet
		BlockPredicate nearWater = BlockPredicate.anyOf(
				BlockPredicate.matchesFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(1, -1, 0)),
				BlockPredicate.matchesFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(-1, -1, 0)),
				BlockPredicate.matchesFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(0, -1, 1)),
				BlockPredicate.matchesFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(0, -1, -1)));

		PATCH_BUSH = PlacementUtils.register("ctbackport:patch_bush", bush,
				RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome());
		PATCH_DRY_GRASS_DESERT = PlacementUtils.register("ctbackport:patch_dry_grass_desert", dryGrass,
				RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome());
		PATCH_DRY_GRASS_BADLANDS = PlacementUtils.register("ctbackport:patch_dry_grass_badlands", dryGrass,
				RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome());
		PATCH_LEAF_LITTER = PlacementUtils.register("ctbackport:patch_leaf_litter", leafLitter,
				CountPlacement.of(2), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome());
		WILDFLOWERS_BIRCH_FOREST = PlacementUtils.register("ctbackport:wildflowers_birch_forest", wildflowersBirch,
				CountPlacement.of(3), RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome());
		WILDFLOWERS_MEADOW = PlacementUtils.register("ctbackport:wildflowers_meadow", wildflowersMeadow,
				NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome());
		PATCH_FIREFLY_BUSH_NEAR_WATER = PlacementUtils.register("ctbackport:patch_firefly_bush_near_water", fireflyBush,
				CountPlacement.of(2), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), BiomeFilter.biome(),
				BlockPredicateFilter.forPredicate(nearWater));
		PATCH_FIREFLY_BUSH_NEAR_WATER_SWAMP = PlacementUtils.register("ctbackport:patch_firefly_bush_near_water_swamp", fireflyBush,
				CountPlacement.of(3), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome(),
				BlockPredicateFilter.forPredicate(nearWater));
		PATCH_FIREFLY_BUSH_SWAMP = PlacementUtils.register("ctbackport:patch_firefly_bush_swamp", fireflyBush,
				RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
				HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), BiomeFilter.biome());
	}

	private static Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> patch(String name, int tries, int xzSpread,
			int ySpread, BlockStateProvider provider) {
		return FeatureUtils.register("ctbackport:" + name, Feature.RANDOM_PATCH,
				new RandomPatchConfiguration(tries, xzSpread, ySpread,
						PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(provider))));
	}

	/** All facings and one to N segments with equal weight, the vanilla lists. */
	private static WeightedStateProvider segmented(Block block,
			net.minecraft.world.level.block.state.properties.EnumProperty<Direction> facing,
			net.minecraft.world.level.block.state.properties.IntegerProperty amount, int maxAmount) {
		SimpleWeightedRandomList.Builder<BlockState> builder = SimpleWeightedRandomList.builder();
		for (int count = 1; count <= maxAmount; count++) {
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				builder.add(block.defaultBlockState().setValue(facing, direction).setValue(amount, count), 1);
			}
		}
		return new WeightedStateProvider(builder.build());
	}
}
