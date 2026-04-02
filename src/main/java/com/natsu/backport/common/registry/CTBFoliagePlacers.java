package com.natsu.backport.common.registry;


import java.lang.reflect.Constructor;

import com.mojang.serialization.Codec;
import com.natsu.backport.CTBackport;
import com.natsu.backport.server.world.feature.tree.foliage.CherryFoliagePlacer;
import com.natsu.backport.server.world.feature.tree.trunk.CherryTrunkPlacer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CTBFoliagePlacers {

	public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES =
            DeferredRegister.create(Registry.FOLIAGE_PLACER_TYPE_REGISTRY, CTBackport.MODID);
	
	
	public static final RegistryObject<FoliagePlacerType<CherryFoliagePlacer>> CHERRY_FOLIAGE_PLACER = FOLIAGE_PLACER_TYPES.register("cherry", () -> createFoliagePlacerType(CherryFoliagePlacer.CODEC));
	
	@SuppressWarnings("unchecked")
    private static <T extends FoliagePlacer> FoliagePlacerType<T> createFoliagePlacerType(Codec<? extends FoliagePlacer> codec) {
        try {
            Constructor<FoliagePlacerType> ctor = FoliagePlacerType.class.getDeclaredConstructor(Codec.class);
            ctor.setAccessible(true);
            return ctor.newInstance(codec);
        } catch (ReflectiveOperationException e) {
        	throw new RuntimeException("Could not create FoliagePlacer (reflection error)", e);
        }
    }
}
