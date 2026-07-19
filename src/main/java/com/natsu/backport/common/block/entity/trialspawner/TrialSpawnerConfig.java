package com.natsu.backport.common.block.entity.trialspawner;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.natsu.backport.CTBackport;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.SpawnData;

public record TrialSpawnerConfig(
		int spawnRange,
		float totalMobs,
		float simultaneousMobs,
		float totalMobsAddedPerPlayer,
		float simultaneousMobsAddedPerPlayer,
		int ticksBetweenSpawn,
		SimpleWeightedRandomList<SpawnData> spawnPotentials,
		SimpleWeightedRandomList<ResourceLocation> lootTablesToEject
) {

	public static final ResourceLocation LOOT_CONSUMABLES = new ResourceLocation(CTBackport.MODID, "spawners/trial_chamber/consumables");
	public static final ResourceLocation LOOT_KEY = new ResourceLocation(CTBackport.MODID, "spawners/trial_chamber/key");

	public static final TrialSpawnerConfig DEFAULT = builder().build();

	public static final Codec<TrialSpawnerConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange),
			Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs),
			Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs).forGetter(TrialSpawnerConfig::simultaneousMobs),
			Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer),
			Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn),
			SpawnData.LIST_CODEC.optionalFieldOf("spawn_potentials", SimpleWeightedRandomList.empty()).forGetter(TrialSpawnerConfig::spawnPotentials),
			SimpleWeightedRandomList.wrappedCodecAllowingEmpty(ResourceLocation.CODEC).optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject).forGetter(TrialSpawnerConfig::lootTablesToEject)
		).apply(i, TrialSpawnerConfig::new));

	public int calculateTargetTotalMobs(int additionalPlayers) {
		return (int) Math.floor(this.totalMobs + this.totalMobsAddedPerPlayer * additionalPlayers);
	}

	public int calculateTargetSimultaneousMobs(int additionalPlayers) {
		return (int) Math.floor(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * additionalPlayers);
	}

	public TrialSpawnerConfig withSpawning(EntityType<?> type) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", type.getRegistryName().toString());
		SpawnData spawnData = new SpawnData(tag, Optional.empty());
		return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer,
				this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, SimpleWeightedRandomList.single(spawnData), this.lootTablesToEject);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private int spawnRange = 4;
		private float totalMobs = 6.0F;
		private float simultaneousMobs = 2.0F;
		private float totalMobsAddedPerPlayer = 2.0F;
		private float simultaneousMobsAddedPerPlayer = 1.0F;
		private int ticksBetweenSpawn = 40;
		private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
		private SimpleWeightedRandomList<ResourceLocation> lootTablesToEject = SimpleWeightedRandomList.<ResourceLocation>builder()
				.add(LOOT_CONSUMABLES, 1).add(LOOT_KEY, 1).build();

		public Builder spawnRange(int spawnRange) { this.spawnRange = spawnRange; return this; }
		public Builder totalMobs(float totalMobs) { this.totalMobs = totalMobs; return this; }
		public Builder simultaneousMobs(float simultaneousMobs) { this.simultaneousMobs = simultaneousMobs; return this; }
		public Builder totalMobsAddedPerPlayer(float v) { this.totalMobsAddedPerPlayer = v; return this; }
		public Builder simultaneousMobsAddedPerPlayer(float v) { this.simultaneousMobsAddedPerPlayer = v; return this; }
		public Builder ticksBetweenSpawn(int ticksBetweenSpawn) { this.ticksBetweenSpawn = ticksBetweenSpawn; return this; }
		public Builder spawnPotentials(SimpleWeightedRandomList<SpawnData> list) { this.spawnPotentials = list; return this; }
		public Builder lootTablesToEject(SimpleWeightedRandomList<ResourceLocation> list) { this.lootTablesToEject = list; return this; }

		public TrialSpawnerConfig build() {
			return new TrialSpawnerConfig(spawnRange, totalMobs, simultaneousMobs, totalMobsAddedPerPlayer,
					simultaneousMobsAddedPerPlayer, ticksBetweenSpawn, spawnPotentials, lootTablesToEject);
		}
	}
}
