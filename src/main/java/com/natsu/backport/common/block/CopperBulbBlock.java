package com.natsu.backport.common.block;

import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            BlockState newState = state;
            if (!state.getValue(POWERED)) {
                newState = state.cycle(LIT);
                level.playSound(null, pos,
                    newState.getValue(LIT) ? CTBSounds.COPPER_BULB_TURN_ON.get() : CTBSounds.COPPER_BULB_TURN_OFF.get(),
                    SoundSource.BLOCKS, 1.0f, 1.0f);
            }

            level.setBlock(pos, newState.setValue(POWERED, powered), 3);
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
