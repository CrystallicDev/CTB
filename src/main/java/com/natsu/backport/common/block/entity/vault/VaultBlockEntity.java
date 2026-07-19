package com.natsu.backport.common.block.entity.vault;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.VaultBlock;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class VaultBlockEntity extends BlockEntity {

	private final VaultServerData serverData = new VaultServerData();
	private final VaultSharedData sharedData = new VaultSharedData();
	private final VaultClientData clientData = new VaultClientData();
	private VaultConfig config;

	public VaultBlockEntity(BlockPos pos, BlockState state) {
		super(CTBBlockEntities.VAULT.get(), pos, state);
		this.config = state.getValue(VaultBlock.OMINOUS) ? VaultConfig.defaultOminousConfig() : VaultConfig.defaultConfig();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		CompoundTag shared = new CompoundTag();
		this.sharedData.save(shared);
		tag.put("shared_data", shared);
		return tag;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("config", this.config.save());
		CompoundTag shared = new CompoundTag();
		this.sharedData.save(shared);
		tag.put("shared_data", shared);
		CompoundTag server = new CompoundTag();
		this.serverData.save(server);
		tag.put("server_data", server);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("server_data")) {
			this.serverData.load(tag.getCompound("server_data"));
		}
		if (tag.contains("config")) {
			this.config = VaultConfig.load(tag.getCompound("config"), this.getBlockState().getValue(VaultBlock.OMINOUS));
		}
		if (tag.contains("shared_data")) {
			this.sharedData.load(tag.getCompound("shared_data"));
		}
	}

	@Nullable
	public VaultServerData getServerData() {
		return this.level != null && !this.level.isClientSide() ? this.serverData : null;
	}

	public VaultSharedData getSharedData() {
		return this.sharedData;
	}

	public VaultClientData getClientData() {
		return this.clientData;
	}

	public VaultConfig getConfig() {
		return this.config;
	}

	public void setConfig(VaultConfig config) {
		this.config = config;
	}

	public static final class Client {

		private static final int PARTICLE_TICK_RATE = 20;
		private static final float IDLE_PARTICLE_CHANCE = 0.5F;
		private static final float AMBIENT_SOUND_CHANCE = 0.02F;

		public static void tick(Level level, BlockPos pos, BlockState state, VaultClientData clientData, VaultSharedData sharedData) {
			clientData.updateDisplayItemSpin();
			if (level.getGameTime() % PARTICLE_TICK_RATE == 0L) {
				emitConnectionParticlesForNearbyPlayers(level, pos, state, sharedData);
			}

			emitIdleParticles(level, pos, sharedData,
					state.getValue(VaultBlock.OMINOUS) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
			playIdleSounds(level, pos, sharedData);
		}

		private static void emitIdleParticles(Level level, BlockPos pos, VaultSharedData sharedData, ParticleOptions flameParticle) {
			Random random = level.getRandom();
			if (random.nextFloat() <= IDLE_PARTICLE_CHANCE) {
				Vec3 particlePos = randomPosInsideCage(pos, random);
				level.addParticle(ParticleTypes.SMOKE, particlePos.x(), particlePos.y(), particlePos.z(), 0.0, 0.0, 0.0);
				if (sharedData.hasDisplayItem()) {
					level.addParticle(flameParticle, particlePos.x(), particlePos.y(), particlePos.z(), 0.0, 0.0, 0.0);
				}
			}
		}

		private static void emitConnectionParticlesForPlayer(Level level, Vec3 keyholePos, Player player) {
			Random random = level.getRandom();
			Vec3 direction = keyholePos.vectorTo(player.position().add(0.0, player.getBbHeight() / 2.0F, 0.0));
			int particleCount = Mth.nextInt(random, 2, 5);

			for (int i = 0; i < particleCount; i++) {
				Vec3 spread = direction.add((random.nextFloat() - 0.5F), (random.nextFloat() - 0.5F), (random.nextFloat() - 0.5F));
				level.addParticle(CTBParticles.VAULT_CONNECTION.get(),
						keyholePos.x(), keyholePos.y(), keyholePos.z(), spread.x(), spread.y(), spread.z());
			}
		}

		private static void emitConnectionParticlesForNearbyPlayers(Level level, BlockPos pos, BlockState state, VaultSharedData sharedData) {
			Set<UUID> connectedPlayers = sharedData.getConnectedPlayers();
			if (!connectedPlayers.isEmpty()) {
				Vec3 keyholePos = keyholePos(pos, state.getValue(VaultBlock.FACING));

				for (UUID uuid : connectedPlayers) {
					Player player = level.getPlayerByUUID(uuid);
					if (player != null && isWithinConnectionRange(pos, sharedData, player)) {
						emitConnectionParticlesForPlayer(level, keyholePos, player);
					}
				}
			}
		}

		private static boolean isWithinConnectionRange(BlockPos vaultPos, VaultSharedData sharedData, Player player) {
			double range = sharedData.connectedParticlesRange();
			return player.blockPosition().distSqr(vaultPos) <= range * range;
		}

		private static void playIdleSounds(Level level, BlockPos pos, VaultSharedData sharedData) {
			if (sharedData.hasDisplayItem()) {
				Random random = level.getRandom();
				if (random.nextFloat() <= AMBIENT_SOUND_CHANCE) {
					level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
							CTBSounds.VAULT_AMBIENT.get(), SoundSource.BLOCKS,
							random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
				}
			}
		}

		private static Vec3 randomPosInsideCage(BlockPos pos, Random random) {
			return Vec3.atLowerCornerOf(pos).add(
					Mth.nextDouble(random, 0.1, 0.9), Mth.nextDouble(random, 0.25, 0.75), Mth.nextDouble(random, 0.1, 0.9));
		}

		private static Vec3 keyholePos(BlockPos pos, Direction facing) {
			return Vec3.atBottomCenterOf(pos).add(facing.getStepX() * 0.5, 1.75, facing.getStepZ() * 0.5);
		}
	}

	public static final class Server {

		private static final int UNLOCKING_DELAY_TICKS = 14;
		private static final int DISPLAY_CYCLE_TICK_RATE = 20;
		private static final int INSERT_FAIL_SOUND_BUFFER_TICKS = 15;

		public static void tick(ServerLevel level, BlockPos pos, BlockState state,
				VaultConfig config, VaultServerData serverData, VaultSharedData sharedData) {
			VaultState currentState = state.getValue(VaultBlock.STATE);
			if (shouldCycleDisplayItem(level.getGameTime(), currentState)) {
				cycleDisplayItemFromLootTable(level, currentState, config, sharedData, pos);
			}

			BlockState nextBlockState = state;
			if (level.getGameTime() >= serverData.stateUpdatingResumesAt()) {
				nextBlockState = nextBlockState.setValue(VaultBlock.STATE,
						currentState.tickAndGetNext(level, pos, config, serverData, sharedData));
				if (state != nextBlockState) {
					setVaultState(level, pos, state, nextBlockState, config, sharedData);
				}
			}

			if (serverData.isDirty || sharedData.isDirty) {
				setChanged(level, pos, state);
				if (sharedData.isDirty) {
					level.sendBlockUpdated(pos, state, nextBlockState, Block.UPDATE_CLIENTS);
				}

				serverData.isDirty = false;
				sharedData.isDirty = false;
			}
		}

		public static void tryInsertKey(ServerLevel level, BlockPos pos, BlockState state, VaultConfig config,
				VaultServerData serverData, VaultSharedData sharedData, Player player, ItemStack stackToInsert) {
			VaultState vaultState = state.getValue(VaultBlock.STATE);
			if (!canEjectReward(config, vaultState)) {
				return;
			}

			if (!isValidToInsert(config, stackToInsert)) {
				playInsertFailSound(level, serverData, pos, CTBSounds.VAULT_INSERT_ITEM_FAIL.get());
			} else if (serverData.hasRewardedPlayer(player)) {
				playInsertFailSound(level, serverData, pos, CTBSounds.VAULT_REJECT_REWARDED_PLAYER.get());
			} else {
				List<ItemStack> itemsToEject = resolveItemsToEject(level, config, pos, player);
				if (!itemsToEject.isEmpty()) {
					player.awardStat(Stats.ITEM_USED.get(stackToInsert.getItem()));
					if (!player.getAbilities().instabuild) {
						stackToInsert.shrink(config.keyItem().getCount());
					}
					unlock(level, state, pos, config, serverData, sharedData, itemsToEject);
					serverData.addToRewardedPlayers(player);
					sharedData.updateConnectedPlayersWithinRange(level, pos, serverData, config, config.deactivationRange());
				}
			}
		}

		static void setVaultState(ServerLevel level, BlockPos pos, BlockState currentBlockState, BlockState newBlockState,
				VaultConfig config, VaultSharedData sharedData) {
			VaultState currentState = currentBlockState.getValue(VaultBlock.STATE);
			VaultState newState = newBlockState.getValue(VaultBlock.STATE);
			level.setBlock(pos, newBlockState, Block.UPDATE_ALL);
			currentState.onTransition(level, pos, newState, config, sharedData, newBlockState.getValue(VaultBlock.OMINOUS));
		}

		static void cycleDisplayItemFromLootTable(ServerLevel level, VaultState state, VaultConfig config,
				VaultSharedData sharedData, BlockPos pos) {
			if (!canEjectReward(config, state)) {
				sharedData.setDisplayItem(ItemStack.EMPTY);
			} else {
				ItemStack displayItem = getRandomDisplayItemFromLootTable(level, pos,
						config.overrideLootTableToDisplay().orElse(config.lootTable()));
				sharedData.setDisplayItem(displayItem);
			}
		}

		private static ItemStack getRandomDisplayItemFromLootTable(ServerLevel level, BlockPos pos, ResourceLocation lootTableId) {
			LootTable lootTable = level.getServer().getLootTables().get(lootTableId);
			LootContext context = new LootContext.Builder(level)
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
					.create(LootContextParamSets.CHEST);
			List<ItemStack> results = lootTable.getRandomItems(context);
			return results.isEmpty() ? ItemStack.EMPTY : Util.getRandom(results, level.getRandom());
		}

		private static void unlock(ServerLevel level, BlockState state, BlockPos pos, VaultConfig config,
				VaultServerData serverData, VaultSharedData sharedData, List<ItemStack> itemsToEject) {
			serverData.setItemsToEject(itemsToEject);
			sharedData.setDisplayItem(serverData.getNextItemToEject());
			serverData.pauseStateUpdatingUntil(level.getGameTime() + UNLOCKING_DELAY_TICKS);
			setVaultState(level, pos, state, state.setValue(VaultBlock.STATE, VaultState.UNLOCKING), config, sharedData);
		}

		private static List<ItemStack> resolveItemsToEject(ServerLevel level, VaultConfig config, BlockPos pos, Player player) {
			LootTable lootTable = level.getServer().getLootTables().get(config.lootTable());
			LootContext context = new LootContext.Builder(level)
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
					.withLuck(player.getLuck())
					.withOptionalParameter(LootContextParams.THIS_ENTITY, player)
					.create(LootContextParamSets.CHEST);
			return lootTable.getRandomItems(context);
		}

		private static boolean canEjectReward(VaultConfig config, VaultState state) {
			return !config.keyItem().isEmpty() && state != VaultState.INACTIVE;
		}

		private static boolean isValidToInsert(VaultConfig config, ItemStack stackToInsert) {
			return ItemStack.isSameItemSameTags(stackToInsert, config.keyItem())
					&& stackToInsert.getCount() >= config.keyItem().getCount();
		}

		private static boolean shouldCycleDisplayItem(long gameTime, VaultState state) {
			return gameTime % DISPLAY_CYCLE_TICK_RATE == 0L && state == VaultState.ACTIVE;
		}

		private static void playInsertFailSound(ServerLevel level, VaultServerData serverData, BlockPos pos, SoundEvent sound) {
			if (level.getGameTime() >= serverData.getLastInsertFailTimestamp() + INSERT_FAIL_SOUND_BUFFER_TICKS) {
				level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
				serverData.setLastInsertFailTimestamp(level.getGameTime());
			}
		}

		// activation and deactivation visuals are level events in modern versions

		static void sendActivationParticles(ServerLevel level, BlockPos pos, boolean isOminous) {
			ParticleOptions flame = isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME;
			Random random = level.getRandom();
			for (int i = 0; i < 20; i++) {
				Vec3 particlePos = Vec3.atLowerCornerOf(pos).add(
						Mth.nextDouble(random, 0.1, 0.9), Mth.nextDouble(random, 0.25, 0.75), Mth.nextDouble(random, 0.1, 0.9));
				level.sendParticles(ParticleTypes.SMOKE, particlePos.x(), particlePos.y(), particlePos.z(), 1, 0.0, 0.0, 0.0, 0.0);
				level.sendParticles(flame, particlePos.x(), particlePos.y(), particlePos.z(), 1, 0.0, 0.0, 0.0, 0.0);
			}
		}

		static void sendDeactivationParticles(ServerLevel level, BlockPos pos, boolean isOminous) {
			ParticleOptions flame = isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME;
			Random random = level.getRandom();
			for (int i = 0; i < 20; i++) {
				Vec3 particlePos = Vec3.atLowerCornerOf(pos).add(
						Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6));
				level.sendParticles(flame, particlePos.x(), particlePos.y(), particlePos.z(), 0,
						random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, 1.0);
			}
		}

		static void sendEjectItemParticles(ServerLevel level, BlockPos pos) {
			Random random = level.getRandom();
			for (int i = 0; i < 20; i++) {
				double x = pos.getX() + 0.4 + random.nextDouble() * 0.2;
				double y = pos.getY() + 0.4 + random.nextDouble() * 0.2;
				double z = pos.getZ() + 0.4 + random.nextDouble() * 0.2;
				level.sendParticles(ParticleTypes.SMALL_FLAME, x, y, z, 0,
						random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02 * 0.25, 1.0);
				level.sendParticles(ParticleTypes.SMOKE, x, y, z, 0,
						random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, 1.0);
			}
		}
	}
}
