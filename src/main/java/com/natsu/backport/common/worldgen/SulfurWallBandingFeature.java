package com.natsu.backport.common.worldgen;

import com.mojang.serialization.Codec;
import com.natsu.backport.common.registry.CTBBiomes;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 * The sulfur caves wall look. 26.2 does it with a 3d noise_threshold surface
 * rule (sulfur_cave_gradient, firstOctave -5, amplitudes 1/0/1) banding the
 * stone body into cinnabar and sulfur ; 1.18.2 surface rules cannot sample 3d
 * noise, so the same bands are painted onto the exposed cave surfaces by this
 * raw-generation feature instead, after the carvers have run.
 */
public class SulfurWallBandingFeature extends Feature<NoneFeatureConfiguration> {

	private static final long NOISE_SALT = 0x53554C46L; // "SULF"
	private static final int BAND_DEPTH = 2;

	private static volatile long noiseSeed = Long.MIN_VALUE;
	private static volatile NormalNoise noise;

	public SulfurWallBandingFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		NormalNoise gradient = noiseFor(level.getSeed());
		ChunkPos chunk = new ChunkPos(context.origin());
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int minY = level.getMinBuildHeight() + 1;
		int maxY = Math.min(level.getMaxBuildHeight() - 1, 256);
		boolean any = false;
		for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x++) {
			for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z++) {
				for (int y = minY; y <= maxY; y++) {
					pos.set(x, y, z);
					BlockState state = level.getBlockState(pos);
					if (!isBandable(state) || !isExposed(level, pos)) {
						continue;
					}
					if (!level.getBiome(pos).is(CTBBiomes.SULFUR_CAVES)) {
						continue;
					}
					paintBand(level, gradient, pos);
					any = true;
				}
			}
		}
		return any;
	}

	/** Paints the surface block and a couple behind it, the vanilla surface depth. */
	private void paintBand(WorldGenLevel level, NormalNoise gradient, BlockPos surface) {
		BlockPos.MutableBlockPos pos = surface.mutable();
		for (Direction direction : Direction.values()) {
			if (!isOpen(level.getBlockState(surface.relative(direction)))) {
				continue;
			}
			pos.set(surface);
			Direction inward = direction.getOpposite();
			for (int depth = 0; depth < BAND_DEPTH; depth++) {
				BlockState state = level.getBlockState(pos);
				if (!isBandable(state)) {
					break;
				}
				level.setBlock(pos, bandState(gradient, pos), 2);
				pos.move(inward);
			}
			return;
		}
	}

	/** The vanilla noise bands : cinnabar / sulfur / cinnabar, stone between them. */
	private BlockState bandState(NormalNoise gradient, BlockPos pos) {
		double value = gradient.getValue(pos.getX(), pos.getY(), pos.getZ());
		if (value >= -0.4 && value < -0.1 || value >= 0.4) {
			return CTBBlocks.CINNABAR.get().defaultBlockState();
		}
		if (value >= 0.0 && value < 0.4) {
			return CTBBlocks.SULFUR.get().defaultBlockState();
		}
		return Blocks.STONE.defaultBlockState();
	}

	private static boolean isBandable(BlockState state) {
		return state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE) || state.is(Blocks.GRANITE)
				|| state.is(Blocks.DIORITE) || state.is(Blocks.ANDESITE) || state.is(Blocks.TUFF);
	}

	private static boolean isOpen(BlockState state) {
		return state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.LAVA);
	}

	private static boolean isExposed(WorldGenLevel level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (isOpen(level.getBlockState(pos.relative(direction)))) {
				return true;
			}
		}
		return false;
	}

	private static NormalNoise noiseFor(long seed) {
		NormalNoise cached = noise;
		if (cached == null || noiseSeed != seed) {
			RandomSource random = new XoroshiroRandomSource(seed ^ NOISE_SALT);
			cached = NormalNoise.create(random, -5, 1.0, 0.0, 1.0);
			noise = cached;
			noiseSeed = seed;
		}
		return cached;
	}
}
