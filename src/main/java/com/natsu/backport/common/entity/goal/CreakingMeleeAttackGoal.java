package com.natsu.backport.common.entity.goal;

import com.natsu.backport.common.entity.Creaking;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CreakingMeleeAttackGoal extends MeleeAttackGoal {

    // 5 ticks of anim transition, then the arm comes down at ~0.375s
    private static final int HIT_DELAY = 12;
    // vanilla Mob#DEFAULT_ATTACK_REACH
    private static final double ATTACK_REACH = Math.sqrt(2.04D) - 0.6D;

    private final Creaking creaking;
    private final double speed;
    private int hitTicks;
    private int cooldown;

    public CreakingMeleeAttackGoal(Creaking c, double speed, boolean longMemory) {
        super(c, speed, longMemory);
        this.creaking = c;
        this.speed = speed;
    }

    // the goal keeps running while frozen, only the locomotion and the
    // attack itself are blocked (that's how the vanilla brain does it)
    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        return super.canUse();
    }

    @Override
    public void stop() {
        super.stop();
        hitTicks = 0;
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
        }
        super.tick();

        // the parent goal paths with a block of slack and gives up just out of
        // reach, walk right into the target like the vanilla brain does
        LivingEntity target = this.mob.getTarget();
        if (target != null && !withinAttackRange(target, 0.0D) && this.mob.getNavigation().isDone()) {
            this.mob.getNavigation().moveTo(this.mob.getNavigation().createPath(target, 0), speed);
        }
    }

    // start the anim first, deal the damage when the swing lands
    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distSqr) {
        if (hitTicks > 0) {
            hitTicks--;
            if (hitTicks == 0) {
                // extra reach on the swing, backpedaling should not trivialize it
                if (creaking.canMove() && withinAttackRange(target, 1.0D)) {
                    this.mob.doHurtTarget(target);
                } else {
                    // dodged (stared at or out of reach), retry quickly
                    cooldown = Math.min(cooldown, this.adjustedTickDelay(5));
                }
            }
        } else if (creaking.canMove() && cooldown <= 0 && withinAttackRange(target, 0.0D)) {
            cooldown = this.adjustedTickDelay(Creaking.ATTACK_INTERVAL);
            hitTicks = HIT_DELAY;
            creaking.startAttackAnim();
        }
    }

    // vanilla checks box against box, way more generous than the goal's center distance
    private boolean withinAttackRange(LivingEntity target, double extra) {
        return this.mob.getBoundingBox().inflate(ATTACK_REACH + extra, extra, ATTACK_REACH + extra)
            .intersects(target.getBoundingBox());
    }
}
