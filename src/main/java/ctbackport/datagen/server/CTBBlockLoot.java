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
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
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
		handleCopperBulbSet(CTBBlocks.COPPER_BULB);

		add(CTBBlocks.CLOSED_EYEBLOSSOM.get(), createSimpleDrop(CTBBlocks.CLOSED_EYEBLOSSOM.get()));
		add(CTBBlocks.OPEN_EYEBLOSSOM.get(), createSimpleDrop(CTBBlocks.CLOSED_EYEBLOSSOM.get()));
		dropSelf(CTBBlocks.PALE_HANGING_MOSS.get());
		dropSelf(CTBBlocks.PALE_OAK_SAPLING.get());
		dropSelf(CTBBlocks.HEAVY_CORE.get());
		// vanilla : neither drops anything, even with silk touch
		add(CTBBlocks.TRIAL_SPAWNER.get(), noDrop());
		add(CTBBlocks.VAULT.get(), noDrop());
		handleCopperSet(CTBBlocks.CHISELED_COPPER);
		for (net.minecraftforge.registries.RegistryObject<Block> b : java.util.List.of(
				CTBBlocks.TUFF_STAIRS, CTBBlocks.TUFF_WALL, CTBBlocks.CHISELED_TUFF, CTBBlocks.POLISHED_TUFF,
				CTBBlocks.POLISHED_TUFF_STAIRS, CTBBlocks.POLISHED_TUFF_WALL, CTBBlocks.TUFF_BRICKS,
				CTBBlocks.TUFF_BRICK_STAIRS, CTBBlocks.TUFF_BRICK_WALL, CTBBlocks.CHISELED_TUFF_BRICKS)) {
			dropSelf(b.get());
		}
		add(CTBBlocks.TUFF_SLAB.get(), createSlabItemTable(CTBBlocks.TUFF_SLAB.get()));
		add(CTBBlocks.POLISHED_TUFF_SLAB.get(), createSlabItemTable(CTBBlocks.POLISHED_TUFF_SLAB.get()));
		add(CTBBlocks.TUFF_BRICK_SLAB.get(), createSlabItemTable(CTBBlocks.TUFF_BRICK_SLAB.get()));
		dropSelf(CTBBlocks.BAMBOO_MOSAIC.get());
		dropSelf(CTBBlocks.BAMBOO_MOSAIC_STAIRS.get());
		// vanilla : itself with silk touch, otherwise 1-3 resin clumps (+fortune)
		add(CTBBlocks.CREAKING_HEART.get(), block -> createSilkTouchDispatchTable(block,
				applyExplosionDecay(block, LootItem.lootTableItem(CTBBlocks.RESIN.resinItem.get())
						.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
						.apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)))));
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
	    add(set.leaves.get(), block -> createLeavesDrops(block, set.saplingItem.get()));

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

	// one clump per face, same structure as vanilla's glow lichen drops but without shears
	private LootTable.Builder createResinClumpDrops(Block block) {
		LootPoolSingletonContainer.Builder<?> entry = LootItem.lootTableItem(block);
		for (Direction dir : Direction.values()) {
			entry.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F), true)
					.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
							.setProperties(StatePropertiesPredicate.Builder.properties()
									.hasProperty(PipeBlock.PROPERTY_BY_DIRECTION.get(dir), true))));
		}
		entry.apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1.0F), true));
		return LootTable.lootTable().withPool(LootPool.lootPool().add(applyExplosionDecay(block, entry)));
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
		add(set.clump.get(), this::createResinClumpDrops);
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
