package com.natsu.backport.server.world.feature.tree;

import java.util.Optional;
import java.util.Random;

import com.natsu.backport.CTBackport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;

// only grows as a 2x2 like dark oak, the feature comes from the datapack registry
public class PaleOakTreeGrower extends AbstractMegaTreeGrower {

	private static final ResourceKey<ConfiguredFeature<?, ?>> TREE = ResourceKey.create(
			Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(CTBackport.MODID, "pale_oak_bonemeal"));

	@Override
	protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random random, boolean flowers) {
		return null;
	}

	@Override
	protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredMegaFeature(Random random) {
		return null;
	}

	@Override
	public boolean placeMega(ServerLevel level, ChunkGenerator gen, BlockPos pos, BlockState sapling, Random random, int dx, int dz) {
		Optional<? extends Holder<ConfiguredFeature<?, ?>>> found = level.registryAccess()
				.registry(Registry.CONFIGURED_FEATURE_REGISTRY)
				.flatMap(reg -> reg.getHolder(TREE));
		if (found.isEmpty()) {
			return false;
		}

		var event = ForgeEventFactory.blockGrowFeature(level, random, pos, found.get());
		if (event.getResult() == Event.Result.DENY) {
			return false;
		}

		ConfiguredFeature<?, ?> feature = event.getFeature().value();
		BlockState air = Blocks.AIR.defaultBlockState();
		level.setBlock(pos.offset(dx, 0, dz), air, 4);
		level.setBlock(pos.offset(dx + 1, 0, dz), air, 4);
		level.setBlock(pos.offset(dx, 0, dz + 1), air, 4);
		level.setBlock(pos.offset(dx + 1, 0, dz + 1), air, 4);
		if (feature.place(level, gen, random, pos.offset(dx, 0, dz))) {
			return true;
		}
		level.setBlock(pos.offset(dx, 0, dz), sapling, 4);
		level.setBlock(pos.offset(dx + 1, 0, dz), sapling, 4);
		level.setBlock(pos.offset(dx, 0, dz + 1), sapling, 4);
		level.setBlock(pos.offset(dx + 1, 0, dz + 1), sapling, 4);
		return false;
	}
}
