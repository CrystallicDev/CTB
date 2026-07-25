package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Green flame ; the particle type registers after blocks, so it resolves lazily. */
public class CopperTorchBlock extends TorchBlock {

	public CopperTorchBlock(Properties props) {
		super(props, ParticleTypes.FLAME);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.7;
		double z = pos.getZ() + 0.5;
		level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
		level.addParticle(CTBParticles.COPPER_FIRE_FLAME.get(), x, y, z, 0.0, 0.0, 0.0);
	}
}
