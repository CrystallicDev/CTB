package com.natsu.backport.common.block;

import java.util.Optional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.extensions.IForgeBlock;

/**
 * Vanilla keeps its oxidation map in an interface constant, so we can't add our
 * blocks to it. We keep our own maps here instead : oxidation reads them through
 * getNext, the axe through getToolModifiedState, and the honeycomb maps get our
 * WAXABLES injected at common setup (see CommonSetup).
 */
public interface CTBWeatheringCopper extends WeatheringCopper, IForgeBlock {

	BiMap<Block, Block> NEXT_BY_BLOCK = HashBiMap.create();
	BiMap<Block, Block> WAXABLES = HashBiMap.create();

	static Optional<Block> getNextBlock(Block block) {
		return Optional.ofNullable(NEXT_BY_BLOCK.get(block));
	}

	@Override
	default Optional<BlockState> getNext(BlockState state) {
		return getNextBlock(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}

	@Override
	default BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction action, boolean simulate) {
		if (action == ToolActions.AXE_SCRAPE && context.getItemInHand().canPerformAction(action)) {
			Block previous = NEXT_BY_BLOCK.inverse().get(state.getBlock());
			if (previous != null) {
				return previous.withPropertiesOf(state);
			}
		}
		return IForgeBlock.super.getToolModifiedState(state, context, action, simulate);
	}
}
