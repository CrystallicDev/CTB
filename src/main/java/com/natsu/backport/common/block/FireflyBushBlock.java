package com.natsu.backport.common.block;

import java.util.Random;

import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/** The glowing bush that spawns fireflies in the dark. */
public class FireflyBushBlock extends SpringBushBlock {

	public FireflyBushBlock(Properties props) {
		super(props);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (random.nextInt(30) == 0
				&& level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) <= pos.getY()) {
			level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					CTBSounds.FIREFLY_BUSH_IDLE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
		}
		if (level.getMaxLocalRawBrightness(pos) <= 13 && random.nextDouble() <= 0.7) {
			double x = pos.getX() + random.nextDouble() * 10.0 - 5.0;
			double y = pos.getY() + random.nextDouble() * 5.0;
			double z = pos.getZ() + random.nextDouble() * 10.0 - 5.0;
			level.addParticle(CTBParticles.FIREFLY.get(), x, y, z, 0.0, 0.0, 0.0);
		}
	}
}
