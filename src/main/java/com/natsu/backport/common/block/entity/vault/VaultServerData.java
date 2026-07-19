package com.natsu.backport.common.block.entity.vault;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VaultServerData {

	private static final int MAX_REWARD_PLAYERS = 128;

	private final Set<UUID> rewardedPlayers = new ObjectLinkedOpenHashSet<>();
	private long stateUpdatingResumesAt;
	private final List<ItemStack> itemsToEject = new ObjectArrayList<>();
	private long lastInsertFailTimestamp;
	private int totalEjectionsNeeded;
	boolean isDirty;

	public void load(CompoundTag tag) {
		this.rewardedPlayers.clear();
		for (Tag entry : tag.getList("rewarded_players", Tag.TAG_INT_ARRAY)) {
			this.rewardedPlayers.add(NbtUtils.loadUUID(entry));
		}
		this.stateUpdatingResumesAt = tag.getLong("state_updating_resumes_at");
		this.itemsToEject.clear();
		for (Tag entry : tag.getList("items_to_eject", Tag.TAG_COMPOUND)) {
			this.itemsToEject.add(ItemStack.of((CompoundTag) entry));
		}
		this.totalEjectionsNeeded = tag.getInt("total_ejections_needed");
	}

	public void save(CompoundTag tag) {
		ListTag players = new ListTag();
		for (UUID uuid : this.rewardedPlayers) {
			players.add(NbtUtils.createUUID(uuid));
		}
		tag.put("rewarded_players", players);
		tag.putLong("state_updating_resumes_at", this.stateUpdatingResumesAt);
		ListTag items = new ListTag();
		for (ItemStack stack : this.itemsToEject) {
			items.add(stack.save(new CompoundTag()));
		}
		tag.put("items_to_eject", items);
		tag.putInt("total_ejections_needed", this.totalEjectionsNeeded);
	}

	void setLastInsertFailTimestamp(long lastInsertFailTimestamp) {
		this.lastInsertFailTimestamp = lastInsertFailTimestamp;
	}

	long getLastInsertFailTimestamp() {
		return this.lastInsertFailTimestamp;
	}

	public Set<UUID> getRewardedPlayers() {
		return this.rewardedPlayers;
	}

	public boolean hasRewardedPlayer(Player player) {
		return this.rewardedPlayers.contains(player.getUUID());
	}

	public void addToRewardedPlayers(Player player) {
		this.rewardedPlayers.add(player.getUUID());
		if (this.rewardedPlayers.size() > MAX_REWARD_PLAYERS) {
			Iterator<UUID> iterator = this.rewardedPlayers.iterator();
			if (iterator.hasNext()) {
				iterator.next();
				iterator.remove();
			}
		}

		this.markChanged();
	}

	public long stateUpdatingResumesAt() {
		return this.stateUpdatingResumesAt;
	}

	public void pauseStateUpdatingUntil(long stateUpdatingResumesAt) {
		this.stateUpdatingResumesAt = stateUpdatingResumesAt;
		this.markChanged();
	}

	List<ItemStack> getItemsToEject() {
		return this.itemsToEject;
	}

	void markEjectionFinished() {
		this.totalEjectionsNeeded = 0;
		this.markChanged();
	}

	void setItemsToEject(List<ItemStack> newItemsToEject) {
		this.itemsToEject.clear();
		this.itemsToEject.addAll(newItemsToEject);
		this.totalEjectionsNeeded = this.itemsToEject.size();
		this.markChanged();
	}

	ItemStack getNextItemToEject() {
		return this.itemsToEject.isEmpty() ? ItemStack.EMPTY
				: Objects.requireNonNullElse(this.itemsToEject.get(this.itemsToEject.size() - 1), ItemStack.EMPTY);
	}

	ItemStack popNextItemToEject() {
		if (this.itemsToEject.isEmpty()) {
			return ItemStack.EMPTY;
		}

		this.markChanged();
		return Objects.requireNonNullElse(this.itemsToEject.remove(this.itemsToEject.size() - 1), ItemStack.EMPTY);
	}

	private void markChanged() {
		this.isDirty = true;
	}

	public float ejectionProgress() {
		return this.totalEjectionsNeeded == 1 ? 1.0F : 1.0F - Mth.inverseLerp(this.getItemsToEject().size(), 1.0F, this.totalEjectionsNeeded);
	}
}
