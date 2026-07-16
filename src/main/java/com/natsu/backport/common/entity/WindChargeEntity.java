package com.natsu.backport.common.entity;


import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.server.events.WindChargePushEntityEvent;
import com.natsu.backport.utils.WindChargeHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class WindChargeEntity extends ThrowableItemProjectile {

	public WindChargeEntity(EntityType<? extends WindChargeEntity> type, Level world) {
		super(type, world);
	}

	public WindChargeEntity(EntityType<? extends WindChargeEntity> entity, double x, double y, double z, Level level) {
	      super(entity, x, y, z, level);
	   }

	public WindChargeEntity(Level world, LivingEntity thrower) {
		super(CTBEntities.WIND_CHARGE_ENTITY.get(), thrower, world);
	}

	@Override
	public Item getDefaultItem() {
		return CTBItems.WIND_CHARGE.get();
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (!level.isClientSide()) {
			// vanilla explodes at the projectile center : on the block face, the
			// exposure raycasts end inside the ground and eat all the knockback
			explodeWind(position());
		}
	}

	@Override
	public void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!level.isClientSide()) {
			// vanilla : one point of damage, the knockback comes from the burst only
			Entity entity = result.getEntity();
			Entity owner = getOwner();
			entity.hurt(DamageSource.thrown(this, owner), 1.0F);
			explodeWind(position());
		}
	}

	private void explodeWind(Vec3 pos) {
		// vanilla numbers : small strong burst for players, wide one for breezes
		boolean fromBreeze = getOwner() instanceof Breeze;
		double radius = fromBreeze ? 3.0 : 1.2;
		double multiplier = fromBreeze ? 1.0 : 1.22;

		for (Entity e : level.getEntities(this, new AABB(pos, pos).inflate(radius * 2.0))) {
			Vec3 kb = WindChargeHelper.windKnockback(pos, e, radius, multiplier);
			if (kb == null) {
				continue;
			}
			WindChargePushEntityEvent event = new WindChargePushEntityEvent(e, this, kb);
			MinecraftForge.EVENT_BUS.post(event);
			if (!event.isCanceled()) {
				e.push(kb.x, kb.y, kb.z);
				e.hurtMarked = true;
			}
		}

		if (level instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(radius < 2.0 ? CTBParticles.GUST_EMITTER_SMALL.get() : CTBParticles.GUST_EMITTER_LARGE.get(),
					pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
		}
		level.playSound(null, pos.x, pos.y, pos.z, CTBSounds.BREEZE_WIND_BURST.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
		this.discard();
	}
}
