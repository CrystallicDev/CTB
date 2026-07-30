package com.natsu.backport.common.block.entity;

import com.natsu.backport.common.block.BrushableBlock;
import com.natsu.backport.common.registry.CTBBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/** The 1.20 brushable block entity : ten brush strokes and the find pops out. */
public class BrushableBlockEntity extends BlockEntity {

	private static final int BRUSH_COOLDOWN_TICKS = 10;
	private static final int BRUSH_RESET_TICKS = 40;
	private static final int REQUIRED_BRUSHES_TO_BREAK = 10;

	private int brushCount;
	private long brushCountResetsAtTick;
	private long coolDownEndsAtTick;
	private ItemStack item = ItemStack.EMPTY;
	private Direction hitDirection;
	private ResourceLocation lootTable;
	private long lootTableSeed;

	public BrushableBlockEntity(BlockPos pos, BlockState state) {
		super(CTBBlockEntities.BRUSHABLE_BLOCK.get(), pos, state);
	}

	public void setLootTable(ResourceLocation lootTable, long seed) {
		this.lootTable = lootTable;
		this.lootTableSeed = seed;
	}

	public boolean brush(long gameTime, ServerLevel level, LivingEntity user, Direction direction, ItemStack brush) {
		if (this.hitDirection == null) {
			this.hitDirection = direction;
		}
		this.brushCountResetsAtTick = gameTime + BRUSH_RESET_TICKS;
		if (gameTime < this.coolDownEndsAtTick) {
			return false;
		}
		this.coolDownEndsAtTick = gameTime + BRUSH_COOLDOWN_TICKS;
		this.unpackLootTable(level, user, brush);
		int previous = this.getCompletionState();
		this.brushCount++;
		if (this.brushCount >= REQUIRED_BRUSHES_TO_BREAK) {
			this.brushingCompleted(level, user, brush);
			return true;
		}
		level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
		int completion = this.getCompletionState();
		if (previous != completion) {
			level.setBlock(this.getBlockPos(),
					this.getBlockState().setValue(BrushableBlock.DUSTED, completion), 3);
		}
		return false;
	}

	private void unpackLootTable(ServerLevel level, LivingEntity user, ItemStack brush) {
		if (this.lootTable == null) {
			return;
		}
		LootContext context = new LootContext.Builder(level)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition))
				.withParameter(LootContextParams.THIS_ENTITY, user)
				.withLuck(user instanceof Player player ? player.getLuck() : 0.0F)
				.withOptionalRandomSeed(this.lootTableSeed)
				.create(LootContextParamSets.GIFT);
		var loot = level.getServer().getLootTables().get(this.lootTable).getRandomItems(context);
		this.item = loot.isEmpty() ? ItemStack.EMPTY : loot.get(0);
		this.lootTable = null;
		this.setChanged();
	}

	private void brushingCompleted(ServerLevel level, LivingEntity user, ItemStack brush) {
		this.dropContent(level, user, brush);
		BlockState state = this.getBlockState();
		level.levelEvent(2001, this.getBlockPos(), Block.getId(state));
		Block turnsInto = state.getBlock() instanceof BrushableBlock brushable
				? brushable.getTurnsInto() : Blocks.AIR;
		level.setBlock(this.worldPosition, turnsInto.defaultBlockState(), 3);
	}

	private void dropContent(ServerLevel level, LivingEntity user, ItemStack brush) {
		this.unpackLootTable(level, user, brush);
		if (this.item.isEmpty()) {
			return;
		}
		Direction dropDirection = this.hitDirection == null ? Direction.UP : this.hitDirection;
		BlockPos dropPos = this.worldPosition.relative(dropDirection);
		ItemEntity entity = new ItemEntity(level, dropPos.getX() + 0.5, dropPos.getY() + 0.5,
				dropPos.getZ() + 0.5, this.item.copy());
		entity.setDeltaMovement(Vec3.ZERO);
		entity.setDefaultPickUpDelay();
		level.addFreshEntity(entity);
		this.item = ItemStack.EMPTY;
	}

	/** The dust settles back a couple of steps at a time when brushing stops. */
	public void checkReset(ServerLevel level) {
		if (this.brushCount != 0 && level.getGameTime() >= this.brushCountResetsAtTick) {
			int previous = this.getCompletionState();
			this.brushCount = Math.max(0, this.brushCount - 2);
			int completion = this.getCompletionState();
			if (previous != completion) {
				level.setBlock(this.getBlockPos(),
						this.getBlockState().setValue(BrushableBlock.DUSTED, completion), 3);
			}
			this.brushCountResetsAtTick = level.getGameTime() + 4L;
		}
		if (this.brushCount == 0) {
			this.hitDirection = null;
			this.brushCountResetsAtTick = 0L;
			this.coolDownEndsAtTick = 0L;
		} else {
			level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
		}
	}

	private int getCompletionState() {
		if (this.brushCount == 0) {
			return 0;
		}
		if (this.brushCount < 3) {
			return 1;
		}
		if (this.brushCount < 6) {
			return 2;
		}
		return 3;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("LootTable")) {
			this.lootTable = new ResourceLocation(tag.getString("LootTable"));
			this.lootTableSeed = tag.getLong("LootTableSeed");
		}
		if (tag.contains("item")) {
			this.item = ItemStack.of(tag.getCompound("item"));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (this.lootTable != null) {
			tag.putString("LootTable", this.lootTable.toString());
			if (this.lootTableSeed != 0L) {
				tag.putLong("LootTableSeed", this.lootTableSeed);
			}
		}
		if (!this.item.isEmpty()) {
			tag.put("item", this.item.save(new CompoundTag()));
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
