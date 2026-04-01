package com.natsu.backport.common.mobeffects.negative;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class InfestedEffect extends MobEffect {

	public InfestedEffect() {
		super(MobEffectCategory.HARMFUL, 0x5C5C5C);
	}
}