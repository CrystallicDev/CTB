package com.natsu.backport.common.entity.ai;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.natsu.backport.common.entity.Breeze;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

public class BreezeAttackEntitySensor extends NearestLivingEntitySensor {
    public static final int BREEZE_SENSOR_RADIUS = 24;

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

	@Override
	protected void doTick(ServerLevel level, LivingEntity entity) {
		if (!(entity instanceof Breeze breeze))
			return;

		double radiusXZ = 24.0;		//1.21 has 2 methods for this, but we dont :(
		double radiusY = 24.0;

		AABB box = entity.getBoundingBox().inflate(radiusXZ, radiusY, radiusXZ);
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, target -> target != entity
				&& EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target) && Sensor.isEntityAttackable(breeze, target));
		entities.sort(Comparator.comparingDouble(entity::distanceToSqr));

		if (!entities.isEmpty()) {
			breeze.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, entities.get(0));
		} else {
			breeze.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
		}
	}

}