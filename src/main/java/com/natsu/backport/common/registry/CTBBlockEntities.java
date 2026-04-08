package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.entity.CreakingHeartBlockEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CTBackport.MODID);
	
	public static final RegistryObject<BlockEntityType<CreakingHeartBlockEntity>> CREAKING_HEART = BLOCK_ENTITIES.register("creaking_heart", 
			() -> BlockEntityType.Builder.of(CreakingHeartBlockEntity::new, CTBBlocks.CREAKING_HEART.get()).build(null)
			);
}
