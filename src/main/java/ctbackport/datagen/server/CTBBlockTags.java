package ctbackport.datagen.server;


import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.utils.sets.DefaultSet;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CTBBlockTags extends BlockTagsProvider {

    public CTBBlockTags(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, CTBackport.MODID, helper);
    }

    @Override
    protected void addTags() {
    	handle(CTBBlocks.CHERRY_WOOD);
    	handle(CTBBlocks.BAMBOO_WOOD);
    	handle(CTBBlocks.PALE_OAK_WOOD);
		handle(CTBBlocks.PALE_MOSS);
    }
    
    public void handle(DefaultSet set) {
    	set.addBlockTags(
	            this::tag
	        );
    }
}