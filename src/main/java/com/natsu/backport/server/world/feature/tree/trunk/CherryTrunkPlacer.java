package com.natsu.backport.server.world.feature.tree.trunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.natsu.backport.common.registry.CTBTrunkPlacers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class CherryTrunkPlacer extends TrunkPlacer {

	private static final Codec<UniformInt> BRANCH_START_CODEC = UniformInt.CODEC.flatXmap(value -> {
		if (value.getMaxValue() - value.getMinValue() < 1) {
			return DataResult.error("Need at least 2 blocks variation for the branch starts to fit both branches");
		}
		return DataResult.success(value);
	}, DataResult::success);

	public static final Codec<CherryTrunkPlacer> CODEC = RecordCodecBuilder
			.create(instance -> trunkPlacerParts(instance)
					.and(instance.group(IntProvider.codec(1, 3).fieldOf("branch_count").forGetter(p -> p.branchCount),
							IntProvider.codec(2, 16).fieldOf("branch_horizontal_length")
									.forGetter(p -> p.branchHorizontalLength),
							BRANCH_START_CODEC.fieldOf("branch_start_offset_from_top")
									.forGetter(p -> p.branchStartOffsetFromTop),
							IntProvider.codec(-16, 16).fieldOf("branch_end_offset_from_top")
									.forGetter(p -> p.branchEndOffsetFromTop)))
					.apply(instance, CherryTrunkPlacer::new));

	private final IntProvider branchCount;
	private final IntProvider branchHorizontalLength;
	private final UniformInt branchStartOffsetFromTop;
	private final UniformInt secondBranchStartOffsetFromTop;
	private final IntProvider branchEndOffsetFromTop;

	public CherryTrunkPlacer(int p_273281_, int p_273327_, int p_272619_, IntProvider p_272873_, IntProvider p_272789_,
			UniformInt p_272917_, IntProvider p_272948_) {
		super(p_273281_, p_273327_, p_272619_);
		this.branchCount = p_272873_;
		this.branchHorizontalLength = p_272789_;
		this.branchStartOffsetFromTop = p_272917_;
		this.secondBranchStartOffsetFromTop = UniformInt.of(p_272917_.getMinValue(), p_272917_.getMaxValue() - 1);
		this.branchEndOffsetFromTop = p_272948_;
	}

	@Override
	protected TrunkPlacerType<?> type() {
		return CTBTrunkPlacers.CHERRY_TRUNK_PLACER.get();
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> blockSetter, Random random, int p_272990_, BlockPos pos,
			TreeConfiguration config) {
		setDirtAt(level, blockSetter, random, pos.below(), config);
		int i = Math.max(0, p_272990_ - 1 + this.branchStartOffsetFromTop.sample(random));
		int j = Math.max(0, p_272990_ - 1 + this.secondBranchStartOffsetFromTop.sample(random));
		if (j >= i) {
			j++;
		}

		int k = this.branchCount.sample(random);
		boolean flag = k == 3;
		boolean flag1 = k >= 2;
		int l;
		if (flag) {
			l = p_272990_;
		} else if (flag1) {
			l = Math.max(i, j) + 1;
		} else {
			l = i + 1;
		}

		for (int i1 = 0; i1 < l; i1++) {
			placeLog(level, blockSetter, random, pos.above(i1), config);
		}

		List<FoliagePlacer.FoliageAttachment> list = new ArrayList<>();
		if (flag) {
			list.add(new FoliagePlacer.FoliageAttachment(pos.above(l), 0, false));
		}

		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
		Function<BlockState, BlockState> function = p_273382_ -> p_273382_.setValue(RotatedPillarBlock.AXIS,
				direction.getAxis());
		list.add(this.generateBranch(level, blockSetter, random, p_272990_, pos, config, function,
				direction, i, i < l - 1, blockpos$mutableblockpos));
		if (flag1) {
			list.add(this.generateBranch(level, blockSetter, random, p_272990_, pos, config, function,
					direction.getOpposite(), j, j < l - 1, blockpos$mutableblockpos));
		}

		return list;
	}

	private FoliagePlacer.FoliageAttachment generateBranch(LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> foliagePlacer, Random random, int p_272659_, BlockPos pos,
			TreeConfiguration config, Function<BlockState, BlockState> state, Direction branchDirection, int p_272980_,
			boolean p_272719_, BlockPos.MutableBlockPos p_273496_) {
		p_273496_.set(pos).move(Direction.UP, p_272980_);
		int i = p_272659_ - 1 + this.branchEndOffsetFromTop.sample(random);
		boolean flag = p_272719_ || i < p_272980_;
		int j = this.branchHorizontalLength.sample(random) + (flag ? 1 : 0);
		BlockPos blockpos = pos.relative(branchDirection, j).above(i);
		int k = flag ? 2 : 1;

		for (int l = 0; l < k; l++) {
			placeLog(level, foliagePlacer, random, p_273496_.move(branchDirection), config, state);
		}

		Direction direction = blockpos.getY() > p_273496_.getY() ? Direction.UP : Direction.DOWN;

		while (true) {
			int i1 = p_273496_.distManhattan(blockpos);
			if (i1 == 0) {
				return new FoliagePlacer.FoliageAttachment(blockpos.above(), 0, false);
			}

			float f = (float) Math.abs(blockpos.getY() - p_273496_.getY()) / (float) i1;
			boolean flag1 = random.nextFloat() < f;
			p_273496_.move(flag1 ? direction : branchDirection);
			placeLog(level, foliagePlacer, random, p_273496_, config,
					flag1 ? Function.identity() : state);
		}
	}
}