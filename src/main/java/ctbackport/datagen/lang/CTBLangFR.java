package ctbackport.datagen.lang;

import java.util.HashMap;
import java.util.Map;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
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


		add(CTBBlocks.CHERRY_LEAVES.get(), "Feuilles de cerisier");
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

	@Override
	public void handleCopperSet(WeatherableCopperSet<?, ?> set) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCopperDoorSet(WeatherableCopperSet<?, ?> set) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCopperTrapdoorSet(WeatherableCopperSet<?, ?> set) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCopperBulbSet(WeatherableCopperSet<?, ?> set) {
		// TODO Auto-generated method stub

	}


}
