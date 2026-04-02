package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.WoodSet;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CTBackport.MODID);
	
	public static final WoodSet CHERRY_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "cherry", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final WoodSet BAMBOO_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "bamboo", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final WoodSet PALE_OAK_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "pale_oak", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final LeavesSet CHERRY_LEAVES = new LeavesSet("cherry", CTBItems.ITEMS, BLOCKS, Items.BIRCH_SAPLING, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final LeavesSet PALE_OAK_LEAVES = new LeavesSet("pale_oak", CTBItems.ITEMS, BLOCKS, Items.BIRCH_SAPLING, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final MossSet PALE_MOSS = new MossSet(CTBItems.ITEMS, BLOCKS, "pale", CreativeModeTab.TAB_BUILDING_BLOCKS);
	
	// Specific Bamboo Blocks
	public static final RegistryObject<Block> BAMBOO_MOSAIC = CTBBlockFactory.makePlanks(BLOCKS, "bamboo_mosaic", 2);
	public static final RegistryObject<Block> BAMBOO_MOSAIC_STAIRS = CTBBlockFactory.makeStairs(BLOCKS, "bamboo_mosaic_stairs", BAMBOO_MOSAIC, 2);
	public static final RegistryObject<Block> BAMBOO_MOSAIC_SLAB = CTBBlockFactory.makeSlab(BLOCKS, "bamboo_mosaic_slab", BAMBOO_MOSAIC, 2);
	
	/*public static final Pair<Supplier<StandingSignBlock>, Supplier<WallSignBlock>> CHERRY_SIGN = create("cherry", Material.WOOD, MaterialColor.COLOR_RED, CHERRY_WOOD.woodType);
	public static final Pair<Supplier<StandingSignBlock>, Supplier<WallSignBlock>> BAMBOO_SIGN = create("bamboo", Material.WOOD, MaterialColor.COLOR_GREEN, BAMBOO_WOOD.woodType);
	

	public static Pair<Supplier<StandingSignBlock>, Supplier<WallSignBlock>> create(String key, Material material, MaterialColor color, WoodType woodType) {
        return create(key, BlockBehaviour.Properties.of(material, color).noCollission().strength(1.0F).sound(SoundType.WOOD), new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), woodType);
    }
	
	public static Pair<Supplier<StandingSignBlock>, Supplier<WallSignBlock>> create(String key, BlockBehaviour.Properties blocks, Item.Properties items, WoodType woodType) {
        Supplier<StandingSignBlock> standing = create(key + "_sign", () -> new StandingSignBlock(blocks, woodType));
        Supplier<WallSignBlock> wall = create(key + "_wall_sign", () -> new WallSignBlock(blocks.dropsLike(standing.get()), woodType));
        CTBItems.ITEMS.register(key + "_sign", () -> new SignItem(items, standing.get(), wall.get()));
        return Pair.of(standing, wall);
    }

    private static <T extends Block> Supplier<T> create(String key, Supplier<T> block) {
        return BLOCKS.register(key, block);
    }*/
	
}
