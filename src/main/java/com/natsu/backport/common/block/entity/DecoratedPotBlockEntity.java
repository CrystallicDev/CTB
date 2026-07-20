package com.natsu.backport.common.block.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class DecoratedPotBlockEntity extends BlockEntity {

	public static final int SHERD_COUNT = 4;

	// north, east, south, west
	private final List<Item> sherds = new ArrayList<>(List.of(Items.BRICK, Items.BRICK, Items.BRICK, Items.BRICK));
	@Nullable
	private ResourceLocation lootTable;
	private ItemStack storedItem = ItemStack.EMPTY;

	public DecoratedPotBlockEntity(BlockPos pos, BlockState state) {
		super(CTBBlockEntities.DECORATED_POT.get(), pos, state);
	}

	public List<Item> getSherds() {
		return this.sherds;
	}

	public void setSherds(List<Item> newSherds) {
		this.sherds.clear();
		for (int i = 0; i < SHERD_COUNT; i++) {
			this.sherds.add(i < newSherds.size() ? newSherds.get(i) : Items.BRICK);
		}
	}

	/** Spills the loot and the stored item on the floor when the pot breaks. */
	public void spillContent(ServerLevel level) {
		Vec3 center = Vec3.atCenterOf(this.worldPosition);
		if (this.lootTable != null) {
			LootTable table = level.getServer().getLootTables().get(this.lootTable);
			LootContext context = new LootContext.Builder(level)
					.withParameter(LootContextParams.ORIGIN, center)
					.create(LootContextParamSets.CHEST);
			for (ItemStack stack : table.getRandomItems(context)) {
				Containers.dropItemStack(level, center.x, center.y, center.z, stack);
			}
			this.lootTable = null;
		}
		if (!this.storedItem.isEmpty()) {
			Containers.dropItemStack(level, center.x, center.y, center.z, this.storedItem);
			this.storedItem = ItemStack.EMPTY;
		}
	}

	/** The dropped pot item, keeping its decoration. */
	public ItemStack asItem() {
		ItemStack stack = new ItemStack(com.natsu.backport.common.registry.CTBBlocks.DECORATED_POT.get());
		if (this.sherds.stream().anyMatch(item -> item != Items.BRICK)) {
			CompoundTag beTag = new CompoundTag();
			this.saveSherds(beTag);
			stack.getOrCreateTag().put("BlockEntityTag", beTag);
		}
		return stack;
	}

	private void saveSherds(CompoundTag tag) {
		ListTag list = new ListTag();
		for (Item sherd : this.sherds) {
			list.add(StringTag.valueOf(sherd.getRegistryName().toString()));
		}
		tag.put("sherds", list);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		this.saveSherds(tag);
		if (this.lootTable != null) {
			tag.putString("LootTable", this.lootTable.toString());
		}
		if (!this.storedItem.isEmpty()) {
			tag.put("item", this.storedItem.save(new CompoundTag()));
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("sherds")) {
			List<Item> loaded = new ArrayList<>();
			for (Tag entry : tag.getList("sherds", Tag.TAG_STRING)) {
				ResourceLocation id = ResourceLocation.tryParse(entry.getAsString());
				Item item = id != null ? ForgeRegistries.ITEMS.getValue(id) : null;
				loaded.add(item != null && item != Items.AIR ? item : Items.BRICK);
			}
			this.setSherds(loaded);
		}
		this.lootTable = tag.contains("LootTable") ? ResourceLocation.tryParse(tag.getString("LootTable")) : null;
		this.storedItem = tag.contains("item") ? ItemStack.of(tag.getCompound("item")) : ItemStack.EMPTY;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		this.saveSherds(tag);
		return tag;
	}

	public static boolean isSherd(Item item) {
		return item == CTBItems.FLOW_POTTERY_SHERD.get()
				|| item == CTBItems.GUSTER_POTTERY_SHERD.get()
				|| item == CTBItems.SCRAPE_POTTERY_SHERD.get();
	}
}
