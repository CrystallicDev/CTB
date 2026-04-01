package com.natsu.backport.common.registry;


import com.natsu.backport.CTBackport;
import com.natsu.backport.common.mobeffects.negative.InfestedEffect;
import com.natsu.backport.common.mobeffects.negative.OozingEffect;
import com.natsu.backport.common.mobeffects.negative.TrialOmenEffect;
import com.natsu.backport.common.mobeffects.negative.WeavingEffect;
import com.natsu.backport.common.mobeffects.negative.WindChargedEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CTBackport.MODID);
	
	public static final RegistryObject<MobEffect> INFESTED = EFFECTS.register("infested", 
			() -> new InfestedEffect());
	public static final RegistryObject<MobEffect> OOZING = EFFECTS.register("oozing", 
			() -> new OozingEffect());
	public static final RegistryObject<MobEffect> WEAVING = EFFECTS.register("weaving", 
			() -> new WeavingEffect());
	public static final RegistryObject<MobEffect> WIND_CHARGED = EFFECTS.register("wind_charged", 
			() -> new WindChargedEffect());
	public static final RegistryObject<MobEffect> TRIAL_OMEN = EFFECTS.register("trial_omen", 
			() -> new TrialOmenEffect());
	
}
