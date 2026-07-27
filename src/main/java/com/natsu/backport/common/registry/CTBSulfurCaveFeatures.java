package com.natsu.backport.common.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;

/**
 * The 26.2 sulfur caves features rebuilt in code, same configs and placements
 * as the vanilla worldgen JSONs. Referenced by name from the biome JSON.
 */
public final class CTBSulfurCaveFeatures {

	private CTBSulfurCaveFeatures() {
	}

	public static void register() {
		Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> spring = FeatureUtils.register(
				"ctbackport:sulfur_spring", CTBFeatures.SULFUR_SPRING.get(), NoneFeatureConfiguration.INSTANCE);
		Holder<PlacedFeature> springInline = PlacementUtils.register("ctbackport:sulfur_spring", spring);

		Holder<ConfiguredFeature<RootSystemConfiguration, ?>> rooted = FeatureUtils.register(
				"ctbackport:rooted_sulfur_spring", Feature.ROOT_SYSTEM,
				new RootSystemConfiguration(springInline, 5, 3, BlockTags.AZALEA_ROOT_REPLACEABLE,
						BlockStateProvider.simple(CTBBlocks.SULFUR.get()), 20, 184, 1, 1,
						BlockStateProvider.simple(CTBBlocks.SULFUR.get()), 1, 1,
						BlockPredicate.matchesBlock(Blocks.AIR, BlockPos.ZERO)));
		PlacementUtils.register("ctbackport:rooted_sulfur_spring", rooted,
				CountPlacement.of(UniformInt.of(1, 2)), InSquarePlacement.spread(),
				HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(256))),
				EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.solid(),
						BlockPredicate.ONLY_IN_AIR_PREDICATE, 12),
				RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BiomeFilter.biome());

		Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> pool = FeatureUtils.register(
				"ctbackport:sulfur_pool", CTBFeatures.SULFUR_POOL.get(), NoneFeatureConfiguration.INSTANCE);
		PlacementUtils.register("ctbackport:sulfur_pool", pool,
				CountPlacement.of(256), InSquarePlacement.spread(),
				HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(256))),
				BlockPredicateFilter.forPredicate(BlockPredicate.solid()),
				EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE,
						BlockPredicate.alwaysTrue(), 32),
				RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
				BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlock(CTBBlocks.SULFUR.get(), BlockPos.ZERO)),
				BiomeFilter.biome());

		Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> spike = FeatureUtils.register(
				"ctbackport:sulfur_spike", CTBFeatures.SULFUR_SPIKE_FEATURE.get(), NoneFeatureConfiguration.INSTANCE);
		PlacementUtils.register("ctbackport:sulfur_spike", spike,
				CountPlacement.of(UniformInt.of(192, 256)), InSquarePlacement.spread(),
				HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(256))),
				CountPlacement.of(UniformInt.of(1, 5)),
				RandomOffsetPlacement.of(ClampedNormalInt.of(0.0F, 3.0F, -10, 10),
						ClampedNormalInt.of(0.0F, 0.6F, -2, 2)),
				BiomeFilter.biome());

		Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> cluster = FeatureUtils.register(
				"ctbackport:sulfur_spike_cluster", CTBFeatures.SULFUR_SPIKE_CLUSTER.get(),
				NoneFeatureConfiguration.INSTANCE);
		PlacementUtils.register("ctbackport:sulfur_spike_cluster", cluster,
				CountPlacement.of(UniformInt.of(48, 96)), InSquarePlacement.spread(),
				HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.bottom(), VerticalAnchor.absolute(256))),
				BiomeFilter.biome());

		Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> banding = FeatureUtils.register(
				"ctbackport:sulfur_wall_banding", CTBFeatures.SULFUR_WALL_BANDING.get(),
				NoneFeatureConfiguration.INSTANCE);
		PlacementUtils.register("ctbackport:sulfur_wall_banding", banding);
	}
}
