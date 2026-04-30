package ctbackport.datagen.server;


import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.utils.sets.DirtDecorationSet;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.ResinSet;
import com.natsu.backport.utils.sets.StoneDecorationSet;
import com.natsu.backport.utils.sets.WeatherableCopperSet;
import com.natsu.backport.utils.sets.WoodSet;

import ctbackport.datagen.DataGenBlockItemHandler;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlockLoot extends BlockLoot implements DataGenBlockItemHandler {

	protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item()
			.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
	protected static final LootItemCondition.Builder HAS_SHEARS =
	        MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
	protected static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();

	@Override
	public void addTables() {
		handleWoodSet(CTBBlocks.CHERRY_WOOD);
		handleWoodSet(CTBBlocks.BAMBOO_WOOD);
		handleWoodSet(CTBBlocks.PALE_OAK_WOOD);
		handleMossSet(CTBBlocks.PALE_MOSS);
		handleLeavesSet(CTBBlocks.PALE_OAK_LEAVES);
		handleResinSet(CTBBlocks.RESIN);
		handleCopperSet(CTBBlocks.COPPER_DOOR);
		handleCopperSet(CTBBlocks.COPPER_TRAPDOOR);
		handleCopperSet(CTBBlocks.COPPER_GRATE);

		add(CTBBlocks.CLOSED_EYEBLOSSOM.get(), createSimpleDrop(CTBBlocks.CLOSED_EYEBLOSSOM.get()));
		add(CTBBlocks.OPEN_EYEBLOSSOM.get(), createSimpleDrop(CTBBlocks.CLOSED_EYEBLOSSOM.get()));
		dropSelf(CTBBlocks.PALE_HANGING_MOSS.get());
		dropSelf(CTBBlocks.BAMBOO_MOSAIC.get());
		dropSelf(CTBBlocks.BAMBOO_MOSAIC_STAIRS.get());
		dropSelf(CTBBlocks.CREAKING_HEART.get());
		add(CTBBlocks.BAMBOO_MOSAIC_SLAB.get(), createSlabItemTable(CTBBlocks.BAMBOO_MOSAIC_SLAB.get()));

		add(CTBBlocks.CHERRY_LEAVES.get(), block -> createLeavesDrops(block, Items.OAK_SAPLING));
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
	    add(set.leaves.get(), block -> createLeavesDrops(block, set.saplingItem));

	}

	@Override
	public void handleStoneDecorationSet(StoneDecorationSet set) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDirtDecorationSet(DirtDecorationSet set) {
		// TODO Auto-generated method stub

	}

	private LootTable.Builder createSimpleDrop(ItemLike block){
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(block)));
	}

	private LootTable.Builder createLeavesDrops(Block leavesBlock, Item saplingItem) {
		LootItemCondition.Builder silkOrShears =
	            HAS_SILK_TOUCH.or(HAS_SHEARS);

	    return LootTable.lootTable()
	            .withPool(LootPool.lootPool()
	                    .setRolls(ConstantValue.exactly(1))
	                    .add(LootItem.lootTableItem(leavesBlock)
	                            .when(silkOrShears))
	                    .add(LootItem.lootTableItem(saplingItem)
	                            .when(HAS_NO_SILK_TOUCH)
	                            .when(BonusLevelTableCondition.bonusLevelFlatChance(
	                                    Enchantments.BLOCK_FORTUNE,
	                                    0.05f,
	                                    0.0625f,
	                                    0.083f,
	                                    0.1f     // fortune 3
	                            )))
	            );
	}

	@Override
	public void handleResinSet(ResinSet set) {
		dropSelf(set.block.get());
		dropSelf(set.brick.get());
		dropSelf(set.brickSlab.get());
		dropSelf(set.brickStairs.get());
		dropSelf(set.brickWalls.get());
		dropSelf(set.chiseledBrick.get());
	}

	@Override
	public void handleCopperSet(WeatherableCopperSet<?, ?> set) {
		dropSelf(set.block.get());
		dropSelf(set.blockWaxed.get());
		dropSelf(set.exposedBlock.get());
		dropSelf(set.exposedBlockWaxed.get());
		dropSelf(set.weatheredBlock.get());
		dropSelf(set.weatheredBlockWaxed.get());
		dropSelf(set.oxidizedBlock.get());
		dropSelf(set.oxidizedBlockWaxed.get());
	}

	@Override
	public void handleCopperDoorSet(WeatherableCopperSet<?, ?> set) {
		dropSelf(set.block.get());
		dropSelf(set.blockWaxed.get());
		dropSelf(set.exposedBlock.get());
		dropSelf(set.exposedBlockWaxed.get());
		dropSelf(set.weatheredBlock.get());
		dropSelf(set.weatheredBlockWaxed.get());
		dropSelf(set.oxidizedBlock.get());
		dropSelf(set.oxidizedBlockWaxed.get());
	}

	@Override
	public void handleCopperTrapdoorSet(WeatherableCopperSet<?, ?> set) {
		dropSelf(set.block.get());
		dropSelf(set.blockWaxed.get());
		dropSelf(set.exposedBlock.get());
		dropSelf(set.exposedBlockWaxed.get());
		dropSelf(set.weatheredBlock.get());
		dropSelf(set.weatheredBlockWaxed.get());
		dropSelf(set.oxidizedBlock.get());
		dropSelf(set.oxidizedBlockWaxed.get());
	}

	@Override
	public void handleCopperBulbSet(WeatherableCopperSet<?, ?> set) {
		dropSelf(set.block.get());
		dropSelf(set.blockWaxed.get());
		dropSelf(set.exposedBlock.get());
		dropSelf(set.exposedBlockWaxed.get());
		dropSelf(set.weatheredBlock.get());
		dropSelf(set.weatheredBlockWaxed.get());
		dropSelf(set.oxidizedBlock.get());
		dropSelf(set.oxidizedBlockWaxed.get());
	}

}
