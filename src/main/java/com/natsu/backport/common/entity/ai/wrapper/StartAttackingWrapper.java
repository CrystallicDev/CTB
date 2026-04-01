package com.natsu.backport.common.entity.ai.wrapper;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;

public class StartAttackingWrapper {

	public static <E extends Mob> StartAttacking<E> create(Function<E, Optional<? extends LivingEntity>> targetGetter) {
		return new StartAttacking<>(entity -> targetGetter.apply(entity).isPresent(), targetGetter);
	}
	
}
