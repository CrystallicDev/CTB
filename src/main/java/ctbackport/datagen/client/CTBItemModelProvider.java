package ctbackport.datagen.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CTBItemModelProvider extends ItemModelProvider implements DataGenBlockItemHandler {

	public CTBItemModelProvider(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, CTBackport.MODID, helper);
    }

	@Override
	protected void registerModels() {
		handleWoodSet(CTBBlocks.CHERRY_WOOD);
		handleWoodSet(CTBBlocks.BAMBOO_WOOD);
		handleWoodSet(CTBBlocks.PALE_OAK_WOOD);
		handleMossSet(CTBBlocks.PALE_MOSS);
		handleLeavesSet(CTBBlocks.PALE_OAK_LEAVES);
		handleResinSet(CTBBlocks.RESIN);
		handleCopperDoorSet(CTBBlocks.COPPER_DOOR);
		handleCopperTrapdoorSet(CTBBlocks.COPPER_TRAPDOOR);
		handleCopperSet(CTBBlocks.COPPER_GRATE);
		handleCopperBulbSet(CTBBlocks.COPPER_BULB);

		// item names, not block model names (eyeblossom_open != open_eyeblossom)
		withExistingParent(CTBBlocks.OPEN_EYEBLOSSOM.getId().getPath(), "item/generated").texture("layer0", CTBackport.MODID+":block/open_eyeblossom");
		withExistingParent(CTBBlocks.CLOSED_EYEBLOSSOM.getId().getPath(), "item/generated").texture("layer0", CTBackport.MODID+":block/closed_eyeblossom");

		singleTexture(CTBItems.WIND_CHARGE.get().getRegistryName().getPath(), new ResourceLocation("item/generated"), "layer0",
				modLoc("item/wind_charge"));

		singleTexture(CTBItems.BREEZE_ROD.get().getRegistryName().getPath(), new ResourceLocation("item/generated"), "layer0",
				modLoc("item/breeze_rod"));

		withExistingParent(CTBBlocks.PALE_OAK_SAPLING.getId().getPath(), "item/generated")
				.texture("layer0", modLoc("block/pale_oak_sapling"));

		withExistingParent(CTBBlocks.BAMBOO_MOSAIC.getId().getPath(), modLoc("block/" + CTBBlocks.BAMBOO_MOSAIC.getId().getPath()));

		withExistingParent(CTBBlocks.BAMBOO_MOSAIC_SLAB.getId().getPath(), modLoc("block/" + CTBBlocks.BAMBOO_MOSAIC_SLAB.getId().getPath()));

		withExistingParent(CTBBlocks.BAMBOO_MOSAIC_STAIRS.getId().getPath(), modLoc("block/" + CTBBlocks.BAMBOO_MOSAIC_STAIRS.getId().getPath()));

		handleCopperSet(CTBBlocks.CHISELED_COPPER);

		for (String n : new String[]{"chiseled_tuff", "polished_tuff", "polished_tuff_stairs", "polished_tuff_slab",
				"tuff_bricks", "tuff_brick_stairs", "tuff_brick_slab", "chiseled_tuff_bricks",
				"tuff_stairs", "tuff_slab", "heavy_core"}) {
			withExistingParent(n, modLoc("block/" + n));
		}
		wallInventory("tuff_wall", mcLoc("block/tuff"));
		wallInventory("polished_tuff_wall", modLoc("block/polished_tuff"));
		wallInventory("tuff_brick_wall", modLoc("block/tuff_bricks"));

		withExistingParent(
	            "cherry_leaves",
	            modLoc("block/cherry_leaves")
	    );
	}

	@Override
	public void handleWoodSet(WoodSet wood) {

		withExistingParent(wood.log.getId().getPath(), modLoc("block/" + wood.log.getId().getPath()));

		withExistingParent(wood.strippedLog.getId().getPath(), modLoc("block/" + wood.strippedLog.getId().getPath()));

		withExistingParent(wood.wood.getId().getPath(), modLoc("block/" + wood.log.getId().getPath()));

		withExistingParent(wood.strippedWood.getId().getPath(), modLoc("block/" + wood.strippedLog.getId().getPath()));

		withExistingParent(wood.planks.getId().getPath(), modLoc("block/" + wood.planks.getId().getPath()));

		withExistingParent(wood.slab.getId().getPath(), modLoc("block/" + wood.slab.getId().getPath()));

		withExistingParent(wood.stairs.getId().getPath(), modLoc("block/" + wood.stairs.getId().getPath()));

		withExistingParent(wood.fence.getId().getPath(), mcLoc("block/fence_inventory")).texture("texture",
				modLoc("block/" + wood.planks.getId().getPath()));

		withExistingParent(wood.fenceGate.getId().getPath(), mcLoc("block/template_fence_gate")).texture("texture",
				modLoc("block/" + wood.planks.getId().getPath()));

		singleTexture(wood.door.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/" + wood.door.getId().getPath() + "_bottom"));

		withExistingParent(wood.trapdoor.getId().getPath(),
				modLoc("block/" + wood.trapdoor.getId().getPath() + "_bottom"));

		withExistingParent(wood.button.getId().getPath(), mcLoc("block/button_inventory")).texture("texture",
				modLoc("block/" + wood.planks.getId().getPath()));

		withExistingParent(wood.pressurePlate.getId().getPath(), mcLoc("block/pressure_plate_up")).texture("texture",
				modLoc("block/" + wood.planks.getId().getPath()));

		basicItem(wood.signItem.get());
	}

	@Override
	public void handleMossSet(MossSet set) {
		String name = set.name;
	    withExistingParent(
	            name + "_moss",
	            modLoc("block/" + name + "_moss")
	    );
	    withExistingParent(
	            name + "_moss_layer",
	            modLoc("block/" + name + "_moss_layer_1")
	    );
	}

	@Override
	public void handleLeavesSet(LeavesSet set) {
		String name = set.name;
	    withExistingParent(
	            name + "_leaves",
	            modLoc("block/" + name + "_leaves")
	    );
	}

	@Override
	public void handleStoneDecorationSet(StoneDecorationSet set) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDirtDecorationSet(DirtDecorationSet set) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleResinSet(ResinSet set) {
		singleTexture(set.resinItem.get().getRegistryName().getPath(), new ResourceLocation("item/generated"), "layer0",
				modLoc("item/"+set.name+"_clump"));
		singleTexture(set.resinBrick.get().getRegistryName().getPath(), new ResourceLocation("item/generated"), "layer0",
				modLoc("item/"+set.name+"_brick"));

		withExistingParent(set.block.getId().getPath(),
	            modLoc("block/" + set.getName()));
		withExistingParent(set.brick.getId().getPath(),
	            modLoc("block/"+set.getName()+"_bricks"));
		withExistingParent(set.brickStairs.getId().getPath(),
	            modLoc("block/" + set.getName()+"_bricks_stairs"));
		withExistingParent(set.brickSlab.getId().getPath(),
	            modLoc("block/" + set.getName()+"_bricks_slab"));
	    wallInventory(set.name + "_bricks_wall",
	    		modLoc("block/" + set.getName()+"_bricks"));
		withExistingParent(set.chiseledBrick.getId().getPath(),
	            modLoc("block/chiseled_" + set.getName() + "_bricks"));
	}

	@Override
	public void handleCopperSet(WeatherableCopperSet<?, ?> set) {
		withExistingParent(set.block.getId().getPath(), mcLoc("block/cube_all")).texture("all",
				modLoc("block/" + set.name));
		withExistingParent(set.exposedBlock.getId().getPath(), mcLoc("block/cube_all")).texture("all",
				modLoc("block/exposed_" + set.name));
		withExistingParent(set.weatheredBlock.getId().getPath(), mcLoc("block/cube_all")).texture("all",
				modLoc("block/weathered_" + set.name));
		withExistingParent(set.oxidizedBlock.getId().getPath(), mcLoc("block/cube_all")).texture("all",
				modLoc("block/oxidized_" + set.name));

		withExistingParent(set.blockWaxed.getId().getPath(), mcLoc("block/cube_all")).texture("all",
				modLoc("block/" + set.name));
		withExistingParent(set.exposedBlockWaxed.getId().getPath(), mcLoc("block/cube_all")).texture("all",
				modLoc("block/exposed_" + set.name));
		withExistingParent(set.weatheredBlockWaxed.getId().getPath(), mcLoc("block/cube_all")).texture("all",
				modLoc("block/weathered_" + set.name));
		withExistingParent(set.oxidizedBlockWaxed.getId().getPath(), mcLoc("block/cube_all")).texture("all",
				modLoc("block/oxidized_" + set.name));
	}

	@Override
	public void handleCopperDoorSet(WeatherableCopperSet<?, ?> set) {
		singleTexture(set.block.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/" + set.name + "_bottom"));
		singleTexture(set.exposedBlock.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/exposed_" + set.name + "_bottom"));
		singleTexture(set.weatheredBlock.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/weathered_" + set.name + "_bottom"));
		singleTexture(set.oxidizedBlock.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/oxidized_" + set.name + "_bottom"));

		singleTexture(set.blockWaxed.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/" + set.name + "_bottom"));
		singleTexture(set.exposedBlockWaxed.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/exposed_" + set.name + "_bottom"));
		singleTexture(set.weatheredBlockWaxed.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/weathered_" + set.name + "_bottom"));
		singleTexture(set.oxidizedBlockWaxed.getId().getPath(), mcLoc("item/generated"), "layer0",
				modLoc("block/oxidized_" + set.name + "_bottom"));
	}

	@Override
	public void handleCopperTrapdoorSet(WeatherableCopperSet<?, ?> set) {
		withExistingParent(set.block.getId().getPath(), modLoc("block/" + set.name + "_bottom"));
		withExistingParent(set.exposedBlock.getId().getPath(), modLoc("block/exposed_" + set.name + "_bottom"));
		withExistingParent(set.weatheredBlock.getId().getPath(), modLoc("block/weathered_" + set.name + "_bottom"));
		withExistingParent(set.oxidizedBlock.getId().getPath(), modLoc("block/oxidized_" + set.name + "_bottom"));

		withExistingParent(set.blockWaxed.getId().getPath(), modLoc("block/" + set.name + "_bottom"));
		withExistingParent(set.exposedBlockWaxed.getId().getPath(), modLoc("block/exposed_" + set.name + "_bottom"));
		withExistingParent(set.weatheredBlockWaxed.getId().getPath(),
				modLoc("block/weathered_" + set.name + "_bottom"));
		withExistingParent(set.oxidizedBlockWaxed.getId().getPath(), modLoc("block/oxidized_" + set.name + "_bottom"));
	}

	@Override
	public void handleCopperBulbSet(WeatherableCopperSet<?, ?> set) {
		withExistingParent(set.block.getId().getPath(), modLoc("block/" + set.name));
		withExistingParent(set.exposedBlock.getId().getPath(), modLoc("block/exposed_" + set.name));
		withExistingParent(set.weatheredBlock.getId().getPath(), modLoc("block/weathered_" + set.name));
		withExistingParent(set.oxidizedBlock.getId().getPath(), modLoc("block/oxidized_" + set.name));

		withExistingParent(set.blockWaxed.getId().getPath(), modLoc("block/" + set.name));
		withExistingParent(set.exposedBlockWaxed.getId().getPath(), modLoc("block/exposed_" + set.name));
		withExistingParent(set.weatheredBlockWaxed.getId().getPath(), modLoc("block/weathered_" + set.name));
		withExistingParent(set.oxidizedBlockWaxed.getId().getPath(), modLoc("block/oxidized_" + set.name));
	}



}
