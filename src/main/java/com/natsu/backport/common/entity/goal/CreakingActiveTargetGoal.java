package com.natsu.backport.common.entity.goal;

import java.util.EnumSet;

import com.natsu.backport.common.entity.Creaking;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

public class CreakingActiveTargetGoal extends TargetGoal {
    private final Creaking creaking;
    private LivingEntity target;

    public CreakingActiveTargetGoal(Creaking c) {
        super(c, false, false);
        this.creaking = c;
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!creaking.isActive()) {
			return false;
		}
        target = creaking.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        creaking.setTarget(target);
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity t = creaking.getTarget();
        return creaking.isActive() && t != null && t.isAlive()
            && creaking.canAttack(t);
    }
}