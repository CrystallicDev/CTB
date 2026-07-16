package com.natsu.backport.common.particles;

import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT)
public class GustEmitterParticle extends NoRenderParticle {

	private final boolean isLarge;
	private float currentRadius;
	private final float expansionSpeed;
	private final int gustsPerRing;
	private final float gustSpeed;

	public GustEmitterParticle(ClientLevel world, double x, double y, double z, boolean isLarge) {
		super(world, x, y, z, 0, 0, 0);
		if (isLarge) {
			this.lifetime = 8;
			this.currentRadius = 0.5f;
			this.expansionSpeed = 0.4f;
			this.gustsPerRing = 8;
			this.gustSpeed = 0.08f;
		} else {
			this.lifetime = 4;
			this.currentRadius = 0.2f;
			this.expansionSpeed = 0.2f;
			this.gustsPerRing = 4;
			this.gustSpeed = 0.05f;
		}
		this.isLarge = isLarge;
	}

	@Override
	public void tick() {
	    for (int i = 0; i < gustsPerRing; i++) {
	        double angle = (i / (double) gustsPerRing) * Math.PI * 2;
	        // random pitch so the burst is a sphere, not a flat ring
	        double pitch = (random.nextDouble() - 0.5) * Math.PI * 0.8;
	        double cosP = Math.cos(pitch);

	        double px = this.x + Math.cos(angle) * cosP * currentRadius;
	        double py = this.y + Math.sin(pitch) * currentRadius;
	        double pz = this.z + Math.sin(angle) * cosP * currentRadius;
	        double vx = Math.cos(angle) * cosP * gustSpeed;
	        double vy = Math.sin(pitch) * gustSpeed;
	        double vz = Math.sin(angle) * cosP * gustSpeed;

	        this.level.addParticle(CTBParticles.GUST.get(), px, py, pz, vx, vy, vz);
	    }
	    this.currentRadius += this.expansionSpeed;
	    if (this.age++ >= this.lifetime) {
	        this.remove();
	    }
	}

	@OnlyIn(value = Dist.CLIENT)
	public static class SmallProvider implements ParticleProvider<SimpleParticleType>{

		private final SpriteSet spriteSet;

		public SmallProvider(SpriteSet set) {
			this.spriteSet = set;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel lvl, double x,
				double y, double z, double xd, double yd, double zd) {
			GustEmitterParticle p = new GustEmitterParticle(lvl, x, y, z, false);
			return p;
		}

	}

	@OnlyIn(value = Dist.CLIENT)
	public static class LargeProvider implements ParticleProvider<SimpleParticleType>{

		private final SpriteSet spriteSet;

		public LargeProvider(SpriteSet set) {
			this.spriteSet = set;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel lvl, double x,
				double y, double z, double xd, double yd, double zd) {
			GustEmitterParticle p = new GustEmitterParticle(lvl, x, y, z, true);
			return p;
		}

	}

}
