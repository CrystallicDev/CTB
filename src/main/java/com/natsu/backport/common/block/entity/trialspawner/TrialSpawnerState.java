package com.natsu.backport.common.block.entity.trialspawner;

import java.util.Random;

import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public enum TrialSpawnerState implements StringRepresentable {
	INACTIVE("inactive", 0, ParticleEmission.NONE, -1.0, false),
	WAITING_FOR_PLAYERS("waiting_for_players", 4, ParticleEmission.SMALL_FLAMES, 200.0, true),
	ACTIVE("active", 8, ParticleEmission.FLAMES_AND_SMOKE, 1000.0, true),
	WAITING_FOR_REWARD_EJECTION("waiting_for_reward_ejection", 8, ParticleEmission.SMALL_FLAMES, -1.0, false),
	EJECTING_REWARD("ejecting_reward", 8, ParticleEmission.SMALL_FLAMES, -1.0, false),
	COOLDOWN("cooldown", 0, ParticleEmission.SMOKE_INSIDE_AND_TOP_FACE, -1.0, false);

	private static final float DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB = 40.0F;
	private static final int TIME_BETWEEN_EACH_EJECTION = Mth.floor(30.0F);

	private final String name;
	private final int lightLevel;
	private final double spinningMobSpeed;
	private final ParticleEmission particleEmission;
	private final boolean isCapableOfSpawning;

	TrialSpawnerState(String name, int lightLevel, ParticleEmission particleEmission, double spinningMobSpeed, boolean isCapableOfSpawning) {
		this.name = name;
		this.lightLevel = lightLevel;
		this.particleEmission = particleEmission;
		this.spinningMobSpeed = spinningMobSpeed;
		this.isCapableOfSpawning = isCapableOfSpawning;
	}

	TrialSpawnerState tickAndGetNext(BlockPos pos, TrialSpawner spawner, ServerLevel level) {
		TrialSpawnerStateData data = spawner.getStateData();
		TrialSpawnerConfig config = spawner.activeConfig();
		Random random = level.getRandom();

		return switch (this) {
			case INACTIVE -> data.getOrCreateDisplayEntity(spawner, level, WAITING_FOR_PLAYERS) == null ? this : WAITING_FOR_PLAYERS;
			case WAITING_FOR_PLAYERS -> {
				if (!spawner.canSpawnInLevel(level)) {
					data.resetStatistics();
					yield this;
				} else if (!data.hasMobToSpawn(spawner, random)) {
					yield INACTIVE;
				} else {
					data.tryDetectPlayers(level, pos, spawner);
					yield data.detectedPlayers.isEmpty() ? this : ACTIVE;
				}
			}
			case ACTIVE -> {
				if (!spawner.canSpawnInLevel(level)) {
					data.resetStatistics();
					yield WAITING_FOR_PLAYERS;
				} else if (!data.hasMobToSpawn(spawner, random)) {
					yield INACTIVE;
				} else {
					int additionalPlayers = data.countAdditionalPlayers();
					data.tryDetectPlayers(level, pos, spawner);
					// the ominous item spawner drops belong to the ominous phase, not wired yet

					if (data.hasFinishedSpawningAllMobs(config, additionalPlayers)) {
						if (data.haveAllCurrentMobsDied()) {
							data.cooldownEndsAt = level.getGameTime() + spawner.getTargetCooldownLength();
							data.totalMobsSpawned = 0;
							data.nextMobSpawnsAt = 0L;
							yield WAITING_FOR_REWARD_EJECTION;
						}
					} else if (data.isReadyToSpawnNextMob(level, config, additionalPlayers)) {
						spawner.spawnMob(level, pos).ifPresent(entityId -> {
							data.currentMobs.add(entityId);
							data.totalMobsSpawned++;
							data.nextMobSpawnsAt = level.getGameTime() + config.ticksBetweenSpawn();
							config.spawnPotentials().getRandom(random).ifPresent(entry -> {
								data.nextSpawnData = java.util.Optional.of(entry.getData());
								spawner.markUpdated();
							});
						});
					}

					yield this;
				}
			}
			case WAITING_FOR_REWARD_EJECTION -> {
				if (data.isReadyToOpenShutter(level, DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB, spawner.getTargetCooldownLength())) {
					level.playSound(null, pos, CTBSounds.TRIAL_SPAWNER_OPEN_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
					yield EJECTING_REWARD;
				} else {
					yield this;
				}
			}
			case EJECTING_REWARD -> {
				if (!data.isReadyToEjectItems(level, TIME_BETWEEN_EACH_EJECTION, spawner.getTargetCooldownLength())) {
					yield this;
				} else if (data.detectedPlayers.isEmpty()) {
					level.playSound(null, pos, CTBSounds.TRIAL_SPAWNER_CLOSE_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
					data.ejectingLootTable = java.util.Optional.empty();
					yield COOLDOWN;
				} else {
					if (data.ejectingLootTable.isEmpty()) {
						data.ejectingLootTable = config.lootTablesToEject().getRandomValue(random);
					}

					data.ejectingLootTable.ifPresent(lootTable -> spawner.ejectReward(level, pos, (ResourceLocation) lootTable));
					data.detectedPlayers.remove(data.detectedPlayers.iterator().next());
					yield this;
				}
			}
			case COOLDOWN -> {
				data.tryDetectPlayers(level, pos, spawner);
				if (!data.detectedPlayers.isEmpty()) {
					data.totalMobsSpawned = 0;
					data.nextMobSpawnsAt = 0L;
					yield ACTIVE;
				} else if (data.isCooldownFinished(level)) {
					spawner.removeOminous(level, pos);
					data.reset();
					yield WAITING_FOR_PLAYERS;
				} else {
					yield this;
				}
			}
		};
	}

	public int lightLevel() {
		return this.lightLevel;
	}

	public double spinningMobSpeed() {
		return this.spinningMobSpeed;
	}

	public boolean hasSpinningMob() {
		return this.spinningMobSpeed >= 0.0;
	}

	public boolean isCapableOfSpawning() {
		return this.isCapableOfSpawning;
	}

	public void emitParticles(Level level, BlockPos pos, boolean isOminous) {
		this.particleEmission.emit(level, level.getRandom(), pos, isOminous);
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	private interface ParticleEmission {
		ParticleEmission NONE = (level, random, pos, isOminous) -> {};
		ParticleEmission SMALL_FLAMES = (level, random, pos, isOminous) -> {
			if (random.nextInt(2) == 0) {
				Vec3 vec = offsetRandom(Vec3.atCenterOf(pos), random, 0.9F);
				addParticle(isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, vec, level);
			}
		};
		ParticleEmission FLAMES_AND_SMOKE = (level, random, pos, isOminous) -> {
			Vec3 vec = offsetRandom(Vec3.atCenterOf(pos), random, 1.0F);
			addParticle(ParticleTypes.SMOKE, vec, level);
			addParticle(isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, vec, level);
		};
		ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = (level, random, pos, isOminous) -> {
			Vec3 vec = offsetRandom(Vec3.atCenterOf(pos), random, 0.9F);
			if (random.nextInt(3) == 0) {
				addParticle(ParticleTypes.SMOKE, vec, level);
			}

			if (level.getGameTime() % 20L == 0L) {
				Vec3 topFace = Vec3.atCenterOf(pos).add(0.0, 0.5, 0.0);
				int smokeCount = level.getRandom().nextInt(4) + 20;
				for (int i = 0; i < smokeCount; i++) {
					addParticle(ParticleTypes.SMOKE, topFace, level);
				}
			}
		};

		private static Vec3 offsetRandom(Vec3 vec, Random random, float spread) {
			return vec.add((random.nextFloat() - 0.5F) * spread, (random.nextFloat() - 0.5F) * spread, (random.nextFloat() - 0.5F) * spread);
		}

		private static void addParticle(SimpleParticleType particle, Vec3 vec, Level level) {
			level.addParticle(particle, vec.x(), vec.y(), vec.z(), 0.0, 0.0, 0.0);
		}

		void emit(Level level, Random random, BlockPos pos, boolean isOminous);
	}
}
