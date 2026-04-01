package com.natsu.backport.server.events;

import com.natsu.backport.common.entity.WindChargeEntity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class WindChargePushEntityEvent extends Event {

	private final Entity entity;
	private final WindChargeEntity windCharge;
	private Vec3 addedVelocity = null;
	
	public WindChargePushEntityEvent(Entity entity, WindChargeEntity wind, Vec3 vel) {
		this.entity = entity;
		this.windCharge = wind;
		this.addedVelocity = vel;
	}

	public Vec3 getAddedVelocity() {
		return addedVelocity;
	}

	public void setAddedVelocity(Vec3 addedVelocity) {
		this.addedVelocity = addedVelocity;
	}

	public Entity getEntity() {
		return entity;
	}

	public WindChargeEntity getWindCharge() {
		return windCharge;
	}
	
	
	
}
