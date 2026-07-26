package com.natsu.backport.common.block;

import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Port of the 26.2 SulfurSpikeBlock and its SpeleothemBlock base : dripstone
 * mechanics that grow from sulfur, capped at two blocks.
 */
public class SulfurSpikeBlock extends Block implements Fallable, SimpleWaterloggedBlock {

	public static final DirectionProperty TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
	public static final EnumProperty<DripstoneThickness> THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;
	private static final int MAX_GROWTH_LENGTH = 2;
	private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;

	private static final VoxelShape SHAPE_TIP_MERGE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	private static final VoxelShape SHAPE_TIP_UP = Block.box(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
	private static final VoxelShape SHAPE_TIP_DOWN = Block.box(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
	private static final VoxelShape SHAPE_FRUSTUM = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
	private static final VoxelShape SHAPE_MIDDLE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
	private static final VoxelShape SHAPE_BASE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
	private static final float MAX_HORIZONTAL_OFFSET = 0.125F;

	private final java.util.function.Supplier<Block> blockToGrowOn;

	public SulfurSpikeBlock(java.util.function.Supplier<Block> blockToGrowOn, Properties properties) {
		super(properties);
		this.blockToGrowOn = blockToGrowOn;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(TIP_DIRECTION, Direction.UP)
				.setValue(THICKNESS, DripstoneThickness.TIP)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(TIP_DIRECTION, THICKNESS, WATERLOGGED);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return this.isValidPlacement(level, pos, state.getValue(TIP_DIRECTION));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		if (direction != Direction.UP && direction != Direction.DOWN) {
			return state;
		}
		Direction tipDirection = state.getValue(TIP_DIRECTION);
		if (tipDirection == Direction.DOWN && level.getBlockTicks().hasScheduledTick(pos, this)) {
			return state;
		}
		if (direction == tipDirection.getOpposite() && !this.canSurvive(state, level, pos)) {
			level.scheduleTick(pos, this, tipDirection == Direction.DOWN ? 2 : 1);
			return state;
		}
		boolean mergeOpposingTips = state.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
		return state.setValue(THICKNESS, this.calculateThickness(level, pos, tipDirection, mergeOpposingTips));
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction wanted = context.getNearestLookingVerticalDirection().getOpposite();
		Direction tipDirection = this.calculateTipDirection(level, pos, wanted);
		if (tipDirection == null) {
			return null;
		}
		boolean mergeOpposingTips = !context.isSecondaryUseActive();
		return this.defaultBlockState()
				.setValue(TIP_DIRECTION, tipDirection)
				.setValue(THICKNESS, this.calculateThickness(level, pos, tipDirection, mergeOpposingTips))
				.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Nullable
	private Direction calculateTipDirection(LevelReader level, BlockPos pos, Direction wanted) {
		if (this.isValidPlacement(level, pos, wanted)) {
			return wanted;
		}
		return this.isValidPlacement(level, pos, wanted.getOpposite()) ? wanted.getOpposite() : null;
	}

	private DripstoneThickness calculateThickness(LevelReader level, BlockPos pos, Direction tipDirection, boolean mergeOpposingTips) {
		Direction baseDirection = tipDirection.getOpposite();
		BlockState inFront = level.getBlockState(pos.relative(tipDirection));
		if (this.isSpikeWithDirection(inFront, baseDirection)) {
			return mergeOpposingTips || inFront.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE
					? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP;
		}
		if (!this.isSpikeWithDirection(inFront, tipDirection)) {
			return DripstoneThickness.TIP;
		}
		DripstoneThickness inFrontThickness = inFront.getValue(THICKNESS);
		if (inFrontThickness == DripstoneThickness.TIP || inFrontThickness == DripstoneThickness.TIP_MERGE) {
			return DripstoneThickness.FRUSTUM;
		}
		BlockState behind = level.getBlockState(pos.relative(baseDirection));
		return !this.isSpikeWithDirection(behind, tipDirection) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
	}

	private boolean isValidPlacement(LevelReader level, BlockPos pos, Direction tipDirection) {
		BlockPos behindPos = pos.relative(tipDirection.getOpposite());
		BlockState behind = level.getBlockState(behindPos);
		return behind.isFaceSturdy(level, behindPos, tipDirection) || this.isSpikeWithDirection(behind, tipDirection);
	}

	private boolean isSpikeWithDirection(BlockState state, Direction tipDirection) {
		return state.is(this) && state.getValue(TIP_DIRECTION) == tipDirection;
	}

	@Override
	public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
		BlockPos pos = hit.getBlockPos();
		if (!level.isClientSide && projectile.mayInteract(level, pos)
				&& projectile instanceof ThrownTrident && projectile.getDeltaMovement().length() > 0.6) {
			level.destroyBlock(pos, true);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (this.isStalagmite(state) && !this.canSurvive(state, level, pos)) {
			level.destroyBlock(pos, true);
		} else {
			this.spawnFallingStalactite(state, level, pos);
		}
	}

	private void spawnFallingStalactite(BlockState state, ServerLevel level, BlockPos pos) {
		BlockPos.MutableBlockPos fallPos = pos.mutable();
		BlockState fallState = state;
		while (this.isStalactite(fallState)) {
			FallingBlockEntity falling = FallingBlockEntity.fall(level, fallPos, fallState);
			if (this.isTip(fallState, true)) {
				int size = Math.max(1 + pos.getY() - fallPos.getY(), 6);
				falling.setHurtsEntities(1.0F * size, 40);
				break;
			}
			fallPos.move(Direction.DOWN);
			fallState = level.getBlockState(fallPos);
		}
	}

	private boolean isStalagmite(BlockState state) {
		return this.isSpikeWithDirection(state, Direction.UP);
	}

	private boolean isStalactite(BlockState state) {
		return this.isSpikeWithDirection(state, Direction.DOWN);
	}

	private boolean isTip(BlockState state, boolean includeMergedTip) {
		if (!state.is(this)) {
			return false;
		}
		DripstoneThickness thickness = state.getValue(THICKNESS);
		return thickness == DripstoneThickness.TIP || includeMergedTip && thickness == DripstoneThickness.TIP_MERGE;
	}

	@Override
	public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity entity) {
		if (!entity.isSilent()) {
			level.playSound(null, pos, CTBSounds.SULFUR_SPIKE_LAND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public DamageSource getFallDamageSource() {
		return DamageSource.FALLING_STALACTITE;
	}

	/** Landing on an upward tip hurts, the dripstone rule. */
	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (state.getValue(TIP_DIRECTION) == Direction.UP && state.getValue(THICKNESS) == DripstoneThickness.TIP) {
			entity.causeFallDamage(fallDistance + 2.0F, 2.0F, DamageSource.STALAGMITE);
		} else {
			super.fallOn(level, state, pos, entity, fallDistance);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape = switch (state.getValue(THICKNESS)) {
			case TIP_MERGE -> SHAPE_TIP_MERGE;
			case TIP -> state.getValue(TIP_DIRECTION) == Direction.DOWN ? SHAPE_TIP_DOWN : SHAPE_TIP_UP;
			case FRUSTUM -> SHAPE_FRUSTUM;
			case MIDDLE -> SHAPE_MIDDLE;
			case BASE -> SHAPE_BASE;
		};
		Vec3 offset = state.getOffset(level, pos);
		return shape.move(offset.x, 0.0, offset.z);
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}

	@Override
	public net.minecraft.world.level.block.state.BlockBehaviour.OffsetType getOffsetType() {
		return net.minecraft.world.level.block.state.BlockBehaviour.OffsetType.XZ;
	}

	@Override
	public float getMaxHorizontalOffset() {
		return MAX_HORIZONTAL_OFFSET;
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public net.minecraft.world.level.material.FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	// --- growth, gated by the block above the stalactite root ---

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (random.nextFloat() < GROWTH_PROBABILITY_PER_RANDOM_TICK && this.isStalactiteStartPos(state, level, pos)) {
			this.growStalactiteOrStalagmiteIfPossible(state, level, pos, random);
		}
	}

	private boolean isStalactiteStartPos(BlockState state, LevelReader level, BlockPos pos) {
		return this.isStalactite(state) && !level.getBlockState(pos.above()).is(this);
	}

	private void growStalactiteOrStalagmiteIfPossible(BlockState startState, ServerLevel level, BlockPos startPos, Random random) {
		if (!level.getBlockState(startPos.above()).is(this.blockToGrowOn.get())) {
			return;
		}
		BlockPos tipPos = this.findTip(startState, level, startPos, MAX_GROWTH_LENGTH, false);
		if (tipPos == null) {
			return;
		}
		BlockState tipState = level.getBlockState(tipPos);
		if (!this.isStalactite(tipState) || tipState.getValue(THICKNESS) != DripstoneThickness.TIP
				|| tipState.getValue(WATERLOGGED) || !this.canTipGrow(tipState, level, tipPos)) {
			return;
		}
		if (random.nextBoolean()) {
			this.grow(level, tipPos, Direction.DOWN);
		} else {
			this.growStalagmiteBelow(level, tipPos);
		}
	}

	@Nullable
	private BlockPos findTip(BlockState state, LevelAccessor level, BlockPos pos, int maxSearchLength, boolean includeMergedTip) {
		if (this.isTip(state, includeMergedTip)) {
			return pos;
		}
		Direction searchDirection = state.getValue(TIP_DIRECTION);
		BiPredicate<BlockPos, BlockState> pathPredicate =
				(p, s) -> s.is(this) && s.getValue(TIP_DIRECTION) == searchDirection;
		return findBlockVertical(level, pos, searchDirection.getAxisDirection(), pathPredicate,
				s -> this.isTip(s, includeMergedTip), maxSearchLength).orElse(null);
	}

	private static Optional<BlockPos> findBlockVertical(LevelAccessor level, BlockPos pos,
			Direction.AxisDirection axisDirection, BiPredicate<BlockPos, BlockState> pathPredicate,
			Predicate<BlockState> targetPredicate, int maxSteps) {
		Direction direction = Direction.get(axisDirection, Direction.Axis.Y);
		BlockPos.MutableBlockPos mutablePos = pos.mutable();
		for (int i = 1; i < maxSteps; i++) {
			mutablePos.move(direction);
			BlockState state = level.getBlockState(mutablePos);
			if (targetPredicate.test(state)) {
				return Optional.of(mutablePos.immutable());
			}
			if (level.isOutsideBuildHeight(mutablePos.getY()) || !pathPredicate.test(mutablePos, state)) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	private boolean canTipGrow(BlockState tipState, ServerLevel level, BlockPos tipPos) {
		Direction growDirection = tipState.getValue(TIP_DIRECTION);
		BlockPos growPos = tipPos.relative(growDirection);
		BlockState stateAtGrowPos = level.getBlockState(growPos);
		if (!stateAtGrowPos.getFluidState().isEmpty()) {
			return false;
		}
		return stateAtGrowPos.isAir() || this.isUnmergedTipWithDirection(stateAtGrowPos, growDirection.getOpposite());
	}

	private boolean isUnmergedTipWithDirection(BlockState state, Direction tipDirection) {
		return this.isTip(state, false) && state.getValue(TIP_DIRECTION) == tipDirection;
	}

	private void grow(ServerLevel level, BlockPos growFromPos, Direction growToDirection) {
		BlockPos targetPos = growFromPos.relative(growToDirection);
		BlockState existing = level.getBlockState(targetPos);
		if (this.isUnmergedTipWithDirection(existing, growToDirection.getOpposite())) {
			this.createMergedTips(existing, level, targetPos);
		} else if (existing.isAir() || existing.is(Blocks.WATER)) {
			this.createSpike(level, targetPos, growToDirection, DripstoneThickness.TIP);
		}
	}

	private void createSpike(LevelAccessor level, BlockPos pos, Direction direction, DripstoneThickness thickness) {
		BlockState state = this.defaultBlockState()
				.setValue(TIP_DIRECTION, direction)
				.setValue(THICKNESS, thickness)
				.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
		level.setBlock(pos, state, 3);
	}

	private void createMergedTips(BlockState tipState, LevelAccessor level, BlockPos tipPos) {
		BlockPos stalactitePos;
		BlockPos stalagmitePos;
		if (tipState.getValue(TIP_DIRECTION) == Direction.UP) {
			stalagmitePos = tipPos;
			stalactitePos = tipPos.above();
		} else {
			stalactitePos = tipPos;
			stalagmitePos = tipPos.below();
		}
		this.createSpike(level, stalactitePos, Direction.DOWN, DripstoneThickness.TIP_MERGE);
		this.createSpike(level, stalagmitePos, Direction.UP, DripstoneThickness.TIP_MERGE);
	}

	private void growStalagmiteBelow(ServerLevel level, BlockPos posAboveStalagmite) {
		BlockPos.MutableBlockPos pos = posAboveStalagmite.mutable();
		for (int i = 0; i < MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING; i++) {
			pos.move(Direction.DOWN);
			BlockState state = level.getBlockState(pos);
			if (!state.getFluidState().isEmpty()) {
				return;
			}
			if (this.isUnmergedTipWithDirection(state, Direction.UP) && this.canTipGrow(state, level, pos)) {
				this.grow(level, pos, Direction.UP);
				return;
			}
			if (this.isValidPlacement(level, pos, Direction.UP) && !level.isWaterAt(pos.below())) {
				this.grow(level, pos.below(), Direction.UP);
				return;
			}
		}
	}
}
