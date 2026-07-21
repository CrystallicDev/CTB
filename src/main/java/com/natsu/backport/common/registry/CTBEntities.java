package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Bogged;
import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.entity.OminousItemSpawner;
import com.natsu.backport.common.entity.WindChargeEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBEntities {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, CTBackport.MODID);

	public static final RegistryObject<EntityType<WindChargeEntity>> WIND_CHARGE_ENTITY = ENTITIES.register("wind_charge",
			() -> EntityType.Builder.<WindChargeEntity>of(WindChargeEntity::new, MobCategory.MISC)
			.sized(0.25f, 0.25f)
			.clientTrackingRange(4)
			.updateInterval(10)
			.build("wind_charge")
			);

	public static final RegistryObject<EntityType<Creaking>> CREAKING = ENTITIES.register("creaking",
			() -> EntityType.Builder.<Creaking>of(Creaking::new, MobCategory.MONSTER)
			.sized(0.9f, 2.7f)
			.clientTrackingRange(8)
			.build("creaking")
			);

	public static final RegistryObject<EntityType<Breeze>> BREEZE = ENTITIES.register("breeze",
			() -> EntityType.Builder.<Breeze>of(Breeze::new, MobCategory.MONSTER).build("breeze")
			);

	public static final RegistryObject<EntityType<Bogged>> BOGGED = ENTITIES.register("bogged",
			() -> EntityType.Builder.<Bogged>of(Bogged::new, MobCategory.MONSTER)
			.sized(0.6f, 1.99f)
			.clientTrackingRange(8)
			.build("bogged")
			);

	public static final RegistryObject<EntityType<OminousItemSpawner>> OMINOUS_ITEM_SPAWNER = ENTITIES.register("ominous_item_spawner",
			() -> EntityType.Builder.<OminousItemSpawner>of(OminousItemSpawner::new, MobCategory.MISC)
			.sized(0.25f, 0.25f)
			.clientTrackingRange(8)
			.updateInterval(10)
			.build("ominous_item_spawner")
			);
	/*
	public static final RegistryObject<EntityType<SulphurCube>> SULPHUR_CUBE = ENTITIES.register("sulphur_cube",
			() -> EntityType.Builder.<SulphurCube>of(SulphurCube::new, MobCategory.MONSTER).build("sulphur_cube")
			);
	*/
}
