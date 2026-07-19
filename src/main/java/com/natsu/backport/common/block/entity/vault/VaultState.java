package com.natsu.backport.common.block.entity.vault;

import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public enum VaultState implements StringRepresentable {
	INACTIVE("inactive", LightLevel.HALF_LIT) {
		@Override
		protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
			sharedData.setDisplayItem(ItemStack.EMPTY);
			level.playSound(null, pos, CTBSounds.VAULT_DEACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			VaultBlockEntity.Server.sendDeactivationParticles(level, pos, isOminous);
		}
	},
	ACTIVE("active", LightLevel.LIT) {
		@Override
		protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
			if (!sharedData.hasDisplayItem()) {
				VaultBlockEntity.Server.cycleDisplayItemFromLootTable(level, this, config, sharedData, pos);
			}

			level.playSound(null, pos, CTBSounds.VAULT_ACTIVATE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
			VaultBlockEntity.Server.sendActivationParticles(level, pos, isOminous);
		}
	},
	UNLOCKING("unlocking", LightLevel.LIT) {
		@Override
		protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
			level.playSound(null, pos, CTBSounds.VAULT_INSERT_ITEM.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	},
	EJECTING("ejecting", LightLevel.LIT) {
		@Override
		protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
			level.playSound(null, pos, CTBSounds.VAULT_OPEN_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}

		@Override
		protected void onExit(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
			level.playSound(null, pos, CTBSounds.VAULT_CLOSE_SHUTTER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	};

	private static final int UPDATE_CONNECTED_PLAYERS_TICK_RATE = 20;
	private static final int DELAY_BETWEEN_EJECTIONS_TICKS = 20;

	private final String stateName;
	private final LightLevel lightLevel;

	VaultState(String stateName, LightLevel lightLevel) {
		this.stateName = stateName;
		this.lightLevel = lightLevel;
	}

	@Override
	public String getSerializedName() {
		return this.stateName;
	}

	public int lightLevel() {
		return this.lightLevel.value;
	}

	public VaultState tickAndGetNext(ServerLevel level, BlockPos pos, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData) {
		return switch (this) {
			case INACTIVE -> updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.activationRange());
			case ACTIVE -> updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.deactivationRange());
			case UNLOCKING -> {
				serverData.pauseStateUpdatingUntil(level.getGameTime() + UPDATE_CONNECTED_PLAYERS_TICK_RATE);
				yield EJECTING;
			}
			case EJECTING -> {
				if (serverData.getItemsToEject().isEmpty()) {
					serverData.markEjectionFinished();
					yield updateStateForConnectedPlayers(level, pos, config, serverData, sharedData, config.deactivationRange());
				} else {
					float ejectionProgress = serverData.ejectionProgress();
					this.ejectResultItem(level, pos, serverData.popNextItemToEject(), ejectionProgress);
					sharedData.setDisplayItem(serverData.getNextItemToEject());
					serverData.pauseStateUpdatingUntil(level.getGameTime() + DELAY_BETWEEN_EJECTIONS_TICKS);
					yield EJECTING;
				}
			}
		};
	}

	private static VaultState updateStateForConnectedPlayers(ServerLevel level, BlockPos pos, VaultConfig config,
			VaultServerData serverData, VaultSharedData sharedData, double range) {
		sharedData.updateConnectedPlayersWithinRange(level, pos, serverData, config, range);
		serverData.pauseStateUpdatingUntil(level.getGameTime() + UPDATE_CONNECTED_PLAYERS_TICK_RATE);
		return sharedData.hasConnectedPlayers() ? ACTIVE : INACTIVE;
	}

	public void onTransition(ServerLevel level, BlockPos pos, VaultState to, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
		this.onExit(level, pos, config, sharedData);
		to.onEnter(level, pos, config, sharedData, isOminous);
	}

	protected void onEnter(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean isOminous) {
	}

	protected void onExit(ServerLevel level, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
	}

	private void ejectResultItem(ServerLevel level, BlockPos pos, ItemStack itemToEject, float ejectionProgress) {
		DefaultDispenseItemBehavior.spawnItem(level, itemToEject, 2, Direction.UP, Vec3.atBottomCenterOf(pos).add(0.0, 1.2, 0.0));
		VaultBlockEntity.Server.sendEjectItemParticles(level, pos);
		level.playSound(null, pos, CTBSounds.VAULT_EJECT_ITEM.get(), SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F * ejectionProgress);
	}

	private enum LightLevel {
		HALF_LIT(6),
		LIT(12);

		final int value;

		LightLevel(int value) {
			this.value = value;
		}
	}
}
