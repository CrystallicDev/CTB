package com.natsu.backport.common.registry;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.natsu.backport.CTBackport;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBMemoryModules {
	public static final Codec<Unit> UNIT_CODEC = Codec.unit(Unit.INSTANCE);

	public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, CTBackport.MODID);

	public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_JUMP_COOLDOWN = MEMORY_MODULES.register("breeze_jump_cooldown", 
			() -> new MemoryModuleType<Unit>(Optional.of(UNIT_CODEC))
			);
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT = MEMORY_MODULES.register("breeze_shoot", 
			() -> new MemoryModuleType<Unit>(Optional.of(UNIT_CODEC))
			);
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_CHARGING = MEMORY_MODULES.register("breeze_shoot_charging", 
			() -> new MemoryModuleType<Unit>(Optional.of(UNIT_CODEC))
			);
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_RECOVERING = MEMORY_MODULES.register("breeze_shoot_recover", 
			() -> new MemoryModuleType<Unit>(Optional.of(UNIT_CODEC))
			);
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_SHOOT_COOLDOWN = MEMORY_MODULES.register("breeze_shoot_cooldown", 
			() -> new MemoryModuleType<Unit>(Optional.of(UNIT_CODEC))
			);
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_JUMP_INHALING = MEMORY_MODULES.register("breeze_jump_inhaling", 
			() -> new MemoryModuleType<Unit>(Optional.of(UNIT_CODEC))
			);;
    public static final RegistryObject<MemoryModuleType<BlockPos>> BREEZE_JUMP_TARGET = MEMORY_MODULES.register("breeze_jump_target", 
			() -> new MemoryModuleType<BlockPos>(Optional.of(BlockPos.CODEC))
			);
    public static final RegistryObject<MemoryModuleType<Unit>> BREEZE_LEAVING_WATER = MEMORY_MODULES.register("breeze_leaving_water", 
			() -> new MemoryModuleType<Unit>(Optional.of(UNIT_CODEC))
			);

	
}
