package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.ai.BreezeAttackEntitySensor;

import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBSensors {

	public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, CTBackport.MODID);
	
	public static final RegistryObject<SensorType<BreezeAttackEntitySensor>> BREEZE_ATTACK_ENTITY_SENSOR = SENSOR_TYPES.register("breeze_attack_entity_sensor", 
			() -> new SensorType<BreezeAttackEntitySensor>(BreezeAttackEntitySensor::new)
	);
	
}
