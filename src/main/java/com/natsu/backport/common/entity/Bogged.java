package com.natsu.backport.common.entity;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class Bogged extends AbstractSkeleton implements Shearable {

	private static final EntityDataAccessor<Boolean> DATA_SHEARED = SynchedEntityData.defineId(Bogged.class, EntityDataSerializers.BOOLEAN);
	private static final int HARD_ATTACK_INTERVAL = 50;
	private static final int NORMAL_ATTACK_INTERVAL = 70;

	public Bogged(EntityType<? extends Bogged> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 16.0);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_SHEARED, false);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("sheared", this.isSheared());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setSheared(tag.getBoolean("sheared"));
	}

	public boolean isSheared() {
		return this.entityData.get(DATA_SHEARED);
	}

	public void setSheared(boolean sheared) {
		this.entityData.set(DATA_SHEARED, sheared);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		if (held.is(Items.SHEARS) && this.readyForShearing()) {
			this.shear(SoundSource.PLAYERS);
			this.gameEvent(GameEvent.SHEAR, player);
			if (!this.level.isClientSide) {
				held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			}

			return InteractionResult.sidedSuccess(this.level.isClientSide);
		}
		return super.mobInteract(player, hand);
	}

	@Override
	public void shear(SoundSource soundSource) {
		this.level.playSound(null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
		if (this.level instanceof ServerLevel) {
			// vanilla shearing loot, two rolls of one mushroom
			for (int i = 0; i < 2; i++) {
				this.spawnAtLocation(this.random.nextBoolean() ? Items.RED_MUSHROOM : Items.BROWN_MUSHROOM, (int) this.getBbHeight());
			}
		}
		this.setSheared(true);
	}

	@Override
	public boolean readyForShearing() {
		return !this.isSheared() && this.isAlive();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return CTBSounds.BOGGED_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return CTBSounds.BOGGED_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return CTBSounds.BOGGED_DEATH.get();
	}

	@Override
	protected SoundEvent getStepSound() {
		return CTBSounds.BOGGED_STEP.get();
	}

	@Override
	protected AbstractArrow getArrow(ItemStack stack, float power) {
		AbstractArrow abstractArrow = super.getArrow(stack, power);
		if (abstractArrow instanceof Arrow arrow) {
			arrow.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
		}

		return abstractArrow;
	}

	// vanilla shoots noticeably slower than a regular skeleton (bowGoal is AT'd)
	@Override
	public void reassessWeaponGoal() {
		super.reassessWeaponGoal();
		if (this.level != null && !this.level.isClientSide && this.bowGoal != null) {
			this.bowGoal.setMinAttackInterval(this.level.getDifficulty() == Difficulty.HARD ? HARD_ATTACK_INTERVAL : NORMAL_ATTACK_INTERVAL);
		}
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return new ResourceLocation(CTBackport.MODID, "entities/bogged");
	}
}
