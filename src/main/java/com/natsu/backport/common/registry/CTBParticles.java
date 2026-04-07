package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBParticles {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CTBackport.MODID);
	
	public static final RegistryObject<SimpleParticleType> CHERRY = PARTICLES.register("cherry_leaves_backport", 
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
	
}
