package com.natsu.backport;


import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

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
    	CTBEntities.ENTITIES.register(modEventBus);
    	CTBItems.ITEMS.register(modEventBus);
    	CTBEffects.EFFECTS.register(modEventBus);
    }
}
