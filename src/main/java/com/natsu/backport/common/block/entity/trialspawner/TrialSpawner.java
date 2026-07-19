package com.natsu.backport.common.block.entity.trialspawner;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import com.natsu.backport.common.block.TrialSpawnerBlock;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TrialSpawner {

	public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
	private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 36000;
	private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;
	private static final int MAX_MOB_TRACKING_DISTANCE = 47;
	private static final int MAX_MOB_TRACKING_DISTANCE_SQR = MAX_MOB_TRACKING_DISTANCE * MAX_MOB_TRACKING_DISTANCE;
	private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;

	private final TrialSpawnerStateData data = new TrialSpawnerStateData();
	private final StateAccessor stateAccessor;
	private PlayerDetector playerDetector;
	private PlayerDetector.EntitySelector entitySelector;
	private boolean overridePeacefulAndMobSpawnRule;
	private boolean isOminous;

	private TrialSpawnerConfig normalConfig = TrialSpawnerConfig.DEFAULT;
	private TrialSpawnerConfig ominousConfig = TrialSpawnerConfig.DEFAULT;
	private int targetCooldownLength = DEFAULT_TARGET_COOLDOWN_LENGTH;
	private int requiredPlayerRange = DEFAULT_PLAYER_SCAN_RANGE;

	public TrialSpawner(StateAccessor stateAccessor, PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
		this.stateAccessor = stateAccessor;
		this.playerDetector = playerDetector;
		this.entitySelector = entitySelector;
	}

	public TrialSpawnerConfig activeConfig() {
		return this.isOminous ? this.ominousConfig : this.normalConfig;
	}

	public TrialSpawnerConfig normalConfig() {
		return this.normalConfig;
	}

	public TrialSpawnerConfig ominousConfig() {
		return this.ominousConfig;
	}

	public void load(CompoundTag tag) {
		this.data.load(tag);
		if (tag.contains("config_id")) {
			// structure placed spawners point at the code-side config registry
			ResourceLocation id = ResourceLocation.tryParse(tag.getString("config_id"));
			TrialSpawnerConfig normal = TrialSpawnerConfigs.get(new ResourceLocation(id.getNamespace(), id.getPath() + "/normal"));
			TrialSpawnerConfig ominous = TrialSpawnerConfigs.get(new ResourceLocation(id.getNamespace(), id.getPath() + "/ominous"));
			this.normalConfig = normal != null ? normal : TrialSpawnerConfig.DEFAULT;
			this.ominousConfig = ominous != null ? ominous : this.normalConfig;
		} else {
			if (tag.contains("normal_config")) {
				this.normalConfig = TrialSpawnerConfig.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("normal_config"))
						.result().orElse(TrialSpawnerConfig.DEFAULT);
			}
			if (tag.contains("ominous_config")) {
				this.ominousConfig = TrialSpawnerConfig.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("ominous_config"))
						.result().orElse(this.normalConfig);
			} else {
				this.ominousConfig = this.normalConfig;
			}
		}
		if (tag.contains("target_cooldown_length")) {
			this.targetCooldownLength = tag.getInt("target_cooldown_length");
		}
		if (tag.contains("required_player_range")) {
			this.requiredPlayerRange = tag.getInt("required_player_range");
		}
	}

	public void save(CompoundTag tag) {
		this.data.save(tag);
		TrialSpawnerConfig.CODEC.encodeStart(NbtOps.INSTANCE, this.normalConfig).result()
				.ifPresent(t -> tag.put("normal_config", t));
		TrialSpawnerConfig.CODEC.encodeStart(NbtOps.INSTANCE, this.ominousConfig).result()
				.ifPresent(t -> tag.put("ominous_config", t));
		tag.putInt("target_cooldown_length", this.targetCooldownLength);
		tag.putInt("required_player_range", this.requiredPlayerRange);
	}

	public void applyOminous(ServerLevel level, BlockPos pos) {
		level.setBlock(pos, level.getBlockState(pos).setValue(TrialSpawnerBlock.OMINOUS, true), Block.UPDATE_ALL);
		this.isOminous = true;
	}

	public void removeOminous(ServerLevel level, BlockPos pos) {
		level.setBlock(pos, level.getBlockState(pos).setValue(TrialSpawnerBlock.OMINOUS, false), Block.UPDATE_ALL);
		this.isOminous = false;
	}

	public boolean isOminous() {
		return this.isOminous;
	}

	public int getTargetCooldownLength() {
		return this.targetCooldownLength;
	}

	public int getRequiredPlayerRange() {
		return this.requiredPlayerRange;
	}

	public TrialSpawnerState getState() {
		return this.stateAccessor.getState();
	}

	public TrialSpawnerStateData getStateData() {
		return this.data;
	}

	public void setState(Level level, TrialSpawnerState state) {
		this.stateAccessor.setState(level, state);
	}

	public void markUpdated() {
		this.stateAccessor.markUpdated();
	}

	public PlayerDetector getPlayerDetector() {
		return this.playerDetector;
	}

	public PlayerDetector.EntitySelector getEntitySelector() {
		return this.entitySelector;
	}

	public boolean canSpawnInLevel(ServerLevel level) {
		return this.overridePeacefulAndMobSpawnRule
				|| level.getDifficulty() != Difficulty.PEACEFUL && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
	}

	public Optional<UUID> spawnMob(ServerLevel level, BlockPos pos) {
		Random random = level.getRandom();
		SpawnData nextSpawnData = this.data.getOrCreateNextSpawnData(this, random);
		CompoundTag entityTag = nextSpawnData.getEntityToSpawn();
		Optional<EntityType<?>> entityType = EntityType.by(entityTag);
		if (entityType.isEmpty()) {
			return Optional.empty();
		}

		ListTag posTag = entityTag.getList("Pos", ListTag.TAG_DOUBLE);
		TrialSpawnerConfig config = this.activeConfig();
		double x = posTag.size() >= 1 ? posTag.getDouble(0)
				: pos.getX() + (random.nextDouble() - random.nextDouble()) * config.spawnRange() + 0.5;
		double y = posTag.size() >= 2 ? posTag.getDouble(1) : pos.getY() + random.nextInt(3) - 1;
		double z = posTag.size() >= 3 ? posTag.getDouble(2)
				: pos.getZ() + (random.nextDouble() - random.nextDouble()) * config.spawnRange() + 0.5;
		if (!level.noCollision(entityType.get().getAABB(x, y, z))) {
			return Optional.empty();
		}

		Vec3 spawnPos = new Vec3(x, y, z);
		if (!inLineOfSight(level, Vec3.atCenterOf(pos), spawnPos)) {
			return Optional.empty();
		}

		// modern spawn rules ignore light for trial spawners, chambers are lit,
		// so only the placement check applies instead of the full monster rules
		BlockPos spawnBlockPos = new BlockPos(spawnPos);
		SpawnPlacements.Type placement = SpawnPlacements.getPlacementType(entityType.get());
		if (!NaturalSpawner.isSpawnPositionOk(placement, level, spawnBlockPos, entityType.get())) {
			return Optional.empty();
		}

		if (nextSpawnData.getCustomSpawnRules().isPresent() && !isValidCustomPosition(level, spawnBlockPos, nextSpawnData.getCustomSpawnRules().get())) {
			return Optional.empty();
		}

		Entity entity = EntityType.loadEntityRecursive(entityTag, level, e -> {
			e.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);
			return e;
		});
		if (entity == null) {
			return Optional.empty();
		}

		if (entity instanceof Mob mob) {
			if (!mob.checkSpawnObstruction(level)) {
				return Optional.empty();
			}

			boolean hasNoConfiguration = entityTag.size() == 1 && entityTag.contains("id", CompoundTag.TAG_STRING);
			if (hasNoConfiguration) {
				mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.SPAWNER, null, null);
			}

			mob.setPersistenceRequired();
		}

		if (!level.tryAddFreshEntityWithPassengers(entity)) {
			return Optional.empty();
		}

		this.playSpawnMobEffects(level, pos, spawnBlockPos);
		level.gameEvent(entity, GameEvent.ENTITY_PLACE, spawnBlockPos);
		return Optional.of(entity.getUUID());
	}

	private static boolean isValidCustomPosition(ServerLevel level, BlockPos pos, SpawnData.CustomSpawnRules rules) {
		return rules.blockLightLimit().isValueInRange(level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos))
				&& rules.skyLightLimit().isValueInRange(level.getBrightness(net.minecraft.world.level.LightLayer.SKY, pos));
	}

	public void ejectReward(ServerLevel level, BlockPos pos, ResourceLocation lootTableId) {
		LootTable lootTable = level.getServer().getLootTables().get(lootTableId);
		LootContext context = new LootContext.Builder(level).create(LootContextParamSets.EMPTY);
		var lootDrops = lootTable.getRandomItems(context);
		if (!lootDrops.isEmpty()) {
			for (ItemStack item : lootDrops) {
				DefaultDispenseItemBehavior.spawnItem(level, item, 2, Direction.UP, Vec3.atBottomCenterOf(pos).add(0.0, 1.2, 0.0));
			}

			level.playSound(null, pos, CTBSounds.TRIAL_SPAWNER_EJECT_ITEM.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			addEjectItemParticles(level, pos);
		}
	}

	public void tickClient(Level level, BlockPos pos, boolean isOminous) {
		this.isOminous = isOminous;
		TrialSpawnerState state = this.getState();
		state.emitParticles(level, pos, isOminous);
		if (state.hasSpinningMob()) {
			double spawnDelay = Math.max(0L, this.data.nextMobSpawnsAt - level.getGameTime());
			this.data.oSpin = this.data.spin;
			this.data.spin = (this.data.spin + state.spinningMobSpeed() / (spawnDelay + 200.0)) % 360.0;
		}

		if (state.isCapableOfSpawning()) {
			Random random = level.getRandom();
			if (random.nextFloat() <= SPAWNING_AMBIENT_SOUND_CHANCE) {
				SoundEvent ambientSound = isOminous ? CTBSounds.TRIAL_SPAWNER_AMBIENT_OMINOUS.get() : CTBSounds.TRIAL_SPAWNER_AMBIENT.get();
				level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ambientSound, SoundSource.BLOCKS,
						random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		}
	}

	public void tickServer(ServerLevel level, BlockPos pos, boolean isOminous) {
		this.isOminous = isOminous;
		TrialSpawnerState state = this.getState();
		if (this.data.currentMobs.removeIf(id -> shouldMobBeUntracked(level, pos, id))) {
			this.data.nextMobSpawnsAt = level.getGameTime() + this.activeConfig().ticksBetweenSpawn();
		}

		TrialSpawnerState nextState = state.tickAndGetNext(pos, this, level);
		if (nextState != state) {
			this.setState(level, nextState);
		}
	}

	private static boolean shouldMobBeUntracked(ServerLevel level, BlockPos pos, UUID id) {
		Entity entity = level.getEntity(id);
		return entity == null
				|| !entity.isAlive()
				|| !entity.level.dimension().equals(level.dimension())
				|| entity.blockPosition().distSqr(pos) > MAX_MOB_TRACKING_DISTANCE_SQR;
	}

	private static boolean inLineOfSight(Level level, Vec3 origin, Vec3 dest) {
		BlockHitResult hit = level.clip(new ClipContext(dest, origin, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
		return hit.getBlockPos().equals(new BlockPos(origin)) || hit.getType() == HitResult.Type.MISS;
	}

	// the following effects are level events in modern versions, sent straight from the server here

	public void playDetectPlayerEffects(ServerLevel level, BlockPos pos, int detectedPlayers) {
		level.playSound(null, pos, CTBSounds.TRIAL_SPAWNER_DETECT_PLAYER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		SimpleParticleType type = this.isOminous ? CTBParticles.TRIAL_SPAWNER_DETECTION_OMINOUS.get() : CTBParticles.TRIAL_SPAWNER_DETECTION.get();
		Random random = level.getRandom();
		for (int i = 0; i < 30 + Math.min(detectedPlayers, 10) * 5; i++) {
			double x = pos.getX() + 0.5 + (2.0F * random.nextFloat() - 1.0F) * 0.65;
			double y = pos.getY() + 0.1 + random.nextFloat() * 0.8;
			double z = pos.getZ() + 0.5 + (2.0F * random.nextFloat() - 1.0F) * 0.65;
			level.sendParticles(type, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
		}
	}

	private void playSpawnMobEffects(ServerLevel level, BlockPos spawnerPos, BlockPos mobPos) {
		SimpleParticleType flame = this.isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
		level.playSound(null, spawnerPos, CTBSounds.TRIAL_SPAWNER_SPAWN_MOB.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		addSpawnParticles(level, spawnerPos, flame);
		addSpawnParticles(level, mobPos, flame);
	}

	private static void addSpawnParticles(ServerLevel level, BlockPos pos, SimpleParticleType particleType) {
		Random random = level.getRandom();
		for (int i = 0; i < 20; i++) {
			double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
			level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
			level.sendParticles(particleType, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
		}
	}

	private static void addEjectItemParticles(ServerLevel level, BlockPos pos) {
		Random random = level.getRandom();
		for (int i = 0; i < 20; i++) {
			double x = pos.getX() + 0.4 + random.nextDouble() * 0.2;
			double y = pos.getY() + 0.4 + random.nextDouble() * 0.2;
			double z = pos.getZ() + 0.4 + random.nextDouble() * 0.2;
			double vx = random.nextGaussian() * 0.02;
			double vy = random.nextGaussian() * 0.02;
			double vz = random.nextGaussian() * 0.02;
			level.sendParticles(ParticleTypes.SMALL_FLAME, x, y, z, 0, vx, vy, vz * 0.25, 1.0);
			level.sendParticles(ParticleTypes.SMOKE, x, y, z, 0, vx, vy, vz, 1.0);
		}
	}

	public void overrideEntityToSpawn(EntityType<?> type, Level level) {
		this.data.reset();
		this.normalConfig = this.normalConfig.withSpawning(type);
		this.ominousConfig = this.ominousConfig.withSpawning(type);
		this.setState(level, TrialSpawnerState.INACTIVE);
	}

	/** Gametest hooks, mock players are invisible to the level's player list. */
	public void setPlayerDetector(PlayerDetector playerDetector) {
		this.playerDetector = playerDetector;
	}

	public void setEntitySelector(PlayerDetector.EntitySelector entitySelector) {
		this.entitySelector = entitySelector;
	}

	public void overridePeacefulAndMobSpawnRule() {
		this.overridePeacefulAndMobSpawnRule = true;
	}

	public interface StateAccessor {
		void setState(Level level, TrialSpawnerState state);

		TrialSpawnerState getState();

		void markUpdated();
	}
}
