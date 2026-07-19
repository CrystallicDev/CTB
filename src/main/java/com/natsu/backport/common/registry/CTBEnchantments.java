package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.MaceItem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBEnchantments {

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CTBackport.MODID);

	public static final EnchantmentCategory MACE = EnchantmentCategory.create("ctb_mace", item -> item instanceof MaceItem);
	private static final EquipmentSlot[] MAINHAND = {EquipmentSlot.MAINHAND};

	public static final RegistryObject<Enchantment> DENSITY = ENCHANTMENTS.register("density", DensityEnchantment::new);
	public static final RegistryObject<Enchantment> BREACH = ENCHANTMENTS.register("breach", BreachEnchantment::new);
	public static final RegistryObject<Enchantment> WIND_BURST = ENCHANTMENTS.register("wind_burst", WindBurstEnchantment::new);

	public static class DensityEnchantment extends Enchantment {
		DensityEnchantment() {
			super(Rarity.UNCOMMON, MACE, MAINHAND);
		}
		@Override public int getMaxLevel() { return 5; }
		@Override public int getMinCost(int level) { return 5 + (level - 1) * 8; }
		@Override public int getMaxCost(int level) { return getMinCost(level) + 20; }
		@Override
		protected boolean checkCompatibility(Enchantment other) {
			return super.checkCompatibility(other) && other != BREACH.get();
		}
	}

	public static class BreachEnchantment extends Enchantment {
		BreachEnchantment() {
			super(Rarity.RARE, MACE, MAINHAND);
		}
		@Override public int getMaxLevel() { return 4; }
		@Override public int getMinCost(int level) { return 15 + (level - 1) * 9; }
		@Override public int getMaxCost(int level) { return getMinCost(level) + 50; }
		@Override
		protected boolean checkCompatibility(Enchantment other) {
			return super.checkCompatibility(other) && other != DENSITY.get();
		}
	}

	public static class WindBurstEnchantment extends Enchantment {
		WindBurstEnchantment() {
			super(Rarity.RARE, MACE, MAINHAND);
		}
		@Override public int getMaxLevel() { return 3; }
		@Override public int getMinCost(int level) { return 15 + (level - 1) * 9; }
		@Override public int getMaxCost(int level) { return getMinCost(level) + 50; }
	}
}
