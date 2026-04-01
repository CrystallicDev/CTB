package ctbackport.datagen.server;


import com.natsu.backport.CTBackport;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CTBBlockTags extends BlockTagsProvider {

    public CTBBlockTags(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, CTBackport.MODID, helper);
    }

    @Override
    protected void addTags() {
    	
    }
}