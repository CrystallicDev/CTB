package com.natsu.backport.common.entity.goal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.entity.CopperChestBlockEntity;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * The vanilla copper golem loop: pull a small stack out of a copper chest,
 * walk it to a chest that holds the same item, drop it there.
 */
public class TransportItemsGoal extends Goal {

	private static final int MAX_CARRIED = 16;
	private static final int SEARCH_RADIUS = 32;
	private static final int INTERACTION_TICKS = 60; // the interaction animations last three seconds
	private static final double REACH = 1.9;
	// vanilla interacts up to three blocks, accept it when pathing cannot get closer
	private static final double REACH_PATH_LIMIT = 2.75;

	private enum Phase { TO_PICKUP, INTERACT_PICKUP, TO_DROPOFF, INTERACT_DROPOFF, RETURNING }

	private final CopperGolem golem;
	private Phase phase = Phase.TO_PICKUP;
	@Nullable
	private BlockPos pickupPos;
	@Nullable
	private BlockPos dropoffPos;
	private int interactTicks;
	private int cooldown;
	private int stuckTicks;

	public TransportItemsGoal(CopperGolem golem) {
		this.golem = golem;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		this.cooldown = 60 + golem.getRandom().nextInt(41);
	}

	@Override
	public boolean canUse() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		}

		this.pickupPos = this.findCopperChest();
		return this.pickupPos != null;
	}

	@Override
	public boolean canContinueToUse() {
		return this.pickupPos != null && this.stuckTicks < 400;
	}

	@Override
	public void start() {
		this.phase = Phase.TO_PICKUP;
		this.interactTicks = 0;
		this.stuckTicks = 0;
		this.walkTo(this.pickupPos);
	}

	@Override
	public void stop() {
		if (this.phase == Phase.INTERACT_PICKUP) {
			this.setChestLid(this.pickupPos, false);
		} else if (this.phase == Phase.INTERACT_DROPOFF) {
			this.setChestLid(this.dropoffPos, false);
		}
		this.golem.setState(CopperGolem.GolemState.IDLE);
		this.pickupPos = null;
		this.dropoffPos = null;
		this.cooldown = 60 + this.golem.getRandom().nextInt(41);
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		this.stuckTicks++;
		switch (this.phase) {
			case TO_PICKUP -> {
				if (this.arrivedAt(this.pickupPos)) {
					this.beginInteraction(Phase.INTERACT_PICKUP);
				} else {
					this.walkTo(this.pickupPos);
				}
			}
			case INTERACT_PICKUP -> {
				// the outcome is decided when the interaction starts so the
				// matching animation plays while the golem stands at the chest
				if (this.interactTicks == INTERACTION_TICKS) {
					Container source = this.containerAt(this.pickupPos);
					ItemStack taken = source != null ? takeFromContainer(source) : ItemStack.EMPTY;
					if (taken.isEmpty()) {
						this.golem.setState(CopperGolem.GolemState.GETTING_NO_ITEM);
						this.golem.playSound(CTBSounds.COPPER_GOLEM_NO_ITEM_NO_GET.get(), 1.0F, 1.0F);
					} else {
						this.golem.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, taken);
						this.golem.setState(CopperGolem.GolemState.GETTING_ITEM);
						this.golem.playSound(CTBSounds.COPPER_GOLEM_NO_ITEM_GET.get(), 1.0F, 1.0F);
					}
				}
				this.golem.getNavigation().stop();
				if (--this.interactTicks > 0) {
					return;
				}
				this.setChestLid(this.pickupPos, false);
				this.golem.setState(CopperGolem.GolemState.IDLE);
				ItemStack carried = this.golem.getMainHandItem();
				if (carried.isEmpty()) {
					this.pickupPos = null; // done, nothing to carry
				} else {
					this.dropoffPos = this.findDropoffChest(carried);
					if (this.dropoffPos != null) {
						this.phase = Phase.TO_DROPOFF;
						this.stuckTicks = 0;
						this.walkTo(this.dropoffPos);
					} else {
						// nowhere to sort it, put it back where it came from
						this.phase = Phase.RETURNING;
					}
				}
			}
			case TO_DROPOFF -> {
				if (this.arrivedAt(this.dropoffPos)) {
					this.beginInteraction(Phase.INTERACT_DROPOFF);
				} else {
					this.walkTo(this.dropoffPos);
				}
			}
			case INTERACT_DROPOFF -> {
				if (this.interactTicks == INTERACTION_TICKS) {
					Container target = this.containerAt(this.dropoffPos);
					ItemStack carried = this.golem.getMainHandItem();
					if (target != null && !carried.isEmpty()) {
						ItemStack leftover = depositIntoContainer(target, carried);
						this.golem.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, leftover);
						boolean dropped = leftover.getCount() < carried.getCount();
						this.golem.setState(dropped ? CopperGolem.GolemState.DROPPING_ITEM : CopperGolem.GolemState.DROPPING_NO_ITEM);
						this.golem.playSound(dropped ? CTBSounds.COPPER_GOLEM_ITEM_DROP.get() : CTBSounds.COPPER_GOLEM_ITEM_NO_DROP.get(), 1.0F, 1.0F);
					}
				}
				this.golem.getNavigation().stop();
				if (--this.interactTicks > 0) {
					return;
				}
				this.setChestLid(this.dropoffPos, false);
				this.golem.setState(CopperGolem.GolemState.IDLE);
				if (this.golem.getMainHandItem().isEmpty()) {
					this.pickupPos = null; // cycle finished
				} else {
					this.phase = Phase.RETURNING;
				}
			}
			case RETURNING -> {
				if (this.arrivedAt(this.pickupPos)) {
					Container source = this.containerAt(this.pickupPos);
					ItemStack carried = this.golem.getMainHandItem();
					if (source != null && !carried.isEmpty()) {
						this.golem.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, depositIntoContainer(source, carried));
					}
					this.golem.setState(CopperGolem.GolemState.DROPPING_ITEM);
					this.pickupPos = null;
				} else {
					this.walkTo(this.pickupPos);
				}
			}
		}
	}

	private void beginInteraction(Phase next) {
		this.golem.getNavigation().stop();
		this.phase = next;
		this.interactTicks = INTERACTION_TICKS;
		this.stuckTicks = 0;
		BlockPos pos = next == Phase.INTERACT_PICKUP ? this.pickupPos : this.dropoffPos;
		this.setChestLid(pos, true);
	}

	/** Drives the lid through the vanilla block event, works for any chest. */
	private void setChestLid(@Nullable BlockPos pos, boolean open) {
		if (pos == null) {
			return;
		}
		net.minecraft.world.level.block.state.BlockState state = this.golem.level.getBlockState(pos);
		this.golem.level.blockEvent(pos, state.getBlock(), 1, open ? 1 : 0);
		boolean copper = this.golem.level.getBlockEntity(pos) instanceof CopperChestBlockEntity;
		net.minecraft.sounds.SoundEvent sound = copper
				? (open ? CTBSounds.COPPER_CHEST_OPEN.get() : CTBSounds.COPPER_CHEST_CLOSE.get())
				: (open ? net.minecraft.sounds.SoundEvents.CHEST_OPEN : net.minecraft.sounds.SoundEvents.CHEST_CLOSE);
		this.golem.level.playSound(null, pos, sound, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F,
				this.golem.getRandom().nextFloat() * 0.1F + 0.9F);
	}

	private void walkTo(BlockPos pos) {
		if (pos != null && this.golem.getNavigation().isDone()) {
			this.golem.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.0);
		}
	}

	private boolean arrivedAt(@Nullable BlockPos pos) {
		if (pos == null) {
			return false;
		}
		if (pos.closerToCenterThan(this.golem.position(), REACH)) {
			return true;
		}
		return this.golem.getNavigation().isDone() && pos.closerToCenterThan(this.golem.position(), REACH_PATH_LIMIT);
	}

	@Nullable
	private Container containerAt(@Nullable BlockPos pos) {
		if (pos == null) {
			return null;
		}
		return this.golem.level.getBlockEntity(pos) instanceof Container container ? container : null;
	}

	@Nullable
	private BlockPos findCopperChest() {
		return this.findChest(be -> be instanceof CopperChestBlockEntity chest && !isContainerEmpty(chest), null);
	}

	@Nullable
	private BlockPos findDropoffChest(ItemStack carried) {
		// a chest already holding the item wins, any chest with room second
		BlockPos matching = this.findChest(be -> be instanceof ChestBlockEntity chest
				&& !(be instanceof CopperChestBlockEntity) && containsSameItem(chest, carried), null);
		if (matching != null) {
			return matching;
		}
		return this.findChest(be -> be instanceof ChestBlockEntity chest
				&& !(be instanceof CopperChestBlockEntity) && hasFreeSlot(chest), null);
	}

	@Nullable
	private BlockPos findChest(java.util.function.Predicate<BlockEntity> filter, @Nullable BlockPos exclude) {
		List<BlockPos> found = new ArrayList<>();
		ChunkPos center = this.golem.chunkPosition();
		int chunkRadius = (SEARCH_RADIUS >> 4) + 1;
		for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
			for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
				LevelChunk chunk = this.golem.level.getChunkSource().getChunkNow(center.x + cx, center.z + cz);
				if (chunk == null) {
					continue;
				}
				for (BlockEntity be : chunk.getBlockEntities().values()) {
					BlockPos pos = be.getBlockPos();
					if (!pos.equals(exclude) && pos.closerThan(this.golem.blockPosition(), SEARCH_RADIUS) && filter.test(be)) {
						found.add(pos);
					}
				}
			}
		}
		return found.stream().min(Comparator.comparingDouble(p -> p.distSqr(this.golem.blockPosition()))).orElse(null);
	}

	private static boolean isContainerEmpty(Container container) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			if (!container.getItem(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static boolean containsSameItem(Container container, ItemStack stack) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack slot = container.getItem(i);
			if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasFreeSlot(Container container) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			if (container.getItem(i).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/** Vanilla takes at most sixteen items of a single random stack. */
	private static ItemStack takeFromContainer(Container container) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack slot = container.getItem(i);
			if (!slot.isEmpty()) {
				int amount = Math.min(slot.getCount(), MAX_CARRIED);
				ItemStack taken = slot.copy();
				taken.setCount(amount);
				slot.shrink(amount);
				container.setChanged();
				return taken;
			}
		}
		return ItemStack.EMPTY;
	}

	private static ItemStack depositIntoContainer(Container container, ItemStack stack) {
		ItemStack remaining = stack.copy();
		// merge into matching stacks first, then free slots
		for (int i = 0; i < container.getContainerSize() && !remaining.isEmpty(); i++) {
			ItemStack slot = container.getItem(i);
			if (ItemStack.isSameItemSameTags(slot, remaining)) {
				int room = slot.getMaxStackSize() - slot.getCount();
				int moved = Math.min(room, remaining.getCount());
				slot.grow(moved);
				remaining.shrink(moved);
				container.setChanged();
			}
		}
		for (int i = 0; i < container.getContainerSize() && !remaining.isEmpty(); i++) {
			if (container.getItem(i).isEmpty()) {
				container.setItem(i, remaining.copy());
				remaining = ItemStack.EMPTY;
				container.setChanged();
			}
		}
		return remaining;
	}
}
