package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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


	public static final RegistryObject<SoundEvent> EYEBLOSSOM_OPEN_LONG = SOUND_EVENTS.register("eyeblossom_open_long", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "eyeblossom_open_long")));
	public static final RegistryObject<SoundEvent> EYEBLOSSOM_CLOSE_LONG = SOUND_EVENTS.register("eyeblossom_close_long", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "eyeblossom_close_long")));
	public static final RegistryObject<SoundEvent> EYEBLOSSOM_OPEN = SOUND_EVENTS.register("eyeblossom_open", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "eyeblossom_open")));
	public static final RegistryObject<SoundEvent> EYEBLOSSOM_CLOSE = SOUND_EVENTS.register("eyeblossom_close", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "eyeblossom_close")));
	public static final RegistryObject<SoundEvent> EYEBLOSSOM_IDLE = SOUND_EVENTS.register("eyeblossom_idle", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "eyeblossom_idle")));

	public static final RegistryObject<SoundEvent> COPPER_BULB_TURN_ON = SOUND_EVENTS.register("copper_bulb_turn", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "copper_bulb_turn")));
	public static final RegistryObject<SoundEvent> COPPER_BULB_TURN_OFF = SOUND_EVENTS.register("copper_bulb_turn_off", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "copper_bulb_turn_off")));

	public static final RegistryObject<SoundEvent> BREEZE_SHOOT = SOUND_EVENTS.register("breeze_shoot", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_shoot")));
	public static final RegistryObject<SoundEvent> BREEZE_WHIRL = SOUND_EVENTS.register("breeze_whirl", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_whirl")));
	public static final RegistryObject<SoundEvent> BREEZE_IDLE_GROUND = SOUND_EVENTS.register("breeze_idle_ground", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_idle_ground")));
	public static final RegistryObject<SoundEvent> BREEZE_IDLE_AIR = SOUND_EVENTS.register("breeze_idle_air", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_idle_air")));
	public static final RegistryObject<SoundEvent> BREEZE_DEATH = SOUND_EVENTS.register("breeze_death", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_death")));
	public static final RegistryObject<SoundEvent> BREEZE_HURT = SOUND_EVENTS.register("breeze_hurt", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_hurt")));
	public static final RegistryObject<SoundEvent> BREEZE_LAND = SOUND_EVENTS.register("breeze_land", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_land")));
	public static final RegistryObject<SoundEvent> BREEZE_JUMP = SOUND_EVENTS.register("breeze_jump", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_jump")));
	public static final RegistryObject<SoundEvent> BREEZE_DEFLECT = SOUND_EVENTS.register("breeze_deflect", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_deflect")));
	public static final RegistryObject<SoundEvent> BREEZE_SLIDE = SOUND_EVENTS.register("breeze_slide", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "breeze_slide")));

	public static final RegistryObject<SoundEvent> MACE_SMASH_AIR = SOUND_EVENTS.register("mace_smash_air", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "mace_smash_air")));
	public static final RegistryObject<SoundEvent> MACE_SMASH_GROUND = SOUND_EVENTS.register("mace_smash_ground", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "mace_smash_ground")));
	public static final RegistryObject<SoundEvent> MACE_SMASH_GROUND_HEAVY = SOUND_EVENTS.register("mace_smash_ground_heavy", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "mace_smash_ground_heavy")));

	public static final RegistryObject<SoundEvent> CREAKING_AMBIENT = SOUND_EVENTS.register("creaking_ambiant", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_ambiant")));
	public static final RegistryObject<SoundEvent> CREAKING_ATTACK = SOUND_EVENTS.register("creaking_attack", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_attack")));
	public static final RegistryObject<SoundEvent> CREAKING_SWAY = SOUND_EVENTS.register("creaking_sway", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_sway")));
	public static final RegistryObject<SoundEvent> CREAKING_DEATH = SOUND_EVENTS.register("creaking_death", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_death")));
	public static final RegistryObject<SoundEvent> CREAKING_STEP = SOUND_EVENTS.register("creaking_step", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_step")));
	public static final RegistryObject<SoundEvent> CREAKING_TWITCH = SOUND_EVENTS.register("creaking_twitch", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_twitch")));
	public static final RegistryObject<SoundEvent> CREAKING_ACTIVATE = SOUND_EVENTS.register("creaking_activate", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_activate")));
	public static final RegistryObject<SoundEvent> CREAKING_DEACTIVATE = SOUND_EVENTS.register("creaking_deactivate", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_deactivate")));
	public static final RegistryObject<SoundEvent> CREAKING_UNFREEZE = SOUND_EVENTS.register("creaking_unfreeze", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_unfreeze")));
	public static final RegistryObject<SoundEvent> CREAKING_FREEZE = SOUND_EVENTS.register("creaking_freeze", () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "creaking_freeze")));

	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT = simple("trial_spawner_ambient");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT_OMINOUS = simple("trial_spawner_ambient_ominous");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_DETECT_PLAYER = simple("trial_spawner_detect_player");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_MOB = simple("trial_spawner_spawn_mob");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_OPEN_SHUTTER = simple("trial_spawner_open_shutter");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_CLOSE_SHUTTER = simple("trial_spawner_close_shutter");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_EJECT_ITEM = simple("trial_spawner_eject_item");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_OMINOUS_ACTIVATE = simple("trial_spawner_ominous_activate");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM = simple("trial_spawner_about_to_spawn_item");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_ITEM = simple("trial_spawner_spawn_item");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_ITEM_BEGIN = simple("trial_spawner_spawn_item_begin");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_BREAK = simple("trial_spawner_break");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_PLACE = simple("trial_spawner_place");
	public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_STEP = simple("trial_spawner_step");

	public static final RegistryObject<SoundEvent> VAULT_AMBIENT = simple("vault_ambient");
	public static final RegistryObject<SoundEvent> VAULT_ACTIVATE = simple("vault_activate");
	public static final RegistryObject<SoundEvent> VAULT_DEACTIVATE = simple("vault_deactivate");
	public static final RegistryObject<SoundEvent> VAULT_INSERT_ITEM = simple("vault_insert_item");
	public static final RegistryObject<SoundEvent> VAULT_INSERT_ITEM_FAIL = simple("vault_insert_item_fail");
	public static final RegistryObject<SoundEvent> VAULT_REJECT_REWARDED_PLAYER = simple("vault_reject_rewarded_player");
	public static final RegistryObject<SoundEvent> VAULT_OPEN_SHUTTER = simple("vault_open_shutter");
	public static final RegistryObject<SoundEvent> VAULT_CLOSE_SHUTTER = simple("vault_close_shutter");
	public static final RegistryObject<SoundEvent> VAULT_EJECT_ITEM = simple("vault_eject_item");
	public static final RegistryObject<SoundEvent> VAULT_BREAK = simple("vault_break");
	public static final RegistryObject<SoundEvent> VAULT_PLACE = simple("vault_place");
	public static final RegistryObject<SoundEvent> VAULT_STEP = simple("vault_step");

	public static final RegistryObject<SoundEvent> BOGGED_AMBIENT = simple("bogged_ambient");
	public static final RegistryObject<SoundEvent> BOGGED_HURT = simple("bogged_hurt");
	public static final RegistryObject<SoundEvent> BOGGED_DEATH = simple("bogged_death");
	public static final RegistryObject<SoundEvent> BOGGED_STEP = simple("bogged_step");

	private static RegistryObject<SoundEvent> simple(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, name)));
	}

}
