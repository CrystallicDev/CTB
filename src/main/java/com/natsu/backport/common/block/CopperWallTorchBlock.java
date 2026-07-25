package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CopperWallTorchBlock extends WallTorchBlock {

	public CopperWallTorchBlock(Properties props) {
		super(props, ParticleTypes.FLAME);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		Direction opposite = state.getValue(FACING).getOpposite();
		double x = pos.getX() + 0.5 + 0.27 * opposite.getStepX();
		double y = pos.getY() + 0.7 + 0.22;
		double z = pos.getZ() + 0.5 + 0.27 * opposite.getStepZ();
		level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
		level.addParticle(CTBParticles.COPPER_FIRE_FLAME.get(), x, y, z, 0.0, 0.0, 0.0);
	}
}
