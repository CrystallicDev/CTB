package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.block.entity.CopperChestBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

/** The oxidizing copper chest : only the lead half ticks, and never while open. */
public class WeatheringCopperChestBlock extends CopperChestBlock implements CTBWeatheringCopper {

	public WeatheringCopperChestBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
		super(weatherState, properties);
	}

	@Override
	public boolean isWaxed() {
		return false;
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return CTBWeatheringCopper.getNextBlock(state.getBlock()).isPresent();
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (state.getValue(TYPE) != ChestType.RIGHT
				&& level.getBlockEntity(pos) instanceof CopperChestBlockEntity chest
				&& chest.getOpenerCount() == 0) {
			this.applyChangeOverTime(state, level, pos, random);
		}
	}

	@Override
	public WeatheringCopper.WeatherState getAge() {
		return this.getWeatherState();
	}
}
