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
            double dist = e.position().distanceTo(pos);
            double falloff = Math.max(0.0, 1.0 - dist / radius);
            if (falloff <= 0.0) {
                continue;
            }
            Vec3 dir = e.position().subtract(pos).normalize();
            e.push(dir.x * 1.6 * falloff, dir.y * 1.2 * falloff + 0.4 * falloff, dir.z * 1.6 * falloff);
            e.hurtMarked = true;
        }

        level.sendParticles(CTBParticles.GUST_EMITTER_LARGE.get(),
            pos.x, pos.y, pos.z, 30, 0.5, 0.5, 0.5, 0.3);
        level.playSound(null, pos.x, pos.y, pos.z,
            CTBSounds.BREEZE_WIND_BURST.get(),
            SoundSource.NEUTRAL, 1.0f, 1.0f);
    }
}
