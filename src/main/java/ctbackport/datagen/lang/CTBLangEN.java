package ctbackport.datagen.lang;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEffects;
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

		add(CTBBlocks.CHERRY_LEAVES.get(), "Cherry Leaves");
		add(CTBBlocks.CREAKING_HEART.get(), "Creaking Heart");
		add(CTBBlocks.OPEN_EYEBLOSSOM.get(), "Open Eyeblossom");
		add(CTBBlocks.CLOSED_EYEBLOSSOM.get(), "Closed Eyeblossom");
		add(CTBBlocks.PALE_HANGING_MOSS.get(), "Pale Hanging Moss");
		add(CTBBlocks.PALE_OAK_SAPLING.get(), "Pale Oak Sapling");
		add(CTBItems.WIND_CHARGE.get(), "Wind Charge");
		add(CTBItems.BREEZE_ROD.get(), "Breeze Rod");
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
