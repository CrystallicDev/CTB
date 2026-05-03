package com.natsu.backport.common.entity;

import javax.annotation.Nullable;

import com.natsu.backport.common.entity.goal.BreezeLongJumpGoal;
import com.natsu.backport.common.entity.goal.BreezeShootGoal;
import com.natsu.backport.common.entity.goal.BreezeShootWhenStuckGoal;
import com.natsu.backport.common.entity.goal.BreezeSlideGoal;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class Breeze extends Monster implements IAnimatable {

    private static final EntityDataAccessor<Integer> DATA_STATE =
        SynchedEntityData.defineId(Breeze.class, EntityDataSerializers.INT);

    public static final int STATE_IDLE = 0;
    public static final int STATE_SHOOTING = 1;
    public static final int STATE_INHALING = 2;
    public static final int STATE_SLIDING = 3;
    public static final int STATE_LONG_JUMPING = 4;

    private static final int SLIDE_PARTICLES_AMOUNT = 20;
    private static final int JUMP_TRAIL_DURATION_TICKS = 5;
    private static final int JUMP_TRAIL_PARTICLES_AMOUNT = 3;
    private static final float FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD = 3.0F;

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private int jumpTrailStartedTick = 0;
    private int soundTick = 0;

    private int shootCooldown = 0;
    private int jumpCooldown = 0;

    public Breeze(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.63F)
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STATE, STATE_IDLE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreezeShootGoal(this));
        this.goalSelector.addGoal(2, new BreezeLongJumpGoal(this));
        this.goalSelector.addGoal(3, new BreezeShootWhenStuckGoal(this));
        this.goalSelector.addGoal(4, new BreezeSlideGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public int getBreezeState() {
        return this.entityData.get(DATA_STATE);
    }

    public void setBreezeState(int state) {
        this.entityData.set(DATA_STATE, state);
    }

	public boolean isShooting() {
		return getBreezeState() == STATE_SHOOTING;
	}

	public boolean isInhaling() {
		return getBreezeState() == STATE_INHALING;
	}

	public boolean isSliding() {
		return getBreezeState() == STATE_SLIDING;
	}

	public boolean isLongJumping() {
		return getBreezeState() == STATE_LONG_JUMPING;
	}

    public int getShootCooldown() { return shootCooldown; }
    public void setShootCooldown(int v) { this.shootCooldown = v; }
    public int getJumpCooldown() { return jumpCooldown; }
    public void setJumpCooldown(int v) { this.jumpCooldown = v; }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide) {
            if (shootCooldown > 0) {
				shootCooldown--;
			}
            if (jumpCooldown > 0) {
				jumpCooldown--;
			}
        }

        int state = getBreezeState();
        switch (state) {
            case STATE_SHOOTING:
            case STATE_INHALING:
            case STATE_IDLE:
                this.resetJumpTrail();
                this.emitGroundParticles(1 + this.getRandom().nextInt(1));
                break;
            case STATE_SLIDING:
                this.emitGroundParticles(SLIDE_PARTICLES_AMOUNT);
                break;
            case STATE_LONG_JUMPING:
                this.emitJumpTrailParticles();
                break;
        }

        this.soundTick = this.soundTick == 0
            ? this.random.nextInt(80) + 1
            : this.soundTick - 1;
        if (this.soundTick == 0) {
            this.playWhirlSound();
        }
    }

    public Breeze resetJumpTrail() {
        this.jumpTrailStartedTick = 0;
        return this;
    }

    public void emitJumpTrailParticles() {
        if (++this.jumpTrailStartedTick <= JUMP_TRAIL_DURATION_TICKS) {
            BlockState blockstate = getRelevantBlockState();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 pos = this.position().add(vec3).add(0.0D, 0.1D, 0.0D);
            for (int i = 0; i < JUMP_TRAIL_PARTICLES_AMOUNT; i++) {
                this.level.addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
                    pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D
                );
            }
        }
    }

    public void emitGroundParticles(int amount) {
        if (this.isPassenger()) {
			return;
		}
        Vec3 center = this.getBoundingBox().getCenter();
        Vec3 pos = new Vec3(center.x, this.position().y, center.z);
        BlockState blockstate = getRelevantBlockState();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < amount; i++) {
                this.level.addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
                    pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D
                );
            }
        }
    }

    private BlockState getRelevantBlockState() {
        BlockPos below = this.blockPosition().below();
        BlockState belowState = this.level.getBlockState(below);
        if (!belowState.isAir()) {
			return belowState;
		}
        BlockState inState = this.level.getBlockState(this.blockPosition());
        return inState;
    }

    public void playWhirlSound() {
        float pitch = 0.7F + 0.4F * this.random.nextFloat();
        float volume = 0.8F + 0.2F * this.random.nextFloat();
        this.level.playLocalSound(
            this.getX(), this.getY(), this.getZ(),
            CTBSounds.BREEZE_WHIRL.get(),
            this.getSoundSource(), volume, pitch, false
        );
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.onGround
            ? CTBSounds.BREEZE_IDLE_GROUND.get()
            : CTBSounds.BREEZE_IDLE_AIR.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CTBSounds.BREEZE_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) {
        return CTBSounds.BREEZE_HURT.get();
    }

    @Override
    public void playAmbientSound() {
        if (this.getTarget() == null || !this.onGround) {
            super.playAmbientSound();
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource src) {
        if (fallDistance > FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD) {
            this.playSound(CTBSounds.BREEZE_LAND.get(), 1.0F, 1.0F);
        }
        return super.causeFallDamage(fallDistance, multiplier, src);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource src) {
        return src.getEntity() instanceof Breeze || super.isInvulnerableTo(src);
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type == EntityType.PLAYER || type == EntityType.IRON_GOLEM;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    public int getHeadRotSpeed() {
        return 25;
    }

    public double getFiringYPosition() {
        return this.getY() + this.getBbHeight() / 2.0F + 0.3F;
    }

    @Override
    public double getFluidJumpThreshold() {
        return this.getEyeHeight();
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    public boolean withinInnerCircleRange(Vec3 target) {
        BlockPos pos = this.blockPosition();
        Vec3 self = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        double dx = target.x - self.x;
        double dz = target.z - self.z;
        double dy = Math.abs(target.y - self.y);
        double horizSqr = dx * dx + dz * dz;
        return horizSqr < 4.0D * 4.0D && dy < 10.0D;
    }

    public void shootWindChargeAtGround(LivingEntity target) {
        if (this.level.isClientSide) {
			return;
		}
        double startX = this.getX();
        double startY = this.getFiringYPosition();
        double startZ = this.getZ();
        double dx = target.getX() - startX;
        double dy = target.getY() - startY;
        double dz = target.getZ() - startZ;
        WindChargeEntity windCharge = new WindChargeEntity(this.level, this);
        windCharge.setPos(startX, startY, startZ);
        windCharge.shoot(dx, dy, dz, 1.6F, 0.0F);
        this.level.addFreshEntity(windCharge);
    }

    public void shootWindCharge(LivingEntity target) {
        if (this.level.isClientSide) {
			return;
		}
        double startX = this.getX();
        double startY = this.getFiringYPosition();
        double startZ = this.getZ();
        double dx = target.getX() - startX;
        double dy = target.getY(0.5D) - startY;
        double dz = target.getZ() - startZ;

        WindChargeEntity windCharge = new WindChargeEntity(this.level, this);
        windCharge.setPos(startX, startY, startZ);
        windCharge.shoot(dx, dy, dz, 1.6F, 0.0F);

        this.level.addFreshEntity(windCharge);
        this.playSound(CTBSounds.BREEZE_SHOOT.get(), 1.5F, 1.0F);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 4;

        switch (getBreezeState()) {
            case STATE_SHOOTING:
                controller.setAnimation(new AnimationBuilder().addAnimation("attack.shoot", false));
                break;
            case STATE_INHALING:
                controller.setAnimation(new AnimationBuilder().addAnimation("moove.inhale", false));
                break;
            case STATE_SLIDING:
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.slide", true));
                break;
            case STATE_LONG_JUMPING:
                controller.setAnimation(new AnimationBuilder().addAnimation("moove.jump", false));
                break;
            case STATE_IDLE:
            default:
                controller.setAnimation(new AnimationBuilder().addAnimation("moove.idle", true));
                break;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 4, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}