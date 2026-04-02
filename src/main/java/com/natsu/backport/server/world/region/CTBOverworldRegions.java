package com.natsu.backport.server.world.region;

import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import com.natsu.backport.common.registry.CTBBiomes;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

public class CTBOverworldRegions extends Region {

	public CTBOverworldRegions(ResourceLocation name, int weight)
    {
        super(name, RegionType.OVERWORLD, weight);
    }


	@Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
		this.addBiome(mapper, 
				Climate.Parameter.span(0.3f, 0.6f),
				Climate.Parameter.span(0.5f, 1.0f),
				Climate.Parameter.span(-0.5f, 0.5f),
				Climate.Parameter.span(-0.3f, 0.3f),
				Climate.Parameter.span(0.0f, 0.5f),
				Climate.Parameter.span(-1.0f, 1.0f),
				0,
				CTBBiomes.CHERRY_GROVE);
		
		this.addBiome(mapper, 
				Climate.Parameter.span(-0.5f, 0.0f),
				Climate.Parameter.span(-0.3f, 0.3f),
				Climate.Parameter.span(-0.5f, 0.5f),
				Climate.Parameter.span(-0.3f, 0.3f),
				Climate.Parameter.span(0.0f, 0.5f),
				Climate.Parameter.span(0.6f, 1.0f),
				0,
				CTBBiomes.PALE_GARDEN);
        
    }
}
