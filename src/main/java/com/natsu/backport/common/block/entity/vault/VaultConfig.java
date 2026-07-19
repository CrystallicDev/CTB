package com.natsu.backport.common.block.entity.vault;

import java.util.Optional;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.entity.trialspawner.PlayerDetector;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record VaultConfig(
		ResourceLocation lootTable,
		double activationRange,
		double deactivationRange,
		ItemStack keyItem,
		Optional<ResourceLocation> overrideLootTableToDisplay,
		PlayerDetector playerDetector,
		PlayerDetector.EntitySelector entitySelector
) {

	public static final ResourceLocation REWARD_LOOT = new ResourceLocation(CTBackport.MODID, "chests/trial_chambers/reward");
	public static final ResourceLocation REWARD_OMINOUS_LOOT = new ResourceLocation(CTBackport.MODID, "chests/trial_chambers/reward_ominous");

	public static VaultConfig defaultConfig() {
		return new VaultConfig(REWARD_LOOT, 4.0, 4.5, new ItemStack(CTBItems.TRIAL_KEY.get()), Optional.empty(),
				PlayerDetector.INCLUDING_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
	}

	public static VaultConfig defaultOminousConfig() {
		return new VaultConfig(REWARD_OMINOUS_LOOT, 4.0, 4.5, new ItemStack(CTBItems.OMINOUS_TRIAL_KEY.get()), Optional.empty(),
				PlayerDetector.INCLUDING_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
	}

	public VaultConfig(ResourceLocation lootTable, double activationRange, double deactivationRange, ItemStack keyItem,
			Optional<ResourceLocation> overrideLootTableToDisplay) {
		this(lootTable, activationRange, deactivationRange, keyItem, overrideLootTableToDisplay,
				PlayerDetector.INCLUDING_CREATIVE_PLAYERS, PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
	}

	public static VaultConfig load(CompoundTag tag, boolean ominous) {
		VaultConfig defaults = ominous ? defaultOminousConfig() : defaultConfig();
		if (tag.isEmpty()) {
			return defaults;
		}
		ResourceLocation lootTable = tag.contains("loot_table")
				? ResourceLocation.tryParse(tag.getString("loot_table")) : defaults.lootTable();
		double activation = tag.contains("activation_range") ? tag.getDouble("activation_range") : defaults.activationRange();
		double deactivation = tag.contains("deactivation_range") ? tag.getDouble("deactivation_range") : defaults.deactivationRange();
		ItemStack key = tag.contains("key_item") ? ItemStack.of(tag.getCompound("key_item")) : defaults.keyItem();
		Optional<ResourceLocation> display = tag.contains("override_loot_table_to_display")
				? Optional.ofNullable(ResourceLocation.tryParse(tag.getString("override_loot_table_to_display")))
				: Optional.empty();
		return new VaultConfig(lootTable, activation, deactivation, key, display);
	}

	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putString("loot_table", this.lootTable.toString());
		tag.putDouble("activation_range", this.activationRange);
		tag.putDouble("deactivation_range", this.deactivationRange);
		tag.put("key_item", this.keyItem.save(new CompoundTag()));
		this.overrideLootTableToDisplay.ifPresent(loot -> tag.putString("override_loot_table_to_display", loot.toString()));
		return tag;
	}
}
