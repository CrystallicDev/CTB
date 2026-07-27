package com.natsu.backport.common.worldgen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * The 1.21.5 fallen trees : a one block stump and a sideways log a couple of
 * blocks away, mushrooms on top, vines on some stumps. Port of the 26.2
 * FallenTreeFeature with the two vanilla decorators folded in.
 */
public class FallenTreeFeature extends Feature<FallenTreeFeature.Config> {

	public record Config(BlockState trunk, int minLength, int maxLength, boolean stumpVines,
			float mushroomProbability) implements FeatureConfiguration {

		public static final Codec<Config> CODEC = RecordCodecBuilder.create(i -> i.group(
				BlockState.CODEC.fieldOf("trunk").forGetter(Config::trunk),
				Codec.INT.fieldOf("min_length").forGetter(Config::minLength),
				Codec.INT.fieldOf("max_length").forGetter(Config::maxLength),
				Codec.BOOL.fieldOf("stump_vines").forGetter(Config::stumpVines),
				Codec.FLOAT.fieldOf("mushroom_probability").forGetter(Config::mushroomProbability))
				.apply(i, Config::new));
	}

	private static final int FALLEN_LOG_MAX_GROUND_GAP = 2;

	public FallenTreeFeature(Codec<Config> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> context) {
		Config config = context.config();
		WorldGenLevel level = context.level();
		Random random = context.random();
		BlockPos origin = context.origin();

		placeStump(config, level, random, origin);

		Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
		int logLength = (config.minLength + random.nextInt(config.maxLength - config.minLength + 1)) - 2;
		BlockPos.MutableBlockPos logStartPos = origin.relative(direction, 2 + random.nextInt(2)).mutable();
		setGroundHeightForFallenLogStartPos(level, logStartPos);
		if (canPlaceEntireFallenLog(level, logLength, logStartPos, direction)) {
			placeFallenLog(config, level, random, logLength, logStartPos, direction);
		}
		return true;
	}

	private void placeStump(Config config, WorldGenLevel level, Random random, BlockPos origin) {
		BlockPos stump = placeLogBlock(config, level, random, origin.mutable(), Function.identity());
		if (config.stumpVines) {
			// the vanilla trunk_vine decorator on a single log
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				if (random.nextInt(4) == 0) {
					BlockPos side = stump.relative(direction);
					if (level.isEmptyBlock(side)) {
						level.setBlock(side, Blocks.VINE.defaultBlockState()
								.setValue(VineBlock.getPropertyForFace(direction.getOpposite()), true), 19);
					}
				}
			}
		}
	}

	private void setGroundHeightForFallenLogStartPos(WorldGenLevel level, BlockPos.MutableBlockPos logStartPos) {
		logStartPos.move(Direction.UP, 1);
		for (int i = 0; i < 6; i++) {
			if (mayPlaceOn(level, logStartPos)) {
				return;
			}
			logStartPos.move(Direction.DOWN);
		}
	}

	private boolean canPlaceEntireFallenLog(WorldGenLevel level, int logLength,
			BlockPos.MutableBlockPos logStartPos, Direction direction) {
		int gapInGround = 0;
		for (int i = 0; i < logLength; i++) {
			if (!TreeFeature.validTreePos(level, logStartPos)) {
				return false;
			}
			if (!isOverSolidGround(level, logStartPos)) {
				gapInGround++;
				if (gapInGround > FALLEN_LOG_MAX_GROUND_GAP) {
					return false;
				}
			} else {
				gapInGround = 0;
			}
			logStartPos.move(direction);
		}
		logStartPos.move(direction.getOpposite(), logLength);
		return true;
	}

	private void placeFallenLog(Config config, WorldGenLevel level, Random random, int logLength,
			BlockPos.MutableBlockPos logStartPos, Direction direction) {
		Set<BlockPos> fallenLog = new HashSet<>();
		for (int i = 0; i < logLength; i++) {
			fallenLog.add(placeLogBlock(config, level, random, logStartPos,
					state -> state.hasProperty(RotatedPillarBlock.AXIS)
							? state.setValue(RotatedPillarBlock.AXIS, direction.getAxis()) : state));
			logStartPos.move(direction);
		}
		// the vanilla attached_to_logs decorator, mushrooms on top of the log
		for (BlockPos log : fallenLog) {
			if (random.nextFloat() >= config.mushroomProbability) {
				continue;
			}
			BlockPos above = log.above();
			if (level.isEmptyBlock(above)) {
				BlockState mushroom = random.nextInt(3) < 2 ? Blocks.RED_MUSHROOM.defaultBlockState()
						: Blocks.BROWN_MUSHROOM.defaultBlockState();
				level.setBlock(above, mushroom, 19);
			}
		}
	}

	private boolean mayPlaceOn(WorldGenLevel level, BlockPos pos) {
		return TreeFeature.validTreePos(level, pos) && isOverSolidGround(level, pos);
	}

	private boolean isOverSolidGround(WorldGenLevel level, BlockPos pos) {
		return level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP);
	}

	private BlockPos placeLogBlock(Config config, WorldGenLevel level, Random random,
			BlockPos.MutableBlockPos pos, Function<BlockState, BlockState> sidewaysStateModifier) {
		level.setBlock(pos, sidewaysStateModifier.apply(config.trunk), 3);
		this.markAboveForPostProcessing(level, pos);
		return pos.immutable();
	}
}
