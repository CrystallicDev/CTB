package com.natsu.backport.common.worldgen;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * The 26.2 sulfur_spike feature : one small stalagmite or stalactite. The
 * vanilla data drives it through a random selector of two speleothem entries
 * (one scanning down for a floor, one scanning up for a ceiling) ; both scans
 * are folded in here.
 */
public class SulfurSpikeFeature extends Feature<NoneFeatureConfiguration> {

	private static final float CHANCE_OF_TALLER_GENERATION = 0.2F;
	private static final float CHANCE_OF_DIRECTIONAL_SPREAD = 0.7F;
	private static final float CHANCE_OF_SPREAD_RADIUS2 = 0.5F;
	private static final float CHANCE_OF_SPREAD_RADIUS3 = 0.5F;
	private static final int SCAN_STEPS = 12;

	public SulfurSpikeFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		Random random = context.random();
		boolean scanDown = random.nextBoolean();
		BlockPos pos = scanToSurface(level, context.origin(), scanDown ? Direction.DOWN : Direction.UP);
		if (pos == null) {
			return false;
		}
		// the vanilla random_offset : one block back towards the open side
		pos = pos.relative(scanDown ? Direction.UP : Direction.DOWN);

		Block base = CTBBlocks.SULFUR.get();
		Block pointed = CTBBlocks.SULFUR_SPIKE.get();
		Optional<Direction> tipDirection = getTipDirection(level, pos, random, base);
		if (tipDirection.isEmpty()) {
			return false;
		}
		BlockPos rootPos = pos.relative(tipDirection.get().getOpposite());
		SulfurSpeleothemUtils.createPatchOfBaseBlocks(level, random, rootPos, base,
				CHANCE_OF_DIRECTIONAL_SPREAD, CHANCE_OF_SPREAD_RADIUS2, CHANCE_OF_SPREAD_RADIUS3);
		int height = random.nextFloat() < CHANCE_OF_TALLER_GENERATION
				&& SulfurSpeleothemUtils.isEmptyOrWater(level.getBlockState(pos.relative(tipDirection.get()))) ? 2 : 1;
		SulfurSpeleothemUtils.growSpeleothem(level, pos, tipDirection.get(), height, false, base, pointed);
		return true;
	}

	/** Walks through air and water towards the first solid block, like the data's environment_scan. */
	private static BlockPos scanToSurface(WorldGenLevel level, BlockPos origin, Direction direction) {
		BlockPos.MutableBlockPos pos = origin.mutable();
		for (int i = 0; i <= SCAN_STEPS; i++) {
			BlockState state = level.getBlockState(pos);
			if (state.getMaterial().isSolidBlocking()) {
				return pos.immutable();
			}
			if (!SulfurSpeleothemUtils.isEmptyOrWater(state)) {
				return null;
			}
			pos.move(direction);
		}
		return null;
	}

	private static Optional<Direction> getTipDirection(WorldGenLevel level, BlockPos pos, Random random, Block base) {
		boolean canPlaceAbove = SulfurSpeleothemUtils.isBase(level.getBlockState(pos.above()), base);
		boolean canPlaceBelow = SulfurSpeleothemUtils.isBase(level.getBlockState(pos.below()), base);
		if (canPlaceAbove && canPlaceBelow) {
			return Optional.of(random.nextBoolean() ? Direction.DOWN : Direction.UP);
		}
		if (canPlaceAbove) {
			return Optional.of(Direction.DOWN);
		}
		if (canPlaceBelow) {
			return Optional.of(Direction.UP);
		}
		return Optional.empty();
	}
}
