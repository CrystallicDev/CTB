package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBPotions {

	public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, CTBackport.MODID);

	// 3 minutes like vanilla, splash and lingering conversions come for free
	public static final RegistryObject<Potion> OOZING = POTIONS.register("oozing",
			() -> new Potion("oozing", new MobEffectInstance(CTBEffects.OOZING.get(), 3600)));
	public static final RegistryObject<Potion> WEAVING = POTIONS.register("weaving",
			() -> new Potion("weaving", new MobEffectInstance(CTBEffects.WEAVING.get(), 3600)));
	public static final RegistryObject<Potion> INFESTED = POTIONS.register("infested",
			() -> new Potion("infested", new MobEffectInstance(CTBEffects.INFESTED.get(), 3600)));
	public static final RegistryObject<Potion> WIND_CHARGED = POTIONS.register("wind_charged",
			() -> new Potion("wind_charged", new MobEffectInstance(CTBEffects.WIND_CHARGED.get(), 3600)));

}
