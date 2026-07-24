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

	public static final RegistryObject<BlockEntityType<CopperChestBlockEntity>> COPPER_CHEST = BLOCK_ENTITIES.register("copper_chest",
			() -> BlockEntityType.Builder.of(CopperChestBlockEntity::new, CTBBlocks.COPPER_CHEST.get()).build(null)
			);

	public static final RegistryObject<BlockEntityType<CopperGolemStatueBlockEntity>> COPPER_GOLEM_STATUE = BLOCK_ENTITIES.register("copper_golem_statue",
			() -> BlockEntityType.Builder.of(CopperGolemStatueBlockEntity::new, CTBBlocks.COPPER_GOLEM_STATUE.get()).build(null)
			);

	public static final RegistryObject<BlockEntityType<DecoratedPotBlockEntity>> DECORATED_POT = BLOCK_ENTITIES.register("decorated_pot",
			() -> BlockEntityType.Builder.of(DecoratedPotBlockEntity::new, CTBBlocks.DECORATED_POT.get()).build(null)
			);
}
