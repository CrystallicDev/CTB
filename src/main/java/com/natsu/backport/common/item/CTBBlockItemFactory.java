package com.natsu.backport.common.item;


import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlockItemFactory {

	public static RegistryObject<Item> blockItem(DeferredRegister<Item> ITEMS, RegistryObject<Block> block) {
        return ITEMS.register(block.getId().getPath(),
            () -> new BlockItem(block.get(), new Item.Properties()));
    }

	public static RegistryObject<Item> blockItem(DeferredRegister<Item> ITEMS, CreativeModeTab tab,
            RegistryObject<Block> block) {
		return ITEMS.register(block.getId().getPath(),
				() -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
	

	
}
