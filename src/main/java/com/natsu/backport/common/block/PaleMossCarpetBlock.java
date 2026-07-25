package com.natsu.backport.common.block;

import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Port of the 1.21.11 MossyCarpetBlock : a carpet whose sides climb the walls. */
public class PaleMossCarpetBlock extends Block implements BonemealableBlock {

	public static final BooleanProperty BASE = BlockStateProperties.BOTTOM;
	public static final EnumProperty<WallSide> NORTH = BlockStateProperties.NORTH_WALL;
	public static final EnumProperty<WallSide> EAST = BlockStateProperties.EAST_WALL;
	public static final EnumProperty<WallSide> SOUTH = BlockStateProperties.SOUTH_WALL;
	public static final EnumProperty<WallSide> WEST = BlockStateProperties.WEST_WALL;
	public static final Map<Direction, EnumProperty<WallSide>> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(
			Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)));

	private static final VoxelShape BASE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final Map<Direction, VoxelShape> LOW_SHAPES = Map.of(
			Direction.NORTH, Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 1.0),
			Direction.SOUTH, Block.box(0.0, 0.0, 15.0, 16.0, 10.0, 16.0),
			Direction.WEST, Block.box(0.0, 0.0, 0.0, 1.0, 10.0, 16.0),
			Direction.EAST, Block.box(15.0, 0.0, 0.0, 16.0, 10.0, 16.0));
	private static final Map<Direction, VoxelShape> TALL_SHAPES = Map.of(
			Direction.NORTH, Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
			Direction.SOUTH, Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
			Direction.WEST, Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0),
			Direction.EAST, Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0));

	private final Map<BlockState, VoxelShape> shapes;

	public PaleMossCarpetBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(BASE, true)
				.setValue(NORTH, WallSide.NONE).setValue(EAST, WallSide.NONE)
				.setValue(SOUTH, WallSide.NONE).setValue(WEST, WallSide.NONE));
		ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
		for (BlockState state : this.stateDefinition.getPossibleStates()) {
			builder.put(state, makeShape(state));
		}
		this.shapes = builder.build();
	}

	private static VoxelShape makeShape(BlockState state) {
		VoxelShape shape = state.getValue(BASE) ? BASE_SHAPE : Shapes.empty();
		for (Map.Entry<Direction, EnumProperty<WallSide>> entry : PROPERTY_BY_DIRECTION.entrySet()) {
			switch (state.getValue(entry.getValue())) {
				case LOW -> shape = Shapes.or(shape, LOW_SHAPES.get(entry.getKey()));
				case TALL -> shape = Shapes.or(shape, TALL_SHAPES.get(entry.getKey()));
				default -> { }
			}
		}
		return shape.isEmpty() ? Shapes.block() : shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return this.shapes.get(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BASE) ? BASE_SHAPE : Shapes.empty();
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState below = level.getBlockState(pos.below());
		return state.getValue(BASE) ? !below.isAir() : below.is(this) && below.getValue(BASE);
	}

	private static boolean hasFaces(BlockState state) {
		if (state.getValue(BASE)) {
			return true;
		}
		for (EnumProperty<WallSide> property : PROPERTY_BY_DIRECTION.values()) {
			if (state.getValue(property) != WallSide.NONE) {
				return true;
			}
		}
		return false;
	}

	private static boolean canSupportAtFace(BlockGetter level, BlockPos pos, Direction direction) {
		if (direction == Direction.UP) {
			return false;
		}
		BlockPos neighborPos = pos.relative(direction);
		BlockState neighbor = level.getBlockState(neighborPos);
		return Block.isFaceFull(neighbor.getBlockSupportShape(level, neighborPos), direction.getOpposite())
				|| Block.isFaceFull(neighbor.getCollisionShape(level, neighborPos), direction.getOpposite());
	}

	private BlockState getUpdatedState(BlockState state, BlockGetter level, BlockPos pos, boolean createSides) {
		BlockState above = null;
		BlockState below = null;
		createSides |= state.getValue(BASE);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			EnumProperty<WallSide> property = getPropertyForFace(direction);
			WallSide side = canSupportAtFace(level, pos, direction)
					? (createSides ? WallSide.LOW : state.getValue(property)) : WallSide.NONE;
			if (side == WallSide.LOW) {
				if (above == null) {
					above = level.getBlockState(pos.above());
				}
				if (above.is(this) && above.getValue(property) != WallSide.NONE && !above.getValue(BASE)) {
					side = WallSide.TALL;
				}
				if (!state.getValue(BASE)) {
					if (below == null) {
						below = level.getBlockState(pos.below());
					}
					if (below.is(this) && below.getValue(property) == WallSide.NONE) {
						side = WallSide.NONE;
					}
				}
			}
			state = state.setValue(property, side);
		}
		return state;
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.getUpdatedState(this.defaultBlockState(), context.getLevel(), context.getClickedPos(), true);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack stack) {
		if (!level.isClientSide) {
			Random random = level.getRandom();
			BlockState topper = this.createTopperWithSideChance(level, pos, random::nextBoolean);
			if (!topper.isAir()) {
				level.setBlock(pos.above(), topper, Block.UPDATE_ALL);
			}
		}
	}

	/** The wall hanging part above, each side kept on a coin flip like vanilla. */
	private BlockState createTopperWithSideChance(BlockGetter level, BlockPos pos, BooleanSupplier sideSurvivalTest) {
		BlockPos abovePos = pos.above();
		BlockState previousAbove = level.getBlockState(abovePos);
		boolean carpetAbove = previousAbove.is(this);
		if ((!carpetAbove || !previousAbove.getValue(BASE))
				&& (carpetAbove || previousAbove.getMaterial().isReplaceable())) {
			BlockState topper = this.getUpdatedState(this.defaultBlockState().setValue(BASE, false), level, abovePos, true);
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				EnumProperty<WallSide> property = getPropertyForFace(direction);
				if (topper.getValue(property) != WallSide.NONE && !sideSurvivalTest.getAsBoolean()) {
					topper = topper.setValue(property, WallSide.NONE);
				}
			}
			return hasFaces(topper) && topper != previousAbove ? topper : Blocks.AIR.defaultBlockState();
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (!state.canSurvive(level, pos)) {
			return Blocks.AIR.defaultBlockState();
		}
		BlockState updated = this.getUpdatedState(state, level, pos, false);
		return !hasFaces(updated) ? Blocks.AIR.defaultBlockState() : updated;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BASE, NORTH, EAST, SOUTH, WEST);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return switch (rotation) {
			case CLOCKWISE_180 -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST))
					.setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
			case COUNTERCLOCKWISE_90 -> state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH))
					.setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
			case CLOCKWISE_90 -> state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH))
					.setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
			default -> state;
		};
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return switch (mirror) {
			case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
			case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
			default -> super.mirror(state, mirror);
		};
	}

	@Nullable
	public static EnumProperty<WallSide> getPropertyForFace(Direction direction) {
		return PROPERTY_BY_DIRECTION.get(direction);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
		return state.getValue(BASE) && !this.createTopperWithSideChance(level, pos, () -> true).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
		BlockState topper = this.createTopperWithSideChance(level, pos, () -> true);
		if (!topper.isAir()) {
			level.setBlock(pos.above(), topper, Block.UPDATE_ALL);
		}
	}
}
