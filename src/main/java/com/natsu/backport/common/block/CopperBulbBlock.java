package com.natsu.backport.common.block;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CopperBulbBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CopperBulbBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(POWERED, false));
    }

    @Override
	public void onPlace(BlockState state, Level level, BlockPos nearPos, BlockState nearState, boolean b) {
        if (nearState.getBlock() != state.getBlock() && level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, nearPos);
        }
    }

    @Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block nearBlock, BlockPos nearPos, boolean isMoving) {
        if (level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }
    }

    public void checkAndFlip(BlockState p_309989_, ServerLevel p_310260_, BlockPos p_310537_) {
        boolean flag = p_310260_.hasNeighborSignal(p_310537_);
        if (flag != p_309989_.getValue(POWERED)) {
            BlockState blockstate = p_309989_;
            if (!p_309989_.getValue(POWERED)) {
                blockstate = p_309989_.cycle(LIT);
                p_310260_.playSound(null, p_310537_, CTBSounds.COPPER_BULB_TURN_ON.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }

            p_310260_.setBlock(p_310537_, blockstate.setValue(POWERED, flag), 3);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_312159_) {
        p_312159_.add(LIT, POWERED);
    }

    @Override
	public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LIT) ? 15 : 0;
    }
}
