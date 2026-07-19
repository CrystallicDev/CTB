package com.natsu.backport.utils.sets;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.common.item.CTBBlockItemFactory;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * <p> This class is used to represent a set of moss blocks.
 * <p> It creates the following blocks :
 * <ul>
 * 	<li><b>{name}_moss</b>: The original moss block</li>
 * 	<li><b>{name}_moss_layer</b>: The moss layer block (like snow)</li>
 * </ul>
 * <p> It requires the following textures for DataGen integration</p>
 * <ul>
 * 	<li><b>block/{name}_moss.png</b>: The original moss texture</li>
 * </ul>
 * */
public class MossSet implements DefaultSet {

	public final String name;

	public final RegistryObject<Block> moss;
	public final RegistryObject<Block> mossLayer;

	public final RegistryObject<Item> mossItem;
	public final RegistryObject<Item> mossLayerItem;

	public MossSet(DeferredRegister<Item> ITEMS, DeferredRegister<Block> BLOCKS, String name, CreativeModeTab tab) {
		this.name = name;

		BlockBehaviour.Properties props = BlockBehaviour.Properties
                .of(Material.MOSS)
                .strength(0.1f)
                .sound(SoundType.MOSS)
                .noOcclusion()
                .noCollission();

		this.moss = CTBBlockFactory.makeMossBlock(BLOCKS, name+"_moss", null);
		this.mossLayer = CTBBlockFactory.makeMossLayerBlock(BLOCKS, name+"_moss_layer", props);

		this.mossItem = CTBBlockItemFactory.blockItem(ITEMS, tab, moss);
		this.mossLayerItem = CTBBlockItemFactory.blockItem(ITEMS, tab, mossLayer);

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
		return this.name;
	}

	@Override
	public void setFlammables(Map<Block, Integer> flameOdds, Map<Block, Integer> burnOdds) {
		flameOdds.put(moss.get(), 5);
		burnOdds.put(moss.get(), 100);
		flameOdds.put(mossLayer.get(), 5);
		burnOdds.put(mossLayer.get(), 100);
	}

	@Override
	public void setCompostables(Object2FloatMap<ItemLike> compostables) {
		compostables.put(mossItem.get(), 0.65F);
		compostables.put(mossLayerItem.get(), 0.3F);
	}

	@Override
	public void setWeatherable(BiMap<Block, Block> nextByBlock, BiMap<Block, Block> previousByBlock) {
		// TODO Auto-generated method stub
		
	}

}
