package com.natsu.backport.common.entity;

import java.util.List;

import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBParticles;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.server.events.WindChargePushEntityEvent;

import net.minecraft.server.level.ServerLevel;
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
			explodeWind(result.getLocation());
		}
	}

	@Override
	public void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!level.isClientSide()) {
			Entity entity = result.getEntity();
			Vec3 kb = this.getDeltaMovement().normalize().add(0, 0.5, 0);
			WindChargePushEntityEvent event = new WindChargePushEntityEvent(entity, this, kb);
			MinecraftForge.EVENT_BUS.post(event);
			if (!event.isCanceled()) {
				entity.push(kb.x, kb.y, kb.z);
				entity.hurtMarked = true;
			}
			explodeWind(result.getLocation());
		}
	}

	private void explodeWind(Vec3 pos) {
		AABB area = new AABB(pos, pos).inflate(3.0);
		List<Entity> entities = level.getEntities(this, area);
		for (Entity e : entities) {
			Vec3 dir = e.position().subtract(pos).normalize();
			Vec3 kb = new Vec3(dir.x * 2, dir.y * 1.5 + 0.5, dir.z * 2);
			WindChargePushEntityEvent event = new WindChargePushEntityEvent(e, this, kb);
			if (!event.isCanceled()) {
				e.push(kb.x, kb.y, kb.z);
				e.hurtMarked = true;
			}
		}

		if (level instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(CTBParticles.GUST_EMITTER_LARGE.get(), pos.x, pos.y, pos.z, 30, 0.5, 0.5, 0.5, 0.3);
		}
		level.playSound(null, pos.x, pos.y, pos.z, CTBSounds.BREEZE_WIND_BURST.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
		this.discard();
	}
}
