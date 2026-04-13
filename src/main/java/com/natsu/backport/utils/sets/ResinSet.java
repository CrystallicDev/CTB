package com.natsu.backport.utils.sets;

import java.util.Properties;
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
 * <p> This class is used to represent a set of decorative resin block.
 * <p> It creates the following blocks :
 * <ul>
 * 	<li><b>{name}</b>: The original decorative dirt block</li>
 *  <li><b>{name}_bricks</b>: The bricks version of the decorative packed dirt block</li>
 * 	<li><b>{name}_bricks_stairs</b>: The derived bricks stairs</li>
 * 	<li><b>{name}_bricks_slabs</b>: The derived bricks slabs</li>
 * 	<li><b>{name}_bricks_walls</b>: The derived bricks walls</li>
 * </ul>
 * <p> It requires the following textures for DataGen integration</p>
 * <ul>
 * 	<li><b>{name}.png</b>: The original decorative dirt texture</li>
 * 	<li><b>packed_{name}.png</b>: The packed decorative dirt texture</li>
 * 	<li><b>{name}_bricks.png</b>: The bricks decorative dirt texture</li>
 * </ul>
 * */
public class ResinSet implements DefaultSet {
	
	public final String name;

	public final RegistryObject<Item> resinItem;
	
	public final RegistryObject<Block> block;
	public final RegistryObject<Block> brick;
	public final RegistryObject<Block> brickSlab;
	public final RegistryObject<Block> brickStairs;
	public final RegistryObject<Block> brickWalls;
	public final RegistryObject<Block> chiseledBrick;
	
	public final RegistryObject<Item> blockItem;
	public final RegistryObject<Item> brickItem;
	public final RegistryObject<Item> brickSlabItem;
	public final RegistryObject<Item> brickStairsItem;
	public final RegistryObject<Item> brickWallsItem;
	public final RegistryObject<Item> chiseledBrickItem;

	
	public ResinSet(DeferredRegister<Item> ITEMS, DeferredRegister<Block> BLOCKS, String name, float baseStrength,
            CreativeModeTab tab) {
		this.name = name;
		
		this.resinItem = ITEMS.register(name+"_clump", () -> new Item(new Item.Properties()));
		
		this.block = CTBBlockFactory.makeResin(BLOCKS, name+"", baseStrength);
		this.brick = CTBBlockFactory.makeResinBrick(BLOCKS, name+"_bricks", baseStrength + 1.5f);
		this.brickSlab = CTBBlockFactory.makeSlab(BLOCKS, name+"_bricks_slab", this.brick, baseStrength + 1.5f);
		this.brickStairs = CTBBlockFactory.makeStairs(BLOCKS, name+"_bricks_stairs", this.brick, baseStrength + 1.5f);
		this.brickWalls = CTBBlockFactory.makeWall(BLOCKS, name+"_bricks_wall", this.brick, baseStrength + 1.5f);
		this.chiseledBrick = CTBBlockFactory.makeResinBrick(BLOCKS, "chiseled_"+name+"_bricks", baseStrength + 1.5f);
		
		this.blockItem = CTBBlockItemFactory.blockItem(ITEMS, tab, this.block);
		this.brickItem = CTBBlockItemFactory.blockItem(ITEMS, tab, this.brick);
		this.brickSlabItem = CTBBlockItemFactory.blockItem(ITEMS, tab, this.brickSlab);
		this.brickStairsItem = CTBBlockItemFactory.blockItem(ITEMS, tab, this.brickStairs);
		this.brickWallsItem = CTBBlockItemFactory.blockItem(ITEMS, tab, this.brickWalls);
		this.chiseledBrickItem = CTBBlockItemFactory.blockItem(ITEMS, tab, this.chiseledBrick);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
    public void addBlockTags(Function<TagKey<Block>, TagAppender<Block>> tag) {

    }

    @Override
    public void setRenderTypes() {
    	
    }
	
	
}