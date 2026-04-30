package com.natsu.backport.server.world.region;

import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import com.natsu.backport.common.registry.CTBBiomes;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils.Continentalness;
import terrablender.api.ParameterUtils.Depth;
import terrablender.api.ParameterUtils.Erosion;
import terrablender.api.ParameterUtils.Humidity;
import terrablender.api.ParameterUtils.Temperature;
import terrablender.api.ParameterUtils.Weirdness;
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
			    Temperature.WARM,
			    Humidity.DRY,
			    Continentalness.FAR_INLAND,
			    Erosion.EROSION_1,
			    Weirdness.HIGH_SLICE_VARIANT_ASCENDING,
			    Depth.SURFACE,
			    0,
			    CTBBiomes.CHERRY_GROVE);

			this.addBiome(mapper,
			    Temperature.WARM,
			    Humidity.DRY,
			    Continentalness.FAR_INLAND,
			    Erosion.EROSION_1,
			    Weirdness.HIGH_SLICE_VARIANT_DESCENDING,
			    Depth.SURFACE,
			    0,
			    CTBBiomes.CHERRY_GROVE);

			this.addBiome(mapper,
			    Temperature.WARM,
			    Humidity.DRY,
			    Continentalness.FAR_INLAND,
			    Erosion.EROSION_2,
			    Weirdness.MID_SLICE_VARIANT_ASCENDING,
			    Depth.SURFACE,
			    0,
			    CTBBiomes.CHERRY_GROVE);

		this.addBiome(mapper,
		    Temperature.COOL,
		    Humidity.WET,
		    Continentalness.INLAND,
		    Erosion.EROSION_2,
		    Weirdness.HIGH_SLICE_VARIANT_ASCENDING,
		    Depth.SURFACE,
		    0,
		    CTBBiomes.PALE_GARDEN);

		this.addBiome(mapper,
		    Temperature.COOL,
		    Humidity.WET,
		    Continentalness.INLAND,
		    Erosion.EROSION_2,
		    Weirdness.MID_SLICE_VARIANT_ASCENDING,
		    Depth.SURFACE,
		    0,
		    CTBBiomes.PALE_GARDEN);

		this.addBiome(mapper,
		    Temperature.COOL,
		    Humidity.WET,
		    Continentalness.INLAND,
		    Erosion.EROSION_3,
		    Weirdness.HIGH_SLICE_VARIANT_ASCENDING,
		    Depth.SURFACE,
		    0,
		    CTBBiomes.PALE_GARDEN);

		this.addBiome(mapper,
		    Temperature.COOL,
		    Humidity.WET,
		    Continentalness.INLAND,
		    Erosion.EROSION_3,
		    Weirdness.MID_SLICE_VARIANT_ASCENDING,
		    Depth.SURFACE,
		    0,
		    CTBBiomes.PALE_GARDEN);

    }
}
