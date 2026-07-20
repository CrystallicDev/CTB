package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.server.world.structure.TrialChambersStructure;

import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBStructures {

	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, CTBackport.MODID);

	public static final RegistryObject<StructureFeature<?>> TRIAL_CHAMBERS = STRUCTURES.register("trial_chambers", TrialChambersStructure::new);

}
