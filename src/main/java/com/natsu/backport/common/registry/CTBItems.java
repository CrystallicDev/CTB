package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.MaceItem;
import com.natsu.backport.common.item.OminousBottleItem;
import com.natsu.backport.common.item.SpearItem;
import com.natsu.backport.common.item.WindChargeItem;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CTBackport.MODID);

	public static final RegistryObject<Item> WIND_CHARGE = ITEMS.register("wind_charge", () ->
			new WindChargeItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(16))
			);

	public static final RegistryObject<Item> MACE = ITEMS.register("mace", () ->
			new MaceItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).durability(500).rarity(Rarity.EPIC))
			);

	public static final RegistryObject<Item> BREEZE_ROD = ITEMS.register("breeze_rod", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);

	public static final RegistryObject<Item> TRIAL_KEY = ITEMS.register("trial_key", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);

	public static final RegistryObject<Item> OMINOUS_TRIAL_KEY = ITEMS.register("ominous_trial_key", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE))
			);

	public static final RegistryObject<Item> OMINOUS_BOTTLE = ITEMS.register("ominous_bottle", () ->
			new OminousBottleItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON))
			);

	public static final RegistryObject<Item> FLOW_POTTERY_SHERD = ITEMS.register("flow_pottery_sherd", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);
	public static final RegistryObject<Item> GUSTER_POTTERY_SHERD = ITEMS.register("guster_pottery_sherd", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);
	public static final RegistryObject<Item> SCRAPE_POTTERY_SHERD = ITEMS.register("scrape_pottery_sherd", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);

	// vanilla 1.21.11 kinetic tuning per tier
	public static final RegistryObject<Item> WOODEN_SPEAR = spear("wooden_spear", Tiers.WOOD,
			new SpearItem.KineticParams(0.65F, 0.7F, 0.75F, 5.0F, 14.0F, 10.0F, 5.1F, 15.0F, 4.6F), true, false);
	public static final RegistryObject<Item> STONE_SPEAR = spear("stone_spear", Tiers.STONE,
			new SpearItem.KineticParams(0.75F, 0.82F, 0.7F, 4.5F, 13.0F, 9.0F, 5.1F, 13.75F, 4.6F), false, false);
	public static final RegistryObject<Item> COPPER_SPEAR = spear("copper_spear", CopperTier.INSTANCE,
			new SpearItem.KineticParams(0.85F, 0.82F, 0.65F, 4.0F, 12.0F, 8.25F, 5.1F, 12.5F, 4.6F), false, false);
	public static final RegistryObject<Item> IRON_SPEAR = spear("iron_spear", Tiers.IRON,
			new SpearItem.KineticParams(0.95F, 0.95F, 0.6F, 2.5F, 11.0F, 6.75F, 5.1F, 11.25F, 4.6F), false, false);
	public static final RegistryObject<Item> GOLDEN_SPEAR = spear("golden_spear", Tiers.GOLD,
			new SpearItem.KineticParams(0.95F, 0.7F, 0.7F, 3.5F, 13.0F, 8.5F, 5.1F, 13.75F, 4.6F), false, false);
	public static final RegistryObject<Item> DIAMOND_SPEAR = spear("diamond_spear", Tiers.DIAMOND,
			new SpearItem.KineticParams(1.05F, 1.075F, 0.5F, 3.0F, 10.0F, 6.5F, 5.1F, 10.0F, 4.6F), false, false);
	public static final RegistryObject<Item> NETHERITE_SPEAR = spear("netherite_spear", Tiers.NETHERITE,
			new SpearItem.KineticParams(1.15F, 1.2F, 0.4F, 2.5F, 9.0F, 5.5F, 5.1F, 8.75F, 4.6F), false, true);

	private static RegistryObject<Item> spear(String name, net.minecraft.world.item.Tier tier,
			SpearItem.KineticParams params, boolean wood, boolean fireResistant) {
		return ITEMS.register(name, () -> {
			Item.Properties props = new Item.Properties().tab(CreativeModeTab.TAB_COMBAT);
			if (fireResistant) {
				props = props.fireResistant();
			}
			return new SpearItem(tier, params, wood, props);
		});
	}

	/** Between stone and iron, matching the 1.21 copper tools. */
	public static class CopperTier implements net.minecraft.world.item.Tier {
		public static final CopperTier INSTANCE = new CopperTier();

		@Override public int getUses() { return 190; }
		@Override public float getSpeed() { return 5.0F; }
		@Override public float getAttackDamageBonus() { return 1.5F; }
		@Override public int getLevel() { return 1; }
		@Override public int getEnchantmentValue() { return 13; }
		@Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
			return net.minecraft.world.item.crafting.Ingredient.of(Items.COPPER_INGOT);
		}
	}

}
