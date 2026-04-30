package com.natsu.backport.common.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CherryParticle extends TextureSheetParticle {

	private static final float ACCELERATION_SCALE = 0.0025F;
    private static final int INITIAL_LIFETIME = 300;
    private static final int CURVE_ENDPOINT_TIME = 299;
    private float rotSpeed;
    private final float particleRandom;
    private final float spinAcceleration;

    protected CherryParticle(ClientLevel p_277612_, double p_278010_, double p_277614_, double p_277673_, SpriteSet p_277465_) {
        super(p_277612_, p_278010_, p_277614_, p_277673_);
        this.setSprite(p_277465_.get(this.random.nextInt(12), 12));
        this.rotSpeed = (float)Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.particleRandom = this.random.nextFloat();
        this.spinAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
        this.lifetime = INITIAL_LIFETIME;
        this.gravity = 7.5E-4F;
        float f = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.quadSize = f;
        this.setSize(f, f);
        this.friction = 1.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        }

        if (!this.removed) {
            float f = 300 - this.lifetime;
            float f1 = Math.min(f / 300.0F, 1.0F);
            double d0 = Math.cos(Math.toRadians(this.particleRandom * 60.0F)) * 2.0 * Math.pow(f1, 1.25);
            double d1 = Math.sin(Math.toRadians(this.particleRandom * 60.0F)) * 2.0 * Math.pow(f1, 1.25);
            this.xd += d0 * ACCELERATION_SCALE;
            this.zd += d1 * ACCELERATION_SCALE;
            this.yd = this.yd - this.gravity;
            this.rotSpeed = this.rotSpeed + this.spinAcceleration / 20.0F;
            this.oRoll = this.roll;
            this.roll = this.roll + this.rotSpeed / 20.0F;
            this.move(this.xd, this.yd, this.zd);
            if (this.onGround || this.lifetime < CURVE_ENDPOINT_TIME && (this.xd == 0.0 || this.zd == 0.0)) {
                this.remove();
            }

            if (!this.removed) {
                this.xd = this.xd * this.friction;
                this.yd = this.yd * this.friction;
                this.zd = this.zd * this.friction;
            }
        }
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
			CherryParticle p = new CherryParticle(lvl, x, y, z, spriteSet);
			return p;
		}

	}

}
