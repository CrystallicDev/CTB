package ctbackport.datagen.lang;

import java.util.HashMap;
import java.util.Map;

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

public class CTBLangFR extends LanguageProvider implements DataGenBlockItemHandler {

	Map<String, String> TRANSLATION = Map.of("a", "b");

	public CTBLangFR(DataGenerator gen) {
		super(gen, CTBackport.MODID, "fr_fr");
		TRANSLATION = new HashMap<>();
		TRANSLATION.put("cherry", "cerisier");
		TRANSLATION.put("pale_oak", "chêne_pâle");
		TRANSLATION.put("bamboo", "bambou");
		TRANSLATION.put("pale", "pâle");
		TRANSLATION.put("resin", "résine");
	}

	@Override
	protected void addTranslations() {
		handleWoodSet(CTBBlocks.BAMBOO_WOOD);
		handleWoodSet(CTBBlocks.CHERRY_WOOD);
		handleWoodSet(CTBBlocks.PALE_OAK_WOOD);
		handleMossSet(CTBBlocks.PALE_MOSS);
		add(CTBItems.COPPER_NAUTILUS_ARMOR.get(), "Armure en cuivre pour nautile");
		add(CTBItems.IRON_NAUTILUS_ARMOR.get(), "Armure en fer pour nautile");
		add(CTBItems.GOLDEN_NAUTILUS_ARMOR.get(), "Armure en or pour nautile");
		add(CTBItems.DIAMOND_NAUTILUS_ARMOR.get(), "Armure en diamant pour nautile");
		add(CTBItems.NETHERITE_NAUTILUS_ARMOR.get(), "Armure en Netherite pour nautile");
		add(CTBBlocks.OAK_SHELF.get(), "Etagere en chene");
		add(CTBBlocks.SPRUCE_SHELF.get(), "Etagere en sapin");
		add(CTBBlocks.BIRCH_SHELF.get(), "Etagere en bouleau");
		add(CTBBlocks.JUNGLE_SHELF.get(), "Etagere en acajou");
		add(CTBBlocks.ACACIA_SHELF.get(), "Etagere en acacia");
		add(CTBBlocks.DARK_OAK_SHELF.get(), "Etagere en chene noir");
		add(CTBBlocks.CRIMSON_SHELF.get(), "Etagere carmin");
		add(CTBBlocks.WARPED_SHELF.get(), "Etagere biscornue");
		add(CTBBlocks.PALE_OAK_SHELF.get(), "Etagere en chene pale");
		add(CTBBlocks.CHERRY_SHELF.get(), "Etagere en cerisier");
		add(CTBBlocks.BAMBOO_SHELF.get(), "Etagere en bambou");
		add(CTBBlocks.SULFUR.get(), "Soufre");
		add(CTBBlocks.SULFUR_STAIRS.get(), "Escalier en soufre");
		add(CTBBlocks.SULFUR_SLAB.get(), "Dalle de soufre");
		add(CTBBlocks.SULFUR_WALL.get(), "Muret de soufre");
		add(CTBBlocks.POLISHED_SULFUR.get(), "Soufre poli");
		add(CTBBlocks.POLISHED_SULFUR_STAIRS.get(), "Escalier en soufre poli");
		add(CTBBlocks.POLISHED_SULFUR_SLAB.get(), "Dalle de soufre poli");
		add(CTBBlocks.POLISHED_SULFUR_WALL.get(), "Muret de soufre poli");
		add(CTBBlocks.SULFUR_BRICKS.get(), "Briques de soufre");
		add(CTBBlocks.SULFUR_BRICK_STAIRS.get(), "Escalier en briques de soufre");
		add(CTBBlocks.SULFUR_BRICK_SLAB.get(), "Dalle de briques de soufre");
		add(CTBBlocks.SULFUR_BRICK_WALL.get(), "Muret de briques de soufre");
		add(CTBBlocks.CHISELED_SULFUR.get(), "Soufre sculpté");
		add(CTBBlocks.CINNABAR.get(), "Cinabre");
		add(CTBBlocks.CINNABAR_STAIRS.get(), "Escalier en cinabre");
		add(CTBBlocks.CINNABAR_SLAB.get(), "Dalle de cinabre");
		add(CTBBlocks.CINNABAR_WALL.get(), "Muret de cinabre");
		add(CTBBlocks.POLISHED_CINNABAR.get(), "Cinabre poli");
		add(CTBBlocks.POLISHED_CINNABAR_STAIRS.get(), "Escalier en cinabre poli");
		add(CTBBlocks.POLISHED_CINNABAR_SLAB.get(), "Dalle de cinabre poli");
		add(CTBBlocks.POLISHED_CINNABAR_WALL.get(), "Muret de cinabre poli");
		add(CTBBlocks.CINNABAR_BRICKS.get(), "Briques de cinabre");
		add(CTBBlocks.CINNABAR_BRICK_STAIRS.get(), "Escalier en briques de cinabre");
		add(CTBBlocks.CINNABAR_BRICK_SLAB.get(), "Dalle de briques de cinabre");
		add(CTBBlocks.CINNABAR_BRICK_WALL.get(), "Muret de briques de cinabre");
		add(CTBBlocks.CHISELED_CINNABAR.get(), "Cinabre sculpté");
		add(CTBBlocks.GOLDEN_DANDELION.get(), "Pissenlit doré");
		add(CTBBlocks.POTTED_GOLDEN_DANDELION.get(), "Pissenlit doré en pot");
		add(CTBItems.MUSIC_DISC_BOUNCE.get(), "Disque de musique");
		add("item.ctbackport.music_disc_bounce.desc", "fingerspit - Bounce");
		add(CTBBlocks.POTENT_SULFUR.get(), "Soufre puissant");
		add(CTBBlocks.SULFUR_SPIKE.get(), "Pointe de soufre");
		add("entity.ctbackport.sulfur_cube", "Cube de soufre");
		add("biome.ctbackport.sulfur_caves", "Grottes de soufre");
		add("block.ctbackport.crafter", "Assembleur");
		add("block.ctbackport.suspicious_sand", "Sable suspect");
		add("block.ctbackport.suspicious_gravel", "Gravier suspect");
		add("item.ctbackport.brush", "Pinceau");
		add("item.ctbackport.angler_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.archer_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.arms_up_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.blade_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.brewer_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.burn_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.danger_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.explorer_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.friend_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.heart_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.heartbreak_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.howl_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.miner_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.mourner_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.plenty_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.prize_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.sheaf_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.shelter_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.skull_pottery_sherd", "Tesson de poterie");
		add("item.ctbackport.snort_pottery_sherd", "Tesson de poterie");
		add("entity.ctbackport.happy_ghast", "Ghast joyeux");
		add("item.ctbackport.happy_ghast_spawn_egg", "Œuf d'apparition de ghast joyeux");
		add("block.ctbackport.dried_ghast", "Ghast séché");
		add("item.ctbackport.white_harness", "Harnais blanc");
		add("item.ctbackport.orange_harness", "Harnais orange");
		add("item.ctbackport.magenta_harness", "Harnais magenta");
		add("item.ctbackport.light_blue_harness", "Harnais bleu clair");
		add("item.ctbackport.yellow_harness", "Harnais jaune");
		add("item.ctbackport.lime_harness", "Harnais vert clair");
		add("item.ctbackport.pink_harness", "Harnais rose");
		add("item.ctbackport.gray_harness", "Harnais gris");
		add("item.ctbackport.light_gray_harness", "Harnais gris clair");
		add("item.ctbackport.cyan_harness", "Harnais cyan");
		add("item.ctbackport.purple_harness", "Harnais violet");
		add("item.ctbackport.blue_harness", "Harnais bleu");
		add("item.ctbackport.brown_harness", "Harnais marron");
		add("item.ctbackport.green_harness", "Harnais vert");
		add("item.ctbackport.red_harness", "Harnais rouge");
		add("item.ctbackport.black_harness", "Harnais noir");
		add("entity.ctbackport.sniffer", "Renifleur");
		add("item.ctbackport.sniffer_spawn_egg", "\u0152uf d'apparition de renifleur");
		add("block.ctbackport.sniffer_egg", "\u0152uf de renifleur");
		add("block.ctbackport.torchflower", "Fleur-torche");
		add("block.ctbackport.torchflower_crop", "Fleur-torche en croissance");
		add("block.ctbackport.pitcher_crop", "Plante \u00e0 urnes en croissance");
		add("block.ctbackport.pitcher_plant", "Plante \u00e0 urnes");
		add("item.ctbackport.torchflower_seeds", "Graines de fleur-torche");
		add("item.ctbackport.pitcher_pod", "Cosse de plante \u00e0 urnes");
		add("entity.ctbackport.camel", "Chameau");
		add("entity.ctbackport.camel_husk", "Chameau husk");
		add("item.ctbackport.camel_spawn_egg", "\u0152uf d'apparition de chameau");
		add("item.ctbackport.camel_husk_spawn_egg", "\u0152uf d'apparition de chameau husk");
		add("item.ctbackport.nautilus_spawn_egg", "\u0152uf d'apparition de nautile");
		add("item.ctbackport.zombie_nautilus_spawn_egg", "\u0152uf d'apparition de nautile zombie");
		add("container.crafter", "Assembleur");
		add("gui.togglable_slot", "Cliquez pour d\u00e9sactiver l'emplacement");
		add("death.attack.sulfurCubeHot", "%1$s est mort car le sol n'est pas la seule lave");
		add("death.attack.sulfurCubeHot.player", "%2$s a montr\u00e9 \u00e0 %1$s que le sol n'est pas la seule lave");
		add(CTBItems.SULPHUR_CUBE_BUCKET.get(), "Cube de soufre dans un seau");
		add(CTBItems.SULPHUR_CUBE_SPAWN_EGG.get(), "Œuf d'apparition de cube de soufre");
		add(CTBItems.BLUE_EGG.get(), "Œuf bleu");
		add(CTBItems.BROWN_EGG.get(), "Œuf marron");
		add(CTBBlocks.BUSH.get(), "Buisson");
		add(CTBBlocks.FIREFLY_BUSH.get(), "Buisson à lucioles");
		add(CTBBlocks.CACTUS_FLOWER.get(), "Fleur de cactus");
		add(CTBBlocks.SHORT_DRY_GRASS.get(), "Herbes sèches basses");
		add(CTBBlocks.TALL_DRY_GRASS.get(), "Herbes sèches hautes");
		add(CTBBlocks.WILDFLOWERS.get(), "Fleurs sauvages");
		add(CTBBlocks.LEAF_LITTER.get(), "Feuilles mortes");
		add(CTBBlocks.POTTED_PALE_OAK_SAPLING.get(), "Pousse de chêne pâle en pot");
		add(CTBBlocks.POTTED_OPEN_EYEBLOSSOM.get(), "Œillade ouverte en pot");
		add(CTBBlocks.POTTED_CLOSED_EYEBLOSSOM.get(), "Œillade fermée en pot");
		handleLeavesSet(CTBBlocks.PALE_OAK_LEAVES);
		handleResinSet(CTBBlocks.RESIN);
		handleCopperBulbSet(CTBBlocks.COPPER_BULB);
		handleCopperSet(CTBBlocks.COPPER_GRATE);
		handleCopperSet(CTBBlocks.COPPER_DOOR);
		handleCopperSet(CTBBlocks.COPPER_TRAPDOOR);
		handleCopperSet(CTBBlocks.CHISELED_COPPER);
		handleCopperSet(CTBBlocks.COPPER_BARS);
		handleCopperSet(CTBBlocks.COPPER_CHAIN);
		handleCopperSet(CTBBlocks.COPPER_LANTERN);
		add(CTBBlocks.COPPER_TORCH.get(), "Torche en cuivre");

		add(CTBBlocks.TUFF_STAIRS.get(), "Escaliers en tuf");
		add(CTBBlocks.TUFF_SLAB.get(), "Dalle en tuf");
		add(CTBBlocks.TUFF_WALL.get(), "Muret en tuf");
		add(CTBBlocks.CHISELED_TUFF.get(), "Tuf sculpté");
		add(CTBBlocks.POLISHED_TUFF.get(), "Tuf poli");
		add(CTBBlocks.POLISHED_TUFF_STAIRS.get(), "Escaliers en tuf poli");
		add(CTBBlocks.POLISHED_TUFF_SLAB.get(), "Dalle en tuf poli");
		add(CTBBlocks.POLISHED_TUFF_WALL.get(), "Muret en tuf poli");
		add(CTBBlocks.TUFF_BRICKS.get(), "Briques de tuf");
		add(CTBBlocks.TUFF_BRICK_STAIRS.get(), "Escaliers en briques de tuf");
		add(CTBBlocks.TUFF_BRICK_SLAB.get(), "Dalle en briques de tuf");
		add(CTBBlocks.TUFF_BRICK_WALL.get(), "Muret en briques de tuf");
		add(CTBBlocks.CHISELED_TUFF_BRICKS.get(), "Briques de tuf sculptées");


		add(CTBBlocks.CHERRY_LEAVES.get(), "Feuilles de cerisier");
		add(CTBBlocks.CREAKING_HEART.get(), "Cœur de Craqueur");
		add(CTBBlocks.OPEN_EYEBLOSSOM.get(), "Œillade ouverte");
		add(CTBBlocks.CLOSED_EYEBLOSSOM.get(), "Œillade fermée");
		add(CTBBlocks.PALE_HANGING_MOSS.get(), "Mousse pâle suspendue");
		add(CTBBlocks.PALE_OAK_SAPLING.get(), "Pousse de chêne pâle");
		add(CTBItems.WIND_CHARGE.get(), "Boule de vent");
		add(CTBItems.BREEZE_ROD.get(), "Bâton de brise");
		add(CTBItems.MACE.get(), "Masse d'armes");
		add(CTBBlocks.HEAVY_CORE.get(), "Cœur pesant");
		add(CTBBlocks.TRIAL_SPAWNER.get(), "Générateur d'épreuve");
		add(CTBBlocks.VAULT.get(), "Chambre forte");
		add(CTBItems.TRIAL_KEY.get(), "Clé d'épreuve");
		add(CTBItems.OMINOUS_TRIAL_KEY.get(), "Clé d'épreuve sinistre");
		add(CTBItems.OMINOUS_BOTTLE.get(), "Fiole sinistre");
		add(CTBBlocks.DECORATED_POT.get(), "Poterie décorée");
		add("entity.ctbackport.bogged", "Enlisé");
		add("entity.ctbackport.parched", "Desséché");
		add("entity.ctbackport.copper_golem", "Golem de cuivre");
		add(CTBBlocks.COPPER_CHEST.get(), "Coffre en cuivre");
		add(CTBBlocks.EXPOSED_COPPER_CHEST.get(), "Coffre en cuivre exposé");
		add(CTBBlocks.WEATHERED_COPPER_CHEST.get(), "Coffre en cuivre érodé");
		add(CTBBlocks.OXIDIZED_COPPER_CHEST.get(), "Coffre en cuivre oxydé");
		add(CTBBlocks.WAXED_COPPER_CHEST.get(), "Coffre en cuivre ciré");
		add(CTBBlocks.WAXED_EXPOSED_COPPER_CHEST.get(), "Coffre en cuivre exposé ciré");
		add(CTBBlocks.WAXED_WEATHERED_COPPER_CHEST.get(), "Coffre en cuivre érodé ciré");
		add(CTBBlocks.WAXED_OXIDIZED_COPPER_CHEST.get(), "Coffre en cuivre oxydé ciré");
		add(CTBBlocks.COPPER_GOLEM_STATUE.get(), "Statue de golem de cuivre");
		add("container.ctbackport.copper_chest", "Coffre en cuivre");
		add(CTBItems.COPPER_NUGGET.get(), "Pépite de cuivre");
		add(CTBItems.COPPER_SWORD.get(), "Épée en cuivre");
		add(CTBItems.COPPER_SHOVEL.get(), "Pelle en cuivre");
		add(CTBItems.COPPER_PICKAXE.get(), "Pioche en cuivre");
		add(CTBItems.COPPER_AXE.get(), "Hache en cuivre");
		add(CTBItems.COPPER_HOE.get(), "Houe en cuivre");
		add(CTBItems.COPPER_HELMET.get(), "Casque en cuivre");
		add(CTBItems.COPPER_CHESTPLATE.get(), "Plastron en cuivre");
		add(CTBItems.COPPER_LEGGINGS.get(), "Jambières en cuivre");
		add(CTBItems.COPPER_BOOTS.get(), "Bottes en cuivre");
		add("subtitles.item.armor.equip_copper", "Cliquetis d'une armure en cuivre");
		add(CTBItems.MUSIC_DISC_PRECIPICE.get(), "Disque de musique");
		add("item.ctbackport.music_disc_precipice.desc", "Aaron Cherof - Precipice");
		add(CTBItems.MUSIC_DISC_CREATOR.get(), "Disque de musique");
		add("item.ctbackport.music_disc_creator.desc", "Lena Raine - Creator");
		add(CTBItems.MUSIC_DISC_CREATOR_MUSIC_BOX.get(), "Disque de musique");
		add("item.ctbackport.music_disc_creator_music_box.desc", "Lena Raine - Creator (boîte à musique)");
		add("entity.ctbackport.nautilus", "Nautile");
		add("entity.ctbackport.zombie_nautilus", "Nautile-zombie");
		add(CTBEffects.BREATH_OF_THE_NAUTILUS.get(), "Respiration du nautile");
		add(CTBItems.WOODEN_SPEAR.get(), "Lance en bois");
		add(CTBItems.STONE_SPEAR.get(), "Lance en pierre");
		add(CTBItems.COPPER_SPEAR.get(), "Lance en cuivre");
		add(CTBItems.IRON_SPEAR.get(), "Lance en fer");
		add(CTBItems.GOLDEN_SPEAR.get(), "Lance en or");
		add(CTBItems.DIAMOND_SPEAR.get(), "Lance en diamant");
		add(CTBItems.NETHERITE_SPEAR.get(), "Lance en netherite");
		add(CTBEnchantments.LUNGE.get(), "Élan");
		add(CTBItems.FLOW_POTTERY_SHERD.get(), "Tesson de poterie flux");
		add(CTBItems.GUSTER_POTTERY_SHERD.get(), "Tesson de poterie bourrasque");
		add(CTBItems.SCRAPE_POTTERY_SHERD.get(), "Tesson de poterie raclage");
		add("item.minecraft.potion.effect.oozing", "Potion de suintement");
		add("item.minecraft.splash_potion.effect.oozing", "Potion jetable de suintement");
		add("item.minecraft.lingering_potion.effect.oozing", "Potion persistante de suintement");
		add("item.minecraft.tipped_arrow.effect.oozing", "Flèche de suintement");
		add("item.minecraft.potion.effect.weaving", "Potion de tissage");
		add("item.minecraft.splash_potion.effect.weaving", "Potion jetable de tissage");
		add("item.minecraft.lingering_potion.effect.weaving", "Potion persistante de tissage");
		add("item.minecraft.tipped_arrow.effect.weaving", "Flèche de tissage");
		add("item.minecraft.potion.effect.infested", "Potion d'infestation");
		add("item.minecraft.splash_potion.effect.infested", "Potion jetable d'infestation");
		add("item.minecraft.lingering_potion.effect.infested", "Potion persistante d'infestation");
		add("item.minecraft.tipped_arrow.effect.infested", "Flèche d'infestation");
		add("item.minecraft.potion.effect.wind_charged", "Potion de rafale");
		add("item.minecraft.splash_potion.effect.wind_charged", "Potion jetable de rafale");
		add("item.minecraft.lingering_potion.effect.wind_charged", "Potion persistante de rafale");
		add("item.minecraft.tipped_arrow.effect.wind_charged", "Flèche de rafale");
		add(CTBEnchantments.DENSITY.get(), "Densité");
		add(CTBEnchantments.BREACH.get(), "Brèche");
		add(CTBEnchantments.WIND_BURST.get(), "Rafale de vent");
		// entities are not registered during datagen, raw keys
		add("entity.ctbackport.creaking", "Craqueur");
		add("entity.ctbackport.breeze", "Brise");
	}

