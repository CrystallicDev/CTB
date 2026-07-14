package com.natsu.backport;


import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBFeatures;
import com.natsu.backport.common.registry.CTBFoliagePlacers;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTreeDecorators;
import com.natsu.backport.common.registry.CTBTrunkPlacers;

import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

@Mod(CTBackport.MODID)
public class CTBackport {
	public static final String MODID = "ctbackport";

    public CTBackport() {
    	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    	CTBSounds.SOUND_EVENTS.register(modEventBus);
    	CTBParticles.PARTICLES.register(modEventBus);
    	CTBEffects.EFFECTS.register(modEventBus);
    	CTBBlocks.BLOCKS.register(modEventBus);
    	CTBItems.ITEMS.register(modEventBus);
    	CTBBlockEntities.BLOCK_ENTITIES.register(modEventBus);
    	GeckoLib.initialize();

    	if (!DatagenModLoader.isRunningDataGen()) {
        	CTBEntities.ENTITIES.register(modEventBus);
        	CTBTrunkPlacers.TRUNK_PLACER_TYPES.register(modEventBus);
        	CTBFoliagePlacers.FOLIAGE_PLACER_TYPES.register(modEventBus);
        	CTBTreeDecorators.TREE_DECORATOR_TYPES.register(modEventBus);
        	CTBFeatures.FEATURES.register(modEventBus);
        	// the bridge classloads TerraBlender, only touch it if the mod is there
        	if (ModList.get().isLoaded("terrablender")) {
        		modEventBus.addListener(TerraBlenderBridge::commonSetup);
        	}
    	}
    }
}
