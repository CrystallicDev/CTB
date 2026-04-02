package com.natsu.backport.utils.sets;


import java.util.function.Function;

import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
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

}
