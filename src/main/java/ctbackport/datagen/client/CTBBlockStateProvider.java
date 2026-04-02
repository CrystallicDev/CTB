package ctbackport.datagen.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.GrowableMossLayerBlock;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.utils.sets.DirtDecorationSet;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.StoneDecorationSet;
import com.natsu.backport.utils.sets.WoodSet;

import ctbackport.datagen.DataGenBlockItemHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CTBBlockStateProvider extends BlockStateProvider implements DataGenBlockItemHandler {

	public CTBBlockStateProvider(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, CTBackport.MODID, helper);
    }

	@Override
	protected void registerStatesAndModels() {
		handleWoodSet(CTBBlocks.CHERRY_WOOD);
		handleWoodSet(CTBBlocks.BAMBOO_WOOD);
		handleWoodSet(CTBBlocks.PALE_OAK_WOOD);
		handleMossSet(CTBBlocks.PALE_MOSS);
		
		simpleBlock(CTBBlocks.BAMBOO_MOSAIC.get());

        slabBlock(
            (SlabBlock) CTBBlocks.BAMBOO_MOSAIC_SLAB.get(),
            blockTexture(CTBBlocks.BAMBOO_MOSAIC.get()),
            blockTexture(CTBBlocks.BAMBOO_MOSAIC.get())
        );

        stairsBlock(
            (StairBlock) CTBBlocks.BAMBOO_MOSAIC_STAIRS.get(),
            blockTexture(CTBBlocks.BAMBOO_MOSAIC.get())
        );
	}

	@Override
	public void handleWoodSet(WoodSet wood) {
		logBlock((RotatedPillarBlock) wood.log.get());
        logBlock((RotatedPillarBlock) wood.strippedLog.get());

		woodBlock((RotatedPillarBlock) wood.wood.get(), blockTexture(wood.log.get()));

		woodBlock((RotatedPillarBlock) wood.strippedWood.get(), blockTexture(wood.strippedLog.get()));

        simpleBlock(wood.planks.get());

        slabBlock(
            (SlabBlock) wood.slab.get(),
            blockTexture(wood.planks.get()),
            blockTexture(wood.planks.get())
        );

        stairsBlock(
            (StairBlock) wood.stairs.get(),
            blockTexture(wood.planks.get())
        );

        fenceBlock(
            (FenceBlock) wood.fence.get(),
            blockTexture(wood.planks.get())
        );

        fenceGateBlock(
            (FenceGateBlock) wood.fenceGate.get(),
            blockTexture(wood.planks.get())
        );

        doorBlock(
            (DoorBlock) wood.door.get(),
            modLoc("block/" + wood.name + "_door_bottom"),
            modLoc("block/" + wood.name + "_door_top")
        );

        trapdoorBlock(
            (TrapDoorBlock) wood.trapdoor.get(),
            modLoc("block/" + wood.name + "_trapdoor"),
            true
        );

        buttonBlock((ButtonBlock) wood.button.get(),
            blockTexture(wood.planks.get()));


        pressurePlateBlock((PressurePlateBlock) wood.pressurePlate.get(),
            blockTexture(wood.planks.get()));

        signBlock(wood.standingsign.get(), wood.wallsign.get(), modLoc("entity/signs/"+wood.name));
	}
	
	@Override
	public void handleMossSet(MossSet set) {
		simpleBlock(set.moss.get());
		
		String name = set.name;
		ResourceLocation mossTexture = modLoc("block/" + name +"_moss");
		
		VariantBlockStateBuilder layerBuilder = getVariantBuilder(set.mossLayer.get());

	    for (int layer = 1; layer <= 8; layer++) {
	        final int l = layer;

	        ModelFile layerModel = models().withExistingParent(
	                name + "_moss_layer_" + l,
	                modLoc("block/default_layer_" + l)) 
	                .texture("all", mossTexture)
	                .texture("particle", mossTexture);

	        layerBuilder
            .partialState()
                .with(GrowableMossLayerBlock.LAYERS, l)
                .modelForState().modelFile(layerModel).addModel();
	    }
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

	
	
	
	
	
	protected void woodBlock(RotatedPillarBlock block, ResourceLocation texture) {
	    axisBlock(block, texture, texture);
	}
}