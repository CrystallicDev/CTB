package com.natsu.backport.common.entity.goal;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.CopperChestBlock;
import com.natsu.backport.common.block.entity.CopperChestBlockEntity;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * One to one port of the 1.21.11 TransportItemsBetweenContainers behavior
 * wired with the copper golem values from CopperGolemAi, expressed as a goal.
 * The brain memories become plain maps with the same expiry rules.
 */
public class TransportItemsGoal extends Goal {

	// vanilla constants, same names
	private static final int TARGET_INTERACTION_TIME = 60;
	private static final int VISITED_POSITIONS_MEMORY_TIME = 6000;
	private static final int TRANSPORTED_ITEM_MAX_STACK_SIZE = 16;
	private static final int MAX_VISITED_POSITIONS = 10;
	private static final int MAX_UNREACHABLE_POSITIONS = 50;
	private static final int IDLE_COOLDOWN = 140;
	private static final double CLOSE_ENOUGH_TO_START_QUEUING_DISTANCE = 3.0;
	private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE = 0.5;
	private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_PATH_END_DISTANCE = 1.0;
	private static final double CLOSE_ENOUGH_TO_CONTINUE_INTERACTING_WITH_TARGET = 2.0;
	// CopperGolemAi wiring
	private static final float SPEED_MODIFIER = 1.0F;
	private static final int HORIZONTAL_SEARCH_DISTANCE = 32;
	private static final int VERTICAL_SEARCH_DISTANCE = 8;

	private enum TransportItemState { TRAVELLING, QUEUING, INTERACTING }

	private enum ContainerInteractionState { PICKUP_ITEM, PICKUP_NO_ITEM, PLACE_ITEM, PLACE_NO_ITEM }

	private record TransportItemTarget(BlockPos pos, Container container, BlockEntity blockEntity, BlockState state) {

		@Nullable
		static TransportItemTarget tryCreatePossibleTarget(BlockEntity blockEntity, Level level) {
			BlockPos pos = blockEntity.getBlockPos();
			BlockState state = blockEntity.getBlockState();
			Container container = getBlockEntityContainer(blockEntity, state, level, pos);
			return container != null ? new TransportItemTarget(pos, container, blockEntity, state) : null;
		}

		@Nullable
		static TransportItemTarget tryCreatePossibleTarget(BlockPos pos, Level level) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			return blockEntity == null ? null : tryCreatePossibleTarget(blockEntity, level);
		}

