package com.natsu.backport.server.world.feature.tree.foliage;

import java.util.Random;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.natsu.backport.common.registry.CTBFoliagePlacers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.material.Fluids;

public class CherryFoliagePlacer extends FoliagePlacer {

	public static final Codec<CherryFoliagePlacer> CODEC = RecordCodecBuilder
			.create(instance -> foliagePlacerParts(instance).and(instance.group(
					IntProvider.codec(4, 16).fieldOf("height").forGetter(p -> p.height),
					Codec.floatRange(0.0F, 1.0F).fieldOf("wide_bottom_layer_hole_chance")
							.forGetter(p -> p.wideBottomLayerHoleChance),
					Codec.floatRange(0.0F, 1.0F).fieldOf("corner_hole_chance").forGetter(p -> p.cornerHoleChance), 
					Codec.floatRange(0.0F, 1.0F).fieldOf("hanging_leaves_chance").forGetter(p -> p.hangingLeavesChance),
					Codec.floatRange(0.0F, 1.0F).fieldOf("hanging_leaves_extension_chance")
							.forGetter(p -> p.hangingLeavesExtensionChance)))
					.apply(instance, CherryFoliagePlacer::new));
	
	private final IntProvider height;
	private final float wideBottomLayerHoleChance;
	private final float cornerHoleChance;
	private final float hangingLeavesChance;
	private final float hangingLeavesExtensionChance;

	public CherryFoliagePlacer(IntProvider p_272646_, IntProvider p_272802_, IntProvider heightProvider, float layerHoleChange,
			float cornerHoleChance, float hangingLeaveChanges, float hangingLeavesExtensionChance) {
		super(p_272646_, p_272802_);
		this.height = heightProvider;
		this.wideBottomLayerHoleChance = layerHoleChange;
		this.cornerHoleChance = cornerHoleChance;
		this.hangingLeavesChance = hangingLeaveChanges;
		this.hangingLeavesExtensionChance = hangingLeavesExtensionChance;
	}

	@Override
	protected FoliagePlacerType<?> type() {
		return CTBFoliagePlacers.CHERRY_FOLIAGE_PLACER.get();
	}

	@Override
	protected void createFoliage(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> foliageSetter,
			Random random, TreeConfiguration treeConfig, int maxFreeTreeHeight, FoliagePlacer.FoliageAttachment attach,
			int foliageHeight, int foliageRadius, int offset) {
		boolean flag = attach.doubleTrunk();
		BlockPos blockpos = attach.pos().above(offset);
		int i = foliageRadius + attach.radiusOffset() - 1;
		this.placeLeavesRow(level, foliageSetter, random, treeConfig, blockpos, i - 2, foliageHeight - 3, flag);
		this.placeLeavesRow(level, foliageSetter, random, treeConfig, blockpos, i - 1, foliageHeight - 4, flag);

		for (int j = foliageHeight - 5; j >= 0; j--) {
			this.placeLeavesRow(level, foliageSetter, random, treeConfig, blockpos, i, j, flag);
		}

		this.placeLeavesRowWithHangingLeavesBelow(level, foliageSetter, random, treeConfig, blockpos, i, -1, flag,
				this.hangingLeavesChance, this.hangingLeavesExtensionChance);
		this.placeLeavesRowWithHangingLeavesBelow(level, foliageSetter, random, treeConfig, blockpos, i - 1, -2, flag,
				this.hangingLeavesChance, this.hangingLeavesExtensionChance);
	}

	@Override
	public int foliageHeight(Random random, int i, TreeConfiguration config) {
		return this.height.sample(random);
	}

	@Override
	protected boolean shouldSkipLocation(Random random, int p_273380_, int p_272865_, int p_272853_, int p_272631_,
			boolean p_273432_) {
		if (p_272865_ == -1 && (p_273380_ == p_272631_ || p_272853_ == p_272631_)
				&& random.nextFloat() < this.wideBottomLayerHoleChance) {
			return true;
		} else {
			boolean flag = p_273380_ == p_272631_ && p_272853_ == p_272631_;
			boolean flag1 = p_272631_ > 2;
			return flag1
					? flag || p_273380_ + p_272853_ > p_272631_ * 2 - 2 && random.nextFloat() < this.cornerHoleChance
					: flag && random.nextFloat() < this.cornerHoleChance;
		}
	}

	protected final void placeLeavesRowWithHangingLeavesBelow(LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> placer, Random random, TreeConfiguration config, BlockPos centerPos,
			int radius, int yOffset, boolean giantTrunk, float chance1, float chance2) {

		this.placeLeavesRow(level, placer, random, config, centerPos, radius, yOffset, giantTrunk);

		int i = giantTrunk ? 1 : 0;
		BlockPos belowPos = centerPos.below();
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			Direction direction1 = direction.getClockWise();
			int j = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? radius + i : radius;

			mutablePos.setWithOffset(centerPos, 0, yOffset - 1, 0).move(direction1, j).move(direction, -radius);

			int k = -radius;

			while (k < radius + i) {
				boolean hasLeavesAbove = level.isStateAtPosition(mutablePos.above(),
						state -> state.is(BlockTags.LEAVES));

				if (hasLeavesAbove && tryPlaceExtension(level, placer, random, config, chance1, belowPos, mutablePos)) {
					mutablePos.move(Direction.DOWN);

					tryPlaceExtension(level, placer, random, config, chance2, belowPos, mutablePos);

					mutablePos.move(Direction.UP);
				}

				k++;
				mutablePos.move(direction);
			}
		}
	}

	private static boolean tryPlaceExtension(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> foliageSetter,
			Random random, TreeConfiguration config, float p_277979_, BlockPos pos,
			BlockPos.MutableBlockPos mutablePos) {
		if (mutablePos.distManhattan(pos) >= 7) {
			return false;
		} else {
			return random.nextFloat() > p_277979_ ? false
					: tryPlaceLeafAdapted(level, foliageSetter, random, config, mutablePos);
		}
	}

	protected static boolean tryPlaceLeafAdapted(LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> foliageSetter, Random random, TreeConfiguration config,
			BlockPos pos) {
		if (!TreeFeature.validTreePos(level, pos)) {
			return false;
		} else {
			BlockState blockstate = config.foliageProvider.getState(random, pos);
			if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED)) {
				blockstate = blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(
						level.isFluidAtPosition(pos, p_225638_ -> p_225638_.isSourceOfType(Fluids.WATER))));
			}

			foliageSetter.accept(pos, blockstate);
			return true;
		}
	}
}
