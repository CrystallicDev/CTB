package com.natsu.backport.common.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

// backport of the 1.21 trail particle : drifts from its spawn point to a target
public class TrailParticle extends TextureSheetParticle {

	private final double startX;
	private final double startY;
	private final double startZ;
	private final Vec3 target;

	protected TrailParticle(ClientLevel level, double x, double y, double z, TrailParticleOption options, SpriteSet sprites) {
		super(level, x, y, z);
		this.startX = x;
		this.startY = y;
		this.startZ = z;
		this.target = options.getTarget();
		this.lifetime = options.getDuration();

		int color = options.getColor();
		setColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);

		this.quadSize = 0.26F + this.random.nextFloat() * 0.12F;
		this.gravity = 0.0F;
		this.hasPhysics = false;
		this.pickSprite(sprites);
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
		double t = (double) this.age / this.lifetime;
		setPos(Mth.lerp(t, startX, target.x), Mth.lerp(t, startY, target.y), Mth.lerp(t, startZ, target.z));
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Provider implements ParticleProvider<TrailParticleOption> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(TrailParticleOption options, ClientLevel level,
				double x, double y, double z, double dx, double dy, double dz) {
			return new TrailParticle(level, x, y, z, options, sprites);
		}
	}
}