	@Override
	public void handleWoodSet(WoodSet set) {
		try {
			add(set.log.get(), "Bûche "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.strippedLog.get(), "Bûche "+accordDe(formatSetName(translateSetName(set.getName()), false))+" écorcée");
			add(set.wood.get(), "Bois "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.strippedWood.get(), "Bois "+accordDe(formatSetName(translateSetName(set.getName()), false))+" écorcée");
			add(set.planks.get(), "Planches "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.slab.get(), "Dalle en "+formatSetName(translateSetName(set.getName()), false));
			add(set.stairs.get(), "Escaliers en "+formatSetName(translateSetName(set.getName()), false));
			add(set.button.get(), "Bouton en "+formatSetName(translateSetName(set.getName()), false));
			add(set.pressurePlate.get(), "Plaque de pression en "+formatSetName(translateSetName(set.getName()), false));
			add(set.door.get(), "Porte en "+formatSetName(translateSetName(set.getName()), false));
			add(set.trapdoor.get(), "Trappe en "+formatSetName(translateSetName(set.getName()), false));
			add(set.fence.get(), "Barrière en "+formatSetName(translateSetName(set.getName()), false));
			add(set.fenceGate.get(), "Portillon en "+formatSetName(translateSetName(set.getName()), false));
			add(set.standingsign.get(), "Panneau en "+formatSetName(translateSetName(set.getName()), false));

		} catch (MissingTranslationException er) {
			throw new IllegalStateException(er.getMessage());
		}
	}



