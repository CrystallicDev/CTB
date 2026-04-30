package com.natsu.backport.common.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperTrapDoorBlock extends TrapDoorBlock implements WeatheringCopper {

	private final WeatheringCopper.WeatherState weatherState;

	public WeatheringCopperTrapDoorBlock(WeatheringCopper.WeatherState weatherState,
			BlockBehaviour.Properties props) {
		super(props);
		this.weatherState = weatherState;
	}

	@Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        this.applyChangeOverTime(state, level, pos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

	@Override
	public WeatheringCopper.WeatherState getAge() {
		return this.weatherState;
	}
}
