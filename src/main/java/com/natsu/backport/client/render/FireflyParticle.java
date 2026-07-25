package com.natsu.backport.client.render;

import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

/** Port of the 1.21.5 firefly : a glowing dot that wanders and fades in and out. */
public class FireflyParticle extends TextureSheetParticle {

	FireflyParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
		super(level, x, y, z, dx, dy, dz);
		this.friction = 0.96F;
		this.quadSize *= 0.75F;
		this.xd *= 0.8;
		this.yd *= 0.8;
		this.zd *= 0.8;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getLightColor(float partialTick) {
		int brightness = (int) (255.0F * getFadeAmount(this.getLifetimeProgress(this.age + partialTick), 0.1F, 0.3F));
		return brightness << 16 | brightness;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
			this.remove();
		} else {
			this.setAlpha(getFadeAmount(this.getLifetimeProgress(this.age), 0.3F, 0.5F));
			if (Math.random() > 0.95 || this.age == 1) {
				this.setParticleSpeed(-0.05F + 0.1F * Math.random(), -0.05F + 0.1F * Math.random(), -0.05F + 0.1F * Math.random());
			}
		}
	}

	private float getLifetimeProgress(float age) {
		return Mth.clamp(age / this.lifetime, 0.0F, 1.0F);
	}

	private static float getFadeAmount(float progress, float fadeOut, float fadeIn) {
		if (progress >= 1.0F - fadeOut) {
			return (1.0F - progress) / fadeOut;
		}
		return progress <= fadeIn ? progress / fadeIn : 1.0F;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {

		private final SpriteSet sprite;

		public Provider(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
				double dx, double dy, double dz) {
			FireflyParticle particle = new FireflyParticle(level, x, y, z, 0.5 - level.random.nextDouble(),
					level.random.nextBoolean() ? dy : -dy, 0.5 - level.random.nextDouble());
			particle.setLifetime(36 + level.random.nextInt(145));
			particle.scale(1.5F);
			particle.pickSprite(this.sprite);
			particle.setAlpha(0.0F);
			return particle;
		}
	}
}
