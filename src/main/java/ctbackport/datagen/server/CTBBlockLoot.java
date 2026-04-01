package ctbackport.datagen.server;


import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlockLoot extends BlockLoot {

	@Override
	public void addTables() {
		
	}

	@Override
    protected Iterable<Block> getKnownBlocks() {
        return CTBBlocks.BLOCKS.getEntries()
            .stream()
            .map(RegistryObject::get)
            .toList();
    }
	
}