		@Nullable
		private static Container getBlockEntityContainer(BlockEntity blockEntity, BlockState state, Level level, BlockPos pos) {
			if (state.getBlock() instanceof ChestBlock chestBlock) {
				return ChestBlock.getContainer(chestBlock, state, level, pos, false);
			}
			return blockEntity instanceof Container container ? container : null;
		}
	}

	private final CopperGolem golem;
	@Nullable
	private TransportItemTarget target;
	private TransportItemState state = TransportItemState.TRAVELLING;
	@Nullable
	private ContainerInteractionState interactionState;
	private int ticksSinceReachingTarget;
	private int cooldown;
	// the brain memories, positions mapped to their expiry game time
	private final Map<BlockPos, Long> visitedPositions = new HashMap<>();
	private final Map<BlockPos, Long> unreachablePositions = new HashMap<>();

	public TransportItemsGoal(CopperGolem golem) {
		this.golem = golem;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		// the golem constructor seeds the first cooldown in vanilla
		this.cooldown = 60 + golem.getRandom().nextInt(41);
	}

	@Override
	public boolean canUse() {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		}
		return !this.golem.isLeashed();
	}

	@Override
	public boolean canContinueToUse() {
		return this.cooldown == 0 && !this.golem.isLeashed();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void start() {
		this.pruneExpiredMemories();
	}

	@Override
	public void stop() {
		this.onStartTravelling();
		this.stopTargetingCurrentTarget();
	}

	@Override
	public void tick() {
		boolean updatedInvalidTarget = this.updateInvalidTarget();
		if (this.target == null) {
			return;
		}
		if (!updatedInvalidTarget) {
			if (this.state == TransportItemState.QUEUING) {
				this.onQueuingForTarget(this.target);
			}
			if (this.state == TransportItemState.TRAVELLING) {
				this.onTravelToTarget(this.target);
			}
			if (this.state == TransportItemState.INTERACTING) {
				this.onReachedTarget(this.target);
			}
		}
	}

	// --- vanilla tick structure ---

	private boolean updateInvalidTarget() {
		if (this.hasValidTarget()) {
			return false;
		}
		this.stopTargetingCurrentTarget();
		Optional<TransportItemTarget> found = this.getTransportTarget();
		if (found.isPresent()) {
			this.target = found.get();
			this.onStartTravelling();
			this.setVisitedBlockPos(this.target.pos);
		} else {
			this.enterCooldownAfterNoMatchingTargetFound();
		}
		return true;
	}

	private void onQueuingForTarget(TransportItemTarget target) {
		if (!this.isAnotherMobInteractingWithTarget(target)) {
			this.setTransportingState(TransportItemState.TRAVELLING);
			this.walkTowardsTarget();
		}
	}

	private void onTravelToTarget(TransportItemTarget target) {
		if (this.isWithinTargetDistance(CLOSE_ENOUGH_TO_START_QUEUING_DISTANCE, target, this.getCenterPos())
				&& this.isAnotherMobInteractingWithTarget(target)) {
			this.stopInPlace();
			this.setTransportingState(TransportItemState.QUEUING);
		} else if (this.isWithinTargetDistance(this.getInteractionRange(), target, this.getCenterPos())) {
			this.startOnReachedTargetInteraction(target);
		} else {
			this.walkTowardsTarget();
		}
	}

	private void onReachedTarget(TransportItemTarget target) {
		if (!this.isWithinTargetDistance(CLOSE_ENOUGH_TO_CONTINUE_INTERACTING_WITH_TARGET, target, this.getCenterPos())) {
			this.onStartTravelling();
		} else {
			this.ticksSinceReachingTarget++;
			this.onTargetInteraction(target);
			if (this.ticksSinceReachingTarget >= TARGET_INTERACTION_TIME) {
				this.doReachedTargetInteraction(target.container,
						container -> this.pickUpItems(container),
						container -> this.stopTargetingCurrentTarget(),
						container -> this.putDownItem(container),
						container -> this.stopTargetingCurrentTarget());
				this.onStartTravelling();
			}
		}
	}

	private void startOnReachedTargetInteraction(TransportItemTarget target) {
		this.doReachedTargetInteraction(target.container,
				container -> this.interactionState = ContainerInteractionState.PICKUP_ITEM,
				container -> this.interactionState = ContainerInteractionState.PICKUP_NO_ITEM,
				container -> this.interactionState = ContainerInteractionState.PLACE_ITEM,
				container -> this.interactionState = ContainerInteractionState.PLACE_NO_ITEM);
		this.setTransportingState(TransportItemState.INTERACTING);
	}

	private void onStartTravelling() {
		// the CopperGolemAi travelling callback
		this.closeOpenedChest();
		this.golem.setState(CopperGolem.GolemState.IDLE);
		this.setTransportingState(TransportItemState.TRAVELLING);
		this.interactionState = null;
		this.ticksSinceReachingTarget = 0;
	}

	private void setTransportingState(TransportItemState state) {
		this.state = state;
	}

	/** The CopperGolemAi per tick interaction, same timeline. */
	private void onTargetInteraction(TransportItemTarget target) {
		this.golem.getLookControl().setLookAt(Vec3.atCenterOf(target.pos));
		this.stopInPlace();
		if (this.interactionState == null) {
			return;
		}
		if (this.ticksSinceReachingTarget == 1) {
			this.openChest(target);
			this.golem.setState(switch (this.interactionState) {
				case PICKUP_ITEM -> CopperGolem.GolemState.GETTING_ITEM;
				case PICKUP_NO_ITEM -> CopperGolem.GolemState.GETTING_NO_ITEM;
				case PLACE_ITEM -> CopperGolem.GolemState.DROPPING_ITEM;
				case PLACE_NO_ITEM -> CopperGolem.GolemState.DROPPING_NO_ITEM;
			});
		}
		if (this.ticksSinceReachingTarget == 9) {
			SoundEvent sound = switch (this.interactionState) {
				case PICKUP_ITEM -> CTBSounds.COPPER_GOLEM_NO_ITEM_GET.get();
				case PICKUP_NO_ITEM -> CTBSounds.COPPER_GOLEM_NO_ITEM_NO_GET.get();
				case PLACE_ITEM -> CTBSounds.COPPER_GOLEM_ITEM_DROP.get();
				case PLACE_NO_ITEM -> CTBSounds.COPPER_GOLEM_ITEM_NO_DROP.get();
			};
			this.golem.playSound(sound, 1.0F, 1.0F);
		}
		if (this.ticksSinceReachingTarget == TARGET_INTERACTION_TIME) {
			this.closeOpenedChest();
		}
	}

	private void doReachedTargetInteraction(Container container,
			java.util.function.Consumer<Container> onPickupSuccess,
			java.util.function.Consumer<Container> onPickupFailure,
			java.util.function.Consumer<Container> onPlaceSuccess,
			java.util.function.Consumer<Container> onPlaceFailure) {
		if (this.isPickingUpItems()) {
			if (!container.isEmpty()) {
				onPickupSuccess.accept(container);
			} else {
				onPickupFailure.accept(container);
			}
		} else if (matchesLeavingItemsRequirement(this.golem.getMainHandItem(), container)) {
			onPlaceSuccess.accept(container);
		} else {
			onPlaceFailure.accept(container);
		}
	}

	// --- target discovery, vanilla getTransportTarget ---

	private Optional<TransportItemTarget> getTransportTarget() {
		this.pruneExpiredMemories();
		AABB searchArea = new AABB(this.golem.blockPosition())
				.inflate(HORIZONTAL_SEARCH_DISTANCE, VERTICAL_SEARCH_DISTANCE, HORIZONTAL_SEARCH_DISTANCE);
		List<ChunkPos> chunks = ChunkPos.rangeClosed(new ChunkPos(this.golem.blockPosition()),
				Math.floorDiv(HORIZONTAL_SEARCH_DISTANCE, 16) + 1).toList();
		TransportItemTarget best = null;
		double closestDistance = Float.MAX_VALUE;

		for (ChunkPos chunkPos : chunks) {
			LevelChunk chunk = this.golem.level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
			if (chunk == null) {
				continue;
			}
			for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
				if (!(blockEntity instanceof ChestBlockEntity) && !(blockEntity instanceof CopperChestBlockEntity)) {
					continue;
				}
				double distance = blockEntity.getBlockPos().distToCenterSqr(this.golem.position());
				if (distance < closestDistance) {
					TransportItemTarget valid = this.isTargetValidToPick(blockEntity, searchArea);
					if (valid != null) {
						best = valid;
						closestDistance = distance;
					}
				}
			}
		}
		return Optional.ofNullable(best);
	}

	@Nullable
	private TransportItemTarget isTargetValidToPick(BlockEntity blockEntity, AABB searchArea) {
		BlockPos pos = blockEntity.getBlockPos();
		if (!searchArea.contains(pos.getX(), pos.getY(), pos.getZ())) {
			return null;
		}
		TransportItemTarget target = TransportItemTarget.tryCreatePossibleTarget(blockEntity, this.golem.level);
		if (target == null) {
			return null;
		}
		boolean valid = this.isWantedBlock(target.state)
				&& !this.isPositionAlreadyVisited(target)
				&& !isContainerLocked(target);
		return valid ? target : null;
	}

	private static boolean isContainerLocked(TransportItemTarget target) {
		if (target.blockEntity instanceof BaseContainerBlockEntity container) {
			String lock = container.saveWithoutMetadata().getString("Lock");
			return !lock.isEmpty();
		}
		return false;
	}

	private boolean isWantedBlock(BlockState state) {
		return this.isPickingUpItems()
				? state.getBlock() instanceof CopperChestBlock
				: state.getBlock() instanceof ChestBlock && !(state.getBlock() instanceof CopperChestBlock);
	}

	private boolean isPickingUpItems() {
		return this.golem.getMainHandItem().isEmpty();
	}

	// --- target validity, vanilla hasValidTarget chain ---

	private boolean hasValidTarget() {
		boolean validType = this.target != null && this.isWantedBlock(this.target.state) && this.targetHasNotChanged(this.target);
		if (validType && !isTargetBlocked(this.golem.level, this.target)) {
			if (this.state != TransportItemState.TRAVELLING) {
				return true;
			}
			if (this.hasValidTravellingPath(this.target)) {
				return true;
			}
			this.markVisitedBlockPosAsUnreachable(this.target.pos);
		}
		return false;
	}

	private boolean targetHasNotChanged(TransportItemTarget target) {
		return target.blockEntity.equals(this.golem.level.getBlockEntity(target.pos));
	}

	private static boolean isTargetBlocked(Level level, TransportItemTarget target) {
		// the vanilla isChestBlockedAt without the cat check
		BlockPos above = target.pos.above();
		return level.getBlockState(above).isRedstoneConductor(level, above);
	}

	private boolean hasValidTravellingPath(TransportItemTarget target) {
		Path path = this.golem.getNavigation().getPath() == null
				? this.golem.getNavigation().createPath(target.pos, 0)
				: this.golem.getNavigation().getPath();
		Vec3 reachFrom = this.getPositionToReachTargetFrom(path);
		boolean canReachTarget = this.isWithinTargetDistance(this.getInteractionRange(), target, reachFrom);
		boolean hasNotYetCreatedPathToTarget = path == null && !canReachTarget;
		return hasNotYetCreatedPathToTarget || (canReachTarget && this.canSeeAnyTargetSide(target, reachFrom));
	}

	private Vec3 getPositionToReachTargetFrom(@Nullable Path path) {
		boolean noValidPath = path == null || path.getEndNode() == null;
		Vec3 bottomCenter = noValidPath ? this.golem.position() : Vec3.atBottomCenterOf(path.getEndNode().asBlockPos());
		return this.setMiddleYPosition(bottomCenter);
	}

	private Vec3 getCenterPos() {
		return this.setMiddleYPosition(this.golem.position());
	}

	private Vec3 setMiddleYPosition(Vec3 pos) {
		return pos.add(0.0, this.golem.getBoundingBox().getYsize() / 2.0, 0.0);
	}

	private double getInteractionRange() {
		Path path = this.golem.getNavigation().getPath();
		boolean finished = path != null && path.isDone();
		return finished ? CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_PATH_END_DISTANCE
				: CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE;
	}

	private boolean isWithinTargetDistance(double distance, TransportItemTarget target, Vec3 fromPos) {
		AABB box = this.golem.getBoundingBox();
		AABB movedBox = AABB.ofSize(fromPos, box.getXsize(), box.getYsize(), box.getZsize());
		return target.state.getCollisionShape(this.golem.level, target.pos).bounds()
				.inflate(distance, 0.5, distance).move(target.pos).intersects(movedBox);
	}

	private boolean canSeeAnyTargetSide(TransportItemTarget target, Vec3 eyePosition) {
		Vec3 center = Vec3.atCenterOf(target.pos);
		return java.util.Arrays.stream(Direction.values())
				.map(dir -> center.add(0.5 * dir.getStepX(), 0.5 * dir.getStepY(), 0.5 * dir.getStepZ()))
				.map(hit -> this.golem.level.clip(new ClipContext(eyePosition, hit, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.golem)))
				.anyMatch(hit -> hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(target.pos));
	}

	// --- queueing, other golems with the chest open ---

	private boolean isAnotherMobInteractingWithTarget(TransportItemTarget target) {
		return this.getConnectedTargets(target).anyMatch(connected ->
				!this.golem.level.getEntitiesOfClass(CopperGolem.class, new AABB(connected.pos).inflate(4.0),
						other -> other != this.golem && connected.pos.equals(other.getOpenedChestPos())).isEmpty());
	}

	private Stream<TransportItemTarget> getConnectedTargets(TransportItemTarget target) {
		if (target.state.hasProperty(ChestBlock.TYPE) && target.state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
			TransportItemTarget connected = TransportItemTarget.tryCreatePossibleTarget(
					target.pos.relative(ChestBlock.getConnectedDirection(target.state)), this.golem.level);
			return connected != null ? Stream.of(target, connected) : Stream.of(target);
		}
		return Stream.of(target);
	}

	// --- memories with the vanilla caps and expiry ---

	private void pruneExpiredMemories() {
		long now = this.golem.level.getGameTime();
		this.visitedPositions.values().removeIf(expiry -> expiry <= now);
		this.unreachablePositions.values().removeIf(expiry -> expiry <= now);
	}

	private boolean isPositionAlreadyVisited(TransportItemTarget target) {
		return this.getConnectedTargets(target)
				.map(TransportItemTarget::pos)
				.anyMatch(pos -> this.visitedPositions.containsKey(pos) || this.unreachablePositions.containsKey(pos));
	}

	private void setVisitedBlockPos(BlockPos pos) {
		this.visitedPositions.put(pos.immutable(), this.golem.level.getGameTime() + VISITED_POSITIONS_MEMORY_TIME);
		if (this.visitedPositions.size() > MAX_VISITED_POSITIONS) {
			this.enterCooldownAfterNoMatchingTargetFound();
		}
	}

	private void markVisitedBlockPosAsUnreachable(BlockPos pos) {
		this.visitedPositions.remove(pos);
		this.unreachablePositions.put(pos.immutable(), this.golem.level.getGameTime() + VISITED_POSITIONS_MEMORY_TIME);
		if (this.unreachablePositions.size() > MAX_UNREACHABLE_POSITIONS) {
			this.enterCooldownAfterNoMatchingTargetFound();
		}
	}

	private void clearMemoriesAfterMatchingTargetFound() {
		this.stopTargetingCurrentTarget();
		this.visitedPositions.clear();
		this.unreachablePositions.clear();
	}

	private void enterCooldownAfterNoMatchingTargetFound() {
		this.stopTargetingCurrentTarget();
		this.cooldown = IDLE_COOLDOWN;
		this.visitedPositions.clear();
		this.unreachablePositions.clear();
	}

	private void stopTargetingCurrentTarget() {
		this.ticksSinceReachingTarget = 0;
		this.target = null;
		this.golem.getNavigation().stop();
	}

	// --- movement ---

	private void walkTowardsTarget() {
		if (this.target != null && this.golem.getNavigation().isDone()) {
			this.golem.getNavigation().moveTo(this.target.pos.getX() + 0.5, this.target.pos.getY(), this.target.pos.getZ() + 0.5, SPEED_MODIFIER);
		}
		if (this.target != null) {
			this.golem.getLookControl().setLookAt(Vec3.atCenterOf(this.target.pos));
		}
	}

	private void stopInPlace() {
		this.golem.getNavigation().stop();
		this.golem.setXxa(0.0F);
		this.golem.setZza(0.0F);
		this.golem.setSpeed(0.0F);
		this.golem.setDeltaMovement(0.0, this.golem.getDeltaMovement().y, 0.0);
	}

	// --- item transfer, vanilla verbatim ---

	private static boolean matchesLeavingItemsRequirement(ItemStack hand, Container container) {
		if (container.isEmpty()) {
			return true;
		}
		for (int i = 0; i < container.getContainerSize(); i++) {
			if (container.getItem(i).sameItem(hand)) {
				return true;
			}
		}
		return false;
	}

	private void pickUpItems(Container container) {
		this.golem.setItemSlot(EquipmentSlot.MAINHAND, pickupItemFromContainer(container));
		this.golem.setDropChance(EquipmentSlot.MAINHAND, 2.0F);
		container.setChanged();
		this.clearMemoriesAfterMatchingTargetFound();
	}

	private void putDownItem(Container container) {
		ItemStack leftover = addItemsToContainer(this.golem.getMainHandItem(), container);
		container.setChanged();
		this.golem.setItemSlot(EquipmentSlot.MAINHAND, leftover);
		if (leftover.isEmpty()) {
			this.clearMemoriesAfterMatchingTargetFound();
		} else {
			this.stopTargetingCurrentTarget();
		}
	}

	private static ItemStack pickupItemFromContainer(Container container) {
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			ItemStack stack = container.getItem(slot);
			if (!stack.isEmpty()) {
				return container.removeItem(slot, Math.min(stack.getCount(), TRANSPORTED_ITEM_MAX_STACK_SIZE));
			}
		}
		return ItemStack.EMPTY;
	}

	private static ItemStack addItemsToContainer(ItemStack hand, Container container) {
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			ItemStack inSlot = container.getItem(slot);
			if (inSlot.isEmpty()) {
				container.setItem(slot, hand);
				return ItemStack.EMPTY;
			}
			if (ItemStack.isSameItemSameTags(inSlot, hand) && inSlot.getCount() < inSlot.getMaxStackSize()) {
				int room = inSlot.getMaxStackSize() - inSlot.getCount();
				int toAdd = Math.min(room, hand.getCount());
				inSlot.grow(toAdd);
				hand.shrink(toAdd);
				container.setItem(slot, inSlot);
				if (hand.isEmpty()) {
					return ItemStack.EMPTY;
				}
			}
		}
		return hand;
	}

	// --- chest lids, driven like the vanilla opener counters ---

	private void openChest(TransportItemTarget target) {
		this.golem.setOpenedChestPos(target.pos);
		this.setChestLid(target.pos, true);
	}

	private void closeOpenedChest() {
		BlockPos opened = this.golem.getOpenedChestPos();
		if (opened != null) {
			this.setChestLid(opened, false);
			this.golem.clearOpenedChestPos();
		}
	}

	private void setChestLid(BlockPos pos, boolean open) {
		BlockState state = this.golem.level.getBlockState(pos);
		this.golem.level.blockEvent(pos, state.getBlock(), 1, open ? 1 : 0);
		boolean copper = this.golem.level.getBlockEntity(pos) instanceof CopperChestBlockEntity;
		SoundEvent sound = copper
				? (open ? CTBSounds.COPPER_CHEST_OPEN.get() : CTBSounds.COPPER_CHEST_CLOSE.get())
				: (open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE);
		this.golem.level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.5F,
				this.golem.getRandom().nextFloat() * 0.1F + 0.9F);
	}
}
