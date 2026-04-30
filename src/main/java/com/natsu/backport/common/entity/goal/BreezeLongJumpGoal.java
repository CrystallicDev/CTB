package com.natsu.backport.common.entity.goal;

import java.util.EnumSet;

import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class BreezeLongJumpGoal extends Goal {

    private static final int COOLDOWN_TICKS = 100;
    private static final int MAX_JUMP_DURATION = 40;
    private static final double JUMP_INNER_RADIUS = 4.0D;
    private static final double JUMP_OUTER_RADIUS = 24.0D;

    private final Breeze breeze;
    private int jumpTicks = 0;
    private Vec3 jumpTarget = null;

    public BreezeLongJumpGoal(Breeze breeze) {
        this.breeze = breeze;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if ((breeze.getJumpCooldown() > 0) || !breeze.isOnGround() || breeze.isInWaterOrBubble()) {
			return false;
		}
        LivingEntity target = breeze.getTarget();
        if (target == null) {
			return false;
		}

        double dist = breeze.distanceTo(target);
        return dist < JUMP_INNER_RADIUS || dist > JUMP_OUTER_RADIUS * 0.6D;
    }

    @Override
    public boolean canContinueToUse() {
        return jumpTicks < MAX_JUMP_DURATION && !breeze.isOnGround()
            || (jumpTicks < 5);
    }

    @Override
    public void start() {
        jumpTicks = 0;
        breeze.setBreezeState(Breeze.STATE_LONG_JUMPING);

        LivingEntity target = breeze.getTarget();
        if (target != null) {
            jumpTarget = computeJumpTarget(target);
            performJump(jumpTarget);
        }
        breeze.playSound(CTBSounds.BREEZE_JUMP.get(), 1.0F, 1.0F);
    }

    @Override
    public void stop() {
        breeze.setBreezeState(Breeze.STATE_IDLE);
        breeze.setJumpCooldown(COOLDOWN_TICKS);
        breeze.resetJumpTrail();
        jumpTarget = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        jumpTicks++;
    }

    private Vec3 computeJumpTarget(LivingEntity target) {
        Vec3 toBreeze = breeze.position().subtract(target.position()).normalize();
        double angle = (breeze.getRandom().nextDouble() - 0.5D) * Math.PI;
        double cos = Math.cos(angle), sin = Math.sin(angle);
        double rx = toBreeze.x * cos - toBreeze.z * sin;
        double rz = toBreeze.x * sin + toBreeze.z * cos;
        return target.position().add(rx * 8.0D, 0, rz * 8.0D);
    }

    private void performJump(Vec3 dest) {
        Vec3 from = breeze.position();
        double dx = dest.x - from.x;
        double dz = dest.z - from.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        if (horiz < 0.001D) {
			return;
		}
        double vx = dx / horiz * 0.9D;
        double vz = dz / horiz * 0.9D;
        double vy = 0.7D + Math.min(horiz, 10.0D) * 0.04D;

        breeze.setDeltaMovement(vx, vy, vz);
        breeze.hasImpulse = true;
    }
}