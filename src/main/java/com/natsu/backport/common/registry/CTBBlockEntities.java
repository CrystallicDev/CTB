package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.entity.CreakingHeartBlockEntity;
import com.natsu.backport.common.block.entity.CopperChestBlockEntity;
import com.natsu.backport.common.block.entity.CopperGolemStatueBlockEntity;
import com.natsu.backport.common.block.entity.DecoratedPotBlockEntity;
import com.natsu.backport.common.block.entity.TrialSpawnerBlockEntity;
import com.natsu.backport.common.block.entity.vault.VaultBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CTBackport.MODID);

	public static final RegistryObject<BlockEntityType<CreakingHeartBlockEntity>> CREAKING_HEART = BLOCK_ENTITIES.register("creaking_heart",
			() -> BlockEntityType.Builder.of(CreakingHeartBlockEntity::new, CTBBlocks.CREAKING_HEART.get()).build(null)
			);

	public static final RegistryObject<BlockEntityType<TrialSpawnerBlockEntity>> TRIAL_SPAWNER = BLOCK_ENTITIES.register("trial_spawner",
			() -> BlockEntityType.Builder.of(TrialSpawnerBlockEntity::new, CTBBlocks.TRIAL_SPAWNER.get()).build(null)
			);

	public static final RegistryObject<BlockEntityType<VaultBlockEntity>> VAULT = BLOCK_ENTITIES.register("vault",
			() -> BlockEntityType.Builder.of(VaultBlockEntity::new, CTBBlocks.VAULT.get()).build(null)
			);

	public static final RegistryObject<BlockEntityType<com.natsu.backport.common.block.entity.CrafterBlockEntity>> CRAFTER =
			BLOCK_ENTITIES.register("crafter", () -> BlockEntityType.Builder.of(
					com.natsu.backport.common.block.entity.CrafterBlockEntity::new,
					CTBBlocks.CRAFTER.get()).build(null));

	public static final RegistryObject<BlockEntityType<com.natsu.backport.common.block.entity.ShelfBlockEntity>> SHELF =
			BLOCK_ENTITIES.register("shelf", () -> BlockEntityType.Builder.of(
					com.natsu.backport.common.block.entity.ShelfBlockEntity::new,
					CTBBlocks.OAK_SHELF.get(), CTBBlocks.SPRUCE_SHELF.get(), CTBBlocks.BIRCH_SHELF.get(), CTBBlocks.JUNGLE_SHELF.get(), CTBBlocks.ACACIA_SHELF.get(), CTBBlocks.DARK_OAK_SHELF.get(), CTBBlocks.CRIMSON_SHELF.get(), CTBBlocks.WARPED_SHELF.get(), CTBBlocks.PALE_OAK_SHELF.get(), CTBBlocks.CHERRY_SHELF.get(), CTBBlocks.BAMBOO_SHELF.get()).build(null));

	public static final RegistryObject<BlockEntityType<com.natsu.backport.common.block.entity.PotentSulfurBlockEntity>> POTENT_SULFUR =
			BLOCK_ENTITIES.register("potent_sulfur", () -> BlockEntityType.Builder.of(
					com.natsu.backport.common.block.entity.PotentSulfurBlockEntity::new,
					CTBBlocks.POTENT_SULFUR.get()).build(null));

	public static final RegistryObject<BlockEntityType<CopperChestBlockEntity>> COPPER_CHEST = BLOCK_ENTITIES.register("copper_chest",
			() -> BlockEntityType.Builder.of(CopperChestBlockEntity::new,
					CTBBlocks.COPPER_CHEST.get(), CTBBlocks.EXPOSED_COPPER_CHEST.get(),
					CTBBlocks.WEATHERED_COPPER_CHEST.get(), CTBBlocks.OXIDIZED_COPPER_CHEST.get(),
					CTBBlocks.WAXED_COPPER_CHEST.get(), CTBBlocks.WAXED_EXPOSED_COPPER_CHEST.get(),
					CTBBlocks.WAXED_WEATHERED_COPPER_CHEST.get(), CTBBlocks.WAXED_OXIDIZED_COPPER_CHEST.get()).build(null)
			);

	public static final RegistryObject<BlockEntityType<CopperGolemStatueBlockEntity>> COPPER_GOLEM_STATUE = BLOCK_ENTITIES.register("copper_golem_statue",
			() -> BlockEntityType.Builder.of(CopperGolemStatueBlockEntity::new, CTBBlocks.COPPER_GOLEM_STATUE.get()).build(null)
			);

	public static final RegistryObject<BlockEntityType<DecoratedPotBlockEntity>> DECORATED_POT = BLOCK_ENTITIES.register("decorated_pot",
			() -> BlockEntityType.Builder.of(DecoratedPotBlockEntity::new, CTBBlocks.DECORATED_POT.get()).build(null)
			);
}
