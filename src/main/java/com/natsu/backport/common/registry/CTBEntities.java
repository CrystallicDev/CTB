package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Bogged;
import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.entity.SulphurCube;
import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.entity.Nautilus;
import com.natsu.backport.common.entity.OminousItemSpawner;
import com.natsu.backport.common.entity.Parched;
import com.natsu.backport.common.entity.ZombieNautilus;
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

	public static final RegistryObject<EntityType<com.natsu.backport.common.entity.VariantEggEntity>> VARIANT_EGG =
			ENTITIES.register("variant_egg", () -> EntityType.Builder
					.<com.natsu.backport.common.entity.VariantEggEntity>of(
							com.natsu.backport.common.entity.VariantEggEntity::new, net.minecraft.world.entity.MobCategory.MISC)
					.sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
					.build("variant_egg"));

	public static final RegistryObject<EntityType<CopperGolem>> COPPER_GOLEM = ENTITIES.register("copper_golem",
			() -> EntityType.Builder.<CopperGolem>of(CopperGolem::new, MobCategory.MISC)
			.sized(0.6f, 0.95f)
			.clientTrackingRange(8)
			.build("copper_golem")
			);

	public static final RegistryObject<EntityType<Parched>> PARCHED = ENTITIES.register("parched",
			() -> EntityType.Builder.<Parched>of(Parched::new, MobCategory.MONSTER)
			.sized(0.6f, 1.99f)
			.clientTrackingRange(8)
			.build("parched")
			);

	public static final RegistryObject<EntityType<com.natsu.backport.common.entity.Armadillo>> ARMADILLO =
			ENTITIES.register("armadillo", () -> EntityType.Builder
					.of(com.natsu.backport.common.entity.Armadillo::new, MobCategory.CREATURE)
					.sized(0.7F, 0.65F).clientTrackingRange(10).build("armadillo"));

	public static final RegistryObject<EntityType<com.natsu.backport.common.entity.HappyGhast>> HAPPY_GHAST =
			ENTITIES.register("happy_ghast", () -> EntityType.Builder
					.of(com.natsu.backport.common.entity.HappyGhast::new, MobCategory.CREATURE)
					.sized(4.0F, 4.0F).clientTrackingRange(10).build("happy_ghast"));

	public static final RegistryObject<EntityType<com.natsu.backport.common.entity.Sniffer>> SNIFFER =
			ENTITIES.register("sniffer", () -> EntityType.Builder
					.of(com.natsu.backport.common.entity.Sniffer::new, MobCategory.CREATURE)
					.sized(1.9F, 1.75F).clientTrackingRange(10).build("sniffer"));

	public static final RegistryObject<EntityType<com.natsu.backport.common.entity.Camel>> CAMEL =
			ENTITIES.register("camel", () -> EntityType.Builder
					.of(com.natsu.backport.common.entity.Camel::new, MobCategory.CREATURE)
					.sized(1.7F, 2.375F).clientTrackingRange(10).build("camel"));
	public static final RegistryObject<EntityType<com.natsu.backport.common.entity.CamelHusk>> CAMEL_HUSK =
			ENTITIES.register("camel_husk", () -> EntityType.Builder
					.of((EntityType<com.natsu.backport.common.entity.CamelHusk> t, net.minecraft.world.level.Level l) ->
							new com.natsu.backport.common.entity.CamelHusk(t, l), MobCategory.CREATURE)
					.sized(1.7F, 2.375F).clientTrackingRange(10).build("camel_husk"));

	public static final RegistryObject<EntityType<Nautilus>> NAUTILUS = ENTITIES.register("nautilus",
			() -> EntityType.Builder.<Nautilus>of(Nautilus::new, MobCategory.WATER_CREATURE)
			.sized(0.9f, 0.9f)
			.clientTrackingRange(10)
			.build("nautilus")
			);

	public static final RegistryObject<EntityType<ZombieNautilus>> ZOMBIE_NAUTILUS = ENTITIES.register("zombie_nautilus",
			() -> EntityType.Builder.<ZombieNautilus>of(ZombieNautilus::new, MobCategory.WATER_CREATURE)
			.sized(0.9f, 0.9f)
			.clientTrackingRange(10)
			.build("zombie_nautilus")
			);

	public static final RegistryObject<EntityType<OminousItemSpawner>> OMINOUS_ITEM_SPAWNER = ENTITIES.register("ominous_item_spawner",
			() -> EntityType.Builder.<OminousItemSpawner>of(OminousItemSpawner::new, MobCategory.MISC)
			.sized(0.25f, 0.25f)
			.clientTrackingRange(8)
			.updateInterval(10)
			.build("ominous_item_spawner")
			);
	public static final RegistryObject<EntityType<SulphurCube>> SULPHUR_CUBE = ENTITIES.register("sulfur_cube",
			() -> EntityType.Builder.<SulphurCube>of(SulphurCube::new, MobCategory.MONSTER)
					.sized(2.04F, 2.04F)
					.clientTrackingRange(10)
					.build("sulfur_cube")
			);
}
