package com.natsu.backport.common;

import java.util.List;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBWeatheringCopper;
import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.utils.sets.WeatherableCopperSet;

import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

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

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		if (DatagenModLoader.isRunningDataGen()) return;

		event.enqueueWork(CommonSetup::registerWeatherables);
	}

	private static void registerWeatherables() {
		for (WeatherableCopperSet<?, ?> set : List.of(CTBBlocks.COPPER_GRATE, CTBBlocks.COPPER_DOOR, CTBBlocks.COPPER_TRAPDOOR)) {
			set.setWeatherable(CTBWeatheringCopper.NEXT_BY_BLOCK, CTBWeatheringCopper.NEXT_BY_BLOCK.inverse());
			set.setWaxables(CTBWeatheringCopper.WAXABLES);
		}

		// the honeycomb maps live in a class, so those we can grow (AT removes the final)
		BiMap<Block, Block> waxables = HashBiMap.create(HoneycombItem.WAXABLES.get());
		waxables.putAll(CTBWeatheringCopper.WAXABLES);
		HoneycombItem.WAXABLES = Suppliers.memoize(() -> ImmutableBiMap.copyOf(waxables));
		HoneycombItem.WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> HoneycombItem.WAXABLES.get().inverse());
	}
}
