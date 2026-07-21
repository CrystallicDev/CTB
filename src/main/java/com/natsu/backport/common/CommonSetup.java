package com.natsu.backport.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.natsu.backport.utils.sets.DefaultSet;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FireBlock;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBWeatheringCopper;
import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBPotions;
import com.natsu.backport.utils.sets.WeatherableCopperSet;

import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CTBackport.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {

	// attributes are needed on both sides, a dedicated server crashes without them
	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		if (DatagenModLoader.isRunningDataGen()) return;

		event.put(CTBEntities.CREAKING.get(), Creaking.createAttributes().build());
		event.put(CTBEntities.BREEZE.get(), Breeze.createAttributes().build());
		event.put(CTBEntities.BOGGED.get(), com.natsu.backport.common.entity.Bogged.createAttributes().build());
		//event.put(CTBEntities.SULPHUR_CUBE.get(), SulphurCube.createAttributes().build());
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		if (DatagenModLoader.isRunningDataGen()) return;

		event.enqueueWork(CommonSetup::registerWeatherables);
		event.enqueueWork(CommonSetup::registerFlammablesAndCompostables);
		event.enqueueWork(CommonSetup::registerBrewingRecipes);
	}

	// vanilla 1.21 mixes, splash and lingering conversions are container level and free
	private static void registerBrewingRecipes() {
		addMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, CTBItems.BREEZE_ROD.get(), CTBPotions.WIND_CHARGED.get());
		addMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, net.minecraft.world.item.Items.SLIME_BLOCK, CTBPotions.OOZING.get());
		addMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, net.minecraft.world.item.Items.COBWEB, CTBPotions.WEAVING.get());
		addMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, net.minecraft.world.item.Items.STONE, CTBPotions.INFESTED.get());
	}

	private static void addMix(net.minecraft.world.item.alchemy.Potion from, net.minecraft.world.item.Item ingredient, net.minecraft.world.item.alchemy.Potion to) {
		for (net.minecraft.world.item.Item container : List.of(net.minecraft.world.item.Items.POTION,
				net.minecraft.world.item.Items.SPLASH_POTION, net.minecraft.world.item.Items.LINGERING_POTION)) {
			net.minecraftforge.common.brewing.BrewingRecipeRegistry.addRecipe(
					net.minecraftforge.common.crafting.NBTIngredient.of(
							net.minecraft.world.item.alchemy.PotionUtils.setPotion(new net.minecraft.world.item.ItemStack(container), from)),
					net.minecraft.world.item.crafting.Ingredient.of(ingredient),
					net.minecraft.world.item.alchemy.PotionUtils.setPotion(new net.minecraft.world.item.ItemStack(container), to));
		}
	}

	// vanilla values, applied through the sets (FireBlock#setFlammable is AT'd)
	private static void registerFlammablesAndCompostables() {
		Map<Block, Integer> flameOdds = new HashMap<>();
		Map<Block, Integer> burnOdds = new HashMap<>();
		Object2FloatMap<ItemLike> compostables = new Object2FloatOpenHashMap<>();

		for (DefaultSet set : List.of(CTBBlocks.CHERRY_WOOD, CTBBlocks.BAMBOO_WOOD, CTBBlocks.PALE_OAK_WOOD,
				CTBBlocks.PALE_OAK_LEAVES, CTBBlocks.PALE_MOSS)) {
			set.setFlammables(flameOdds, burnOdds);
			set.setCompostables(compostables);
		}

		// the loners
		flameOdds.put(CTBBlocks.BAMBOO_MOSAIC.get(), 5);
		burnOdds.put(CTBBlocks.BAMBOO_MOSAIC.get(), 20);
		flameOdds.put(CTBBlocks.BAMBOO_MOSAIC_STAIRS.get(), 5);
		burnOdds.put(CTBBlocks.BAMBOO_MOSAIC_STAIRS.get(), 20);
		flameOdds.put(CTBBlocks.BAMBOO_MOSAIC_SLAB.get(), 5);
		burnOdds.put(CTBBlocks.BAMBOO_MOSAIC_SLAB.get(), 20);
		flameOdds.put(CTBBlocks.CHERRY_LEAVES.get(), 30);
		burnOdds.put(CTBBlocks.CHERRY_LEAVES.get(), 60);
		compostables.put(CTBBlocks.CHERRY_LEAVES.get().asItem(), 0.3F);
		compostables.put(CTBBlocks.PALE_HANGING_MOSS.get().asItem(), 0.3F);
		compostables.put(CTBBlocks.PALE_OAK_SAPLING.get().asItem(), 0.3F);
		compostables.put(CTBBlocks.OPEN_EYEBLOSSOM.get().asItem(), 0.65F);
		compostables.put(CTBBlocks.CLOSED_EYEBLOSSOM.get().asItem(), 0.65F);

		FireBlock fire = (FireBlock) Blocks.FIRE;
		flameOdds.forEach((block, flame) -> fire.setFlammable(block, flame, burnOdds.get(block)));
		ComposterBlock.COMPOSTABLES.putAll(compostables);
	}

	private static void registerWeatherables() {
		for (WeatherableCopperSet<?, ?> set : List.of(CTBBlocks.COPPER_GRATE, CTBBlocks.COPPER_DOOR, CTBBlocks.COPPER_TRAPDOOR, CTBBlocks.COPPER_BULB, CTBBlocks.CHISELED_COPPER)) {
			set.setWeatherable(CTBWeatheringCopper.NEXT_BY_BLOCK, CTBWeatheringCopper.NEXT_BY_BLOCK.inverse());
			set.setWaxables(CTBWeatheringCopper.WAXABLES);
		}

		// the honeycomb maps live in a class, so those we can grow (AT removes the final)
		BiMap<Block, Block> waxables = HashBiMap.create(HoneycombItem.WAXABLES.get());
		waxables.putAll(CTBWeatheringCopper.WAXABLES);
		HoneycombItem.WAXABLES = Suppliers.memoize(() -> ImmutableBiMap.copyOf(waxables));
		HoneycombItem.WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> HoneycombItem.WAXABLES.get().inverse());
	}
}
