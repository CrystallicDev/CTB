package com.natsu.backport.client.render;

import com.natsu.backport.common.particle.GeyserParticleOptions;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;

/** Ports of the 26.2 geyser and sulfur cave particles. */
public final class GeyserParticles {

	private GeyserParticles() {
	}

	/** The water jet : shoots up the column then hangs five frames. */
	public static class Plume extends TextureSheetParticle {

		private final SpriteSet sprites;
		private final double startY;
		private final double maxY;
		private final float initialPropulsion;
		private final float horizontalSprayX;
		private final float horizontalSprayZ;
		private boolean done;

		Plume(ClientLevel level, double x, double y, double z, int waterBlocks, SpriteSet sprites) {
			super(level, x, y, z);
			int plumeHeight = 5 * Math.max(1, waterBlocks);
			this.hasPhysics = true;
			this.lifetime = plumeHeight * 5;
			this.yd = 0.0;
			this.startY = y;
			this.maxY = this.startY + plumeHeight - 1.0;
			this.horizontalSprayX = (level.random.nextFloat() - 0.5F) * 0.2F;
			this.horizontalSprayZ = (level.random.nextFloat() - 0.5F) * 0.2F;
			this.friction = 1.0F;
			this.initialPropulsion = (waterBlocks == 1 ? 1.5F : 1.0F) * plumeHeight * 1.45F;
			this.gravity = -this.initialPropulsion;
			float initialSize = this.quadSize * 0.75F;
			this.quadSize = initialSize * (2.0F + plumeHeight / 8.0F);
			this.sprites = sprites;
			this.setSpriteFromAge(sprites);
		}

		@Override
		public void tick() {
			super.tick();
			this.setSpriteFromAge(this.sprites);
			if (!this.done && (this.yd < 0.0 || this.y > this.maxY || this.y == this.yo)) {
				this.lifetime = Math.min(this.lifetime, this.age + 5);
				this.friction = 0.0F;
				this.done = true;
			}
			double progress = Mth.clamp((this.y - this.startY) / (this.maxY - this.startY), 0.0, 1.0);
			this.gravity = this.initialPropulsion * (float) Math.pow(progress, 3.0) * 0.12F;
			this.xd = progress * this.horizontalSprayX;
			this.zd = progress * this.horizontalSprayZ;
		}

		@Override
		public ParticleRenderType getRenderType() {
			return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
		}

		public static class Provider implements ParticleProvider<GeyserParticleOptions> {

			private final SpriteSet sprites;

			public Provider(SpriteSet sprites) {
				this.sprites = sprites;
			}

			@Override
			public Particle createParticle(GeyserParticleOptions options, ClientLevel level, double x, double y,
					double z, double dx, double dy, double dz) {
				double randomX = x + (level.random.nextFloat() - 0.5F) * 0.2F;
				double randomY = y + level.random.nextFloat();
				double randomZ = z + (level.random.nextFloat() - 0.5F) * 0.2F;
				// the surface burst accompanies every plume emission
				for (int i = 0; i < 6; i++) {
					level.addParticle(com.natsu.backport.common.registry.CTBParticles.GEYSER_BASE.get(),
							x + (level.random.nextFloat() - 0.5) * 0.5, y + 0.2,
							z + (level.random.nextFloat() - 0.5) * 0.5,
							0.0, 0.05 + 0.25 * options.waterBlocks(), 0.0);
				}
				return new Plume(level, randomX, randomY, randomZ, options.waterBlocks(), this.sprites);
			}
		}
	}

	/** The steam mushroom at the surface. */
	public static class Base extends TextureSheetParticle {

		Base(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
			super(level, x, y, z);
			this.friction = 0.725F;
			this.setColor(1.0F, 1.0F, 1.0F);
			this.xd = (this.random.nextFloat() - 0.5F) * 0.12;
			this.yd = Math.abs(dy);
			this.zd = (this.random.nextFloat() - 0.5F) * 0.12;
			this.quadSize *= 3.0F;
			this.lifetime = (int) (25.0F * (0.8F + 0.2F * level.random.nextFloat()));
			this.setSpriteFromAge(sprites);
			this.spriteSet = sprites;
		}

		private final SpriteSet spriteSet;

		@Override
		public void tick() {
			super.tick();
			this.setSpriteFromAge(this.spriteSet);
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
			public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
					double dx, double dy, double dz) {
				return new Base(level, x, y, z, dx, dy, dz, this.sprites);
			}
		}
	}

	/** A small bubble wobbling up the water column. */
	public static class SulfurBubble extends TextureSheetParticle {

		private final double yStart;
		private final double yEnd;
		private final float sizeStart;
		private double yPrev;

		SulfurBubble(ClientLevel level, double x, double y, double z, double dx, double dz) {
			super(level, x, y, z);
			this.gravity = -0.04F;
			this.friction = 0.85F;
			this.setSize(0.02F, 0.02F);
			this.xd = dx * 0.2F + (this.random.nextFloat() * 2.0F - 1.0F) * 0.02F;
			this.zd = dz * 0.2F + (this.random.nextFloat() * 2.0F - 1.0F) * 0.02F;
			this.quadSize = this.sizeStart = 0.02F + 0.02F * this.random.nextFloat();
			this.lifetime = Integer.MAX_VALUE;
			this.yStart = this.yo;
			this.yEnd = this.yo + 3.0;
			this.yPrev = y;
		}

		@Override
		public void tick() {
			super.tick();
			if (!this.removed && this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).getType() != Fluids.WATER) {
				this.remove();
			}
			if (!this.removed && (this.y >= this.yEnd || this.y <= this.yPrev)) {
				this.remove();
			}
			this.xd += this.wiggle();
			this.zd += this.wiggle();
			this.move(this.xd, 0.0, this.zd);
			float progress = (float) ((this.y - this.yStart) / (this.yEnd - this.yStart));
			this.quadSize = this.sizeStart + progress * (0.15F - this.sizeStart);
			this.yPrev = this.y;
		}

		private double wiggle() {
			return this.random.nextFloat() * 0.003F * (this.random.nextBoolean() ? 1 : -1) * 0.5;
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
				SulfurBubble bubble = new SulfurBubble(level, x, y, z, dx, dz);
				bubble.pickSprite(this.sprites);
				return bubble;
			}
		}
	}

	/** The slow green cloud above a wet potent sulfur block. */
	public static class NoxiousGas extends TextureSheetParticle {

		private final SpriteSet sprites;

		NoxiousGas(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
			super(level, x, y, z);
			this.friction = 0.96F;
			this.yd = 0.008 + level.random.nextFloat() * 0.01;
			this.xd = (level.random.nextFloat() - 0.5F) * 0.008;
			this.zd = (level.random.nextFloat() - 0.5F) * 0.008;
			this.quadSize *= 2.5F;
			this.lifetime = 60 + level.random.nextInt(40);
			this.setAlpha(0.65F);
			this.sprites = sprites;
			this.setSpriteFromAge(sprites);
		}

		@Override
		public void tick() {
			super.tick();
			this.setSpriteFromAge(this.sprites);
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
			public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
					double dx, double dy, double dz) {
				return new NoxiousGas(level, x, y, z, this.sprites);
			}
		}
	}
}
