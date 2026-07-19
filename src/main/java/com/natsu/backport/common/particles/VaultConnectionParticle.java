package com.natsu.backport.common.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Flies in a straight line from the vault keyhole to the connected player,
 * the speed vector carries the full travel distance.
 */
public class VaultConnectionParticle extends TextureSheetParticle {

	private final double xStart;
	private final double yStart;
	private final double zStart;
	private final double xTravel;
	private final double yTravel;
	private final double zTravel;

	VaultConnectionParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
		super(level, x, y, z);
		this.xStart = x;
		this.yStart = y;
		this.zStart = z;
		this.xTravel = vx;
		this.yTravel = vy;
		this.zTravel = vz;
		this.quadSize *= 0.75F;
		this.lifetime = (int) (Math.random() * 10.0) + 20;
		this.hasPhysics = false;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
			return;
		}

		float progress = (float) this.age / (float) this.lifetime;
		this.setPos(this.xStart + this.xTravel * progress, this.yStart + this.yTravel * progress, this.zStart + this.zTravel * progress);
		this.alpha = 1.0F - progress;
	}

	@Override
	public int getLightColor(float partialTick) {
		return 240;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
			VaultConnectionParticle particle = new VaultConnectionParticle(level, x, y, z, vx, vy, vz);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}
}
