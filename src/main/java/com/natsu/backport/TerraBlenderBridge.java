package com.natsu.backport;


import com.natsu.backport.common.registry.CTBSurfaceRules;
import com.natsu.backport.server.world.region.CTBOverworldRegions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public class TerraBlenderBridge {

	public static void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            // Given we only add two biomes, we should keep our weight relatively low.
            Regions.register(new CTBOverworldRegions(new ResourceLocation(CTBackport.MODID, "overworld"), 5));

            // Register our surface rules
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, CTBackport.MODID, CTBSurfaceRules.makeRules());


        });
    }

}
