package com.natsu.backport.common.entity.goal;

import com.natsu.backport.common.entity.Creaking;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CreakingMeleeAttackGoal extends MeleeAttackGoal {

    // the arm comes down at ~0.375s in attack.melee
    private static final int HIT_DELAY = 7;

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
            // staring at the creaking mid swing cancels the hit
            if (hitTicks == 0 && creaking.canMove() && distSqr <= this.getAttackReachSqr(target)) {
                this.mob.doHurtTarget(target);
            }
        } else if (creaking.canMove() && cooldown <= 0 && distSqr <= this.getAttackReachSqr(target)) {
            cooldown = this.adjustedTickDelay(Creaking.ATTACK_INTERVAL);
            hitTicks = HIT_DELAY;
            creaking.startAttackAnim();
        }
    }
}
