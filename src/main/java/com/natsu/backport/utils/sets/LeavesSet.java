package com.natsu.backport.utils.sets;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.common.item.CTBBlockItemFactory;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * <p> This class is used to represent a set of leaves.
 * <p> It creates the following blocks :
 * <ul>
 * 	<li><b>{name}_leaves</b>: The original leave block</li>
 * </ul>
 * <p> It requires the following textures for DataGen integration</p>
 * <ul>
 * 	<li><b>block/{name}_leaves.png</b>: The original leaves texture</li>
 * </ul>
 * */
public class LeavesSet implements DefaultSet {

    public final String name;
    public final RegistryObject<Block> leaves;

    /*** The sapling item for this tree type.
     * Should be tied to an {@link net.minecraft.world.level.block.grower.AbstractTreeGrower}.*/
    public final Supplier<Item> saplingItem;
    public final RegistryObject<Item> leavesItem;

    /**
     * Creates and registers all blocks for this LeavesSet.
     *
     * @param name               The base name of the tree type (e.g. {@code "cherry"}).
     * @param ITEMS              The mod's item DeferredRegister used to register blockitems
     * @param BLOCKS             The mod's block DeferredRegister used to register leaves and layer.
     * @param saplingItem        The sapling item this tree's leaves drop.
     * @param fallingParticles   Falling leaf particles, or null for plain leaves.
     */
    public LeavesSet(
            String name,
            DeferredRegister<Item> ITEMS,
            DeferredRegister<Block> BLOCKS,
            Supplier<Item> saplingItem,
            CreativeModeTab tab,
            RegistryObject<SimpleParticleType> fallingParticles
    ) {

        this.name = name;
        this.saplingItem = saplingItem;

        this.leaves = fallingParticles == null
                ? CTBBlockFactory.makeLeaves(BLOCKS, name+"_leaves")
                : CTBBlockFactory.makeCherryLeaves(BLOCKS, name+"_leaves", fallingParticles);
        this.leavesItem = CTBBlockItemFactory.blockItem(ITEMS, tab, leaves);
    }

	@Override
	public void addBlockTags(Function<TagKey<Block>, TagAppender<Block>> tag) {
		tag.apply(BlockTags.LEAVES).add(leaves.get());
		tag.apply(BlockTags.MINEABLE_WITH_HOE).add(leaves.get());
		tag.apply(BlockTags.PARROTS_SPAWNABLE_ON).add(leaves.get());
		tag.apply(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE).add(leaves.get());
	}

	@Override
	public void setRenderTypes() {
		ItemBlockRenderTypes.setRenderLayer(leaves.get(), RenderType.cutout());
	}

	public String getName() {
		return this.name;
	}

	@Override
	public void setFlammables(Map<Block, Integer> flameOdds, Map<Block, Integer> burnOdds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCompostables(Object2FloatMap<ItemLike> compostables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWeatherable(BiMap<Block, Block> nextByBlock, BiMap<Block, Block> previousByBlock) {
		// TODO Auto-generated method stub
		
	}
}