	@Override
	public void handleLeavesSet(LeavesSet set) {
		try {
			add(set.leaves.get(), "Feuilles "+accordDe(formatSetName(translateSetName(set.getName()), false)));
		} catch (MissingTranslationException er) {
			throw new IllegalStateException(er.getMessage());
		}
	}

	@Override
	public void handleStoneDecorationSet(StoneDecorationSet set) {
		try {
			add(set.stone.get(), formatSetName(translateSetName(set.getName())));
			add(set.polishedStone.get(), formatSetName(translateSetName(set.getName()))+" polie");
			add(set.stairs.get(), "Escaliers en "+formatSetName(translateSetName(set.getName()), false));
			add(set.polishedStairs.get(), "Escaliers en "+formatSetName(translateSetName(set.getName()), false)+ " polis");
			add(set.slabs.get(), "Dalle en "+formatSetName(translateSetName(set.getName()), false));
			add(set.polishedSlabs.get(), "Dalle en "+formatSetName(translateSetName(set.getName()), false)+" polie");
			add(set.walls.get(), "Mur en "+formatSetName(translateSetName(set.getName()), false));
			add(set.polishedWalls.get(), "Mur en "+formatSetName(translateSetName(set.getName()), false)+" polie");
		} catch (MissingTranslationException er) {
			throw new IllegalStateException(er.getMessage());
		}
	}

