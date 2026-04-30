package com.natsu.backport.utils.sets;


import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.BiMap;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public interface DefaultSet {

	/**
	 * Used by the datagen to set the tags of blocks in the set
	 * */
	public void addBlockTags(Function<TagKey<Block>, TagAppender<Block>> tag);
	/**
	 * Used by the datagen, for block render types (ex: cutout / alpha channels)
	 * */
	public void setRenderTypes();


	public void setFlammables(final Map<Block, Integer> flameOdds, final Map<Block, Integer> burnOdds);
	public void setCompostables(final Object2FloatMap<ItemLike> compostables);
	public void setWeatherable(final BiMap<Block, Block> nextByBlock, final BiMap<Block, Block> previousByBlock);
	
}
