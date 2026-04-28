package com.natsu.backport.common.entity.goal;

import java.util.EnumSet;

import com.natsu.backport.common.entity.Breeze;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class BreezeShootWhenStuckGoal extends Goal {

    private static final int STUCK_THRESHOLD_TICKS = 60;
    private static final int SHOOT_DURATION = 30;

    private final Breeze breeze;
    private int stuckTicks = 0;
    private int activeTicks = 0;

    public BreezeShootWhenStuckGoal(Breeze breeze) {
        this.breeze = breeze;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = breeze.getTarget();
        if (target == null) return false;
        if (breeze.getShootCooldown() > 0) return false;
        if (breeze.getNavigation().isDone() && !breeze.hasLineOfSight(target)) {
            stuckTicks++;
        } else {
            stuckTicks = Math.max(0, stuckTicks - 1);
        }
        return stuckTicks >= STUCK_THRESHOLD_TICKS;
    }

    @Override
    public boolean canContinueToUse() {
        return activeTicks < SHOOT_DURATION && breeze.getTarget() != null;
    }

    @Override
    public void start() {
        activeTicks = 0;
        stuckTicks = 0;
        breeze.setBreezeState(Breeze.STATE_INHALING);
    }

    @Override
    public void stop() {
        breeze.setBreezeState(Breeze.STATE_IDLE);
        breeze.setShootCooldown(40);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = breeze.getTarget();
        if (target == null) return;
        breeze.getLookControl().setLookAt(target, 30F, 30F);
        activeTicks++;

        if (activeTicks == 15) {
            breeze.setBreezeState(Breeze.STATE_SHOOTING);
            breeze.shootWindChargeAtGround(target);
        }
    }
}