	@Override
	public void handleDirtDecorationSet(DirtDecorationSet set) {
		try {
			String name = formatSetName(translateSetName(set.getName()));
			add(set.dirt.get(), name);
			add(set.packedDirt.get(), name+" crue");
			add(set.brick.get(), "Briques "+accordDe(formatSetName(translateSetName(set.getName()), false))+" crue");
			add(set.brickSlab.get(), "Dalle de briques "+accordDe(formatSetName(translateSetName(set.getName()), false))+" crue");
			add(set.brickStairs.get(), "Escaliers de briques "+accordDe(formatSetName(translateSetName(set.getName()), false))+" crue");
			add(set.brickWalls.get(), "Mur de briques "+accordDe(formatSetName(translateSetName(set.getName()), false))+" crue");

		} catch (MissingTranslationException er) {
			throw new IllegalStateException(er.getMessage());
		}
	}


	@Override
	public void handleMossSet(MossSet set) {
		try {
			String name = formatSetName(translateSetName(set.getName()), false);
			add(set.moss.get(), "Mousse "+name);
			add(set.mossLayer.get(), "Tapis de mousse "+name);
		} catch (MissingTranslationException er) {
			throw new IllegalStateException(er.getMessage());
		}

	}

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
	    return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public String translateSetName(String setName) throws MissingTranslationException {
		String translation = TRANSLATION.get(setName);
		if (translation == null) {
			throw new MissingTranslationException("Missing Translation for "+getName()+", DefaultSet: "+setName);
		}
		return translation;
	}

