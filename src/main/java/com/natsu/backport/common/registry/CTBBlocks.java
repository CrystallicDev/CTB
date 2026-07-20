package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.common.block.CopperBulbBlock;
import com.natsu.backport.common.block.CopperDoorBlock;
import com.natsu.backport.common.block.HeavyCoreBlock;
import com.natsu.backport.common.block.WeatheringCopperBlock;
import com.natsu.backport.common.block.WeatheringCopperBulbBlock;
import com.natsu.backport.common.block.WeatheringCopperDoorBlock;
import com.natsu.backport.server.world.feature.tree.PaleOakTreeGrower;
import com.natsu.backport.common.block.WeatheringCopperTrapDoorBlock;
import com.natsu.backport.common.block.WeatheringTransparentBlock;
import com.natsu.backport.utils.sets.LeavesSet;
import com.natsu.backport.utils.sets.MossSet;
import com.natsu.backport.utils.sets.ResinSet;
import com.natsu.backport.utils.sets.WeatherableCopperSet;
import com.natsu.backport.utils.sets.WoodSet;
import com.natsu.salm.block.backport.WaterLoggedTransparentBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CTBackport.MODID);

	public static final WoodSet CHERRY_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "cherry", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final WoodSet BAMBOO_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "bamboo", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final WoodSet PALE_OAK_WOOD = new WoodSet(CTBBlockEntities.BLOCK_ENTITIES, CTBItems.ITEMS, BLOCKS, "pale_oak", 2, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Block> PALE_OAK_SAPLING = registerWithItem(BLOCKS, CTBItems.ITEMS, "pale_oak_sapling",
			CTBBlockFactory.makeSapling(BLOCKS, "pale_oak_sapling", new PaleOakTreeGrower()));
	public static final LeavesSet PALE_OAK_LEAVES = new LeavesSet("pale_oak", CTBItems.ITEMS, BLOCKS,
			() -> PALE_OAK_SAPLING.get().asItem(), CreativeModeTab.TAB_BUILDING_BLOCKS, CTBParticles.PALE_OAK_LEAVES);
	public static final ResinSet RESIN = new ResinSet(CTBItems.ITEMS, BLOCKS, "resin", 0, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final MossSet PALE_MOSS = new MossSet(CTBItems.ITEMS, BLOCKS, "pale", CreativeModeTab.TAB_BUILDING_BLOCKS);

	public static final RegistryObject<Block> CHERRY_LEAVES = registerWithItem(BLOCKS, CTBItems.ITEMS, "cherry_leaves", CTBBlockFactory.makeCherryLeaves(BLOCKS, "cherry_leaves", CTBParticles.CHERRY));

	// Specific Bamboo Blocks
	public static final RegistryObject<Block> BAMBOO_MOSAIC = registerWithItem(BLOCKS, CTBItems.ITEMS, "bamboo_mosaic", CTBBlockFactory.makePlanks(BLOCKS, "bamboo_mosaic", 2));
	public static final RegistryObject<Block> BAMBOO_MOSAIC_STAIRS = registerWithItem(BLOCKS, CTBItems.ITEMS, "bamboo_mosaic_stairs", CTBBlockFactory.makeStairs(BLOCKS, "bamboo_mosaic_stairs", BAMBOO_MOSAIC, 2));
	public static final RegistryObject<Block> BAMBOO_MOSAIC_SLAB = registerWithItem(BLOCKS, CTBItems.ITEMS, "bamboo_mosaic_slab", CTBBlockFactory.makeSlab(BLOCKS, "bamboo_mosaic_slab", BAMBOO_MOSAIC, 2));
	public static final RegistryObject<Block> CREAKING_HEART = registerWithItem(BLOCKS, CTBItems.ITEMS, "creaking_heart", CTBBlockFactory.makeCreakingHeart(BLOCKS, "creaking_heart"));

	//Eyeblossom blocks
	public static final RegistryObject<Block> OPEN_EYEBLOSSOM = registerWithItem(BLOCKS, CTBItems.ITEMS, "eyeblossom_open", CTBBlockFactory.makeEyeblossom(BLOCKS, "eyeblossom_open", true));
	public static final RegistryObject<Block> CLOSED_EYEBLOSSOM = registerWithItem(BLOCKS, CTBItems.ITEMS, "eyeblossom_close", CTBBlockFactory.makeEyeblossom(BLOCKS, "eyeblossom_close", false));

	// Pale Hanging Moss
	public static final RegistryObject<Block> PALE_HANGING_MOSS = registerWithItem(BLOCKS, CTBItems.ITEMS, "pale_hanging_moss", CTBBlockFactory.makePaleHangingMoss(BLOCKS, "pale_hanging_moss"));

	//Copper Weatherable blocks
	public static final WeatherableCopperSet<WeatheringTransparentBlock, WaterLoggedTransparentBlock> COPPER_GRATE = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_grate", 3, WeatheringTransparentBlock.class, WaterLoggedTransparentBlock.class);
	public static final WeatherableCopperSet<WeatheringCopperDoorBlock, CopperDoorBlock> COPPER_DOOR = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_door", 3, WeatheringCopperDoorBlock.class, CopperDoorBlock.class);
	public static final WeatherableCopperSet<WeatheringCopperTrapDoorBlock, TrapDoorBlock> COPPER_TRAPDOOR = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_trapdoor", 3, WeatheringCopperTrapDoorBlock.class, TrapDoorBlock.class);
	// lit light drops with the oxidation, like vanilla
	public static final WeatherableCopperSet<WeatheringCopperBulbBlock, CopperBulbBlock> COPPER_BULB = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_bulb", 3, WeatheringCopperBulbBlock.class, CopperBulbBlock.class, new int[]{15, 12, 8, 4});

	// Tuff family, the base tuff block is already vanilla
	public static final RegistryObject<Block> TUFF_STAIRS = registerWithItem(BLOCKS, CTBItems.ITEMS, "tuff_stairs", CTBBlockFactory.makeStairsOf(BLOCKS, "tuff_stairs", () -> net.minecraft.world.level.block.Blocks.TUFF, 1.5f));
	public static final RegistryObject<Block> TUFF_SLAB = registerWithItem(BLOCKS, CTBItems.ITEMS, "tuff_slab", CTBBlockFactory.makeSlabOf(BLOCKS, "tuff_slab", () -> net.minecraft.world.level.block.Blocks.TUFF, 1.5f));
	public static final RegistryObject<Block> TUFF_WALL = registerWithItem(BLOCKS, CTBItems.ITEMS, "tuff_wall", CTBBlockFactory.makeWallOf(BLOCKS, "tuff_wall", () -> net.minecraft.world.level.block.Blocks.TUFF, 1.5f));
	public static final RegistryObject<Block> CHISELED_TUFF = registerWithItem(BLOCKS, CTBItems.ITEMS, "chiseled_tuff", CTBBlockFactory.makeStoneOf(BLOCKS, "chiseled_tuff", 1.5f));
	public static final RegistryObject<Block> POLISHED_TUFF = registerWithItem(BLOCKS, CTBItems.ITEMS, "polished_tuff", CTBBlockFactory.makeStoneOf(BLOCKS, "polished_tuff", 1.5f));
	public static final RegistryObject<Block> POLISHED_TUFF_STAIRS = registerWithItem(BLOCKS, CTBItems.ITEMS, "polished_tuff_stairs", CTBBlockFactory.makeStairs(BLOCKS, "polished_tuff_stairs", POLISHED_TUFF, 1.5f));
	public static final RegistryObject<Block> POLISHED_TUFF_SLAB = registerWithItem(BLOCKS, CTBItems.ITEMS, "polished_tuff_slab", CTBBlockFactory.makeSlab(BLOCKS, "polished_tuff_slab", POLISHED_TUFF, 1.5f));
	public static final RegistryObject<Block> POLISHED_TUFF_WALL = registerWithItem(BLOCKS, CTBItems.ITEMS, "polished_tuff_wall", CTBBlockFactory.makeWall(BLOCKS, "polished_tuff_wall", POLISHED_TUFF, 1.5f));
	public static final RegistryObject<Block> TUFF_BRICKS = registerWithItem(BLOCKS, CTBItems.ITEMS, "tuff_bricks", CTBBlockFactory.makeStoneOf(BLOCKS, "tuff_bricks", 1.5f));
	public static final RegistryObject<Block> TUFF_BRICK_STAIRS = registerWithItem(BLOCKS, CTBItems.ITEMS, "tuff_brick_stairs", CTBBlockFactory.makeStairs(BLOCKS, "tuff_brick_stairs", TUFF_BRICKS, 1.5f));
	public static final RegistryObject<Block> TUFF_BRICK_SLAB = registerWithItem(BLOCKS, CTBItems.ITEMS, "tuff_brick_slab", CTBBlockFactory.makeSlab(BLOCKS, "tuff_brick_slab", TUFF_BRICKS, 1.5f));
	public static final RegistryObject<Block> TUFF_BRICK_WALL = registerWithItem(BLOCKS, CTBItems.ITEMS, "tuff_brick_wall", CTBBlockFactory.makeWall(BLOCKS, "tuff_brick_wall", TUFF_BRICKS, 1.5f));
	public static final RegistryObject<Block> CHISELED_TUFF_BRICKS = registerWithItem(BLOCKS, CTBItems.ITEMS, "chiseled_tuff_bricks", CTBBlockFactory.makeStoneOf(BLOCKS, "chiseled_tuff_bricks", 1.5f));

	public static final WeatherableCopperSet<WeatheringCopperBlock, Block> CHISELED_COPPER = new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "chiseled_copper", 3, WeatheringCopperBlock.class, Block.class);

	public static final RegistryObject<Block> HEAVY_CORE = registerWithItem(BLOCKS, CTBItems.ITEMS, "heavy_core",
			BLOCKS.register("heavy_core", () -> new HeavyCoreBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties
					.of(net.minecraft.world.level.material.Material.METAL)
					.strength(10.0F, 1200.0F)
					.requiresCorrectToolForDrops()
					.sound(net.minecraft.world.level.block.SoundType.NETHERITE_BLOCK))));

	// Trial Chambers, light and sounds depend on the block state like vanilla
	public static final RegistryObject<Block> TRIAL_SPAWNER = registerWithItem(BLOCKS, CTBItems.ITEMS, "trial_spawner",
			BLOCKS.register("trial_spawner", () -> new com.natsu.backport.common.block.TrialSpawnerBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.STONE)
						.requiresCorrectToolForDrops()
						.lightLevel(state -> state.getValue(com.natsu.backport.common.block.TrialSpawnerBlock.STATE).lightLevel())
						.strength(50.0F)
						.sound(new net.minecraftforge.common.util.ForgeSoundType(1.0F, 1.0F,
								CTBSounds.TRIAL_SPAWNER_BREAK, CTBSounds.TRIAL_SPAWNER_STEP, CTBSounds.TRIAL_SPAWNER_PLACE,
								CTBSounds.TRIAL_SPAWNER_BREAK, CTBSounds.TRIAL_SPAWNER_STEP))
						.isViewBlocking((state, level, pos) -> false)
						.noOcclusion())));

	public static final RegistryObject<Block> VAULT = registerWithItem(BLOCKS, CTBItems.ITEMS, "vault",
			BLOCKS.register("vault", () -> new com.natsu.backport.common.block.VaultBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.STONE)
						.requiresCorrectToolForDrops()
						.lightLevel(state -> state.getValue(com.natsu.backport.common.block.VaultBlock.STATE).lightLevel())
						.strength(50.0F)
						.sound(new net.minecraftforge.common.util.ForgeSoundType(1.0F, 1.0F,
								CTBSounds.VAULT_BREAK, CTBSounds.VAULT_STEP, CTBSounds.VAULT_PLACE,
								CTBSounds.VAULT_BREAK, CTBSounds.VAULT_STEP))
						.isViewBlocking((state, level, pos) -> false)
						.noOcclusion())));

	public static final RegistryObject<Block> DECORATED_POT = registerWithItem(BLOCKS, CTBItems.ITEMS, "decorated_pot",
			BLOCKS.register("decorated_pot", () -> new com.natsu.backport.common.block.DecoratedPotBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.DECORATION)
						.strength(0.0F)
						.sound(net.minecraft.world.level.block.SoundType.STONE)
						.noOcclusion())));

	private static RegistryObject<Block> registerWithItem(DeferredRegister<Block> blocks, DeferredRegister<Item> items,
			String name, RegistryObject<Block> reg) {
		items.register(name,
				() -> new BlockItem(reg.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
		return reg;
	}


}
