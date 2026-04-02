package ctbackport.datagen.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.utils.sets.DirtDecorationSet;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.StoneDecorationSet;
import com.natsu.backport.utils.sets.WoodSet;

import ctbackport.datagen.DataGenBlockItemHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
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
		handleLeavesSet(CTBBlocks.CHERRY_LEAVES);
		handleLeavesSet(CTBBlocks.PALE_OAK_LEAVES);
		
		
		
		singleTexture(CTBItems.WIND_CHARGE.get().getRegistryName().getPath(), new ResourceLocation("item/generated"), "layer0",
				modLoc("item/wind_charge"));
		
		withExistingParent(CTBBlocks.BAMBOO_MOSAIC.getId().getPath(), modLoc("block/" + CTBBlocks.BAMBOO_MOSAIC.getId().getPath()));

		withExistingParent(CTBBlocks.BAMBOO_MOSAIC_SLAB.getId().getPath(), modLoc("block/" + CTBBlocks.BAMBOO_MOSAIC_SLAB.getId().getPath()));

		withExistingParent(CTBBlocks.BAMBOO_MOSAIC_STAIRS.getId().getPath(), modLoc("block/" + CTBBlocks.BAMBOO_MOSAIC_STAIRS.getId().getPath()));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleStoneDecorationSet(StoneDecorationSet set) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleDirtDecorationSet(DirtDecorationSet set) {
		// TODO Auto-generated method stub
		
	}


}