	/**
	 * C'est bien chiant le français la vache
	 * ENG : French is really damn annoying
	 * */
	public static String accordDe(String nom) {
		if (nom == null || nom.isEmpty()) {
			return "de "+nom;
		}

		char p = Character.toLowerCase(nom.charAt(0));
		return (p == 'a' || p == 'e' || p == 'i' || p == 'o' || p == 'u' || p == 'h' || p == 'h') ? "d'"+nom : "de "+nom;
	}

	@Override
	public void handleResinSet(ResinSet set) {
		try {
			add(set.clump.get(), "Amas "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.resinBrick.get(), "Brique "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.block.get(), "Block "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.brick.get(), "Block de briques "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.brickSlab.get(), "Dalle de briques "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.brickStairs.get(), "Escaliers de briques "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.brickWalls.get(), "Mur de briques "+accordDe(formatSetName(translateSetName(set.getName()), false)));
			add(set.chiseledBrick.get(), "Briques taillées "+accordDe(formatSetName(translateSetName(set.getName()), false)));
		} catch (MissingTranslationException er) {
			throw new IllegalStateException(er.getMessage());
		}
	}

	private static final java.util.Map<String, String[]> COPPER_FR = java.util.Map.of(
			"copper_grate", new String[]{"Grille de cuivre", "e"},
			"copper_door", new String[]{"Porte en cuivre", "e"},
			"copper_trapdoor", new String[]{"Trappe en cuivre", "e"},
			"chiseled_copper", new String[]{"Cuivre sculpté", ""},
			"copper_bars", new String[]{"Barreaux en cuivre", "s"},
			"copper_chain", new String[]{"Chaîne en cuivre", "e"},
			"copper_lantern", new String[]{"Lanterne en cuivre", "e"});

