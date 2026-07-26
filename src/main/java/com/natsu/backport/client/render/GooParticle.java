package com.natsu.backport.client.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * The sulfur cube landing goo : lifted off the ground so it samples the light
 * above the surface, with a small outward hop like breaking particles.
 */
public class GooParticle extends TextureSheetParticle {

	GooParticle(ClientLevel level, double x, double y, double z) {
		super(level, x, y + 0.2, z);
		this.gravity = 0.7F;
		this.friction = 0.9F;
		this.xd = (this.random.nextFloat() - 0.5F) * 0.15;
		this.yd = 0.08 + this.random.nextFloat() * 0.1;
		this.zd = (this.random.nextFloat() - 0.5F) * 0.15;
		this.quadSize *= 0.9F;
		this.lifetime = 8 + this.random.nextInt(8);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {

		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
				double dx, double dy, double dz) {
			GooParticle particle = new GooParticle(level, x, y, z);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}
}
