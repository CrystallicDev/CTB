package com.natsu.backport.common.registry;

import com.natsu.backport.CTBackport;
import com.natsu.backport.server.world.feature.tree.decorator.CreakingHeartDecorator;
import com.natsu.backport.server.world.feature.tree.decorator.PaleMossDecorator;

import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CTBTreeDecorators {

	public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, CTBackport.MODID);

	public static final RegistryObject<TreeDecoratorType<PaleMossDecorator>> PALE_MOSS = TREE_DECORATOR_TYPES.register("pale_moss",
			() -> new TreeDecoratorType<>(PaleMossDecorator.CODEC));

	public static final RegistryObject<TreeDecoratorType<CreakingHeartDecorator>> CREAKING_HEART = TREE_DECORATOR_TYPES.register("creaking_heart",
			() -> new TreeDecoratorType<>(CreakingHeartDecorator.CODEC));
}
