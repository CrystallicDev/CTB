package com.natsu.backport.common.entity;

import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** The 26.x camel husk : the undead camel of the desert cavalry. */
public class CamelHusk extends Camel {

	public CamelHusk(EntityType<? extends Camel> type, Level level) {
		super(type, level);
	}

	@Override
	public boolean removeWhenFarAway(double distSqr) {
		return true;
	}

	public boolean isMobControlled() {
		return this.getFirstPassenger() instanceof Mob;
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 location, InteractionHand hand) {
		this.setPersistenceRequired();
		return super.interactAt(player, location, hand);
	}

	@Override
	public boolean canBeLeashed(Player player) {
		return !this.isMobControlled();
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(CTBTags.Items.CAMEL_HUSK_FOOD);
	}

	@Override
	public boolean canMate(Animal partner) {
		return false;
	}

	@Override
	public Camel getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return null;
	}

	@Override
	public boolean canFallInLove() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return CTBSounds.CAMEL_HUSK_AMBIENT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return CTBSounds.CAMEL_HUSK_DEATH.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return CTBSounds.CAMEL_HUSK_HURT.get();
	}

	@Override
	protected SoundEvent getEatingSound() {
		return CTBSounds.CAMEL_HUSK_EAT.get();
	}

	@Override
	protected SoundEvent getStandUpSound() {
		return CTBSounds.CAMEL_HUSK_STAND.get();
	}

	@Override
	protected SoundEvent getSitDownSound() {
		return CTBSounds.CAMEL_HUSK_SIT.get();
	}

	@Override
	protected SoundEvent getSaddleSoundEvent() {
		return CTBSounds.CAMEL_HUSK_SADDLE.get();
	}

	@Override
	protected SoundEvent getDashingSound() {
		return CTBSounds.CAMEL_HUSK_DASH.get();
	}

	@Override
	protected SoundEvent getDashReadySound() {
		return CTBSounds.CAMEL_HUSK_DASH_READY.get();
	}

	@Override
	protected SoundEvent getStepSound() {
		return CTBSounds.CAMEL_HUSK_STEP.get();
	}

	@Override
	protected SoundEvent getSandStepSound() {
		return CTBSounds.CAMEL_HUSK_STEP_SAND.get();
	}
}
