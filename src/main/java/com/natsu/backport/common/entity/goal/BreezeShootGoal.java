package com.natsu.backport.common.entity.goal;

import java.util.EnumSet;

import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.WindChargeEntity;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class BreezeShootGoal extends Goal {

    private static final int INHALE_DURATION = 15;
    private static final int SHOOT_RECOVERY = 15;
    private static final int COOLDOWN_TICKS = 50;
    private static final double MAX_SHOOT_RANGE_SQR = 24.0D * 24.0D;
    private static final double MIN_SHOOT_RANGE_SQR = 4.0D * 4.0D;

    private final Breeze breeze;
    private int phaseTicks = 0;
    private boolean hasFired = false;

    public BreezeShootGoal(Breeze breeze) {
        this.breeze = breeze;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (breeze.getShootCooldown() > 0) {
			return false;
		}
        LivingEntity target = breeze.getTarget();
        if (target == null || !target.isAlive() || !breeze.isOnGround()) {
			return false;
		}
        double distSqr = breeze.distanceToSqr(target);
        if (distSqr < MIN_SHOOT_RANGE_SQR || distSqr > MAX_SHOOT_RANGE_SQR) {
			return false;
		}
        return breeze.hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = breeze.getTarget();
        return target != null && target.isAlive()
            && phaseTicks < INHALE_DURATION + SHOOT_RECOVERY;
    }

    @Override
    public void start() {
        phaseTicks = 0;
        hasFired = false;
        breeze.setBreezeState(Breeze.STATE_INHALING);
    }

    @Override
    public void stop() {
        breeze.setBreezeState(Breeze.STATE_IDLE);
        breeze.setShootCooldown(COOLDOWN_TICKS);
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

        phaseTicks++;
        if (phaseTicks == INHALE_DURATION) {
            breeze.setBreezeState(Breeze.STATE_SHOOTING);
            shootWindCharge(target);
            hasFired = true;
        }
    }

    private void shootWindCharge(LivingEntity target) {
        if (breeze.level.isClientSide) {
			return;
		}
        double startX = breeze.getX();
        double startY = breeze.getFiringYPosition();
        double startZ = breeze.getZ();
        double dx = target.getX() - startX;
        double dy = target.getY(0.5D) - startY;
        double dz = target.getZ() - startZ;

        WindChargeEntity windCharge = new WindChargeEntity(breeze.level, breeze);
        windCharge.setPos(startX, startY, startZ);
        breeze.level.addFreshEntity(windCharge);
        breeze.playSound(CTBSounds.BREEZE_SHOOT.get(), 1.5F, 1.0F);
    }
}