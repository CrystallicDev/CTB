package com.natsu.backport.common.entity.goal;

import javax.annotation.Nullable;

import com.natsu.backport.common.entity.Creaking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.phys.Vec3;

public class CreakingHomeStrollGoal extends WaterAvoidingRandomStrollGoal {
    private static final int MAX_DIST_SQ = 1024;
    private final Creaking creaking;

    public CreakingHomeStrollGoal(Creaking c, double speed) {
        super(c, speed);
        this.creaking = c;
    }

    @Override
    public boolean canUse() { return creaking.canMove() && super.canUse(); }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        Vec3 candidate = super.getPosition();
        BlockPos home  = creaking.getHomePos();
        if (candidate == null || home == null) return candidate;
        return home.distToCenterSqr(candidate) > MAX_DIST_SQ ? null : candidate;
    }
}