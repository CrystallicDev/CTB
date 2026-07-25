package ctbackport.datagen.lang;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.common.registry.CTBEnchantments;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.utils.sets.DirtDecorationSet;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.ResinSet;
import com.natsu.backport.utils.sets.StoneDecorationSet;
import com.natsu.backport.utils.sets.WeatherableCopperSet;
import com.natsu.backport.utils.sets.WoodSet;

import ctbackport.datagen.DataGenBlockItemHandler;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class CTBLangEN extends LanguageProvider implements DataGenBlockItemHandler {

	public CTBLangEN(DataGenerator gen) {
		super(gen, CTBackport.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add(CTBEffects.INFESTED.get(), "Infested");
		add(CTBEffects.OOZING.get(), "Oozing");
		add(CTBEffects.TRIAL_OMEN.get(), "Trial Omen");
		add(CTBEffects.WEAVING.get(), "Weaving");
		add(CTBEffects.WIND_CHARGED.get(), "Wind Charged");
		handleWoodSet(CTBBlocks.BAMBOO_WOOD);
		handleWoodSet(CTBBlocks.CHERRY_WOOD);
		handleWoodSet(CTBBlocks.PALE_OAK_WOOD);
		handleMossSet(CTBBlocks.PALE_MOSS);
		handleResinSet(CTBBlocks.RESIN);
		handleCopperBulbSet(CTBBlocks.COPPER_BULB);
		handleCopperSet(CTBBlocks.COPPER_GRATE);
		handleCopperSet(CTBBlocks.COPPER_DOOR);
		handleCopperSet(CTBBlocks.COPPER_TRAPDOOR);
		handleCopperSet(CTBBlocks.CHISELED_COPPER);
		handleCopperSet(CTBBlocks.COPPER_BARS);
		handleCopperSet(CTBBlocks.COPPER_CHAIN);
		handleCopperSet(CTBBlocks.COPPER_LANTERN);
		add(CTBBlocks.COPPER_TORCH.get(), "Copper Torch");

		add(CTBBlocks.TUFF_STAIRS.get(), "Tuff Stairs");
		add(CTBBlocks.TUFF_SLAB.get(), "Tuff Slab");
		add(CTBBlocks.TUFF_WALL.get(), "Tuff Wall");
		add(CTBBlocks.CHISELED_TUFF.get(), "Chiseled Tuff");
		add(CTBBlocks.POLISHED_TUFF.get(), "Polished Tuff");
		add(CTBBlocks.POLISHED_TUFF_STAIRS.get(), "Polished Tuff Stairs");
		add(CTBBlocks.POLISHED_TUFF_SLAB.get(), "Polished Tuff Slab");
		add(CTBBlocks.POLISHED_TUFF_WALL.get(), "Polished Tuff Wall");
		add(CTBBlocks.TUFF_BRICKS.get(), "Tuff Bricks");
		add(CTBBlocks.TUFF_BRICK_STAIRS.get(), "Tuff Brick Stairs");
		add(CTBBlocks.TUFF_BRICK_SLAB.get(), "Tuff Brick Slab");
		add(CTBBlocks.TUFF_BRICK_WALL.get(), "Tuff Brick Wall");
		add(CTBBlocks.CHISELED_TUFF_BRICKS.get(), "Chiseled Tuff Bricks");

		add(CTBBlocks.CHERRY_LEAVES.get(), "Cherry Leaves");
		add(CTBBlocks.CREAKING_HEART.get(), "Creaking Heart");
		add(CTBBlocks.OPEN_EYEBLOSSOM.get(), "Open Eyeblossom");
		add(CTBBlocks.CLOSED_EYEBLOSSOM.get(), "Closed Eyeblossom");
		add(CTBBlocks.PALE_HANGING_MOSS.get(), "Pale Hanging Moss");
		add(CTBBlocks.PALE_OAK_SAPLING.get(), "Pale Oak Sapling");
		add(CTBItems.WIND_CHARGE.get(), "Wind Charge");
		add(CTBItems.BREEZE_ROD.get(), "Breeze Rod");
		add(CTBItems.MACE.get(), "Mace");
		add(CTBBlocks.HEAVY_CORE.get(), "Heavy Core");
		add(CTBBlocks.TRIAL_SPAWNER.get(), "Trial Spawner");
		add(CTBBlocks.VAULT.get(), "Vault");
		add(CTBItems.TRIAL_KEY.get(), "Trial Key");
		add(CTBItems.OMINOUS_TRIAL_KEY.get(), "Ominous Trial Key");
		add(CTBItems.OMINOUS_BOTTLE.get(), "Ominous Bottle");
		add(CTBBlocks.DECORATED_POT.get(), "Decorated Pot");
		add("entity.ctbackport.bogged", "Bogged");
		add("entity.ctbackport.parched", "Parched");
		add("entity.ctbackport.copper_golem", "Copper Golem");
		add(CTBBlocks.COPPER_CHEST.get(), "Copper Chest");
		add(CTBBlocks.EXPOSED_COPPER_CHEST.get(), "Exposed Copper Chest");
		add(CTBBlocks.WEATHERED_COPPER_CHEST.get(), "Weathered Copper Chest");
		add(CTBBlocks.OXIDIZED_COPPER_CHEST.get(), "Oxidized Copper Chest");
		add(CTBBlocks.WAXED_COPPER_CHEST.get(), "Waxed Copper Chest");
		add(CTBBlocks.WAXED_EXPOSED_COPPER_CHEST.get(), "Waxed Exposed Copper Chest");
		add(CTBBlocks.WAXED_WEATHERED_COPPER_CHEST.get(), "Waxed Weathered Copper Chest");
		add(CTBBlocks.WAXED_OXIDIZED_COPPER_CHEST.get(), "Waxed Oxidized Copper Chest");
		add(CTBBlocks.COPPER_GOLEM_STATUE.get(), "Copper Golem Statue");
		add("container.ctbackport.copper_chest", "Copper Chest");
		add(CTBItems.COPPER_NUGGET.get(), "Copper Nugget");
		add(CTBItems.COPPER_SWORD.get(), "Copper Sword");
		add(CTBItems.COPPER_SHOVEL.get(), "Copper Shovel");
		add(CTBItems.COPPER_PICKAXE.get(), "Copper Pickaxe");
		add(CTBItems.COPPER_AXE.get(), "Copper Axe");
		add(CTBItems.COPPER_HOE.get(), "Copper Hoe");
		add(CTBItems.COPPER_HELMET.get(), "Copper Helmet");
		add(CTBItems.COPPER_CHESTPLATE.get(), "Copper Chestplate");
		add(CTBItems.COPPER_LEGGINGS.get(), "Copper Leggings");
		add(CTBItems.COPPER_BOOTS.get(), "Copper Boots");
		add("subtitles.item.armor.equip_copper", "Copper armor clinks");
		add(CTBItems.MUSIC_DISC_PRECIPICE.get(), "Music Disc");
		add("item.ctbackport.music_disc_precipice.desc", "Aaron Cherof - Precipice");
		add(CTBItems.MUSIC_DISC_CREATOR.get(), "Music Disc");
		add("item.ctbackport.music_disc_creator.desc", "Lena Raine - Creator");
		add(CTBItems.MUSIC_DISC_CREATOR_MUSIC_BOX.get(), "Music Disc");
		add("item.ctbackport.music_disc_creator_music_box.desc", "Lena Raine - Creator (Music Box)");
		add("entity.ctbackport.nautilus", "Nautilus");
		add("entity.ctbackport.zombie_nautilus", "Zombie Nautilus");
		add(CTBEffects.BREATH_OF_THE_NAUTILUS.get(), "Breath of the Nautilus");
		add(CTBItems.WOODEN_SPEAR.get(), "Wooden Spear");
		add(CTBItems.STONE_SPEAR.get(), "Stone Spear");
		add(CTBItems.COPPER_SPEAR.get(), "Copper Spear");
		add(CTBItems.IRON_SPEAR.get(), "Iron Spear");
		add(CTBItems.GOLDEN_SPEAR.get(), "Golden Spear");
		add(CTBItems.DIAMOND_SPEAR.get(), "Diamond Spear");
		add(CTBItems.NETHERITE_SPEAR.get(), "Netherite Spear");
		add(CTBEnchantments.LUNGE.get(), "Lunge");
		add(CTBItems.FLOW_POTTERY_SHERD.get(), "Flow Pottery Sherd");
		add(CTBItems.GUSTER_POTTERY_SHERD.get(), "Guster Pottery Sherd");
		add(CTBItems.SCRAPE_POTTERY_SHERD.get(), "Scrape Pottery Sherd");
		add("item.minecraft.potion.effect.oozing", "Potion of Oozing");
		add("item.minecraft.splash_potion.effect.oozing", "Splash Potion of Oozing");
		add("item.minecraft.lingering_potion.effect.oozing", "Lingering Potion of Oozing");
		add("item.minecraft.tipped_arrow.effect.oozing", "Arrow of Oozing");
		add("item.minecraft.potion.effect.weaving", "Potion of Weaving");
		add("item.minecraft.splash_potion.effect.weaving", "Splash Potion of Weaving");
		add("item.minecraft.lingering_potion.effect.weaving", "Lingering Potion of Weaving");
		add("item.minecraft.tipped_arrow.effect.weaving", "Arrow of Weaving");
		add("item.minecraft.potion.effect.infested", "Potion of Infestation");
		add("item.minecraft.splash_potion.effect.infested", "Splash Potion of Infestation");
		add("item.minecraft.lingering_potion.effect.infested", "Lingering Potion of Infestation");
		add("item.minecraft.tipped_arrow.effect.infested", "Arrow of Infestation");
		add("item.minecraft.potion.effect.wind_charged", "Potion of Wind Charging");
		add("item.minecraft.splash_potion.effect.wind_charged", "Splash Potion of Wind Charging");
		add("item.minecraft.lingering_potion.effect.wind_charged", "Lingering Potion of Wind Charging");
		add("item.minecraft.tipped_arrow.effect.wind_charged", "Arrow of Wind Charging");
		add(CTBEnchantments.DENSITY.get(), "Density");
		add(CTBEnchantments.BREACH.get(), "Breach");
		add(CTBEnchantments.WIND_BURST.get(), "Wind Burst");
		// entities are not registered during datagen, raw keys
		add("entity.ctbackport.creaking", "Creaking");
		add("entity.ctbackport.breeze", "Breeze");
	}

	@Override
	public void handleWoodSet(WoodSet set) {
		String name = formatSetName(set.getName());
		add(set.log.get(), name+" Log");
		add(set.strippedLog.get(), "Stripped "+name+" Log");
		add(set.wood.get(), name+" Wood");
		add(set.strippedWood.get(), "Stripped "+name+" Wood");
		add(set.planks.get(), name+" Planks");
		add(set.slab.get(), name+" Slab");
		add(set.stairs.get(), name+" Stairs");
		add(set.button.get(), name+" Button");
		add(set.pressurePlate.get(), name+" Pressure Plate");
		add(set.door.get(), name+" Door");
		add(set.trapdoor.get(), name+" Trapdoor");
		add(set.fence.get(), name+" Fence");
		add(set.fenceGate.get(), name+" Fence Gate");
		add(set.standingsign.get(), name+" Sign");

	}

	@Override
	public void handleLeavesSet(LeavesSet set) {
		String name = formatSetName(set.getName());
		add(set.leaves.get(), name+" Leaves");
	}

	@Override
	public void handleStoneDecorationSet(StoneDecorationSet set) {
		String name = formatSetName(set.getName());
		add(set.stone.get(), name);
		add(set.polishedStone.get(), "Polished "+name);
		add(set.stairs.get(), name+ " Stairs");
		add(set.polishedStairs.get(), "Polished "+name+ " Stairs");
		add(set.slabs.get(), name+" Slab");
		add(set.polishedSlabs.get(), "Polished "+name+" Slab");
		add(set.walls.get(), name+" Wall");
		add(set.polishedWalls.get(), "Polished "+name+" Wall");
	}

	@Override
	public void handleDirtDecorationSet(DirtDecorationSet set) {
		String name = formatSetName(set.getName());
		add(set.dirt.get(), name);
		add(set.packedDirt.get(), "Packed "+name);
		add(set.brick.get(), "Packed "+name+" Brick");
		add(set.brickSlab.get(), "Packed "+name+" Brick Slab");
		add(set.brickStairs.get(), "Packed "+name+" Brick Stairs");
		add(set.brickWalls.get(), "Packed "+name+" Brick Wall");
	}

	@Override
	public void handleMossSet(MossSet set) {
		String name = formatSetName(set.getName());
		add(set.moss.get(), name+" Moss");
		add(set.mossLayer.get(), name+" Moss Layer");

	}

	/**
	 * Utils to formatSetName and split on "_" the sets names
	 * */
	public static String formatSetName(String s) {
		return formatSetName(s, true);
	}

	public static String formatSetName(String s, boolean addCapital) {
	    if (s == null || s.isEmpty()) {
			return s;
		}
	    s = s.replaceAll("_", " ");
	    if (!addCapital) {
			return s;
		}
	    String[] words = s.split(" ");
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < words.length; i++) {
	    	String word = words[i];
	    	if (!word.isEmpty()) {
	    		builder.append(Character.toUpperCase(word.charAt(0)));
	    		if (word.length() > 1) {
	    			builder.append(word.substring(1).toLowerCase());
	    		}
	    	}
	    	if (i < words.length - 1) {
				builder.append(" ");
			}
	    }
	    return builder.toString();
	}

	@Override
	public void handleResinSet(ResinSet set) {
		String name = formatSetName(set.getName());
		add(set.clump.get(), name+" Clump");
		add(set.resinBrick.get(), name+" Brick");
		add(set.block.get(), name+" Block");
		add(set.brick.get(), name+" Bricks Block");
		add(set.brickSlab.get(), name+" Bricks Slab");
		add(set.brickStairs.get(), name+" Bricks Stairs");
		add(set.brickWalls.get(), name+" Bricks Wall");
		add(set.chiseledBrick.get(), "Chiseled "+name+" Bricks");
	}

	@Override
	public void handleCopperSet(WeatherableCopperSet<?, ?> set) {
		String name = formatSetName(set.name);
		add(set.block.get(), name);
		add(set.exposedBlock.get(), "Exposed " + name);
		add(set.weatheredBlock.get(), "Weathered " + name);
		add(set.oxidizedBlock.get(), "Oxidized " + name);
		add(set.blockWaxed.get(), "Waxed " + name);
		add(set.exposedBlockWaxed.get(), "Waxed Exposed " + name);
		add(set.weatheredBlockWaxed.get(), "Waxed Weathered " + name);
		add(set.oxidizedBlockWaxed.get(), "Waxed Oxidized " + name);
	}

	@Override
	public void handleCopperDoorSet(WeatherableCopperSet<?, ?> set) {
		handleCopperSet(set);
	}

	@Override
	public void handleCopperTrapdoorSet(WeatherableCopperSet<?, ?> set) {
		handleCopperSet(set);
	}

	@Override
	public void handleCopperBulbSet(WeatherableCopperSet<?, ?> set) {
		add(set.block.get(), "Copper Bulb");
		add(set.exposedBlock.get(), "Exposed Copper Bulb");
		add(set.weatheredBlock.get(), "Weathered Copper Bulb");
		add(set.oxidizedBlock.get(), "Oxidized Copper Bulb");
		add(set.blockWaxed.get(), "Waxed Copper Bulb");
		add(set.exposedBlockWaxed.get(), "Waxed Exposed Copper Bulb");
		add(set.weatheredBlockWaxed.get(), "Waxed Weathered Copper Bulb");
		add(set.oxidizedBlockWaxed.get(), "Waxed Oxidized Copper Bulb");
	}

}
