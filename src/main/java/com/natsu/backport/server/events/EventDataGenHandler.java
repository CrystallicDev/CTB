package com.natsu.backport.server.events;

import com.natsu.backport.utils.sets.DirtDecorationSet;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.ResinSet;
import com.natsu.backport.utils.sets.StoneDecorationSet;
import com.natsu.backport.utils.sets.WeatherableCopperSet;
import com.natsu.backport.utils.sets.WoodSet;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public interface EventDataGenHandler {

	public abstract void handleWoodSet(WoodSet set, FMLCommonSetupEvent event);
	public abstract void handleLeavesSet(LeavesSet set, FMLCommonSetupEvent event);
	public abstract void handleStoneDecorationSet(StoneDecorationSet set, FMLCommonSetupEvent event);
	public abstract void handleDirtDecorationSet(DirtDecorationSet set, FMLCommonSetupEvent event);
	public abstract void handleMossSet(MossSet set, FMLCommonSetupEvent event);
	public abstract void handleResinSet(ResinSet set, FMLCommonSetupEvent event);
	public abstract void handleCopperSet(WeatherableCopperSet<?, ?> set, FMLCommonSetupEvent event);
	public abstract void handleCopperDoorSet(WeatherableCopperSet<?, ?> set, FMLCommonSetupEvent event);
	public abstract void handleCopperTrapdoorSet(WeatherableCopperSet<?, ?> set, FMLCommonSetupEvent event);
	public abstract void handleCopperBulbSet(WeatherableCopperSet<?, ?> set, FMLCommonSetupEvent event);

}
