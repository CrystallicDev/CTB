package com.natsu.backport.common.entity;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.CreakingHeartBlock;
import com.natsu.backport.common.block.entity.CreakingHeartBlockEntity;
import com.natsu.backport.common.block.state.CreakingHeartState;
import com.natsu.backport.common.entity.goal.CreakingActiveTargetGoal;
import com.natsu.backport.common.entity.goal.CreakingHomeStrollGoal;
import com.natsu.backport.common.entity.goal.CreakingMeleeAttackGoal;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class Creaking extends Monster implements IAnimatable {

    private static final EntityDataAccessor<Boolean> CAN_MOVE =
        SynchedEntityData.defineId(Creaking.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ACTIVE =
        SynchedEntityData.defineId(Creaking.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_TEARING_DOWN =
        SynchedEntityData.defineId(Creaking.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> HOME_POS =
        SynchedEntityData.defineId(Creaking.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

	public static final int ATTACK_INTERVAL = 40;
	// 5 ticks of geckolib transition + the 0.5s animation, like vanilla's 15
	private static final int ATTACK_ANIM_DURATION = 15;
	private static final int INVULN_ANIM_DURATION = 8;
	private static final int TEAR_DOWN_DURATION = 45;
	private static final int MAX_PLAYER_STUCK_COUNTER = 4;
	private static final float ACTIVATION_RANGE_SQ = 144.0F; 
	private static final double LOOK_ANGLE_TOLERANCE = 0.5D;

    private static final String TAG_HOME_POS_X = "CreakingHomePosX";
    private static final String TAG_HOME_POS_Y = "CreakingHomePosY";
    private static final String TAG_HOME_POS_Z = "CreakingHomePosZ";

    private int attackAnimTicks;
    private int invulnerabilityAnimTicks;
    private int playerStuckCounter;
    private int damageTicks;       

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public Creaking(EntityType<? extends Creaking> type, Level level) {
        super(type, level);
        this.lookControl = new CreakingLookControl(this);
        this.moveControl = new CreakingMoveControl(this);
        this.jumpControl = new CreakingJumpControl(this);
        this.maxUpStep   = 1.0625F;
        this.getNavigation().setCanFloat(true);
        this.xpReward = 0;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new CreakingBodyRotationControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 1.0)
            .add(Attributes.MOVEMENT_SPEED, 0.4F)
            .add(Attributes.ATTACK_DAMAGE, 3.0)
            .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CAN_MOVE, true);
        this.entityData.define(IS_ACTIVE, false);
        this.entityData.define(IS_TEARING_DOWN, false);
        this.entityData.define(HOME_POS, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CreakingMeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(7, new CreakingHomeStrollGoal(this, 0.3D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreakingActiveTargetGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new CreakingPathNavigation(this, level);
    }

    // Link with Creaking Heart Block ENtity
	public void setTransient(BlockPos pos) {
		setHomePos(pos);
		setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 8.0F);
		setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
		setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
		setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
	}

	public boolean isHeartBound() {
		return getHomePos() != null;
	}

	public void setHomePos(BlockPos p) {
		entityData.set(HOME_POS, Optional.of(p));
	}

	@Nullable
	public BlockPos getHomePos() {
		return entityData.get(HOME_POS).orElse(null);
	}


    @Override
    public boolean hurt(DamageSource source, float amount) {
        BlockPos home = getHomePos();
        if (home == null || source.isBypassInvul()) {
			return super.hurt(source, amount);
		}
        if (this.isInvulnerableTo(source) || invulnerabilityAnimTicks > 0 || isDeadOrDying()) {
			return false;
		}

        Entity direct  = source.getDirectEntity();
        Entity blame   = source.getEntity();
        Player blamer  = blame instanceof Player p ? p : null;

        if (!(direct instanceof LivingEntity) && !(direct instanceof Projectile) && blamer == null) {
            return false;
        }

        invulnerabilityAnimTicks = INVULN_ANIM_DURATION;
        damageTicks = 10;
        level.broadcastEntityEvent(this, (byte) 66);

        if (!level.isClientSide
            && level.getBlockEntity(home) instanceof CreakingHeartBlockEntity heart) {
            if (blamer != null) {
				heart.creakingHurt();
			}
            playHurtSound(source);
        }
        return true;
    }

    // the goal starts the anim, the damage comes a few ticks later on the swing
    public void startAttackAnim() {
        attackAnimTicks = ATTACK_ANIM_DURATION;
        level.broadcastEntityEvent(this, (byte) 4);
        makeSound(CTBSounds.CREAKING_ATTACK.get());
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!(target instanceof LivingEntity)) {
			return false;
		}
        if (attackAnimTicks <= 0) {
            startAttackAnim();
        }
        return super.doHurtTarget(target);
    }





    @Override
    public void aiStep() {
        if (invulnerabilityAnimTicks > 0) {
			invulnerabilityAnimTicks--;
		}
        if (attackAnimTicks > 0) {
			attackAnimTicks--;
		}
        if (damageTicks > 0) {
			damageTicks--;
		}

        if (!level.isClientSide) {
            boolean wasMoving = entityData.get(CAN_MOVE);
            boolean canMoveNow = checkCanMove();

            if (canMoveNow != wasMoving) {
                if (canMoveNow) {
                    makeSound(CTBSounds.CREAKING_UNFREEZE.get());
                } else {
                    stopInPlace();
                    makeSound(CTBSounds.CREAKING_FREEZE.get());
                }
                entityData.set(CAN_MOVE, canMoveNow);
            }
        }
        super.aiStep();
    }

    @Override
    public void tick() {
        // die if the heart is gone, but never judge an unloaded chunk
        if (!level.isClientSide) {
            BlockPos home = getHomePos();
            if (home != null && level.isLoaded(home)) {
                boolean valid = level.getBlockEntity(home) instanceof CreakingHeartBlockEntity h
                             && h.isProtector(this);
                if (!valid) {
                    setHealth(0.0F);
                }
            }
        }
        super.tick();
    }

    @Override
    protected void tickDeath() {
        if (isHeartBound() && isTearingDown()) {
            this.deathTime++;
            if (!level.isClientSide && this.deathTime > TEAR_DOWN_DURATION && !isRemoved()) {
                tearDown();
            }
        } else {
            super.tickDeath();
        }
    }



    // annoying because we dont have a "Brain", but goals in this version
    public boolean checkCanMove() {
        List<Player> nearby = level.getEntitiesOfClass(Player.class,
            getBoundingBox().inflate(32.0D), p -> !p.isSpectator());

        boolean active = isActive();

        if (nearby.isEmpty()) {
            if (active) {
				deactivate();
			}
            return true;
        }

        boolean hasPotentialTarget = false;
        for (Player p : nearby) {
            if (canAttack(p) && !isAlliedTo(p)) {
                hasPotentialTarget = true;
                if (isLookingAtMe(p)) {
                    if (active) {
						return false;
					}
                    if (p.distanceToSqr(this) < ACTIVATION_RANGE_SQ) {
                        activate(p);
                        return false;
                    }
                }
            }
        }
        if (!hasPotentialTarget && active) {
			deactivate();
		}
        return true;
    }

    private boolean isLookingAtMe(Player p) {
        Vec3 view = p.getViewVector(1.0F).normalize();
        double[] yLevels = { getEyeY(), getY() + 0.5D * getScale(), (getEyeY() + getY()) / 2.0D };
        for (double y : yLevels) {
            Vec3 toMe = new Vec3(getX() - p.getX(), y - p.getEyeY(), getZ() - p.getZ());
            double dot  = view.dot(toMe.normalize());
            // vanilla uses a fixed cone, not narrowed by distance
            if (dot > 1.0D - LOOK_ANGLE_TOLERANCE && p.hasLineOfSight(this)) {
                return true;
            }
        }
        return false;
    }

    public void activate(Player target) {
        setTarget(target);
        makeSound(CTBSounds.CREAKING_ACTIVATE.get());
        setIsActive(true);
    }

    public void deactivate() {
        setTarget(null);
        makeSound(CTBSounds.CREAKING_DEACTIVATE.get());
        setIsActive(false);
    }

    public boolean playerIsStuckInYou() {
        AABB box = getBoundingBox();
        for (Player p : level.getEntitiesOfClass(Player.class, box.inflate(0.5D))) {
            if (box.contains(p.getEyePosition())) {
                playerStuckCounter++;
                return playerStuckCounter > MAX_PLAYER_STUCK_COUNTER;
            }
        }
        playerStuckCounter = 0;
        return false;
    }

    public void creakingDeathEffects(DamageSource src) {
        die(src);
        makeSound(CTBSounds.CREAKING_TWITCH.get());
    }

    public void tearDown() {
        if (level instanceof ServerLevel sl) {
            AABB box = getBoundingBox();
            Vec3 c   = box.getCenter();
            double dx = box.getXsize() * 0.3D, dy = box.getYsize() * 0.3D, dz = box.getZsize() * 0.3D;
            sl.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, CTBBlocks.PALE_OAK_WOOD.wood.get().defaultBlockState()),
                c.x, c.y, c.z, 100, dx, dy, dz, 0.0D);
            sl.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, CTBBlocks.CREAKING_HEART.get().defaultBlockState()
                    .setValue(CreakingHeartBlock.STATE, CreakingHeartState.AWAKE)),
                c.x, c.y, c.z, 10, dx, dy, dz, 0.0D);
        }
        makeSound(getDeathSound());
        remove(RemovalReason.DISCARDED);
    }

	@Override
	protected SoundEvent getAmbientSound() {
		return isActive() ? null : CTBSounds.CREAKING_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource src) {
		return isHeartBound() ? CTBSounds.CREAKING_SWAY.get() : super.getHurtSound(src);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return CTBSounds.CREAKING_DEATH.get();
	}

	@Override
	protected void playStepSound(BlockPos p, BlockState s) {
		playSound(CTBSounds.CREAKING_STEP.get(), 0.15F, 1.0F);
	}

    // Mob#stopInPlace only exists in newer versions, without it the last
    // movement inputs stick and the frozen creaking slides along the ground
    public void stopInPlace() {
        getNavigation().stop();
        setXxa(0.0F);
        setYya(0.0F);
        setZza(0.0F);
    }

    public void makeSound(@Nullable SoundEvent ev) {
        if (ev != null) {
			level.playSound(null, blockPosition(), ev, SoundSource.HOSTILE, 1.0F, 1.0F);
		}
    }

    @Override public boolean fireImmune() { return isHeartBound() || super.fireImmune(); }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 66) { invulnerabilityAnimTicks = INVULN_ANIM_DURATION; damageTicks = 10; }
        else if (id == 4) { attackAnimTicks = ATTACK_ANIM_DURATION; } else {
			super.handleEntityEvent(id);
		}
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        BlockPos h = getHomePos();
        if (h != null) {
            tag.putInt(TAG_HOME_POS_X, h.getX());
            tag.putInt(TAG_HOME_POS_Y, h.getY());
            tag.putInt(TAG_HOME_POS_Z, h.getZ());
        }
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_HOME_POS_X)) {
            setTransient(new BlockPos(
                tag.getInt(TAG_HOME_POS_X),
                tag.getInt(TAG_HOME_POS_Y),
                tag.getInt(TAG_HOME_POS_Z)));
        }
    }

    @Override public boolean isPushable() { return super.isPushable() && canMove(); }
    @Override public void push(double x, double y, double z) { if (canMove()) {
		super.push(x, y, z);
	} }
    @Override public void knockback(double s, double x, double z) { if (canMove()) {
		super.knockback(s, x, z);
	} }
    public boolean canMove() { return entityData.get(CAN_MOVE); }
    public boolean isActive() { return entityData.get(IS_ACTIVE); }
    public void setIsActive(boolean v) { entityData.set(IS_ACTIVE, v); }
    public boolean isTearingDown() { return entityData.get(IS_TEARING_DOWN); }
    public void setTearingDown() { entityData.set(IS_TEARING_DOWN, true); }




    // GeckoLib
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (isTearingDown()) {
            event.getController().setAnimation(new AnimationBuilder()
                .addAnimation("death", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (damageTicks > 0) {
            event.getController().setAnimation(new AnimationBuilder()
                .addAnimation("damage.block", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        if (attackAnimTicks > 0) {
            event.getController().setAnimation(new AnimationBuilder()
                .addAnimation("attack.melee", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        // the creaking strolls slowly, isMoving() misses it (0.15 threshold), and
        // without hysteresis the walk keeps restarting and never completes a cycle
        boolean walking = event.getController().getCurrentAnimation() != null
            && "moove.walk".equals(event.getController().getCurrentAnimation().animationName);
        if (canMove() && Math.abs(event.getLimbSwingAmount()) > (walking ? 0.004F : 0.035F)) {
            event.getController().setAnimation(new AnimationBuilder()
                .addAnimation("moove.walk", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        // never STOP the controller, it snaps to the default pose without blending
        event.getController().setAnimation(new AnimationBuilder()
            .addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override public AnimationFactory getFactory() { return factory; }









    static class CreakingLookControl extends LookControl {
        private final Creaking c;
        CreakingLookControl(Creaking c) { super(c); this.c = c; }
        @Override public void tick() { if (c.canMove()) {
			super.tick();
		} }
    }
    static class CreakingMoveControl extends MoveControl {
        private final Creaking c;
        CreakingMoveControl(Creaking c) { super(c); this.c = c; }

        @Override
        public void tick() {
            if (c.canMove()) {
                super.tick();
            }
        }
    }

    static class CreakingBodyRotationControl extends BodyRotationControl {
        private final Creaking c;
        CreakingBodyRotationControl(Creaking c) { super(c); this.c = c; }

        @Override
        public void clientTick() {
            if (c.canMove()) {
                super.clientTick();
            }
        }
    }
    static class CreakingJumpControl extends JumpControl {
        private final Creaking c;
        CreakingJumpControl(Creaking c) { super(c); this.c = c; }
        @Override public void tick() { if (c.canMove()) {
			super.tick();
		} else {
			c.setJumping(false);
		} }
    }

    static class CreakingPathNavigation extends GroundPathNavigation {
        CreakingPathNavigation(Creaking c, Level l) { 
            super(c, l); 
        }
        
        @Override 
        public void tick() { 
            if (((Creaking) this.mob).canMove()) super.tick(); 
        }
        
        @Override
        protected PathFinder createPathFinder(int max) {
            this.nodeEvaluator = new HomeNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, max);
        }
    }

    static class HomeNodeEvaluator extends WalkNodeEvaluator {
        private static final int MAX_DIST_SQ = 1024;
        
        @Override
        public BlockPathTypes getBlockPathType(BlockGetter lvl, int x, int y, int z) {
            if (this.mob instanceof Creaking c) {       
                BlockPos home = c.getHomePos();
                if (home != null) {
                    double here = home.distSqr(new Vec3i(x, y, z));
                    double mobDist = home.distSqr(this.mob.blockPosition());
                    if (here > MAX_DIST_SQ && here >= mobDist) {
                        return BlockPathTypes.BLOCKED;
                    }
                }
            }
            return super.getBlockPathType(lvl, x, y, z);
        }
    }
}
