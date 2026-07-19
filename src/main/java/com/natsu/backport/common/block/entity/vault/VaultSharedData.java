package com.natsu.backport.common.block.entity.vault;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class VaultSharedData {

	private ItemStack displayItem = ItemStack.EMPTY;
	private Set<UUID> connectedPlayers = new ObjectLinkedOpenHashSet<>();
	private double connectedParticlesRange = 4.5;
	boolean isDirty;

	public void load(CompoundTag tag) {
		this.displayItem = tag.contains("display_item") ? ItemStack.of(tag.getCompound("display_item")) : ItemStack.EMPTY;
		Set<UUID> players = new ObjectLinkedOpenHashSet<>();
		for (Tag entry : tag.getList("connected_players", Tag.TAG_INT_ARRAY)) {
			players.add(NbtUtils.loadUUID(entry));
		}
		this.connectedPlayers = players;
		if (tag.contains("connected_particles_range")) {
			this.connectedParticlesRange = tag.getDouble("connected_particles_range");
		}
	}

	public void save(CompoundTag tag) {
		tag.put("display_item", this.displayItem.save(new CompoundTag()));
		ListTag players = new ListTag();
		for (UUID uuid : this.connectedPlayers) {
			players.add(NbtUtils.createUUID(uuid));
		}
		tag.put("connected_players", players);
		tag.putDouble("connected_particles_range", this.connectedParticlesRange);
	}

	public ItemStack getDisplayItem() {
		return this.displayItem;
	}

	public boolean hasDisplayItem() {
		return !this.displayItem.isEmpty();
	}

	public void setDisplayItem(ItemStack stack) {
		if (!ItemStack.matches(this.displayItem, stack)) {
			this.displayItem = stack.copy();
			this.markDirty();
		}
	}

	boolean hasConnectedPlayers() {
		return !this.connectedPlayers.isEmpty();
	}

	public Set<UUID> getConnectedPlayers() {
		return this.connectedPlayers;
	}

	double connectedParticlesRange() {
		return this.connectedParticlesRange;
	}

	void updateConnectedPlayersWithinRange(ServerLevel level, BlockPos pos, VaultServerData serverData, VaultConfig config, double limit) {
		Set<UUID> current = config.playerDetector()
				.detect(level, config.entitySelector(), pos, limit, false)
				.stream()
				.filter(uuid -> !serverData.getRewardedPlayers().contains(uuid))
				.collect(Collectors.toSet());
		if (!this.connectedPlayers.equals(current)) {
			this.connectedPlayers = current;
			this.markDirty();
		}
	}

	private void markDirty() {
		this.isDirty = true;
	}
}
