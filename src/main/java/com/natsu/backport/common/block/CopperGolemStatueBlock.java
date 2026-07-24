package com.natsu.backport.common.block;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.entity.CopperGolemStatueBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;

/**
 * One block for the whole statue family, the block entity carries the
 * weather level, wax flag and pose. Scraping a clean statue frees the golem.
 */
public class CopperGolemStatueBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<Pose> POSE = EnumProperty.create("pose", Pose.class);

	private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 15.0, 13.0);

	public enum Pose implements StringRepresentable {
		STANDING("standing"), SITTING("sitting"), RUNNING("running"), STAR("star");

		private final String name;

		Pose(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public Pose next() {
			return values()[(this.ordinal() + 1) % values().length];
		}
	}

	public CopperGolemStatueBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH).setValue(POSE, Pose.STANDING));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POSE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CopperGolemStatueBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		if (!(level.getBlockEntity(pos) instanceof CopperGolemStatueBlockEntity statue)) {
			return InteractionResult.PASS;
		}

		if (held.is(Items.HONEYCOMB) && !statue.isWaxed()) {
			if (!level.isClientSide) {
				level.levelEvent(null, 3003, pos, 0);
				statue.setWaxed(true);
				if (!player.getAbilities().instabuild) {
					held.shrink(1);
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		if (held.getItem() instanceof net.minecraft.world.item.AxeItem) {
			if (!level.isClientSide) {
				level.playSound(null, pos, SoundEvents.AXE_SCRAPE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
				held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				if (statue.isWaxed()) {
					level.levelEvent(null, 3004, pos, 0);
					statue.setWaxed(false);
				} else if (statue.getWeatherLevel() > 0) {
					level.levelEvent(null, 3005, pos, 0);
					statue.setWeatherLevel(statue.getWeatherLevel() - 1);
				} else {
					// a fully scraped statue wakes back up
					statue.releaseGolem(level, pos, state);
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		if (held.isEmpty() && player.isSecondaryUseActive()) {
			if (!level.isClientSide) {
				level.setBlock(pos, state.setValue(POSE, state.getValue(POSE).next()), Block.UPDATE_ALL);
				level.playSound(null, pos, SoundEvents.COPPER_STEP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
