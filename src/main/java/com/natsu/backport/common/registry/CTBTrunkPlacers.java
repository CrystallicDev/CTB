package com.natsu.backport.common.registry;


import java.lang.reflect.Constructor;

import com.mojang.serialization.Codec;
import com.natsu.backport.CTBackport;
import com.natsu.backport.server.world.feature.tree.trunk.CherryTrunkPlacer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CTBTrunkPlacers {

	public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(Registry.TRUNK_PLACER_TYPE_REGISTRY, CTBackport.MODID);
	

	public static final RegistryObject<TrunkPlacerType<CherryTrunkPlacer>> CHERRY_TRUNK_PLACER = TRUNK_PLACER_TYPES.register("cherry", () -> createTrunkPlacerType(CherryTrunkPlacer.CODEC));
	
	
	@SuppressWarnings("unchecked")
    private static <T extends TrunkPlacer> TrunkPlacerType<T> createTrunkPlacerType(Codec<? extends TrunkPlacer> codec) {
        try {
            Constructor<TrunkPlacerType> ctor = TrunkPlacerType.class.getDeclaredConstructor(Codec.class);
            ctor.setAccessible(true);
            return ctor.newInstance(codec);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not create TrunkPlacer (reflection error)", e);
        }
    }
}
