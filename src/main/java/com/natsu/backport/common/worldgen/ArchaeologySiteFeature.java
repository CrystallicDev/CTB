package com.natsu.backport.common.worldgen;

import java.util.Random;

import com.mojang.serialization.Codec;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.entity.BrushableBlockEntity;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/**
 * The 1.20 archaeology sites : suspicious sand seeded around desert pyramids
 * and suspicious gravel in ocean ruins, each holding the matching dig loot.
 */
public class ArchaeologySiteFeature extends Feature<NoneFeatureConfiguration> {

	public ArchaeologySiteFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		Random random = context.random();
		BlockPos origin = context.origin();
		boolean any = false;
		any |= this.seedStructure(level, random, origin, StructureFeature.DESERT_PYRAMID,
				Blocks.SAND, CTBBlocks.SUSPICIOUS_SAND.get(),
				new ResourceLocation(CTBackport.MODID, "archaeology/desert_pyramid"), 6);
		boolean warm = level.getBiome(origin).unwrapKey()
				.map(key -> key.location().getPath().contains("warm")).orElse(false);
		any |= this.seedStructure(level, random, origin, StructureFeature.OCEAN_RUIN,
				Blocks.GRAVEL, CTBBlocks.SUSPICIOUS_GRAVEL.get(),
				new ResourceLocation(CTBackport.MODID,
						warm ? "archaeology/ocean_ruin_warm" : "archaeology/ocean_ruin_cold"), 4);
		return any;
	}

	private boolean seedStructure(WorldGenLevel level, Random random, BlockPos origin,
			StructureFeature<?> structure, Block replaceable, Block suspicious, ResourceLocation loot, int count) {
		boolean placed = false;
		for (StructureStart start : level.getLevel().structureFeatureManager()
				.startsForFeature(SectionPos.of(origin), configured -> configured.feature == structure)) {
			BoundingBox box = start.getBoundingBox();
			for (int i = 0; i < count; i++) {
				int x = Mth.clamp(box.minX() + random.nextInt(Math.max(1, box.getXSpan())),
						origin.getX(), origin.getX() + 15);
				int z = Mth.clamp(box.minZ() + random.nextInt(Math.max(1, box.getZSpan())),
						origin.getZ(), origin.getZ() + 15);
				BlockPos top = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, new BlockPos(x, 0, z)).below();
				// dig one or two blocks down so the find is buried
				BlockPos target = top.below(random.nextInt(2));
				if (!level.getBlockState(target).is(replaceable)) {
					continue;
				}
				level.setBlock(target, suspicious.defaultBlockState(), 2);
				if (level.getBlockEntity(target) instanceof BrushableBlockEntity entity) {
					entity.setLootTable(loot, random.nextLong());
				}
				placed = true;
			}
		}
		return placed;
	}
}
