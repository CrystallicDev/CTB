package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.common.item.CTBBlockItemFactory;
import com.natsu.backport.common.item.WindChargeItem;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CTBackport.MODID);
	
	public static final RegistryObject<Item> WIND_CHARGE = ITEMS.register("wind_charge", () -> 
			new WindChargeItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(16))
			);

	public static final RegistryObject<Item> CHERRY_LEAVES_ITEM = CTBBlockItemFactory.blockItem(ITEMS, CreativeModeTab.TAB_BUILDING_BLOCKS, CTBBlocks.CHERRY_LEAVES);
	public static final RegistryObject<Item> BAMBOO_MOSAIC_ITEM = CTBBlockItemFactory.blockItem(ITEMS, CreativeModeTab.TAB_BUILDING_BLOCKS, CTBBlocks.BAMBOO_MOSAIC);
	public static final RegistryObject<Item> BAMBOO_MOSAIC_STAIRS_ITEM = CTBBlockItemFactory.blockItem(ITEMS, CreativeModeTab.TAB_BUILDING_BLOCKS, CTBBlocks.BAMBOO_MOSAIC_STAIRS);
	public static final RegistryObject<Item> BAMBOO_MOSAIC_SLAB_ITEM = CTBBlockItemFactory.blockItem(ITEMS, CreativeModeTab.TAB_BUILDING_BLOCKS, CTBBlocks.BAMBOO_MOSAIC_SLAB);
}
