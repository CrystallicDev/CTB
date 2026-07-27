package com.natsu.backport.common.registry;


import com.natsu.backport.CTBackport;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class CTBBiomes {

    public static final ResourceKey<Biome> CHERRY_GROVE = register("cherry_grove");
    public static final ResourceKey<Biome> PALE_GARDEN = register("pale_garden");
    public static final ResourceKey<Biome> SULFUR_CAVES = register("sulfur_caves");

    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(CTBackport.MODID, name));
    }
    public static Biome getBiome(Level level, ResourceKey<Biome> key) {
        return level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(key);
    }

}
