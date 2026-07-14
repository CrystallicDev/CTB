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

		// lowest buried log, so the heart ends up in the trunk and not in the canopy
		BlockPos best = null;
		for (BlockPos pos : logs) {
			boolean buried = true;
			for (Direction dir : Direction.values()) {
				if (!level.isStateAtPosition(pos.relative(dir), s -> s.is(CTBTags.Blocks.PALE_OAK_LOGS))) {
					buried = false;
					break;
				}
			}
			if (buried && (best == null || pos.getY() < best.getY())) {
				best = pos;
			}
		}
		if (best != null) {
			setter.accept(best, CTBBlocks.CREAKING_HEART.get().defaultBlockState()
					.setValue(CreakingHeartBlock.STATE, CreakingHeartState.DORMANT)
					.setValue(CreakingHeartBlock.NATURAL, true));
		}
	}
}
