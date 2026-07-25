package com.natsu.backport.common.block;

import java.util.Random;
import java.util.function.Supplier;

import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;

/** A potted eyeblossom follows the day and night cycle, like the planted one. */
public class PottedEyeblossomBlock extends FlowerPotBlock {

	private final boolean open;
	private final Supplier<Block> other;

	public PottedEyeblossomBlock(boolean open, Supplier<Block> other, Supplier<? extends Block> plant, Properties props) {
		super(() -> (FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT, plant, props);
		this.open = open;
		this.other = other;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (level.dimensionType().natural() && CreakingHeartBlock.isNaturalNight(level) != this.open) {
			level.setBlockAndUpdate(pos, this.other.get().defaultBlockState());
			level.playSound(null, pos,
					this.open ? CTBSounds.EYEBLOSSOM_CLOSE_LONG.get() : CTBSounds.EYEBLOSSOM_OPEN_LONG.get(),
					SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}
}
