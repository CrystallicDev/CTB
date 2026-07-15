package com.natsu.backport.utils;

import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindChargeHelper {

    // knockback burst like a wind charge explosion, no block or entity damage
    public static void explodeWind(ServerLevel level, Vec3 pos, float radius) {
        AABB area = new AABB(pos, pos).inflate(radius);
        for (Entity e : level.getEntities(null, area)) {
            Vec3 dir = e.position().subtract(pos).normalize();
            e.push(dir.x * 2.0, dir.y * 1.5 + 0.5, dir.z * 2.0);
            e.hurtMarked = true;
        }

        level.sendParticles(CTBParticles.GUST_EMITTER_LARGE.get(),
            pos.x, pos.y, pos.z, 30, 0.5, 0.5, 0.5, 0.3);
        level.playSound(null, pos.x, pos.y, pos.z,
            CTBSounds.BREEZE_WIND_BURST.get(),
            SoundSource.NEUTRAL, 1.0f, 1.0f);
    }
}
