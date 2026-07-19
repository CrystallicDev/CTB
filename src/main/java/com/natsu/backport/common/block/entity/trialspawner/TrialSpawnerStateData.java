package com.natsu.backport.common.block.entity.trialspawner;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.serialization.DataResult;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;

public class TrialSpawnerStateData {

	private static final int DELAY_BETWEEN_PLAYER_SCANS = 20;

	public final Set<UUID> detectedPlayers = new HashSet<>();
	public final Set<UUID> currentMobs = new HashSet<>();
	public long cooldownEndsAt;
	public long nextMobSpawnsAt;
	int totalMobsSpawned;
	public Optional<SpawnData> nextSpawnData = Optional.empty();
	Optional<ResourceLocation> ejectingLootTable = Optional.empty();
	@Nullable
	private Entity displayEntity;
	public double spin;
	public double oSpin;

	public void load(CompoundTag tag) {
		this.reset();
		this.resetStatistics();
		readUuids(tag, "registered_players", this.detectedPlayers);
		readUuids(tag, "current_mobs", this.currentMobs);
		this.cooldownEndsAt = tag.getLong("cooldown_ends_at");
		this.nextMobSpawnsAt = tag.getLong("next_mob_spawns_at");
		this.totalMobsSpawned = tag.getInt("total_mobs_spawned");
		if (tag.contains("spawn_data")) {
			this.nextSpawnData = SpawnData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("spawn_data")).result();
		}
		if (tag.contains("ejecting_loot_table")) {
			this.ejectingLootTable = Optional.ofNullable(ResourceLocation.tryParse(tag.getString("ejecting_loot_table")));
		}
		this.displayEntity = null;
	}

	public void save(CompoundTag tag) {
		writeUuids(tag, "registered_players", this.detectedPlayers);
		writeUuids(tag, "current_mobs", this.currentMobs);
		tag.putLong("cooldown_ends_at", this.cooldownEndsAt);
		tag.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
		tag.putInt("total_mobs_spawned", this.totalMobsSpawned);
		this.nextSpawnData.ifPresent(spawnData -> {
			DataResult<Tag> encoded = SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, spawnData);
			encoded.result().ifPresent(t -> tag.put("spawn_data", t));
		});
		this.ejectingLootTable.ifPresent(loot -> tag.putString("ejecting_loot_table", loot.toString()));
	}

	private static void readUuids(CompoundTag tag, String key, Set<UUID> into) {
		into.clear();
		for (Tag entry : tag.getList(key, Tag.TAG_INT_ARRAY)) {
			into.add(NbtUtils.loadUUID(entry));
		}
	}

	private static void writeUuids(CompoundTag tag, String key, Set<UUID> uuids) {
		ListTag list = new ListTag();
		for (UUID uuid : uuids) {
			list.add(NbtUtils.createUUID(uuid));
		}
		tag.put(key, list);
	}

	public void reset() {
		this.currentMobs.clear();
		this.nextSpawnData = Optional.empty();
		this.resetStatistics();
	}

	public void resetStatistics() {
		this.detectedPlayers.clear();
		this.totalMobsSpawned = 0;
		this.nextMobSpawnsAt = 0L;
		this.cooldownEndsAt = 0L;
	}

	public boolean hasMobToSpawn(TrialSpawner spawner, Random random) {
		boolean hasNextMobToSpawn = this.getOrCreateNextSpawnData(spawner, random).getEntityToSpawn().contains("id", Tag.TAG_STRING);
		return hasNextMobToSpawn || !spawner.activeConfig().spawnPotentials().isEmpty();
	}

	public boolean hasFinishedSpawningAllMobs(TrialSpawnerConfig config, int additionalPlayers) {
		return this.totalMobsSpawned >= config.calculateTargetTotalMobs(additionalPlayers);
	}

	public boolean haveAllCurrentMobsDied() {
		return this.currentMobs.isEmpty();
	}

	public boolean isReadyToSpawnNextMob(ServerLevel level, TrialSpawnerConfig config, int additionalPlayers) {
		return level.getGameTime() >= this.nextMobSpawnsAt && this.currentMobs.size() < config.calculateTargetSimultaneousMobs(additionalPlayers);
	}

	public int countAdditionalPlayers() {
		return Math.max(0, this.detectedPlayers.size() - 1);
	}

	public void tryDetectPlayers(ServerLevel level, BlockPos pos, TrialSpawner spawner) {
		boolean isThrottled = (pos.asLong() + level.getGameTime()) % DELAY_BETWEEN_PLAYER_SCANS != 0L;
		if (isThrottled) {
			return;
		}

		// bad omen conversion to trial omen belongs to the ominous phase, and only it
		// can restart a trial during cooldown, so nothing to detect here yet
		if (spawner.getState() == TrialSpawnerState.COOLDOWN) {
			return;
		}

		boolean isSearchingForFirstPlayer = this.detectedPlayers.isEmpty();
		List<UUID> foundPlayers = spawner.getPlayerDetector()
				.detect(level, spawner.getEntitySelector(), pos, spawner.getRequiredPlayerRange(), isSearchingForFirstPlayer);
		if (this.detectedPlayers.addAll(foundPlayers)) {
			this.nextMobSpawnsAt = Math.max(level.getGameTime() + 40L, this.nextMobSpawnsAt);
			spawner.playDetectPlayerEffects(level, pos, this.detectedPlayers.size());
		}
	}

	public boolean isReadyToOpenShutter(ServerLevel level, float delayBeforeOpen, int targetCooldownLength) {
		long cooldownStartedAt = this.cooldownEndsAt - targetCooldownLength;
		return (float) level.getGameTime() >= (float) cooldownStartedAt + delayBeforeOpen;
	}

	public boolean isReadyToEjectItems(ServerLevel level, float timeBetweenEjections, int targetCooldownLength) {
		long cooldownStartedAt = this.cooldownEndsAt - targetCooldownLength;
		return (float) (level.getGameTime() - cooldownStartedAt) % timeBetweenEjections == 0.0F;
	}

	public boolean isCooldownFinished(ServerLevel level) {
		return level.getGameTime() >= this.cooldownEndsAt;
	}

	protected SpawnData getOrCreateNextSpawnData(TrialSpawner spawner, Random random) {
		if (this.nextSpawnData.isPresent()) {
			return this.nextSpawnData.get();
		}

		SimpleWeightedRandomList<SpawnData> potentials = spawner.activeConfig().spawnPotentials();
		Optional<SpawnData> selected = potentials.isEmpty() ? this.nextSpawnData : potentials.getRandomValue(random);
		this.nextSpawnData = Optional.of(selected.orElseGet(SpawnData::new));
		spawner.markUpdated();
		return this.nextSpawnData.get();
	}

	@Nullable
	public Entity getOrCreateDisplayEntity(TrialSpawner spawner, Level level, TrialSpawnerState state) {
		if (!state.hasSpinningMob()) {
			return null;
		}

		if (this.displayEntity == null) {
			CompoundTag entityToSpawn = this.getOrCreateNextSpawnData(spawner, level.getRandom()).getEntityToSpawn();
			if (entityToSpawn.contains("id", Tag.TAG_STRING)) {
				this.displayEntity = EntityType.loadEntityRecursive(entityToSpawn, level, e -> e);
			}
		}

		return this.displayEntity;
	}

	public double getSpin() {
		return this.spin;
	}

	public double getOSpin() {
		return this.oSpin;
	}
}
