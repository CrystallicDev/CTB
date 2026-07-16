package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.common.block.CopperBulbBlock;
import com.natsu.backport.common.block.CopperDoorBlock;
import com.natsu.backport.common.block.WeatheringCopperBulbBlock;
import com.natsu.backport.common.block.WeatheringCopperDoorBlock;
import com.natsu.backport.server.world.feature.tree.PaleOakTreeGrower;
import com.natsu.backport.common.block.WeatheringCopperTrapDoorBlock;
import com.natsu.backport.common.block.WeatheringTransparentBlock;
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
	public static final RegistryObject<Block> PALE_OAK_SAPLING = registerWithItem(BLOCKS, CTBItems.ITEMS, "pale_oak_sapling",
			CTBBlockFactory.makeSapling(BLOCKS, "pale_oak_sapling", new PaleOakTreeGrower()));
	public static final LeavesSet PALE_OAK_LEAVES = new LeavesSet("pale_oak", CTBItems.ITEMS, BLOCKS,
			() -> PALE_OAK_SAPLING.get().asItem(), CreativeModeTab.TAB_BUILDING_BLOCKS, CTBParticles.PALE_OAK_LEAVES);
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

	// Pale Hanging Moss
	public static final RegistryObject<Block> PALE_HANGING_MOSS = registerWithItem(BLOCKS, CTBItems.ITEMS, "pale_hanging_moss", CTBBlockFactory.makePaleHangingMoss(BLOCKS, "pale_hanging_moss"));

	//Copper Weatherable blocks
	public static final WeatherableCopperSet<WeatheringTransparentBlock, WaterLoggedTransparentBlock> COPPER_GRATE = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_grate", 3, WeatheringTransparentBlock.class, WaterLoggedTransparentBlock.class);
	public static final WeatherableCopperSet<WeatheringCopperDoorBlock, CopperDoorBlock> COPPER_DOOR = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_door", 3, WeatheringCopperDoorBlock.class, CopperDoorBlock.class);
	public static final WeatherableCopperSet<WeatheringCopperTrapDoorBlock, TrapDoorBlock> COPPER_TRAPDOOR = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_trapdoor", 3, WeatheringCopperTrapDoorBlock.class, TrapDoorBlock.class);
	// lit light drops with the oxidation, like vanilla
	public static final WeatherableCopperSet<WeatheringCopperBulbBlock, CopperBulbBlock> COPPER_BULB = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_bulb", 3, WeatheringCopperBulbBlock.class, CopperBulbBlock.class, new int[]{15, 12, 8, 4});

	private static RegistryObject<Block> registerWithItem(DeferredRegister<Block> blocks, DeferredRegister<Item> items,
			String name, RegistryObject<Block> reg) {
		items.register(name,
				() -> new BlockItem(reg.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
		return reg;
	}


}
