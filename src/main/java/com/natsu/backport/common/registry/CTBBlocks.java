package com.natsu.backport.common.registry;

import java.util.function.Function;
import java.util.function.Supplier;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.common.block.WeatheringCopperDoorBlock;
import com.natsu.backport.common.item.CTBBlockItemFactory;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.ResinSet;
import com.natsu.backport.utils.sets.WeatherableCopperSet;
import com.natsu.backport.utils.sets.WoodSet;
import com.natsu.salm.block.backport.WaterLoggedTransparentBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CTBackport.MODID);
	
	public static final WoodSet CHERRY_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "cherry", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final WoodSet BAMBOO_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "bamboo", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final WoodSet PALE_OAK_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "pale_oak", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final LeavesSet PALE_OAK_LEAVES = new LeavesSet("pale_oak", CTBItems.ITEMS, BLOCKS, Items.BIRCH_SAPLING, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final ResinSet RESIN = new ResinSet(CTBItems.ITEMS, BLOCKS, "resin", 0, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final MossSet PALE_MOSS = new MossSet(CTBItems.ITEMS, BLOCKS, "pale", CreativeModeTab.TAB_BUILDING_BLOCKS);

	public static final RegistryObject<Block> CHERRY_LEAVES = registerWithItem(BLOCKS, CTBItems.ITEMS, "cherry_leaves", CTBBlockFactory.makeCherryLeaves(BLOCKS, "cherry_leaves", CTBParticles.CHERRY));
	
	// Specific Bamboo Blocks
	public static final RegistryObject<Block> BAMBOO_MOSAIC = registerWithItem(BLOCKS, CTBItems.ITEMS, "bamboo_mosaic", CTBBlockFactory.makePlanks(BLOCKS, "bamboo_mosaic", 2));
	public static final RegistryObject<Block> BAMBOO_MOSAIC_STAIRS = registerWithItem(BLOCKS, CTBItems.ITEMS, "bamboo_mosaic_stairs", CTBBlockFactory.makeStairs(BLOCKS, "bamboo_mosaic_stairs", BAMBOO_MOSAIC, 2));
	public static final RegistryObject<Block> BAMBOO_MOSAIC_SLAB = registerWithItem(BLOCKS, CTBItems.ITEMS, "bamboo_mosaic_slab", CTBBlockFactory.makeSlab(BLOCKS, "bamboo_mosaic_slab", BAMBOO_MOSAIC, 2));
	public static final RegistryObject<Block> CREAKING_HEART = registerWithItem(BLOCKS, CTBItems.ITEMS, "creaking_heart", CTBBlockFactory.makeCreakingHeart(BLOCKS, "creaking_heart"));
	
	//Eyeblossom blocks
	public static final RegistryObject<Block> OPEN_EYEBLOSSOM = registerWithItem(BLOCKS, CTBItems.ITEMS, "eyeblossom_open", CTBBlockFactory.makeEyeblossom(BLOCKS, "eyeblossom_open", true));
	public static final RegistryObject<Block> CLOSED_EYEBLOSSOM = registerWithItem(BLOCKS, CTBItems.ITEMS, "eyeblossom_close", CTBBlockFactory.makeEyeblossom(BLOCKS, "eyeblossom_close", false));
	
	//Copper Weatherable blocks
	//public static final WeatherableCopperSet<WeatheringCopperDoorBlock, WaterLoggedTransparentBlock> COPPER_GRATE = new WeatherableCopperSet<WeatheringCopperDoorBlock, WaterLoggedTransparentBlock>(BLOCKS, "copper_grate", 3, WeatheringCopperDoorBlock.class, WaterLoggedTransparentBlock.class);
	//public static final WeatherableCopperSet<WeatheringCopperDoorBlock, DoorBlock> COPPER_DOOR = new WeatherableCopperSet<WeatheringCopperDoorBlock, DoorBlock>(BLOCKS, "copper_door", 3, WeatheringCopperDoorBlock.class, DoorBlock.class);
	//public static final WeatherableCopperSet<WeatheringCopperDoorBlock, TrapDoorBlock> COPPER_TRAPDOOR = new WeatherableCopperSet<WeatheringCopperDoorBlock, TrapDoorBlock>(BLOCKS, "copper_trapdoor", 3, WeatheringCopperDoorBlock.class, TrapDoorBlock.class);
	//public static final RegistryObject<Block> COPPER_BULB = CTBBlockFactory.makeCopperBulb(BLOCKS, "copper_bulb", 3f);
	
	private static RegistryObject<Block> registerWithItem(DeferredRegister<Block> blocks, DeferredRegister<Item> items,
			String name, RegistryObject<Block> reg) {
		items.register(name,
				() -> new BlockItem(reg.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
		return reg;
	}
	
	
}
