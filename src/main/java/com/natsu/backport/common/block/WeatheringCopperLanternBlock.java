package com.natsu.backport.common.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

// copper lantern that oxidizes
public class WeatheringCopperLanternBlock extends LanternBlock implements CTBWeatheringCopper {

	private final WeatheringCopper.WeatherState weatherState;

	public WeatheringCopperLanternBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties props) {
		super(props);
		this.weatherState = weatherState;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		this.applyChangeOverTime(state, level, pos, random);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return CTBWeatheringCopper.getNextBlock(state.getBlock()).isPresent();
	}

	@Override
	public WeatheringCopper.WeatherState getAge() {
		return this.weatherState;
	}
}