	@Override
	public void handleCopperSet(WeatherableCopperSet<?, ?> set) {
		String[] entry = COPPER_FR.get(set.name);
		String name = entry[0];
		String g = entry[1];
		add(set.block.get(), name);
		add(set.exposedBlock.get(), name + " exposé" + g);
		add(set.weatheredBlock.get(), name + " érodé" + g);
		add(set.oxidizedBlock.get(), name + " oxydé" + g);
		add(set.blockWaxed.get(), name + " ciré" + g);
		add(set.exposedBlockWaxed.get(), name + " exposé" + g + " ciré" + g);
		add(set.weatheredBlockWaxed.get(), name + " érodé" + g + " ciré" + g);
		add(set.oxidizedBlockWaxed.get(), name + " oxydé" + g + " ciré" + g);
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
		add(set.block.get(), "Ampoule en cuivre");
		add(set.exposedBlock.get(), "Ampoule en cuivre exposé");
		add(set.weatheredBlock.get(), "Ampoule en cuivre érodé");
		add(set.oxidizedBlock.get(), "Ampoule en cuivre oxydé");
		add(set.blockWaxed.get(), "Ampoule en cuivre ciré");
		add(set.exposedBlockWaxed.get(), "Ampoule en cuivre exposé ciré");
		add(set.weatheredBlockWaxed.get(), "Ampoule en cuivre érodé ciré");
		add(set.oxidizedBlockWaxed.get(), "Ampoule en cuivre oxydé ciré");
	}


}
