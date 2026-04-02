package ctbackport.datagen.server;


import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.utils.sets.DirtDecorationSet;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.StoneDecorationSet;
import com.natsu.backport.utils.sets.WoodSet;

import ctbackport.datagen.DataGenBlockItemHandler;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlockLoot extends BlockLoot implements DataGenBlockItemHandler {

	@Override
	public void addTables() {
		handleWoodSet(CTBBlocks.CHERRY_WOOD);
		handleWoodSet(CTBBlocks.BAMBOO_WOOD);
		handleWoodSet(CTBBlocks.PALE_OAK_WOOD);
		handleMossSet(CTBBlocks.PALE_MOSS);
		
		dropSelf(CTBBlocks.BAMBOO_MOSAIC.get());
		dropSelf(CTBBlocks.BAMBOO_MOSAIC_STAIRS.get());
		add(CTBBlocks.BAMBOO_MOSAIC_SLAB.get(), createSlabItemTable(CTBBlocks.BAMBOO_MOSAIC_SLAB.get()));
	}

	@Override
    protected Iterable<Block> getKnownBlocks() {
        return CTBBlocks.BLOCKS.getEntries()
            .stream()
            .map(RegistryObject::get)
            .toList();
    }
	
	@Override
	public void handleWoodSet(WoodSet set) {
		dropSelf(set.log.get());
        dropSelf(set.strippedLog.get());
		dropSelf(set.wood.get());
        dropSelf(set.strippedWood.get());
        dropSelf(set.planks.get());
        add(set.slab.get(), createSlabItemTable(set.slab.get()));
        dropSelf(set.stairs.get());
        dropSelf(set.fence.get());
        dropSelf(set.fenceGate.get());
        dropSelf(set.door.get());
        dropSelf(set.trapdoor.get());
        dropSelf(set.button.get());
        dropSelf(set.pressurePlate.get());
        dropSelf(set.standingsign.get());
	}

	@Override
	public void handleMossSet(MossSet set) {
		dropSelf(set.moss.get());
		add(set.mossLayer.get(), noDrop());
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
