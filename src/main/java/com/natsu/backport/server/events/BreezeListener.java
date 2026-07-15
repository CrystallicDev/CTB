package com.natsu.backport.server.events;

import java.util.Random;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.WindChargeEntity;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class BreezeListener {

	// vanilla breezes deflect projectiles, only wind charges can hit them
	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event) {
		if (!(event.getRayTraceResult() instanceof EntityHitResult hit)
				|| !(hit.getEntity() instanceof Breeze breeze)
				|| event.getProjectile() instanceof WindChargeEntity) {
			return;
		}

		event.setCanceled(true);
		Projectile projectile = event.getProjectile();
		if (breeze.level.isClientSide) {
			return;
		}

		Random random = breeze.getRandom();
		Vec3 motion = projectile.getDeltaMovement();
		projectile.setDeltaMovement(new Vec3(
				-motion.x * 0.5 + (random.nextDouble() - 0.5) * 0.4,
				Math.abs(motion.y) * 0.5 + 0.2,
				-motion.z * 0.5 + (random.nextDouble() - 0.5) * 0.4));
		projectile.hurtMarked = true;
		breeze.level.playSound(null, breeze.blockPosition(), CTBSounds.BREEZE_DEFLECT.get(),
				SoundSource.HOSTILE, 1.0F, 1.0F);
	}
}
