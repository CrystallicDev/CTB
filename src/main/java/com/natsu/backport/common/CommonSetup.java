package com.natsu.backport.common;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CTBackport.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {

	// attributes are needed on both sides, a dedicated server crashes without them
	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		if (DatagenModLoader.isRunningDataGen()) return;

		event.put(CTBEntities.CREAKING.get(), Creaking.createAttributes().build());
		event.put(CTBEntities.BREEZE.get(), Breeze.createAttributes().build());
		//event.put(CTBEntities.SULPHUR_CUBE.get(), SulphurCube.createAttributes().build());
	}
}
