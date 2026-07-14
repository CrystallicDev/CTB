package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HangingMossBlock extends Block implements BonemealableBlock {

    public static final BooleanProperty TIP = BooleanProperty.create("tip");

    private static final VoxelShape SHAPE_BASE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    private static final VoxelShape SHAPE_TIP  = Block.box(1.0, 2.0, 1.0, 15.0, 16.0, 15.0);

    public HangingMossBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TIP, true));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return state.getValue(TIP) ? SHAPE_TIP : SHAPE_BASE;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        if (random.nextInt(500) == 0) {
            BlockState above = world.getBlockState(pos.above());
            if (above.is(BlockTags.LOGS) || above.is(CTBBlocks.PALE_OAK_LEAVES.leaves.get())) {
                world.playLocalSound(
                    pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.VINE_STEP,
                    SoundSource.AMBIENT, 1.0F, 1.0F, false
                );
            }
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canStayAtPosition(world, pos);
    }

    private boolean canStayAtPosition(BlockGetter world, BlockPos pos) {
        BlockPos above = pos.relative(Direction.UP);
        BlockState aboveState = world.getBlockState(above);
        // vanilla also accepts a full collision face, that's how it hangs from leaves
        return aboveState.isFaceSturdy(world, above, Direction.DOWN)
            || Block.isFaceFull(aboveState.getCollisionShape(world, above), Direction.DOWN)
            || aboveState.is(this);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                   LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!canStayAtPosition(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        return state.setValue(TIP, !world.getBlockState(pos.below()).is(this));
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        if (!canStayAtPosition(world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIP);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient) {
        return canGrowInto(world.getBlockState(getTip(world, pos).below()));
    }

    private boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    public BlockPos getTip(BlockGetter world, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        BlockState state;
        do {
            mutable.move(Direction.DOWN);
            state = world.getBlockState(mutable);
        } while (state.is(this));
        return mutable.relative(Direction.UP).immutable();
    }

    @Override
    public boolean isBonemealSuccess(Level world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, Random random, BlockPos pos, BlockState state) {
        BlockPos tip = getTip(world, pos).below();
        if (canGrowInto(world.getBlockState(tip))) {
            world.setBlockAndUpdate(tip, state.setValue(TIP, true));
        }
    }
}