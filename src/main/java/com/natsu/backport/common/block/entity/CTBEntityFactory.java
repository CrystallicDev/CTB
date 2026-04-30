package com.natsu.backport.common.block.entity;

import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CTBEntityFactory {

	/*public static final RegistryObject<EntityType<Boat>> makeBoat(DeferredRegister<Entity> ENTITIES, String name){
		return ENTITIES.register(name,
				() -> EntityType.Builder.<Boat>of(Boat::new, MobCategory.MISC)
					.sized(1.375F, 0.5625F)
					.build(name)
				);
	}*/

	public static final RegistryObject<BlockEntityType<SignBlockEntity>> makeSign(DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES, String name, RegistryObject<StandingSignBlock> sign, RegistryObject<WallSignBlock> wallSign) {
		return BLOCK_ENTITIES.register(name,
				() -> BlockEntityType.Builder.of(
						SignBlockEntity::new,
						sign.get(),
						wallSign.get()
						).build(null));
	}
}