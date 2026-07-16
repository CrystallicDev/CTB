package com.natsu.backport.utils;

import javax.annotation.Nullable;

import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindChargeHelper {

    /**
     * Vanilla explosion knockback : direction aimed at the eyes, linear falloff
     * over the diameter, scaled by line-of-sight exposure. Null when out of range.
     */
    @Nullable
    public static Vec3 windKnockback(Vec3 center, Entity target, double radius, double multiplier) {
        double diameter = radius * 2.0;
        double distScaled = Math.sqrt(target.distanceToSqr(center)) / diameter;
        if (distScaled > 1.0) {
            return null;
        }
        double dx = target.getX() - center.x;
        double dy = target.getEyeY() - center.y;
        double dz = target.getZ() - center.z;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0.0) {
            return null;
        }
        double impact = (1.0 - distScaled) * Explosion.getSeenPercent(center, target) * multiplier;
        return new Vec3(dx / len * impact, dy / len * impact, dz / len * impact);
    }

    // knockback burst on death of a wind charged entity, no damage
    public static void explodeWind(ServerLevel level, Vec3 pos, float radius) {
        AABB area = new AABB(pos, pos).inflate(radius * 2.0);
        for (Entity e : level.getEntities(null, area)) {
            Vec3 kb = windKnockback(pos, e, radius, 1.0);
            if (kb != null) {
                e.push(kb.x, kb.y, kb.z);
                e.hurtMarked = true;
            }
        }

        level.sendParticles(radius < 2.0F ? CTBParticles.GUST_EMITTER_SMALL.get() : CTBParticles.GUST_EMITTER_LARGE.get(),
            pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
        level.playSound(null, pos.x, pos.y, pos.z,
            CTBSounds.BREEZE_WIND_BURST.get(),
            SoundSource.NEUTRAL, 1.0f, 1.0f);
    }
}
