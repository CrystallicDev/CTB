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

	public static final RegistryObject<SoundEvent> NAUTILUS_AMBIENT = simple("nautilus_ambient");
	public static final RegistryObject<SoundEvent> NAUTILUS_AMBIENT_LAND = simple("nautilus_ambient_land");
	public static final RegistryObject<SoundEvent> NAUTILUS_HURT = simple("nautilus_hurt");
	public static final RegistryObject<SoundEvent> NAUTILUS_HURT_LAND = simple("nautilus_hurt_land");
	public static final RegistryObject<SoundEvent> NAUTILUS_DEATH = simple("nautilus_death");
	public static final RegistryObject<SoundEvent> NAUTILUS_DEATH_LAND = simple("nautilus_death_land");
	public static final RegistryObject<SoundEvent> NAUTILUS_DASH = simple("nautilus_dash");
	public static final RegistryObject<SoundEvent> NAUTILUS_DASH_LAND = simple("nautilus_dash_land");
	public static final RegistryObject<SoundEvent> NAUTILUS_DASH_READY = simple("nautilus_dash_ready");
	public static final RegistryObject<SoundEvent> NAUTILUS_DASH_READY_LAND = simple("nautilus_dash_ready_land");
	public static final RegistryObject<SoundEvent> NAUTILUS_EAT = simple("nautilus_eat");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_AMBIENT = simple("zombie_nautilus_ambient");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_AMBIENT_LAND = simple("zombie_nautilus_ambient_land");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_HURT = simple("zombie_nautilus_hurt");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_HURT_LAND = simple("zombie_nautilus_hurt_land");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_DEATH = simple("zombie_nautilus_death");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_DEATH_LAND = simple("zombie_nautilus_death_land");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_DASH_LAND = simple("zombie_nautilus_dash_land");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_DASH_READY = simple("zombie_nautilus_dash_ready");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_DASH_READY_LAND = simple("zombie_nautilus_dash_ready_land");
	public static final RegistryObject<SoundEvent> ZOMBIE_NAUTILUS_EAT = simple("zombie_nautilus_eat");
	public static final RegistryObject<SoundEvent> NAUTILUS_SADDLE_EQUIP = simple("nautilus_saddle_equip");
	public static final RegistryObject<SoundEvent> NAUTILUS_SADDLE_UNDERWATER_EQUIP = simple("nautilus_saddle_underwater_equip");

	public static final RegistryObject<SoundEvent> SPEAR_USE = simple("spear_use");
	public static final RegistryObject<SoundEvent> SPEAR_HIT = simple("spear_hit");
	public static final RegistryObject<SoundEvent> SPEAR_ATTACK = simple("spear_attack");
	public static final RegistryObject<SoundEvent> SPEAR_LUNGE = simple("spear_lunge");
	public static final RegistryObject<SoundEvent> SPEAR_WOOD_USE = simple("spear_wood_use");
	public static final RegistryObject<SoundEvent> SPEAR_WOOD_HIT = simple("spear_wood_hit");
	public static final RegistryObject<SoundEvent> SPEAR_WOOD_ATTACK = simple("spear_wood_attack");

	public static final RegistryObject<SoundEvent> PARCHED_AMBIENT = simple("parched_ambient");
	public static final RegistryObject<SoundEvent> PARCHED_HURT = simple("parched_hurt");
	public static final RegistryObject<SoundEvent> PARCHED_DEATH = simple("parched_death");
	public static final RegistryObject<SoundEvent> PARCHED_STEP = simple("parched_step");
	public static final RegistryObject<SoundEvent> SNIFFER_IDLE = simple("entity.sniffer.idle");
	public static final RegistryObject<SoundEvent> SNIFFER_HURT = simple("entity.sniffer.hurt");
	public static final RegistryObject<SoundEvent> SNIFFER_DEATH = simple("entity.sniffer.death");
	public static final RegistryObject<SoundEvent> SNIFFER_STEP = simple("entity.sniffer.step");
	public static final RegistryObject<SoundEvent> SNIFFER_SNIFFING = simple("entity.sniffer.sniffing");
	public static final RegistryObject<SoundEvent> SNIFFER_SCENTING = simple("entity.sniffer.scenting");
	public static final RegistryObject<SoundEvent> SNIFFER_DIGGING = simple("entity.sniffer.digging");
	public static final RegistryObject<SoundEvent> SNIFFER_DIGGING_STOP = simple("entity.sniffer.digging_stop");
	public static final RegistryObject<SoundEvent> SNIFFER_DROP_SEED = simple("entity.sniffer.drop_seed");
	public static final RegistryObject<SoundEvent> SNIFFER_HAPPY = simple("entity.sniffer.happy");
	public static final RegistryObject<SoundEvent> SNIFFER_EGG_PLOP = simple("entity.sniffer.egg_plop");
	public static final RegistryObject<SoundEvent> SNIFFER_EGG_CRACK = simple("block.sniffer_egg.crack");
	public static final RegistryObject<SoundEvent> SNIFFER_EGG_HATCH = simple("block.sniffer_egg.hatch");
	public static final RegistryObject<SoundEvent> CAMEL_AMBIENT = simple("entity.camel.ambient");
	public static final RegistryObject<SoundEvent> CAMEL_DASH = simple("entity.camel.dash");
	public static final RegistryObject<SoundEvent> CAMEL_DASH_READY = simple("entity.camel.dash_ready");
	public static final RegistryObject<SoundEvent> CAMEL_DEATH = simple("entity.camel.death");
	public static final RegistryObject<SoundEvent> CAMEL_EAT = simple("entity.camel.eat");
	public static final RegistryObject<SoundEvent> CAMEL_HURT = simple("entity.camel.hurt");
	public static final RegistryObject<SoundEvent> CAMEL_SADDLE = simple("entity.camel.saddle");
	public static final RegistryObject<SoundEvent> CAMEL_SIT = simple("entity.camel.sit");
	public static final RegistryObject<SoundEvent> CAMEL_STAND = simple("entity.camel.stand");
	public static final RegistryObject<SoundEvent> CAMEL_STEP = simple("entity.camel.step");
	public static final RegistryObject<SoundEvent> CAMEL_STEP_SAND = simple("entity.camel.step_sand");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_AMBIENT = simple("entity.camel_husk.ambient");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_DASH = simple("entity.camel_husk.dash");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_DASH_READY = simple("entity.camel_husk.dash_ready");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_DEATH = simple("entity.camel_husk.death");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_EAT = simple("entity.camel_husk.eat");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_HURT = simple("entity.camel_husk.hurt");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_SADDLE = simple("entity.camel_husk.saddle");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_SIT = simple("entity.camel_husk.sit");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_STAND = simple("entity.camel_husk.stand");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_STEP = simple("entity.camel_husk.step");
	public static final RegistryObject<SoundEvent> CAMEL_HUSK_STEP_SAND = simple("entity.camel_husk.step_sand");
	public static final RegistryObject<SoundEvent> CRAFTER_CRAFT = simple("block.crafter.craft");
	public static final RegistryObject<SoundEvent> CRAFTER_FAIL = simple("block.crafter.fail");
	public static final RegistryObject<SoundEvent> MUSIC_SULFUR_CAVES = simple("music.overworld.sulfur_caves");
	public static final RegistryObject<SoundEvent> MUSIC_DISC_PRECIPICE = simple("music_disc_precipice");
	public static final RegistryObject<SoundEvent> MUSIC_DISC_CREATOR = simple("music_disc_creator");
	public static final RegistryObject<SoundEvent> MUSIC_DISC_CREATOR_MUSIC_BOX = simple("music_disc_creator_music_box");

	public static final RegistryObject<SoundEvent> COPPER_GOLEM_REGULAR_HURT = simple("copper_golem_regular_hurt");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_REGULAR_DEATH = simple("copper_golem_regular_death");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_REGULAR_STEP = simple("copper_golem_regular_step");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_REGULAR_SPIN = simple("copper_golem_regular_spin");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_WEATHERED_HURT = simple("copper_golem_weathered_hurt");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_WEATHERED_DEATH = simple("copper_golem_weathered_death");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_WEATHERED_STEP = simple("copper_golem_weathered_step");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_WEATHERED_SPIN = simple("copper_golem_weathered_spin");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_OXIDIZED_HURT = simple("copper_golem_oxidized_hurt");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_OXIDIZED_DEATH = simple("copper_golem_oxidized_death");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_OXIDIZED_STEP = simple("copper_golem_oxidized_step");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_OXIDIZED_SPIN = simple("copper_golem_oxidized_spin");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_SPAWN = simple("copper_golem_spawn");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_ITEM_DROP = simple("copper_golem_item_drop");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_ITEM_NO_DROP = simple("copper_golem_item_no_drop");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_NO_ITEM_GET = simple("copper_golem_no_item_get");
	public static final RegistryObject<SoundEvent> COPPER_GOLEM_NO_ITEM_NO_GET = simple("copper_golem_no_item_no_get");
	public static final RegistryObject<SoundEvent> POTENT_SULFUR_BREAK = simple("potent_sulfur_break");
	public static final RegistryObject<SoundEvent> POTENT_SULFUR_STEP = simple("potent_sulfur_step");
	public static final RegistryObject<SoundEvent> POTENT_SULFUR_PLACE = simple("potent_sulfur_place");
	public static final RegistryObject<SoundEvent> POTENT_SULFUR_HIT = simple("potent_sulfur_hit");
	public static final RegistryObject<SoundEvent> SULFUR_SPIKE_LAND = simple("sulfur_spike_land");
	public static final RegistryObject<SoundEvent> NOXIOUS_GAS = simple("noxious_gas");
	public static final RegistryObject<SoundEvent> GEYSER_ERUPTION_START = simple("geyser_eruption_start");
	public static final RegistryObject<SoundEvent> GEYSER_ERUPTION_ACTIVE = simple("geyser_eruption_active");
	public static final RegistryObject<SoundEvent> GEYSER_CONTINUOUS_START = simple("geyser_continuous_start");
	public static final RegistryObject<SoundEvent> GEYSER_CONTINUOUS_ACTIVE = simple("geyser_continuous_active");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_REGULAR_HIT = simple("sulfur_cube_regular_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_REGULAR_PUSH = simple("sulfur_cube_regular_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_BOUNCY_HIT = simple("sulfur_cube_bouncy_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_BOUNCY_PUSH = simple("sulfur_cube_bouncy_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SLOW_BOUNCY_HIT = simple("sulfur_cube_slow_bouncy_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SLOW_BOUNCY_PUSH = simple("sulfur_cube_slow_bouncy_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SLOW_FLAT_HIT = simple("sulfur_cube_slow_flat_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SLOW_FLAT_PUSH = simple("sulfur_cube_slow_flat_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_FAST_FLAT_HIT = simple("sulfur_cube_fast_flat_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_FAST_FLAT_PUSH = simple("sulfur_cube_fast_flat_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_LIGHT_HIT = simple("sulfur_cube_light_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_LIGHT_PUSH = simple("sulfur_cube_light_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_FAST_SLIDING_HIT = simple("sulfur_cube_fast_sliding_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_FAST_SLIDING_PUSH = simple("sulfur_cube_fast_sliding_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SLOW_SLIDING_HIT = simple("sulfur_cube_slow_sliding_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SLOW_SLIDING_PUSH = simple("sulfur_cube_slow_sliding_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_STICKY_HIT = simple("sulfur_cube_sticky_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_STICKY_PUSH = simple("sulfur_cube_sticky_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_HIGH_RESISTANCE_HIT = simple("sulfur_cube_high_resistance_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_HIGH_RESISTANCE_PUSH = simple("sulfur_cube_high_resistance_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_EXPLOSIVE_HIT = simple("sulfur_cube_explosive_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_EXPLOSIVE_PUSH = simple("sulfur_cube_explosive_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_HOT_HIT = simple("sulfur_cube_hot_hit");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_HOT_PUSH = simple("sulfur_cube_hot_push");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_ABSORB = simple("sulfur_cube_absorb");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_EJECT = simple("sulfur_cube_eject");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_BOUNCE = simple("sulfur_cube_bounce");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_HURT = simple("sulfur_cube_hurt");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_DEATH = simple("sulfur_cube_death");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SQUISH = simple("sulfur_cube_squish");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_JUMP = simple("sulfur_cube_jump");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SMALL_HURT = simple("sulfur_cube_small_hurt");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SMALL_DEATH = simple("sulfur_cube_small_death");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SMALL_SQUISH = simple("sulfur_cube_small_squish");
	public static final RegistryObject<SoundEvent> SULFUR_CUBE_SMALL_JUMP = simple("sulfur_cube_small_jump");
	public static final RegistryObject<SoundEvent> SULFUR_BREAK = simple("sulfur_break");
	public static final RegistryObject<SoundEvent> SULFUR_STEP = simple("sulfur_step");
	public static final RegistryObject<SoundEvent> SULFUR_PLACE = simple("sulfur_place");
	public static final RegistryObject<SoundEvent> SULFUR_HIT = simple("sulfur_hit");
	public static final RegistryObject<SoundEvent> CINNABAR_BREAK = simple("cinnabar_break");
	public static final RegistryObject<SoundEvent> CINNABAR_STEP = simple("cinnabar_step");
	public static final RegistryObject<SoundEvent> CINNABAR_PLACE = simple("cinnabar_place");
	public static final RegistryObject<SoundEvent> CINNABAR_HIT = simple("cinnabar_hit");
	public static final RegistryObject<SoundEvent> MUSIC_DISC_BOUNCE = SOUND_EVENTS.register("music_disc_bounce",
			() -> new SoundEvent(new ResourceLocation(CTBackport.MODID, "music_disc_bounce")));
	public static final RegistryObject<SoundEvent> SHELF_ACTIVATE = simple("shelf_activate");
	public static final RegistryObject<SoundEvent> SHELF_DEACTIVATE = simple("shelf_deactivate");
	public static final RegistryObject<SoundEvent> SHELF_PLACE_ITEM = simple("shelf_place_item");
	public static final RegistryObject<SoundEvent> SHELF_TAKE_ITEM = simple("shelf_take_item");
	public static final RegistryObject<SoundEvent> SHELF_SINGLE_SWAP = simple("shelf_single_swap");
	public static final RegistryObject<SoundEvent> SHELF_MULTI_SWAP = simple("shelf_multi_swap");
	public static final RegistryObject<SoundEvent> ARMOR_EQUIP_COPPER = simple("armor_equip_copper");
	public static final RegistryObject<SoundEvent> FIREFLY_BUSH_IDLE = simple("firefly_bush_idle");
	public static final RegistryObject<SoundEvent> COPPER_CHEST_OPEN = simple("copper_chest_open");
	public static final RegistryObject<SoundEvent> COPPER_CHEST_CLOSE = simple("copper_chest_close");

	private static RegistryObject<SoundEvent> simple(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(CTBackport.MODID, name)));
	}

}
