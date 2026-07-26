package com.natsu.backport.common.entity;

import java.util.function.Supplier;

import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The twelve 26.2 sulfur cube archetypes, hardcoded : the 26.x physics
 * attributes do not exist in 1.18.2, the entity applies these numbers by hand.
 * speed scales received knockback, bounce the ground rebound, friction the
 * ground slide, drag the air slowdown.
 */
public enum SulfurCubeArchetype {

	REGULAR(1.0F, 0.5F, 0.3F, 0.1F, true, null, 0.0F, 0.4125F, 0.09F,
			() -> CTBSounds.SULFUR_CUBE_REGULAR_HIT.get(), () -> CTBSounds.SULFUR_CUBE_REGULAR_PUSH.get(), 0.2F, 0.5F),
	BOUNCY(2.0F, 0.9F, 0.3F, 0.01F, true, null, 0.0F, 0.4125F, 0.105F,
			() -> CTBSounds.SULFUR_CUBE_BOUNCY_HIT.get(), () -> CTBSounds.SULFUR_CUBE_BOUNCY_PUSH.get(), 0.3F, 0.7F),
	SLOW_BOUNCY(-0.4F, 0.6F, 0.3F, 0.05F, false, null, 0.0F, 0.4125F, 0.24F,
			() -> CTBSounds.SULFUR_CUBE_SLOW_BOUNCY_HIT.get(), () -> CTBSounds.SULFUR_CUBE_SLOW_BOUNCY_PUSH.get(), 0.05F, 0.5F),
	SLOW_FLAT(-0.5F, 0.4F, 0.4F, 0.1F, false, null, 0.0F, 0.4125F, 0.105F,
			() -> CTBSounds.SULFUR_CUBE_SLOW_FLAT_HIT.get(), () -> CTBSounds.SULFUR_CUBE_SLOW_FLAT_PUSH.get(), 0.03F, 0.9F),
	FAST_FLAT(1.0F, 0.5F, 0.2F, 0.01F, false, null, 0.0F, 0.9125F, 0.09F,
			() -> CTBSounds.SULFUR_CUBE_FAST_FLAT_HIT.get(), () -> CTBSounds.SULFUR_CUBE_FAST_FLAT_PUSH.get(), 0.03F, 0.9F),
	LIGHT(1.0F, 1.0F, 0.3F, 1.8F, true, null, 0.0F, 0.4125F, 0.18F,
			() -> CTBSounds.SULFUR_CUBE_LIGHT_HIT.get(), () -> CTBSounds.SULFUR_CUBE_LIGHT_PUSH.get(), 0.2F, 0.7F),
	FAST_SLIDING(-0.5F, 0.1F, 0.05F, 0.01F, false, null, 0.0F, 0.6625F, 0.09F,
			() -> CTBSounds.SULFUR_CUBE_FAST_SLIDING_HIT.get(), () -> CTBSounds.SULFUR_CUBE_FAST_SLIDING_PUSH.get(), 0.05F, 1.0F),
	SLOW_SLIDING(-0.8F, 0.1F, 0.05F, 0.01F, false, null, 0.0F, 0.4125F, 0.09F,
			() -> CTBSounds.SULFUR_CUBE_SLOW_SLIDING_HIT.get(), () -> CTBSounds.SULFUR_CUBE_SLOW_SLIDING_PUSH.get(), 0.02F, 1.0F),
	STICKY(2.0F, 0.0F, 2.0F, 0.01F, false, null, 0.0F, 0.4125F, 0.09F,
			() -> CTBSounds.SULFUR_CUBE_STICKY_HIT.get(), () -> CTBSounds.SULFUR_CUBE_STICKY_PUSH.get(), 0.05F, 0.5F),
	HIGH_RESISTANCE(-0.7F, 0.2F, 1.0F, 0.01F, false, null, 0.0F, 0.4125F, 0.09F,
			() -> CTBSounds.SULFUR_CUBE_HIGH_RESISTANCE_HIT.get(), () -> CTBSounds.SULFUR_CUBE_HIGH_RESISTANCE_PUSH.get(), 0.03F, 0.7F),
	EXPLOSIVE(1.0F, 0.5F, 0.3F, 0.3F, true, new ExplosionData(3, false, 120), 0.0F, 0.4125F, 0.09F,
			() -> CTBSounds.SULFUR_CUBE_EXPLOSIVE_HIT.get(), () -> CTBSounds.SULFUR_CUBE_EXPLOSIVE_PUSH.get(), 0.1F, 0.7F),
	HOT(1.0F, 0.5F, 0.3F, 0.1F, true, null, 1.0F, 0.4125F, 0.09F,
			() -> CTBSounds.SULFUR_CUBE_HOT_HIT.get(), () -> CTBSounds.SULFUR_CUBE_HOT_PUSH.get(), 0.2F, 0.7F);

	public record ExplosionData(int power, boolean causesFire, int fuse) {
	}

	public final float speed;
	public final float bounce;
	public final float friction;
	public final float drag;
	public final boolean buoyant;
	public final ExplosionData explosion;
	public final float contactDamage;
	public final float knockbackHorizontal;
	public final float knockbackVertical;
	public final Supplier<SoundEvent> hitSound;
	public final Supplier<SoundEvent> pushSound;
	public final float pushSoundImpulseThreshold;
	public final float pushSoundCooldown;

	SulfurCubeArchetype(float speed, float bounce, float friction, float drag, boolean buoyant,
			ExplosionData explosion, float contactDamage, float knockbackHorizontal, float knockbackVertical,
			Supplier<SoundEvent> hitSound, Supplier<SoundEvent> pushSound,
			float pushSoundImpulseThreshold, float pushSoundCooldown) {
		this.speed = speed;
		this.bounce = bounce;
		this.friction = friction;
		this.drag = drag;
		this.buoyant = buoyant;
		this.explosion = explosion;
		this.contactDamage = contactDamage;
		this.knockbackHorizontal = knockbackHorizontal;
		this.knockbackVertical = knockbackVertical;
		this.hitSound = hitSound;
		this.pushSound = pushSound;
		this.pushSoundImpulseThreshold = pushSoundImpulseThreshold;
		this.pushSoundCooldown = pushSoundCooldown;
	}

	public TagKey<Item> itemTag() {
		return CTBTags.Items.SULFUR_CUBE_ARCHETYPES.get(this);
	}

	/** The first archetype whose item tag matches, regular as fallback. */
	public static SulfurCubeArchetype byItem(ItemStack stack) {
		for (SulfurCubeArchetype archetype : values()) {
			if (stack.is(archetype.itemTag())) {
				return archetype;
			}
		}
		return REGULAR;
	}

	public static boolean isSwallowable(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		for (SulfurCubeArchetype archetype : values()) {
			if (stack.is(archetype.itemTag())) {
				return true;
			}
		}
		return false;
	}
}
