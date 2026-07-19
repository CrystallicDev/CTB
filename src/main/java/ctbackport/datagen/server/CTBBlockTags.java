package ctbackport.datagen.server;


import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.utils.sets.DefaultSet;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
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
		handle(CTBBlocks.PALE_OAK_LEAVES);
		handle(CTBBlocks.RESIN);
		handle(CTBBlocks.COPPER_DOOR);
		handle(CTBBlocks.COPPER_TRAPDOOR);
		handle(CTBBlocks.COPPER_GRATE);
		handle(CTBBlocks.COPPER_BULB);

		this.tag(BlockTags.LEAVES).add(CTBBlocks.CHERRY_LEAVES.get());
		this.tag(BlockTags.MINEABLE_WITH_HOE).add(CTBBlocks.CHERRY_LEAVES.get());
		this.tag(BlockTags.PARROTS_SPAWNABLE_ON).add(CTBBlocks.CHERRY_LEAVES.get());
		this.tag(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE).add(CTBBlocks.CHERRY_LEAVES.get());
		tag(BlockTags.MINEABLE_WITH_HOE).add(CTBBlocks.PALE_HANGING_MOSS.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.CREAKING_HEART.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.HEAVY_CORE.get());
		tag(BlockTags.SAPLINGS).add(CTBBlocks.PALE_OAK_SAPLING.get());
		tag(BlockTags.SMALL_FLOWERS).add(CTBBlocks.OPEN_EYEBLOSSOM.get(), CTBBlocks.CLOSED_EYEBLOSSOM.get());
    }

    public void handle(DefaultSet set) {
    	set.addBlockTags(
	            this::tag
	        );
    }
}