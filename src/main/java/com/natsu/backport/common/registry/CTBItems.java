package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.MaceItem;
import com.natsu.backport.common.item.OminousBottleItem;
import com.natsu.backport.common.item.SpearItem;
import com.natsu.backport.common.registry.CTBSounds;
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

	// copper gear, vanilla 1.21.11 stats
	public static final RegistryObject<Item> COPPER_NUGGET = ITEMS.register("copper_nugget", () ->
			new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);
	public static final RegistryObject<Item> COPPER_SWORD = ITEMS.register("copper_sword", () ->
			new net.minecraft.world.item.SwordItem(CopperTier.INSTANCE, 3, -2.4F,
					new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);
	public static final RegistryObject<Item> COPPER_SHOVEL = ITEMS.register("copper_shovel", () ->
			new net.minecraft.world.item.ShovelItem(CopperTier.INSTANCE, 1.5F, -3.0F,
					new Item.Properties().tab(CreativeModeTab.TAB_TOOLS))
			);
	public static final RegistryObject<Item> COPPER_PICKAXE = ITEMS.register("copper_pickaxe", () ->
			new net.minecraft.world.item.PickaxeItem(CopperTier.INSTANCE, 1, -2.8F,
					new Item.Properties().tab(CreativeModeTab.TAB_TOOLS))
			);
	public static final RegistryObject<Item> COPPER_AXE = ITEMS.register("copper_axe", () ->
			new net.minecraft.world.item.AxeItem(CopperTier.INSTANCE, 7.0F, -3.2F,
					new Item.Properties().tab(CreativeModeTab.TAB_TOOLS))
			);
	public static final RegistryObject<Item> COPPER_HOE = ITEMS.register("copper_hoe", () ->
			new net.minecraft.world.item.HoeItem(CopperTier.INSTANCE, -1, -2.0F,
					new Item.Properties().tab(CreativeModeTab.TAB_TOOLS))
			);
	public static final RegistryObject<Item> COPPER_HELMET = ITEMS.register("copper_helmet", () ->
			new net.minecraft.world.item.ArmorItem(com.natsu.backport.common.item.CTBArmorMaterials.COPPER,
					net.minecraft.world.entity.EquipmentSlot.HEAD, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);
	public static final RegistryObject<Item> COPPER_CHESTPLATE = ITEMS.register("copper_chestplate", () ->
			new net.minecraft.world.item.ArmorItem(com.natsu.backport.common.item.CTBArmorMaterials.COPPER,
					net.minecraft.world.entity.EquipmentSlot.CHEST, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);
	public static final RegistryObject<Item> COPPER_LEGGINGS = ITEMS.register("copper_leggings", () ->
			new net.minecraft.world.item.ArmorItem(com.natsu.backport.common.item.CTBArmorMaterials.COPPER,
					net.minecraft.world.entity.EquipmentSlot.LEGS, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);
	public static final RegistryObject<Item> COPPER_BOOTS = ITEMS.register("copper_boots", () ->
			new net.minecraft.world.item.ArmorItem(com.natsu.backport.common.item.CTBArmorMaterials.COPPER,
					net.minecraft.world.entity.EquipmentSlot.FEET, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);

	// nautilus body armor, the vanilla material body values
	public static final RegistryObject<Item> COPPER_NAUTILUS_ARMOR = ITEMS.register("copper_nautilus_armor", () ->
			new com.natsu.backport.common.item.NautilusArmorItem(4, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);
	public static final RegistryObject<Item> IRON_NAUTILUS_ARMOR = ITEMS.register("iron_nautilus_armor", () ->
			new com.natsu.backport.common.item.NautilusArmorItem(5, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);
	public static final RegistryObject<Item> GOLDEN_NAUTILUS_ARMOR = ITEMS.register("golden_nautilus_armor", () ->
			new com.natsu.backport.common.item.NautilusArmorItem(7, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);
	public static final RegistryObject<Item> DIAMOND_NAUTILUS_ARMOR = ITEMS.register("diamond_nautilus_armor", () ->
			new com.natsu.backport.common.item.NautilusArmorItem(11, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT))
			);
	public static final RegistryObject<Item> NETHERITE_NAUTILUS_ARMOR = ITEMS.register("netherite_nautilus_armor", () ->
			new com.natsu.backport.common.item.NautilusArmorItem(19, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).fireResistant())
			);

	public static final RegistryObject<Item> SULPHUR_CUBE_BUCKET = ITEMS.register("sulfur_cube_bucket", () ->
			new net.minecraft.world.item.MobBucketItem(
					com.natsu.backport.common.registry.CTBEntities.SULPHUR_CUBE,
					() -> net.minecraft.world.level.material.Fluids.WATER,
					() -> net.minecraft.sounds.SoundEvents.BUCKET_EMPTY_FISH,
					new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1))
			);
	public static final RegistryObject<Item> SULPHUR_CUBE_SPAWN_EGG = ITEMS.register("sulfur_cube_spawn_egg", () ->
			new net.minecraftforge.common.ForgeSpawnEggItem(
					com.natsu.backport.common.registry.CTBEntities.SULPHUR_CUBE,
					0xE8D24A, 0xB08F1F,
					new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);

	public static final RegistryObject<Item> NAUTILUS_SPAWN_EGG = ITEMS.register("nautilus_spawn_egg", () ->
			new net.minecraftforge.common.ForgeSpawnEggItem(
					com.natsu.backport.common.registry.CTBEntities.NAUTILUS,
					0xC86A50, 0xF3E1CE,
					new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);
	public static final RegistryObject<Item> ZOMBIE_NAUTILUS_SPAWN_EGG = ITEMS.register("zombie_nautilus_spawn_egg", () ->
			new net.minecraftforge.common.ForgeSpawnEggItem(
					com.natsu.backport.common.registry.CTBEntities.ZOMBIE_NAUTILUS,
					0x527A62, 0xA1C4A8,
					new Item.Properties().tab(CreativeModeTab.TAB_MISC))
			);

	public static final RegistryObject<Item> BLUE_EGG = ITEMS.register("blue_egg", () ->
			new com.natsu.backport.common.item.VariantEggItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(16))
			);
	public static final RegistryObject<Item> BROWN_EGG = ITEMS.register("brown_egg", () ->
			new com.natsu.backport.common.item.VariantEggItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(16))
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

	public static final RegistryObject<Item> MUSIC_DISC_BOUNCE = ITEMS.register("music_disc_bounce", () ->
			new net.minecraft.world.item.RecordItem(14, CTBSounds.MUSIC_DISC_BOUNCE,
					new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1).rarity(Rarity.RARE))
			);
	public static final RegistryObject<Item> MUSIC_DISC_PRECIPICE = ITEMS.register("music_disc_precipice", () ->
			new net.minecraft.world.item.RecordItem(13, CTBSounds.MUSIC_DISC_PRECIPICE,
					new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1).rarity(Rarity.RARE))
			);
	public static final RegistryObject<Item> MUSIC_DISC_CREATOR = ITEMS.register("music_disc_creator", () ->
			new net.minecraft.world.item.RecordItem(12, CTBSounds.MUSIC_DISC_CREATOR,
					new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1).rarity(Rarity.RARE))
			);
	public static final RegistryObject<Item> MUSIC_DISC_CREATOR_MUSIC_BOX = ITEMS.register("music_disc_creator_music_box", () ->
			new net.minecraft.world.item.RecordItem(11, CTBSounds.MUSIC_DISC_CREATOR_MUSIC_BOX,
					new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1).rarity(Rarity.RARE))
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
		@Override public float getAttackDamageBonus() { return 1.0F; }
		@Override public int getLevel() { return 1; }
		@Override public int getEnchantmentValue() { return 13; }
		@Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
			return net.minecraft.world.item.crafting.Ingredient.of(Items.COPPER_INGOT);
		}
	}

}
