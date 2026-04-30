package com.natsu.backport.common.item;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CTBItemFactory {

	public static final RegistryObject<Item> makeBoat(DeferredRegister<Item> ITEMS, String name, CreativeModeTab tab) {
		return ITEMS.register(name,
	            () -> new BoatItem(Boat.Type.OAK, new Item.Properties().tab(tab)));
	}

	public static final RegistryObject<Item> makeSign(DeferredRegister<Item> ITEMS, String name, CreativeModeTab tab, RegistryObject<WallSignBlock> wallSign, RegistryObject<StandingSignBlock> standingSign) {
		return ITEMS.register(name,
	            () -> new SignItem(new Item.Properties().tab(tab), standingSign.get(), wallSign.get()));
	}

}