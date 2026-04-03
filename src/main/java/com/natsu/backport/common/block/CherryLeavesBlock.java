package com.natsu.backport.common.block;

import java.util.Random;

import com.mojang.serialization.MapCodec;
import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CherryLeavesBlock extends LeavesBlock {

    public CherryLeavesBlock(BlockBehaviour.Properties p_273704_) {
        super(p_273704_);
    }

    @Override
    public void animateTick(BlockState p_272714_, Level p_272837_, BlockPos p_273218_, Random p_273360_) {
        super.animateTick(p_272714_, p_272837_, p_273218_, p_273360_);
        if (p_273360_.nextInt(10) == 0) {
            BlockPos blockpos = p_273218_.below();
            BlockState blockstate = p_272837_.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(p_272837_, blockpos), Direction.UP)) {
                spawnParticleBelow(p_272837_, p_273218_, p_273360_, CTBParticles.CHERRY.get());
            }
        }
    }
    
    public static void spawnParticleBelow(Level p_273159_, BlockPos p_273452_, Random p_273538_, ParticleOptions p_273419_) {
        double d0 = (double)p_273452_.getX() + p_273538_.nextDouble();
        double d1 = (double)p_273452_.getY() - 0.05;
        double d2 = (double)p_273452_.getZ() + p_273538_.nextDouble();
        p_273159_.addParticle(p_273419_, d0, d1, d2, 0.0, 0.0, 0.0);
    }
}