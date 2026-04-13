package ctbackport.datagen.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.GrowableMossLayerBlock;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.utils.sets.DirtDecorationSet;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.ResinSet;
import com.natsu.backport.utils.sets.StoneDecorationSet;
import com.natsu.backport.utils.sets.WoodSet;

import ctbackport.datagen.DataGenBlockItemHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
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
		handleLeavesSet(CTBBlocks.PALE_OAK_LEAVES);
		handleResinSet(CTBBlocks.RESIN);
		
		eyeblossom(CTBBlocks.CLOSED_EYEBLOSSOM.get(), "closed_eyeblossom");
		eyeblossom(CTBBlocks.OPEN_EYEBLOSSOM.get(), "open_eyeblossom");
		
		ResourceLocation leavesTexture = modLoc("block/cherry_leaves");
        ModelFile normalModel = models().withExistingParent("cherry_leaves",
                new ResourceLocation("minecraft", "block/leaves"))
                .texture("all", leavesTexture)
                .texture("particle", leavesTexture);
        simpleBlock(CTBBlocks.CHERRY_LEAVES.get(), normalModel);
		
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
		String name = set.name; 

	    ResourceLocation leavesTexture = modLoc("block/" + name + "_leaves");
        ModelFile normalModel = models().withExistingParent(name + "_leaves",
                new ResourceLocation("minecraft", "block/leaves"))
                .texture("all", leavesTexture)
                .texture("particle", leavesTexture);

        simpleBlock(set.leaves.get(), normalModel);
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
		simpleBlock(set.block.get());
		simpleBlock(set.brick.get());
		ResourceLocation brickTex = blockTexture(set.brick.get());
		stairsBlock((StairBlock) set.brickStairs.get(), brickTex);
		slabBlock((SlabBlock) set.brickSlab.get(), brickTex, brickTex);
		wallBlock((WallBlock) set.brickWalls.get(), brickTex);
		simpleBlock(set.chiseledBrick.get());
	}

	
	
	
	protected void eyeblossom(Block block, String name) {
		ModelFile model = models().cross(name, modLoc("block/"+name));
		simpleBlock(block, model);
	}
	
	protected void woodBlock(RotatedPillarBlock block, ResourceLocation texture) {
	    axisBlock(block, texture, texture);
	}
}