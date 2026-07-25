package com.natsu.backport.common.item;

import java.util.function.Supplier;

import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.Lazy;

/** Copper armor, the vanilla 1.21.11 numbers : ×11 durability, 2/4/3/1, enchantability 8. */
public enum CTBArmorMaterials implements ArmorMaterial {

	COPPER("ctbackport:copper", 11, new int[] { 1, 3, 4, 2 }, 8,
			() -> CTBSounds.ARMOR_EQUIP_COPPER.get(), 0.0F, 0.0F,
			() -> Ingredient.of(Items.COPPER_INGOT));

	// boots, leggings, chestplate, helmet base durability, same as vanilla
	private static final int[] HEALTH_PER_SLOT = new int[] { 13, 15, 16, 11 };

	private final String name;
	private final int durabilityMultiplier;
	private final int[] slotProtections;
	private final int enchantmentValue;
	private final Supplier<SoundEvent> sound;
	private final float toughness;
	private final float knockbackResistance;
	private final Lazy<Ingredient> repairIngredient;

	CTBArmorMaterials(String name, int durabilityMultiplier, int[] slotProtections, int enchantmentValue,
			Supplier<SoundEvent> sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.slotProtections = slotProtections;
		this.enchantmentValue = enchantmentValue;
		this.sound = sound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairIngredient = Lazy.of(repairIngredient);
	}

	@Override
	public int getDurabilityForSlot(EquipmentSlot slot) {
		return HEALTH_PER_SLOT[slot.getIndex()] * this.durabilityMultiplier;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot slot) {
		return this.slotProtections[slot.getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.sound.get();
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
