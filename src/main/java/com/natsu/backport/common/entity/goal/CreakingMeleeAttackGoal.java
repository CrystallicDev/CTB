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
    private int hitTicks;
    private int cooldown;

    public CreakingMeleeAttackGoal(Creaking c, double speed, boolean longMemory) {
        super(c, speed, longMemory);
        this.creaking = c;
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
    }

    // start the anim first, deal the damage when the swing lands
    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distSqr) {
        if (hitTicks > 0) {
            hitTicks--;
            if (hitTicks == 0) {
                if (creaking.canMove() && withinAttackRange(target)) {
                    this.mob.doHurtTarget(target);
                } else {
                    // dodged (stared at or out of reach), retry quickly
                    cooldown = Math.min(cooldown, this.adjustedTickDelay(5));
                }
            }
        } else if (creaking.canMove() && cooldown <= 0 && withinAttackRange(target)) {
            cooldown = this.adjustedTickDelay(Creaking.ATTACK_INTERVAL);
            hitTicks = HIT_DELAY;
            creaking.startAttackAnim();
        }
    }

    // vanilla checks box against box, way more generous than the goal's center distance
    private boolean withinAttackRange(LivingEntity target) {
        return this.mob.getBoundingBox().inflate(ATTACK_REACH, 0.0D, ATTACK_REACH)
            .intersects(target.getBoundingBox());
    }
}
