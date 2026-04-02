package com.natsu.backport.utils.sets;

import java.util.function.Function;

import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.common.item.CTBBlockItemFactory;

import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * <p> This class is used to represent a set of decorative stone block.
 * <p> It creates the following blocks :
 * <ul>
 * 	<li><b>{name}</b>: The original decorative stone block</li>
 * 	<li><b>{name}_stairs</b>: The derived stairs</li>
 * 	<li><b>{name}_slabs</b>: The derived slabs</li>
 * 	<li><b>{name}_walls</b>: The derived walls</li>
 *  <li><b>polished_{name}</b>: The polished version of the decorative stone block</li>
 * 	<li><b>polished_{name}_stairs</b>: The derived polished stairs</li>
 * 	<li><b>polished_{name}_slabs</b>: The derived polished slabs</li>
 * 	<li><b>polished_{name}_walls</b>: The derived polished walls</li>
 * </ul>
 * <p> It requires the following textures for DataGen integration</p>
 * <ul>
 * 	<li><b>block/{name}.png</b>: The original decorative stone texture</li>
 * 	<li><b>block/polished_{name}.png</b>: The original decorative stone texture</li>
 * </ul>
 * */
public class StoneDecorationSet implements DefaultSet {

	public final String name;
	
	public final RegistryObject<Block> stone;
	public final RegistryObject<Block> polishedStone;
	public final RegistryObject<Block> stairs;
	public final RegistryObject<Block> polishedStairs;
	public final RegistryObject<Block> walls;
	public final RegistryObject<Block> polishedWalls;
	public final RegistryObject<Block> slabs;
	public final RegistryObject<Block> polishedSlabs;
	
	public final RegistryObject<Item> stoneItem;
	public final RegistryObject<Item> polishedStoneItem;
	public final RegistryObject<Item> stairsItem;
	public final RegistryObject<Item> polishedStairsItem;
	public final RegistryObject<Item> wallsItem;
	public final RegistryObject<Item> polishedWallsItem;
	public final RegistryObject<Item> slabsItem;
	public final RegistryObject<Item> polishedSlabsItem;
	
	/**
     * Creates and registers all blocks for this LeavesSet.
     *
     * @param name               The base name of the stone type (e.g. {@code "andesite"}).
     *                           Blocks will be registered as {@code {name}} and
     *                           {@code polished_{name} etc}.
     * @param ITEMS              The mod's item DeferredRegister used to register blockitems
     * @param BLOCKS             The mod's block DeferredRegister used to register blocks.
     * @param baseStrength       The stone type block's strength
     * @throws IllegalArgumentException if {@code hasFloweredVariant} is true and
     *                                  {@code fruitItem} is null.
     */
	public StoneDecorationSet(String name, DeferredRegister<Item> ITEM, DeferredRegister<Block> BLOCK, float baseStrength,
            CreativeModeTab tab) {
		this.name = name;
		
		this.stone = CTBBlockFactory.makeDecorativeStone(BLOCK, name, baseStrength);
		this.polishedStone = CTBBlockFactory.makeDecorativeStone(BLOCK, "polished_"+name, baseStrength);
		this.slabs = CTBBlockFactory.makeSlab(BLOCK, name+"_slab", stone, baseStrength);
		this.polishedSlabs = CTBBlockFactory.makeSlab(BLOCK, "polished_"+name+"_slab", polishedStone, baseStrength);
		this.stairs = CTBBlockFactory.makeStairs(BLOCK, name+"_stairs", stone, baseStrength);
		this.polishedStairs = CTBBlockFactory.makeStairs(BLOCK, "polished_"+name+"_stairs", polishedStone, baseStrength);
		this.walls = CTBBlockFactory.makeWall(BLOCK, name+"_wall", stone, baseStrength);
		this.polishedWalls = CTBBlockFactory.makeWall(BLOCK, "polished_"+name+"_wall", polishedStone, baseStrength);
		
		this.stoneItem = CTBBlockItemFactory.blockItem(ITEM, tab, stone);
		this.polishedStoneItem = CTBBlockItemFactory.blockItem(ITEM, tab, polishedStone);
		this.slabsItem = CTBBlockItemFactory.blockItem(ITEM, tab, slabs);
		this.polishedSlabsItem = CTBBlockItemFactory.blockItem(ITEM, tab, polishedSlabs);
		this.stairsItem = CTBBlockItemFactory.blockItem(ITEM, tab, stairs);
		this.polishedStairsItem = CTBBlockItemFactory.blockItem(ITEM, tab, polishedStairs);
		this.wallsItem = CTBBlockItemFactory.blockItem(ITEM, tab, walls);
		this.polishedWallsItem = CTBBlockItemFactory.blockItem(ITEM, tab, polishedWalls);
		
	}
	
	@Override
	public void addBlockTags(Function<TagKey<Block>, TagAppender<Block>> tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRenderTypes() {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return name;
	}

}
