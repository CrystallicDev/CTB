package com.natsu.backport.common.registry;

import com.mojang.serialization.Codec;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.particles.TrailParticleOption;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBParticles {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CTBackport.MODID);

	public static final RegistryObject<ParticleType<TrailParticleOption>> TRAIL = PARTICLES.register("trail",
			() -> new ParticleType<TrailParticleOption>(false, TrailParticleOption.DESERIALIZER) {
				@Override
				public Codec<TrailParticleOption> codec() {
					return TrailParticleOption.CODEC;
				}
			});

	public static final RegistryObject<net.minecraft.core.particles.ParticleType<com.natsu.backport.common.particle.GeyserParticleOptions>> GEYSER =
			PARTICLES.register("geyser", () -> new net.minecraft.core.particles.ParticleType<com.natsu.backport.common.particle.GeyserParticleOptions>(
					false, com.natsu.backport.common.particle.GeyserParticleOptions.DESERIALIZER) {
				@Override
				public com.mojang.serialization.Codec<com.natsu.backport.common.particle.GeyserParticleOptions> codec() {
					return com.natsu.backport.common.particle.GeyserParticleOptions.CODEC;
				}
			});
	public static final RegistryObject<SimpleParticleType> GEYSER_BASE = PARTICLES.register("geyser_base",
			() -> new SimpleParticleType(false)
	);
	public static final RegistryObject<SimpleParticleType> SULFUR_BUBBLES = PARTICLES.register("sulfur_bubbles",
			() -> new SimpleParticleType(false)
	);
	public static final RegistryObject<SimpleParticleType> NOXIOUS_GAS = PARTICLES.register("noxious_gas",
			() -> new SimpleParticleType(false)
	);
	public static final RegistryObject<SimpleParticleType> FIREFLY = PARTICLES.register("firefly",
			() -> new SimpleParticleType(false)
	);
	public static final RegistryObject<SimpleParticleType> COPPER_FIRE_FLAME = PARTICLES.register("copper_fire_flame",
			() -> new SimpleParticleType(false)
	);
	public static final RegistryObject<SimpleParticleType> CHERRY = PARTICLES.register("cherry_leaves_backport",
			() -> new SimpleParticleType(true)
	);
	public static final RegistryObject<SimpleParticleType> PALE_OAK_LEAVES = PARTICLES.register("pale_oak_leaves",
			() -> new SimpleParticleType(true)
	);
	public static final RegistryObject<SimpleParticleType> GUST = PARTICLES.register("gust",
				() -> new SimpleParticleType(true)
			);
	public static final RegistryObject<SimpleParticleType> GUST_EMITTER_SMALL = PARTICLES.register("gust_emitter_small",
			() -> new SimpleParticleType(true)
		);
	public static final RegistryObject<SimpleParticleType> GUST_EMITTER_LARGE = PARTICLES.register("gust_emitter_large",
			() -> new SimpleParticleType(true)
		);

	public static final RegistryObject<SimpleParticleType> TRIAL_SPAWNER_DETECTION = PARTICLES.register("trial_spawner_detection",
			() -> new SimpleParticleType(true)
		);
	public static final RegistryObject<SimpleParticleType> TRIAL_SPAWNER_DETECTION_OMINOUS = PARTICLES.register("trial_spawner_detection_ominous",
			() -> new SimpleParticleType(true)
		);
	public static final RegistryObject<SimpleParticleType> VAULT_CONNECTION = PARTICLES.register("vault_connection",
			() -> new SimpleParticleType(true)
		);
	public static final RegistryObject<SimpleParticleType> TRIAL_OMEN = PARTICLES.register("trial_omen",
			() -> new SimpleParticleType(true)
		);
	public static final RegistryObject<SimpleParticleType> OMINOUS_SPAWNING = PARTICLES.register("ominous_spawning",
			() -> new SimpleParticleType(true)
		);

}
