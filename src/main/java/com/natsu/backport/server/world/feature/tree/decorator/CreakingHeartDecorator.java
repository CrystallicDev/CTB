package com.natsu.backport.server.world.feature.tree.decorator;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;
import com.natsu.backport.common.block.CreakingHeartBlock;
import com.natsu.backport.common.block.state.CreakingHeartState;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBTags;
import com.natsu.backport.common.registry.CTBTreeDecorators;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

/**
 * Vanilla behavior : the heart replaces a trunk log that is fully buried in
 * logs on all six sides, so it only shows up in big or intertwined trees.
 */
public class CreakingHeartDecorator extends TreeDecorator {

	public static final Codec<CreakingHeartDecorator> CODEC = Codec.floatRange(0.0F, 1.0F)
			.fieldOf("probability")
			.xmap(CreakingHeartDecorator::new, d -> d.probability)
			.codec();

	private final float probability;

	public CreakingHeartDecorator(float probability) {
		this.probability = probability;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return CTBTreeDecorators.CREAKING_HEART.get();
	}

	@Override
	public void place(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> setter, Random random,
			List<BlockPos> logs, List<BlockPos> leaves) {
		if (logs.isEmpty() || random.nextFloat() >= this.probability) {
			return;
		}

		// vanilla wants a log buried on all six sides, but a 2x2 trunk never has
		// one below the canopy. Instead : inside the trunk (logs above and below,
		// at least two horizontal log neighbours), as close to mid height as possible.
		int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
		for (BlockPos pos : logs) {
			minY = Math.min(minY, pos.getY());
			maxY = Math.max(maxY, pos.getY());
		}
		int midY = (minY + maxY) / 2;

		BlockPos best = null;
		for (BlockPos pos : logs) {
			if (!isLog(level, pos.above()) || !isLog(level, pos.below())) {
				continue;
			}
			int horizontal = 0;
			for (Direction dir : Direction.Plane.HORIZONTAL) {
				if (isLog(level, pos.relative(dir))) {
					horizontal++;
				}
			}
			if (horizontal >= 2 && (best == null || Math.abs(pos.getY() - midY) < Math.abs(best.getY() - midY))) {
				best = pos;
			}
		}
		if (best != null) {
			setter.accept(best, CTBBlocks.CREAKING_HEART.get().defaultBlockState()
					.setValue(CreakingHeartBlock.STATE, CreakingHeartState.DORMANT)
					.setValue(CreakingHeartBlock.NATURAL, true));
		}
	}

	private static boolean isLog(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, s -> s.is(CTBTags.Blocks.PALE_OAK_LOGS));
	}
}
