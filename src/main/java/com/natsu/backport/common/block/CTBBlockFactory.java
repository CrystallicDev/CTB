package com.natsu.backport.common.block;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CTBBlockFactory {

	public static RegistryObject<Block> makeLog(DeferredRegister<Block> BLOCKS, String name, float strength) {
        return BLOCKS.register(name,
            () -> new RotatedPillarBlock(
                BlockBehaviour.Properties.copy(Blocks.OAK_LOG)
                .strength(strength)
            ));
    }
	
	public static RegistryObject<Block> makeDecorativeStone(DeferredRegister<Block> BLOCKS, String name, float strength) {
        return BLOCKS.register(name,
            () -> new Block(
                BlockBehaviour.Properties.copy(Blocks.ANDESITE)
                .strength(strength)
            ));
    }
	
	public static RegistryObject<Block> makeDecorativeDirt(DeferredRegister<Block> BLOCKS, String name, float strength) {
        return BLOCKS.register(name,
            () -> new Block(
                BlockBehaviour.Properties.copy(Blocks.DIRT)
                .strength(strength)
            ));
    }
	
	public static RegistryObject<Block> makeMossBlock(DeferredRegister<Block> BLOCKS, String name, Holder<ConfiguredFeature<VegetationPatchConfiguration, ?>> boneMealFeature){
		return BLOCKS.register(name, 
				() -> new GrowableMossBlock(BlockBehaviour.Properties.of(Material.MOSS)
						.requiresCorrectToolForDrops()
						.sound(SoundType.MOSS)
						.strength(0.1f),
						boneMealFeature
				));
	}
	
	public static RegistryObject<Block> makeMossLayerBlock(DeferredRegister<Block> BLOCKS, String name, BlockBehaviour.Properties props) {
		return BLOCKS.register(
            name,
            () -> new GrowableMossLayerBlock(props, true)
            );
	}

	public static RegistryObject<Block> makeWood(DeferredRegister<Block> BLOCKS, String name, float strength) {
        return BLOCKS.register(name,
            () -> new RotatedPillarBlock(
                BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)
                .strength(strength)
            ));
    }
	
	
    public static RegistryObject<Block> makePlanks(DeferredRegister<Block> BLOCKS, String name, float strength) {
        return BLOCKS.register(name,
            () -> new Block(
                BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                .strength(strength)
            ));
    }

    public static RegistryObject<Block> makeSlab(DeferredRegister<Block> BLOCKS, String name, RegistryObject<Block> base, float strength) {
        return BLOCKS.register(name,
            () -> new SlabBlock(
                BlockBehaviour.Properties.copy(base.get())
                .strength(strength)
            ));
    }

    public static RegistryObject<Block> makeStairs(DeferredRegister<Block> BLOCKS, String name, RegistryObject<Block> base, float strength) {
        return BLOCKS.register(name,
            () -> new StairBlock(
                () -> base.get().defaultBlockState(),
                BlockBehaviour.Properties.copy(base.get())
                .strength(strength)
            ));
    }

    public static RegistryObject<Block> makeFence(DeferredRegister<Block> BLOCKS, String name, RegistryObject<Block> planks, float strength) {
        return BLOCKS.register(name,
            () -> new FenceBlock(
                BlockBehaviour.Properties.copy(planks.get())
                .strength(strength)
            ));
    }

    public static RegistryObject<Block> makeFenceGate(DeferredRegister<Block> BLOCKS, String name, RegistryObject<Block> planks, float strength) {
        return BLOCKS.register(name,
            () -> new FenceGateBlock(
                BlockBehaviour.Properties.copy(planks.get())
                .strength(strength)
            ));
    }
    
    public static RegistryObject<Block> makeWall(DeferredRegister<Block> BLOCKS, String name, RegistryObject<Block> stone, float strength) {
        return BLOCKS.register(name,
            () -> new WallBlock(
                BlockBehaviour.Properties.copy(stone.get())
                .strength(strength)
            ));
    }

    public static RegistryObject<Block> makeButton(DeferredRegister<Block> BLOCKS, String name) {
        return BLOCKS.register(name,
            () -> new WoodButtonBlock(
                BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON)
            ));
    }

    public static RegistryObject<Block> makePressurePlate(DeferredRegister<Block> BLOCKS, String name, RegistryObject<Block> planks) {
        return BLOCKS.register(name,
            () -> new PressurePlateBlock(
                PressurePlateBlock.Sensitivity.EVERYTHING,
                BlockBehaviour.Properties.copy(planks.get())
            ));
    }

    public static RegistryObject<Block> makeDoor(DeferredRegister<Block> BLOCKS, String name, RegistryObject<Block> planks, float strength) {
        return BLOCKS.register(name,
            () -> new DoorBlock(
                BlockBehaviour.Properties.copy(planks.get())
                .strength(strength)
            ));
    }

    public static RegistryObject<Block> makeTrapdoor(DeferredRegister<Block> BLOCKS, String name, RegistryObject<Block> planks, float strength) {
        return BLOCKS.register(name,
            () -> new TrapDoorBlock(
                BlockBehaviour.Properties.copy(planks.get())
                .strength(strength)
            ));
    }

    public static RegistryObject<Block> makeSapling(DeferredRegister<Block> BLOCKS, String name, AbstractTreeGrower treeGrower) {
        return BLOCKS.register(name,
            () -> new SaplingBlock(
                treeGrower,
                BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
            ));
    }

    public static RegistryObject<StandingSignBlock> makeStandingSign(DeferredRegister<Block> BLOCKS, String name, WoodType type) {
        return BLOCKS.register(name,
            () -> new StandingSignBlock(
                BlockBehaviour.Properties.copy(Blocks.OAK_SIGN)
                    .noCollission()
                    .strength(1.0F),
                    type
            ));
    }

    public static RegistryObject<WallSignBlock> makeWallSign(DeferredRegister<Block> BLOCKS, String name, WoodType type,
                                                       RegistryObject<StandingSignBlock> standingSign) {
        return BLOCKS.register(name,
            () -> new WallSignBlock(
                BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN)
                    .noCollission()
                    .strength(1.0F)
                    .lootFrom(standingSign),
                type
            ));
    }

    public static RegistryObject<Block> makeLeaves(DeferredRegister<Block> BLOCKS, String name) {
        return BLOCKS.register(name,
            () -> new LeavesBlock(
                BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)
            ));
    }
    
    public static RegistryObject<Block> makeCherryLeaves(DeferredRegister<Block> BLOCKS, String name) {
        return BLOCKS.register(name,
            () -> new CherryLeavesBlock(
                BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)
            ));
    }
    
    public static RegistryObject<Block> makeLeaves(DeferredRegister<Block> BLOCKS, String name, BlockBehaviour.Properties props) {
        return BLOCKS.register(name,
            () -> new LeavesBlock(
            	props
            ));
    }
    
	public static RegistryObject<Block> makeAmethystBlock(DeferredRegister<Block> BLOCKS, String name, float strength) {
		return BLOCKS.register(name, () -> new AmethystBlock(BlockBehaviour.Properties.of(Material.AMETHYST)
            	.strength(strength).sound(SoundType.AMETHYST).requiresCorrectToolForDrops()));
	}

	public static RegistryObject<Block> makeAmethystClusterBlock(DeferredRegister<Block> BLOCKS, String name, float strength) {
		return BLOCKS.register(name,
				() -> new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.of(Material.AMETHYST).noOcclusion()
		            	.strength(strength)
						.randomTicks().sound(SoundType.AMETHYST_CLUSTER).strength(1.5F).lightLevel((state) -> 7)));
	}
	
	public static RegistryObject<Block> makeAmethystLargeBud(DeferredRegister<Block> BLOCKS, String name, float strength) {
		return BLOCKS.register(name, 
				() -> new AmethystClusterBlock(5, 3,
		            	BlockBehaviour.Properties.of(Material.AMETHYST)
		            	.strength(strength)
		                .sound(SoundType.MEDIUM_AMETHYST_BUD)
		                .lightLevel((state) -> 5))
				);
	}
	
	public static RegistryObject<Block> makeAmethystMediumBud(DeferredRegister<Block> BLOCKS, String name, float strength) {
		return BLOCKS.register(name, 
				() -> new AmethystClusterBlock(4, 3,
		            	BlockBehaviour.Properties.of(Material.AMETHYST)
		            	.strength(strength)
		                .sound(SoundType.LARGE_AMETHYST_BUD)
		                .lightLevel((state) -> 2))
				);
	}
	
	public static RegistryObject<Block> makeAmethystSmallBud(DeferredRegister<Block> BLOCKS, String name, float strength) {
		return BLOCKS.register(name, 
				() -> new AmethystClusterBlock(3, 4,
		            	BlockBehaviour.Properties.of(Material.AMETHYST)
		            	.strength(strength)
		                .sound(SoundType.SMALL_AMETHYST_BUD)
		                .lightLevel((state) -> 1))
				);
	}

	public static RegistryObject<Block> makeSand(DeferredRegister<Block> BLOCKS, String name, float strength) {
		return BLOCKS.register(name, 
				() -> new FallingBlock(
		            	BlockBehaviour.Properties.of(Material.SAND)
		            	.strength(strength)
		                .sound(SoundType.SAND))
				);
	}

	public static RegistryObject<Block> makeGlass(DeferredRegister<Block> BLOCKS, String name, float strength) {
		return BLOCKS.register(name, 
				() -> new GlassBlock(
		            	BlockBehaviour.Properties.of(Material.GLASS)
		            	.strength(strength)
		                .sound(SoundType.GLASS))
				);
	}
	
	public static RegistryObject<Block> makeSandStone(DeferredRegister<Block> BLOCKS, String name, float strength) {
		return BLOCKS.register(name, 
				() -> new FallingBlock(
		            	BlockBehaviour.Properties.copy(Blocks.SANDSTONE)
		            	.strength(strength))
				);
	}
	 
    
}
