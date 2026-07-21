package com.natsu.backport.common.mobeffects.positive;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BreathOfTheNautilus extends MobEffect {

	public BreathOfTheNautilus() {
		super(MobEffectCategory.BENEFICIAL, 0xFFEE);
	}

	// keeps the air bar topped up, like water breathing
	@Override
	public void applyEffectTick(net.minecraft.world.entity.LivingEntity entity, int amplifier) {
		entity.setAirSupply(Math.min(entity.getMaxAirSupply(), entity.getAirSupply() + 5));
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}