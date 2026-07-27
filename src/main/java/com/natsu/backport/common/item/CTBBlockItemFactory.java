package com.natsu.backport.common.item;


import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlockItemFactory {

	public static RegistryObject<Item> blockItem(DeferredRegister<Item> ITEMS, RegistryObject<Block> block) {
        return ITEMS.register(block.getId().getPath(),
            () -> new BlockItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    }

	public static RegistryObject<Item> blockItem(DeferredRegister<Item> ITEMS, CreativeModeTab tab,
            RegistryObject<Block> block) {
		return ITEMS.register(block.getId().getPath(),
				() -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}

	public static RegistryObject<Item> blockItem(DeferredRegister<Item> items, CreativeModeTab tab, String name,
			RegistryObject<Block> block) {

		return items.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}

}
