package com.natsu.backport.common.particles;

import java.util.Random;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GustParticle extends TextureSheetParticle {

	private final SpriteSet sprites;
	
	public GustParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, SpriteSet spriteSet) {
		super(world, x, y, z, dx, dy, dz);
		this.sprites = spriteSet;
		this.lifetime = 10 + new Random().nextInt(6);
		this.hasPhysics = false;
		this.gravity = 0f;
		this.quadSize = 0.5f + new Random().nextFloat() * 0.5f;
		this.alpha = 1.0f;
		this.rCol = 1.0f;
		this.gCol = 1.0f;
		this.bCol = 1.0f;
		
		this.xd = dx;
		this.yd = dy;
		this.zd = dz;
		this.setSpriteFromAge(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(sprites);
		if (this.age > this.lifetime / 2) {
			float progress = (float)(this.age - this.lifetime / 2) / (float)(this.lifetime / 2);
			this.alpha = 1.0f - progress;
		}
		this.xd *= 0.9;
		this.yd *= 0.9;
		this.zd *= 0.9;
	}
	
	@OnlyIn(value = Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType>{

		private final SpriteSet spriteSet;
		
		public Provider(SpriteSet set) {
			this.spriteSet = set;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel lvl, double x,
				double y, double z, double xd, double yd, double zd) {
			GustParticle p = new GustParticle(lvl, x, y, z, xd, yd, zd, spriteSet);
			return p;
		}

	}


}
