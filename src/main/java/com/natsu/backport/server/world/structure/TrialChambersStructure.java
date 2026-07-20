package com.natsu.backport.server.world.structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.natsu.backport.CTBackport;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class TrialChambersStructure extends StructureFeature<JigsawConfiguration> {

	// vanilla values from the 1.21 structure json
	private static final int MAX_DEPTH = 20;
	private static final int MAX_DISTANCE_FROM_CENTER = 116;
	private static final int MIN_Y = -40;
	private static final int MAX_Y = -20;

	public TrialChambersStructure() {
		super(JigsawConfiguration.CODEC, TrialChambersStructure::pieceGenerator);
	}

	// modded structures are absent from the vanilla step map
	@Override
	public GenerationStep.Decoration step() {
		return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
	}

	private static Optional<PieceGenerator<JigsawConfiguration>> pieceGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
		WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
		random.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);

		int y = Mth.randomBetweenInclusive(random, MIN_Y, MAX_Y);
		BlockPos startPos = new BlockPos(context.chunkPos().getMinBlockX(), y, context.chunkPos().getMinBlockZ());

		// the json size is capped at 7 by the codec, the real depth lives here
		PieceGeneratorSupplier.Context<JigsawConfiguration> deepContext = new PieceGeneratorSupplier.Context<>(
				context.chunkGenerator(), context.biomeSource(), context.seed(), context.chunkPos(),
				new JigsawConfiguration(context.config().startPool(), MAX_DEPTH),
				context.heightAccessor(), context.validBiome(), context.structureManager(), context.registryAccess());

		return CTBJigsawPlacement.addPieces(deepContext, startPos, MAX_DISTANCE_FROM_CENTER, rollPoolAliases(random));
	}

	/**
	 * The 1.21 pool aliases : one consistent ranged mob family per structure,
	 * and one random pick for each melee content pool.
	 */
	private static Map<ResourceLocation, ResourceLocation> rollPoolAliases(WorldgenRandom random) {
		Map<ResourceLocation, ResourceLocation> aliases = new HashMap<>();
		String ranged = pick(random, "skeleton", "stray", "poison_skeleton");
		aliases.put(pool("spawner/contents/ranged"), pool("spawner/ranged/" + ranged));
		aliases.put(pool("spawner/contents/slow_ranged"), pool("spawner/slow_ranged/" + ranged));
		aliases.put(pool("spawner/contents/melee"), pool("spawner/melee/" + pick(random, "zombie", "husk", "spider")));
		aliases.put(pool("spawner/contents/small_melee"), pool("spawner/small_melee/" + pick(random, "slime", "cave_spider", "silverfish", "baby_zombie")));
		return aliases;
	}

	private static String pick(WorldgenRandom random, String... options) {
		return options[random.nextInt(options.length)];
	}

	private static ResourceLocation pool(String path) {
		return new ResourceLocation(CTBackport.MODID, "trial_chambers/" + path);
	}
}
