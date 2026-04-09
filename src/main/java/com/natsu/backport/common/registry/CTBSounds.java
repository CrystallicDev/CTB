package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBSounds {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CTBackport.MODID);
	
	public static final RegistryObject<SoundEvent> BREEZE_WIND_BURST = SOUND_EVENTS.register("wind_burst", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "wind_burst")));
	public static final RegistryObject<SoundEvent> CREAKING_HEART_HURT = SOUND_EVENTS.register("creaking_heart_hurt", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_heart_hurt")));
	public static final RegistryObject<SoundEvent> CREAKING_HEART_SPAWN = SOUND_EVENTS.register("creaking_heart_spawn", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_heart_spawn")));
	public static final RegistryObject<SoundEvent> CREAKING_SPAWN = SOUND_EVENTS.register("creaking_spawn", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_spawn")));
	public static final RegistryObject<SoundEvent> RESIN_PLACE = SOUND_EVENTS.register("resin_place", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "resin_place")));
	public static final RegistryObject<SoundEvent> CREAKING_HEART_IDLE = SOUND_EVENTS.register("creaking_heart_idle", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_heart_idle")));
	
}
