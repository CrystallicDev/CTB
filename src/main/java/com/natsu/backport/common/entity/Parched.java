package com.natsu.backport.common.entity;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** The desert skeleton, its arrows sap strength instead of poisoning. */
public class Parched extends AbstractSkeleton {

	private static final int HARD_ATTACK_INTERVAL = 50;
	private static final int NORMAL_ATTACK_INTERVAL = 70;

	public Parched(EntityType<? extends Parched> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 16.0);
	}

	@Override
	protected AbstractArrow getArrow(ItemStack stack, float power) {
		AbstractArrow abstractArrow = super.getArrow(stack, power);
		if (abstractArrow instanceof Arrow arrow) {
			arrow.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
		}

		return abstractArrow;
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return effect.getEffect() != MobEffects.WEAKNESS && super.canBeAffected(effect);
	}

	@Override
	public void reassessWeaponGoal() {
		super.reassessWeaponGoal();
		if (this.level != null && !this.level.isClientSide && this.bowGoal != null) {
			this.bowGoal.setMinAttackInterval(this.level.getDifficulty() == Difficulty.HARD ? HARD_ATTACK_INTERVAL : NORMAL_ATTACK_INTERVAL);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return CTBSounds.PARCHED_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return CTBSounds.PARCHED_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return CTBSounds.PARCHED_DEATH.get();
	}

	@Override
	protected SoundEvent getStepSound() {
		return CTBSounds.PARCHED_STEP.get();
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return new ResourceLocation(CTBackport.MODID, "entities/parched");
	}
}
