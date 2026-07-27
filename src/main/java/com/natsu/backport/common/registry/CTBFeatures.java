package com.natsu.backport.common.registry;


import com.natsu.backport.CTBackport;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, CTBackport.MODID);

	public static final RegistryObject<Feature<NoneFeatureConfiguration>> SULFUR_SPRING =
			FEATURES.register("sulfur_spring", () -> new com.natsu.backport.common.worldgen.SulfurSpringFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> SULFUR_POOL =
			FEATURES.register("sulfur_pool", () -> new com.natsu.backport.common.worldgen.SulfurPoolFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> SULFUR_SPIKE_FEATURE =
			FEATURES.register("sulfur_spike", () -> new com.natsu.backport.common.worldgen.SulfurSpikeFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> SULFUR_SPIKE_CLUSTER =
			FEATURES.register("sulfur_spike_cluster", () -> new com.natsu.backport.common.worldgen.SulfurSpikeClusterFeature(NoneFeatureConfiguration.CODEC));
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> SULFUR_WALL_BANDING =
			FEATURES.register("sulfur_wall_banding", () -> new com.natsu.backport.common.worldgen.SulfurWallBandingFeature(NoneFeatureConfiguration.CODEC));
}
