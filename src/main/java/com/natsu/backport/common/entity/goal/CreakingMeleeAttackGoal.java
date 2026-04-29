package com.natsu.backport.common.entity.goal;

import com.natsu.backport.common.entity.Creaking;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class CreakingMeleeAttackGoal extends MeleeAttackGoal {
    private final Creaking creaking;

    public CreakingMeleeAttackGoal(Creaking c, double speed, boolean longMemory) {
        super(c, speed, longMemory);
        this.creaking = c;
    }

    @Override public boolean canUse() { return creaking.canMove() && super.canUse(); }
    @Override public boolean canContinueToUse() { return creaking.canMove() && super.canContinueToUse(); }

    @Override
    protected int getAttackInterval() { return Creaking.ATTACK_INTERVAL; }
}