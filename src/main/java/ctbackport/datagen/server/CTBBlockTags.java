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
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.TRIAL_SPAWNER.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.VAULT.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.COPPER_CHEST.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(CTBBlocks.COPPER_CHEST.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.EXPOSED_COPPER_CHEST.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(CTBBlocks.EXPOSED_COPPER_CHEST.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.WEATHERED_COPPER_CHEST.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(CTBBlocks.WEATHERED_COPPER_CHEST.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.OXIDIZED_COPPER_CHEST.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(CTBBlocks.OXIDIZED_COPPER_CHEST.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.WAXED_COPPER_CHEST.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(CTBBlocks.WAXED_COPPER_CHEST.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.WAXED_EXPOSED_COPPER_CHEST.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(CTBBlocks.WAXED_EXPOSED_COPPER_CHEST.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.WAXED_WEATHERED_COPPER_CHEST.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(CTBBlocks.WAXED_WEATHERED_COPPER_CHEST.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.WAXED_OXIDIZED_COPPER_CHEST.get());
		tag(BlockTags.NEEDS_STONE_TOOL).add(CTBBlocks.WAXED_OXIDIZED_COPPER_CHEST.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CTBBlocks.COPPER_GOLEM_STATUE.get());
		handle(CTBBlocks.CHISELED_COPPER);
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.OAK_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.SPRUCE_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.BIRCH_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.JUNGLE_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.ACACIA_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.DARK_OAK_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.CRIMSON_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.WARPED_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.PALE_OAK_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.CHERRY_SHELF.get());
		tag(BlockTags.MINEABLE_WITH_AXE).add(CTBBlocks.BAMBOO_SHELF.get());
		handle(CTBBlocks.COPPER_BARS);
		handle(CTBBlocks.COPPER_CHAIN);
		handle(CTBBlocks.COPPER_LANTERN);
		for (net.minecraftforge.registries.RegistryObject<net.minecraft.world.level.block.Block> b : java.util.List.of(
				CTBBlocks.TUFF_STAIRS, CTBBlocks.TUFF_SLAB, CTBBlocks.TUFF_WALL, CTBBlocks.CHISELED_TUFF,
				CTBBlocks.POLISHED_TUFF, CTBBlocks.POLISHED_TUFF_STAIRS, CTBBlocks.POLISHED_TUFF_SLAB,
				CTBBlocks.POLISHED_TUFF_WALL, CTBBlocks.TUFF_BRICKS, CTBBlocks.TUFF_BRICK_STAIRS,
				CTBBlocks.TUFF_BRICK_SLAB, CTBBlocks.TUFF_BRICK_WALL, CTBBlocks.CHISELED_TUFF_BRICKS)) {
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get());
		}
		tag(BlockTags.WALLS).add(CTBBlocks.TUFF_WALL.get(), CTBBlocks.POLISHED_TUFF_WALL.get(), CTBBlocks.TUFF_BRICK_WALL.get());
		tag(BlockTags.SAPLINGS).add(CTBBlocks.PALE_OAK_SAPLING.get());
		tag(BlockTags.SMALL_FLOWERS).add(CTBBlocks.OPEN_EYEBLOSSOM.get(), CTBBlocks.CLOSED_EYEBLOSSOM.get());
    }

    public void handle(DefaultSet set) {
    	set.addBlockTags(
	            this::tag
	        );
    }
}