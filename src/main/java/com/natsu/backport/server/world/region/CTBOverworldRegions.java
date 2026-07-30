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
		// seed the region with the whole vanilla parameter set : without it the
		// nearest-point search only knows our biomes and paints them over the
		// entire region cell, at every depth
		this.addModifiedVanillaOverworldBiomes(mapper, builder -> {});

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

		// vanilla puts the pale garden in the dark forest climate, on the
		// "variant" half of the weirdness range (hence the occasional hills)
		this.addBiome(mapper,
		    Temperature.NEUTRAL.parameter(),
		    Humidity.span(Humidity.WET, Humidity.HUMID),
		    Continentalness.span(Continentalness.MID_INLAND, Continentalness.FAR_INLAND),
		    Erosion.span(Erosion.EROSION_0, Erosion.EROSION_4),
		    Weirdness.span(Weirdness.MID_SLICE_VARIANT_ASCENDING, Weirdness.MID_SLICE_VARIANT_DESCENDING),
		    Depth.SURFACE.parameter(),
		    0,
		    CTBBiomes.PALE_GARDEN);

		// 26.2 : underground, coast to inland, heavily eroded, low weirdness band
		this.addBiome(mapper,
		    Climate.Parameter.span(-1.0F, 1.0F),
		    Climate.Parameter.span(-1.0F, 1.0F),
		    Climate.Parameter.span(-0.19F, 0.55F),
		    Climate.Parameter.span(0.45F, 1.0F),
		    Climate.Parameter.span(-1.1F, -0.85F),
		    Depth.UNDERGROUND.parameter(),
		    0,
		    CTBBiomes.SULFUR_CAVES);

    }
}
