package com.natsu.backport.common.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperDoorBlock extends DoorBlock implements WeatheringCopper {

	private final WeatheringCopper.WeatherState weatherState;

	public WeatheringCopperDoorBlock(WeatheringCopper.WeatherState weather,
			BlockBehaviour.Properties props) {
		super(props);
		this.weatherState = weather;
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