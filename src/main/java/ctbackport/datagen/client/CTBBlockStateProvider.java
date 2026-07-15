package ctbackport.datagen.client;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CopperBulbBlock;
import com.natsu.backport.common.block.GrowableMossLayerBlock;
import com.natsu.backport.common.block.HangingMossBlock;
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
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

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
		handleCopperDoorSet(CTBBlocks.COPPER_DOOR);
		handleCopperTrapdoorSet(CTBBlocks.COPPER_TRAPDOOR);
		handleCopperSet(CTBBlocks.COPPER_GRATE);
		handleCopperBulbSet(CTBBlocks.COPPER_BULB);

		simpleBlock(CTBBlocks.PALE_OAK_SAPLING.get(),
				models().cross("pale_oak_sapling", modLoc("block/pale_oak_sapling")));
		eyeblossom(CTBBlocks.CLOSED_EYEBLOSSOM.get(), "closed_eyeblossom");
		eyeblossom(CTBBlocks.OPEN_EYEBLOSSOM.get(), "open_eyeblossom");
		hangingMoss(CTBBlocks.PALE_HANGING_MOSS);


		//simpleBlock(CTBBlocks.COPPER_BULB.get());
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

	@Override
	public void handleCopperSet(WeatherableCopperSet<?, ?> set) {
		simpleBlock(set.block.get());
		simpleBlock(set.exposedBlock.get());
		simpleBlock(set.weatheredBlock.get());
		simpleBlock(set.oxidizedBlock.get());

		simpleBlock(set.blockWaxed.get(), models().getExistingFile(modLoc("block/" + set.name)));
		simpleBlock(set.exposedBlockWaxed.get(), models().getExistingFile(modLoc("block/exposed_" + set.name)));
		simpleBlock(set.weatheredBlockWaxed.get(), models().getExistingFile(modLoc("block/weathered_" + set.name)));
		simpleBlock(set.oxidizedBlockWaxed.get(), models().getExistingFile(modLoc("block/oxidized_" + set.name)));
	}

	@Override
	public void handleCopperDoorSet(WeatherableCopperSet<?, ?> set) {
		doorBlock((DoorBlock) set.block.get(), modLoc("block/" + set.name + "_bottom"),
				modLoc("block/" + set.name + "_top"));
		doorBlock((DoorBlock) set.exposedBlock.get(), modLoc("block/exposed_" + set.name + "_bottom"),
				modLoc("block/exposed_" + set.name + "_top"));
		doorBlock((DoorBlock) set.weatheredBlock.get(), modLoc("block/weathered_" + set.name + "_bottom"),
				modLoc("block/weathered_" + set.name + "_top"));
		doorBlock((DoorBlock) set.oxidizedBlock.get(), modLoc("block/oxidized_" + set.name + "_bottom"),
				modLoc("block/oxidized_" + set.name + "_top"));

		doorBlock((DoorBlock) set.blockWaxed.get(), modLoc("block/" + set.name + "_bottom"),
				modLoc("block/" + set.name + "_top"));
		doorBlock((DoorBlock) set.exposedBlockWaxed.get(), modLoc("block/exposed_" + set.name + "_bottom"),
				modLoc("block/exposed_" + set.name + "_top"));
		doorBlock((DoorBlock) set.weatheredBlockWaxed.get(), modLoc("block/weathered_" + set.name + "_bottom"),
				modLoc("block/weathered_" + set.name + "_top"));
		doorBlock((DoorBlock) set.oxidizedBlockWaxed.get(), modLoc("block/oxidized_" + set.name + "_bottom"),
				modLoc("block/oxidized_" + set.name + "_top"));

	}

	@Override
	public void handleCopperTrapdoorSet(WeatherableCopperSet<?, ?> set) {
		trapdoorBlock((TrapDoorBlock) set.block.get(), modLoc("block/" + set.name), true);
		trapdoorBlock((TrapDoorBlock) set.exposedBlock.get(), modLoc("block/exposed_" + set.name), true);
		trapdoorBlock((TrapDoorBlock) set.weatheredBlock.get(), modLoc("block/weathered_" + set.name), true);
		trapdoorBlock((TrapDoorBlock) set.oxidizedBlock.get(), modLoc("block/oxidized_" + set.name), true);

		trapdoorBlock((TrapDoorBlock) set.blockWaxed.get(), modLoc("block/" + set.name), true);
		trapdoorBlock((TrapDoorBlock) set.exposedBlockWaxed.get(), modLoc("block/exposed_" + set.name), true);
		trapdoorBlock((TrapDoorBlock) set.weatheredBlockWaxed.get(), modLoc("block/weathered_" + set.name), true);
		trapdoorBlock((TrapDoorBlock) set.oxidizedBlockWaxed.get(), modLoc("block/oxidized_" + set.name), true);
	}

	@Override
	public void handleCopperBulbSet(WeatherableCopperSet<?, ?> set) {
		copperBulb(set.block.get(), set.name);
		copperBulb(set.exposedBlock.get(), "exposed_" + set.name);
		copperBulb(set.weatheredBlock.get(), "weathered_" + set.name);
		copperBulb(set.oxidizedBlock.get(), "oxidized_" + set.name);

		copperBulb(set.blockWaxed.get(), set.name);
		copperBulb(set.exposedBlockWaxed.get(), "exposed_" + set.name);
		copperBulb(set.weatheredBlockWaxed.get(), "weathered_" + set.name);
		copperBulb(set.oxidizedBlockWaxed.get(), "oxidized_" + set.name);
	}

	// four models : base, lit, powered, lit_powered
	private void copperBulb(Block block, String name) {
		VariantBlockStateBuilder builder = getVariantBuilder(block);
		for (boolean lit : new boolean[]{false, true}) {
			for (boolean powered : new boolean[]{false, true}) {
				String suffix = (lit ? "_lit" : "") + (powered ? "_powered" : "");
				ModelFile model = models().cubeAll(name + suffix, modLoc("block/" + name + suffix));
				builder.partialState()
					.with(CopperBulbBlock.LIT, lit)
					.with(CopperBulbBlock.POWERED, powered)
					.modelForState().modelFile(model).addModel();
			}
		}
	}

	protected void eyeblossom(Block block, String name) {
		ModelFile model = models().cross(name, modLoc("block/"+name));
		simpleBlock(block, model);
	}

	protected void woodBlock(RotatedPillarBlock block, ResourceLocation texture) {
	    axisBlock(block, texture, texture);
	}

	private void hangingMoss(RegistryObject<Block> block) {
	    Block b = block.get();

	    ModelFile base = models().getExistingFile(modLoc("block/pale_hanging_moss"));
	    ModelFile tip = models().getExistingFile(modLoc("block/pale_hanging_moss_tip"));

	    getVariantBuilder(b)
	        .partialState().with(HangingMossBlock.TIP, false).addModels(new ConfiguredModel(base))
	        .partialState().with(HangingMossBlock.TIP, true).addModels(new ConfiguredModel(tip));
	}


}