package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.MaceItem;
import com.natsu.backport.common.item.WindChargeItem;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CTBackport.MODID);

	public static final RegistryObject<Item> WIND_CHARGE = ITEMS.register("wind_charge", () ->
			new WindChargeItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(16))
			);

	public static final RegistryObject<Item> MACE = ITEMS.register("mace", () ->
			new MaceItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).durability(500).rarity(Rarity.EPIC))
			);

	public static final RegistryObject<Item> BREEZE_ROD = ITEMS.register("breeze_rod", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);

	public static final RegistryObject<Item> TRIAL_KEY = ITEMS.register("trial_key", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);

	public static final RegistryObject<Item> OMINOUS_TRIAL_KEY = ITEMS.register("ominous_trial_key", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE))
			);

}
