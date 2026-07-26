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

	public static final RegistryObject<Block> DECORATED_POT = BLOCKS.register("decorated_pot",
			() -> new com.natsu.backport.common.block.DecoratedPotBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.DECORATION)
						.strength(0.0F)
						.sound(net.minecraft.world.level.block.SoundType.STONE)
						.noOcclusion()));
	// custom item, the pot renders through the block entity renderer even in hand
	private static final RegistryObject<Item> DECORATED_POT_ITEM = CTBItems.ITEMS.register("decorated_pot",
			() -> new com.natsu.backport.common.item.DecoratedPotItem(DECORATED_POT.get(),
					new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

	public static final WeatherableCopperSet<com.natsu.backport.common.block.WeatheringCopperBarsBlock, net.minecraft.world.level.block.IronBarsBlock> COPPER_BARS =
			new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_bars",
					() -> net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.METAL)
							.requiresCorrectToolForDrops().strength(5.0F, 6.0F)
							.sound(net.minecraft.world.level.block.SoundType.COPPER).noOcclusion(),
					com.natsu.backport.common.block.WeatheringCopperBarsBlock.class,
					net.minecraft.world.level.block.IronBarsBlock.class);

	public static final WeatherableCopperSet<com.natsu.backport.common.block.WeatheringCopperChainBlock, net.minecraft.world.level.block.ChainBlock> COPPER_CHAIN =
			new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_chain",
					() -> net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.METAL)
							.requiresCorrectToolForDrops().strength(5.0F, 6.0F)
							.sound(net.minecraft.world.level.block.SoundType.CHAIN).noOcclusion(),
					com.natsu.backport.common.block.WeatheringCopperChainBlock.class,
					net.minecraft.world.level.block.ChainBlock.class);

	public static final WeatherableCopperSet<com.natsu.backport.common.block.WeatheringCopperLanternBlock, net.minecraft.world.level.block.LanternBlock> COPPER_LANTERN =
			new WeatherableCopperSet<>(BLOCKS, CTBItems.ITEMS, "copper_lantern",
					() -> net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.METAL)
							.requiresCorrectToolForDrops().strength(3.5F)
							.sound(net.minecraft.world.level.block.SoundType.LANTERN)
							.lightLevel(state -> 15).noOcclusion(),
					com.natsu.backport.common.block.WeatheringCopperLanternBlock.class,
					net.minecraft.world.level.block.LanternBlock.class);

	// the copper torch does not oxidize, it just burns green
	public static final RegistryObject<Block> COPPER_TORCH = BLOCKS.register("copper_torch",
			() -> new com.natsu.backport.common.block.CopperTorchBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.DECORATION)
							.noCollission().instabreak()
							.lightLevel(state -> 14)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)));
	public static final RegistryObject<Block> COPPER_WALL_TORCH = BLOCKS.register("copper_wall_torch",
			() -> new com.natsu.backport.common.block.CopperWallTorchBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.DECORATION)
							.noCollission().instabreak()
							.lightLevel(state -> 14)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.dropsLike(COPPER_TORCH.get())));
	private static final RegistryObject<Item> COPPER_TORCH_ITEM = CTBItems.ITEMS.register("copper_torch",
			() -> new net.minecraft.world.item.StandingAndWallBlockItem(COPPER_TORCH.get(), COPPER_WALL_TORCH.get(),
					new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

	public static final RegistryObject<Block> OAK_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "oak_shelf",
			BLOCKS.register("oak_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> SPRUCE_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "spruce_shelf",
			BLOCKS.register("spruce_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> BIRCH_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "birch_shelf",
			BLOCKS.register("birch_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> JUNGLE_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "jungle_shelf",
			BLOCKS.register("jungle_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> ACACIA_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "acacia_shelf",
			BLOCKS.register("acacia_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> DARK_OAK_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "dark_oak_shelf",
			BLOCKS.register("dark_oak_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> CRIMSON_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "crimson_shelf",
			BLOCKS.register("crimson_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> WARPED_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "warped_shelf",
			BLOCKS.register("warped_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> PALE_OAK_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "pale_oak_shelf",
			BLOCKS.register("pale_oak_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> CHERRY_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "cherry_shelf",
			BLOCKS.register("cherry_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));
	public static final RegistryObject<Block> BAMBOO_SHELF = registerWithItem(BLOCKS, CTBItems.ITEMS, "bamboo_shelf",
			BLOCKS.register("bamboo_shelf", () -> new com.natsu.backport.common.block.ShelfBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.WOOD)
							.strength(2.0F, 3.0F)
							.sound(net.minecraft.world.level.block.SoundType.WOOD)
							.noOcclusion())));

	public static final RegistryObject<Block> COPPER_CHEST = copperChest("copper_chest", net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED, false);
	public static final RegistryObject<Block> EXPOSED_COPPER_CHEST = copperChest("exposed_copper_chest", net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED, false);
	public static final RegistryObject<Block> WEATHERED_COPPER_CHEST = copperChest("weathered_copper_chest", net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED, false);
	public static final RegistryObject<Block> OXIDIZED_COPPER_CHEST = copperChest("oxidized_copper_chest", net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED, false);
	public static final RegistryObject<Block> WAXED_COPPER_CHEST = copperChest("waxed_copper_chest", net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED, true);
	public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_CHEST = copperChest("waxed_exposed_copper_chest", net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED, true);
	public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_CHEST = copperChest("waxed_weathered_copper_chest", net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED, true);
	public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_CHEST = copperChest("waxed_oxidized_copper_chest", net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED, true);

	private static RegistryObject<Block> copperChest(String name, net.minecraft.world.level.block.WeatheringCopper.WeatherState state, boolean waxed) {
		RegistryObject<Block> block = BLOCKS.register(name, () -> waxed
				? new com.natsu.backport.common.block.CopperChestBlock(state, copperChestProps())
				: new com.natsu.backport.common.block.WeatheringCopperChestBlock(state, copperChestProps()));
		CTBItems.ITEMS.register(name, () -> new com.natsu.backport.common.item.CopperChestItem(block.get(),
				new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
		return block;
	}

	private static net.minecraft.world.level.block.state.BlockBehaviour.Properties copperChestProps() {
		return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.METAL)
				.strength(3.0F, 6.0F)
				.requiresCorrectToolForDrops()
				.sound(net.minecraft.world.level.block.SoundType.COPPER)
				.noOcclusion();
	}

	public static final RegistryObject<Block> COPPER_GOLEM_STATUE = registerStatue(
			BLOCKS.register("copper_golem_statue", () -> new com.natsu.backport.common.block.CopperGolemStatueBlock(
					net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.METAL)
						.strength(3.0F, 6.0F)
						.requiresCorrectToolForDrops()
						.sound(net.minecraft.world.level.block.SoundType.COPPER)
						.noOcclusion())));

	private static RegistryObject<Block> registerStatue(RegistryObject<Block> reg) {
		CTBItems.ITEMS.register("copper_golem_statue",
				() -> new com.natsu.backport.common.item.CopperGolemStatueItem(reg.get(),
						new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
		return reg;
	}

	// spring to life plants
	public static final RegistryObject<Block> BUSH = plant("bush",
			() -> new com.natsu.backport.common.block.SpringBushBlock(plantProps(false)));
	public static final RegistryObject<Block> FIREFLY_BUSH = plant("firefly_bush",
			() -> new com.natsu.backport.common.block.FireflyBushBlock(plantProps(false).lightLevel(state -> 2)));
	public static final RegistryObject<Block> CACTUS_FLOWER = plant("cactus_flower",
			() -> new com.natsu.backport.common.block.CactusFlowerBlock(plantProps(true)));
	public static final RegistryObject<Block> SHORT_DRY_GRASS = plant("short_dry_grass",
			() -> new com.natsu.backport.common.block.ShortDryGrassBlock(plantProps(false)));
	public static final RegistryObject<Block> TALL_DRY_GRASS = plant("tall_dry_grass",
			() -> new com.natsu.backport.common.block.TallDryGrassBlock(plantProps(false)));
	public static final RegistryObject<Block> WILDFLOWERS = plant("wildflowers",
			() -> new com.natsu.backport.common.block.FlowerBedBlock(plantProps(true)));
	public static final RegistryObject<Block> LEAF_LITTER = plant("leaf_litter",
			() -> new com.natsu.backport.common.block.LeafLitterBlock(plantProps(false)));

	private static net.minecraft.world.level.block.state.BlockBehaviour.Properties plantProps(boolean solid) {
		return net.minecraft.world.level.block.state.BlockBehaviour.Properties
				.of(solid ? net.minecraft.world.level.material.Material.PLANT
						: net.minecraft.world.level.material.Material.REPLACEABLE_PLANT)
				.noCollission().instabreak()
				.sound(net.minecraft.world.level.block.SoundType.GRASS);
	}

	private static RegistryObject<Block> plant(String name, java.util.function.Supplier<Block> supplier) {
		RegistryObject<Block> block = BLOCKS.register(name, supplier);
		CTBItems.ITEMS.register(name,
				() -> new BlockItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
		return block;
	}

	// pale garden flower pots ; the eyeblossom ones flip with the night
	public static final RegistryObject<Block> POTTED_PALE_OAK_SAPLING = BLOCKS.register("potted_pale_oak_sapling",
			() -> new net.minecraft.world.level.block.FlowerPotBlock(
					() -> (net.minecraft.world.level.block.FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT,
					() -> PALE_OAK_SAPLING.get(),
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.DECORATION).instabreak().noOcclusion()));
	public static final RegistryObject<Block> POTTED_OPEN_EYEBLOSSOM = BLOCKS.register("potted_open_eyeblossom",
			() -> new com.natsu.backport.common.block.PottedEyeblossomBlock(true,
					() -> CTBBlocks.POTTED_CLOSED_EYEBLOSSOM.get(), () -> OPEN_EYEBLOSSOM.get(),
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.DECORATION).instabreak().noOcclusion().randomTicks()));
	public static final RegistryObject<Block> POTTED_CLOSED_EYEBLOSSOM = BLOCKS.register("potted_closed_eyeblossom",
			() -> new com.natsu.backport.common.block.PottedEyeblossomBlock(false,
					() -> POTTED_OPEN_EYEBLOSSOM.get(), () -> CLOSED_EYEBLOSSOM.get(),
					net.minecraft.world.level.block.state.BlockBehaviour.Properties
							.of(net.minecraft.world.level.material.Material.DECORATION).instabreak().noOcclusion().randomTicks()));

	private static RegistryObject<Block> registerWithItem(DeferredRegister<Block> blocks, DeferredRegister<Item> items,
			String name, RegistryObject<Block> reg) {
		items.register(name,
				() -> new BlockItem(reg.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
		return reg;
	}


}
