package com.natsu.backport.common.entity.goal;

import java.util.EnumSet;

import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class BreezeSlideGoal extends Goal {

	public static final float SPEED_MULTIPLIER = 0.6F;
	private static final double SLIDE_INNER_RADIUS = 4.0D;
	private static final double SLIDE_OUTER_RADIUS = 24.0D;
	private static final int RECOMPUTE_PATH_EVERY = 10;
	private static final int POST_SLIDE_SHOOT_DELAY = 60;

    private final Breeze breeze;
    private final double speedModifier;
    private int recomputeTicks = 0;

    public BreezeSlideGoal(Breeze breeze, double speedModifier) {
        this.breeze = breeze;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = breeze.getTarget();
        if (target == null || !target.isAlive() || !breeze.isOnGround() || breeze.isInWaterOrBubble()) {
			return false;
		}

        double dist = breeze.distanceTo(target);
        return dist > SLIDE_INNER_RADIUS && dist < SLIDE_OUTER_RADIUS;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = breeze.getTarget();
        if (target == null || !target.isAlive()) {
			return false;
		}

        double dist = breeze.distanceTo(target);
        return dist > SLIDE_INNER_RADIUS - 0.5D
            && dist < SLIDE_OUTER_RADIUS + 0.5D
            && !breeze.isInWaterOrBubble();
    }

    @Override
    public void start() {
        breeze.setBreezeState(Breeze.STATE_SLIDING);
        breeze.playSound(CTBSounds.BREEZE_SLIDE.get(), 1.0F, 1.0F);
        recomputeTicks = 0;
    }

    @Override
    public void stop() {
        breeze.setBreezeState(Breeze.STATE_IDLE);
        breeze.getNavigation().stop();
        if (breeze.getTarget() != null) {
            breeze.setShootCooldown(Math.max(breeze.getShootCooldown(), POST_SLIDE_SHOOT_DELAY));
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = breeze.getTarget();
        if (target == null) {
			return;
		}

        breeze.getLookControl().setLookAt(target, 30F, 30F);

        if (--recomputeTicks <= 0) {
            recomputeTicks = RECOMPUTE_PATH_EVERY;
            breeze.getNavigation().moveTo(target, speedModifier * SPEED_MULTIPLIER);
        }
    }
}