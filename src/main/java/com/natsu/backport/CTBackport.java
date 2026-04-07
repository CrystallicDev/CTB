package com.natsu.backport;


import com.natsu.backport.common.registry.CTBBiomes;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBFeatures;
import com.natsu.backport.common.registry.CTBFoliagePlacers;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTrunkPlacers;

import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CTBackport.MODID)
public class CTBackport {
	public static final String MODID = "ctbackport";

    public CTBackport() {
    	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    	CTBSounds.SOUND_EVENTS.register(modEventBus);
    	CTBParticles.PARTICLES.register(modEventBus);
    	CTBBlockEntities.BLOCK_ENTITIES.register(modEventBus);
    	CTBItems.ITEMS.register(modEventBus);
    	CTBBlocks.BLOCKS.register(modEventBus);
    	CTBEntities.ENTITIES.register(modEventBus);
    	CTBEffects.EFFECTS.register(modEventBus);
    
    	if (!DatagenModLoader.isRunningDataGen()) {
        	CTBTrunkPlacers.TRUNK_PLACER_TYPES.register(modEventBus);
        	CTBFoliagePlacers.FOLIAGE_PLACER_TYPES.register(modEventBus);
        	CTBFeatures.FEATURES.register(modEventBus);
    		FMLJavaModLoadingContext.get().getModEventBus().addListener(TerraBlenderBridge::commonSetup);
    	}
    }
}
