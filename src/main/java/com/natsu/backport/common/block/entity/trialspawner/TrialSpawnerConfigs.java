package com.natsu.backport.common.block.entity.trialspawner;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.SpawnData;

/**
 * The vanilla datapack registry of spawner configs, flattened to a code-side map.
 * Structure NBT references entries through the "config_id" tag.
 */
public class TrialSpawnerConfigs {

	private static Map<ResourceLocation, TrialSpawnerConfig> BY_ID;

	public static TrialSpawnerConfig get(ResourceLocation id) {
		return all().get(id);
	}

	public static synchronized Map<ResourceLocation, TrialSpawnerConfig> all() {
		if (BY_ID == null) {
			BY_ID = new HashMap<>();
			bootstrap();
		}
		return BY_ID;
	}

	private static void bootstrap() {
		register("trial_chamber/breeze",
				TrialSpawnerConfig.builder()
					.simultaneousMobs(1.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20)
					.totalMobs(2.0F).totalMobsAddedPerPlayer(1.0F)
					.spawnPotentials(single(CTBEntities.BREEZE::get)).build(),
				TrialSpawnerConfig.builder()
					.simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20)
					.totalMobs(4.0F).totalMobsAddedPerPlayer(1.0F)
					.spawnPotentials(single(CTBEntities.BREEZE::get)).build());

		register("trial_chamber/melee/husk",
				base().spawnPotentials(single(() -> EntityType.HUSK)).build(),
				base().spawnPotentials(single(() -> EntityType.HUSK)).build());
		register("trial_chamber/melee/spider",
				base().spawnPotentials(single(() -> EntityType.SPIDER)).build(),
				meleeOminous().spawnPotentials(single(() -> EntityType.SPIDER)).build());
		register("trial_chamber/melee/zombie",
				base().spawnPotentials(single(() -> EntityType.ZOMBIE)).build(),
				base().spawnPotentials(single(() -> EntityType.ZOMBIE)).build());

		// the bogged is not backported yet, its spawners fall back to regular skeletons
		register("trial_chamber/ranged/poison_skeleton",
				base().spawnPotentials(single(() -> EntityType.SKELETON)).build(),
				base().spawnPotentials(single(() -> EntityType.SKELETON)).build());
		register("trial_chamber/ranged/skeleton",
				base().spawnPotentials(single(() -> EntityType.SKELETON)).build(),
				base().spawnPotentials(single(() -> EntityType.SKELETON)).build());
		register("trial_chamber/ranged/stray",
				base().spawnPotentials(single(() -> EntityType.STRAY)).build(),
				base().spawnPotentials(single(() -> EntityType.STRAY)).build());
		register("trial_chamber/slow_ranged/poison_skeleton",
				slowRanged().spawnPotentials(single(() -> EntityType.SKELETON)).build(),
				slowRanged().spawnPotentials(single(() -> EntityType.SKELETON)).build());
		register("trial_chamber/slow_ranged/skeleton",
				slowRanged().spawnPotentials(single(() -> EntityType.SKELETON)).build(),
				slowRanged().spawnPotentials(single(() -> EntityType.SKELETON)).build());
		register("trial_chamber/slow_ranged/stray",
				slowRanged().spawnPotentials(single(() -> EntityType.STRAY)).build(),
				slowRanged().spawnPotentials(single(() -> EntityType.STRAY)).build());

		register("trial_chamber/small_melee/baby_zombie",
				TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20)
					.spawnPotentials(single(() -> EntityType.ZOMBIE, tag -> tag.putBoolean("IsBaby", true))).build(),
				TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20)
					.spawnPotentials(single(() -> EntityType.ZOMBIE, tag -> tag.putBoolean("IsBaby", true))).build());
		register("trial_chamber/small_melee/cave_spider",
				base().spawnPotentials(single(() -> EntityType.CAVE_SPIDER)).build(),
				meleeOminous().spawnPotentials(single(() -> EntityType.CAVE_SPIDER)).build());
		register("trial_chamber/small_melee/silverfish",
				base().spawnPotentials(single(() -> EntityType.SILVERFISH)).build(),
				meleeOminous().spawnPotentials(single(() -> EntityType.SILVERFISH)).build());
		register("trial_chamber/small_melee/slime",
				base().spawnPotentials(SimpleWeightedRandomList.<SpawnData>builder()
						.add(spawnData(() -> EntityType.SLIME, tag -> tag.putByte("Size", (byte) 1)), 3)
						.add(spawnData(() -> EntityType.SLIME, tag -> tag.putByte("Size", (byte) 2)), 1)
						.build()).build(),
				meleeOminous().spawnPotentials(SimpleWeightedRandomList.<SpawnData>builder()
						.add(spawnData(() -> EntityType.SLIME, tag -> tag.putByte("Size", (byte) 1)), 3)
						.add(spawnData(() -> EntityType.SLIME, tag -> tag.putByte("Size", (byte) 2)), 1)
						.build()).build());
	}

	private static void register(String id, TrialSpawnerConfig normal, TrialSpawnerConfig ominous) {
		BY_ID.put(new ResourceLocation(CTBackport.MODID, id + "/normal"), normal);
		BY_ID.put(new ResourceLocation(CTBackport.MODID, id + "/ominous"), ominous);
	}

	private static TrialSpawnerConfig.Builder base() {
		return TrialSpawnerConfig.builder().simultaneousMobs(3.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20);
	}

	private static TrialSpawnerConfig.Builder meleeOminous() {
		return TrialSpawnerConfig.builder().simultaneousMobs(4.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).totalMobs(12.0F);
	}

	private static TrialSpawnerConfig.Builder slowRanged() {
		return TrialSpawnerConfig.builder().simultaneousMobs(4.0F).simultaneousMobsAddedPerPlayer(2.0F).ticksBetweenSpawn(160);
	}

	private static SimpleWeightedRandomList<SpawnData> single(Supplier<EntityType<?>> type) {
		return SimpleWeightedRandomList.single(spawnData(type, tag -> {}));
	}

	private static SimpleWeightedRandomList<SpawnData> single(Supplier<EntityType<?>> type, Consumer<CompoundTag> tagModifier) {
		return SimpleWeightedRandomList.single(spawnData(type, tagModifier));
	}

	private static SpawnData spawnData(Supplier<EntityType<?>> type, Consumer<CompoundTag> tagModifier) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", type.get().getRegistryName().toString());
		tagModifier.accept(tag);
		return new SpawnData(tag, java.util.Optional.empty());
	}
}
