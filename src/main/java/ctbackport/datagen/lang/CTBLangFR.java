package ctbackport.datagen.lang;

import java.util.HashMap;
import java.util.Map;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
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
		handleLeavesSet(CTBBlocks.PALE_OAK_LEAVES);
		handleResinSet(CTBBlocks.RESIN);
		handleCopperBulbSet(CTBBlocks.COPPER_BULB);
		handleCopperSet(CTBBlocks.COPPER_GRATE);
		handleCopperSet(CTBBlocks.COPPER_DOOR);
		handleCopperSet(CTBBlocks.COPPER_TRAPDOOR);
		handleCopperSet(CTBBlocks.CHISELED_COPPER);

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
			add(set.mossLayer.get(), "Couche de mousse "+name);
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
			"chiseled_copper", new String[]{"Cuivre sculpté", ""});

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
