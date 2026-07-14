package com.natsu.backport.server.world.feature.tree.decorator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.HangingMossBlock;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBTreeDecorators;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

/**
 * Moss patch at the foot of the tree, hanging moss under the logs and leaves.
 */
public class PaleMossDecorator extends TreeDecorator {

	public static final Codec<PaleMossDecorator> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.floatRange(0.0F, 1.0F).fieldOf("leaves_probability").forGetter(d -> d.leavesProbability),
			Codec.floatRange(0.0F, 1.0F).fieldOf("trunk_probability").forGetter(d -> d.trunkProbability),
			Codec.floatRange(0.0F, 1.0F).fieldOf("ground_probability").forGetter(d -> d.groundProbability))
			.apply(i, PaleMossDecorator::new));

	private final float leavesProbability;
	private final float trunkProbability;
	private final float groundProbability;

	public PaleMossDecorator(float leavesProbability, float trunkProbability, float groundProbability) {
		this.leavesProbability = leavesProbability;
		this.trunkProbability = trunkProbability;
		this.groundProbability = groundProbability;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return CTBTreeDecorators.PALE_MOSS.get();
	}

	@Override
	public void place(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> setter, Random random,
			List<BlockPos> logs, List<BlockPos> leaves) {
		if (logs.isEmpty()) {
			return;
		}

		BlockPos lowest = Collections.min(logs, Comparator.comparingInt(Vec3i::getY));
		if (level instanceof WorldGenLevel wgl && random.nextFloat() < this.groundProbability) {
			wgl.registryAccess().registry(Registry.CONFIGURED_FEATURE_REGISTRY)
					.flatMap(reg -> reg.getOptional(new ResourceLocation(CTBackport.MODID, "pale_moss_patch")))
					.ifPresent(patch -> patch.place(wgl, wgl.getLevel().getChunkSource().getGenerator(), random, lowest.above()));
		}

		for (BlockPos pos : logs) {
			if (random.nextFloat() < this.trunkProbability && isAir(level, pos.below())) {
				addMossHanger(level, setter, random, pos.below());
			}
		}
		for (BlockPos pos : leaves) {
			if (random.nextFloat() < this.leavesProbability && isAir(level, pos.below())) {
				addMossHanger(level, setter, random, pos.below());
			}
		}
	}

	private static void addMossHanger(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> setter,
			Random random, BlockPos pos) {
		BlockState moss = CTBBlocks.PALE_HANGING_MOSS.get().defaultBlockState();
		while (isAir(level, pos.below()) && random.nextFloat() >= 0.5F) {
			setter.accept(pos, moss.setValue(HangingMossBlock.TIP, false));
			pos = pos.below();
		}
		setter.accept(pos, moss.setValue(HangingMossBlock.TIP, true));
	}

	private static boolean isAir(LevelSimulatedReader level, BlockPos pos) {
		return level.isStateAtPosition(pos, BlockState::isAir);
	}
}
