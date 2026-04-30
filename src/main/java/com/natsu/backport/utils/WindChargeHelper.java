package com.natsu.backport.utils;

import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindChargeHelper {

    public static void explodeWind(Level level, Vec3 pos) {
        if (!level.isClientSide) {
            AABB area = new AABB(pos, pos).inflate(3.0);
            for (Entity e : level.getEntities(null, area)) {
                Vec3 dir = e.position().subtract(pos).normalize();
                e.push(dir.x * 2.0, dir.y * 1.5 + 0.5, dir.z * 2.0);
            }
            level.playSound(null, pos.x, pos.y, pos.z,
                CTBSounds.BREEZE_WIND_BURST.get(),
                SoundSource.NEUTRAL, 1.0f, 1.0f);
        }

        if (level.isClientSide) {
            level.addParticle(CTBParticles.GUST_EMITTER_LARGE.get(),
                pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }
}