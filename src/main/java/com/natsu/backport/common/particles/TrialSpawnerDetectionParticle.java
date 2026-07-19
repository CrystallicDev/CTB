package com.natsu.backport.common.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class TrialSpawnerDetectionParticle extends TextureSheetParticle {

	private final SpriteSet sprites;

	TrialSpawnerDetectionParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet sprites) {
		super(level, x, y, z, 0.0, 0.0, 0.0);
		this.sprites = sprites;
		this.friction = 0.96F;
		this.gravity = -0.1F;
		this.speedUpWhenYMotionIsBlocked = true;
		this.xd *= 0.0;
		this.yd *= 0.9;
		this.zd *= 0.0;
		this.xd += vx;
		this.yd += vy;
		this.zd += vz;
		this.quadSize *= 0.75F * scale;
		this.lifetime = (int) (8.0 / (this.random.nextDouble() * 0.8 + 0.2));
		this.setSpriteFromAge(sprites);
		this.hasPhysics = false;
	}

	@Override
	public int getLightColor(float partialTick) {
		return 240;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.sprites);
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
			return new TrialSpawnerDetectionParticle(level, x, y, z, vx, vy, vz, 1.5F, this.sprites);
		}
	}
}
