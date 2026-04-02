package com.natsu.backport.utils.sets;

import java.util.function.Function;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBBlockFactory;
import com.natsu.backport.common.block.entity.CTBEntityFactory;
import com.natsu.backport.common.item.CTBBlockItemFactory;
import com.natsu.backport.common.item.CTBItemFactory;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * <p> This class is used to represent a new wood type.
 * <p> It creates the following blocks :
 * <ul>
 * 	<li><b>{name}_log</b>: The original log block</li>
 * 	<li><b>stripped_{name}_log</b>: The stripped log block</li>
 * 	<li><b>{name}_wood</b>: The original wood block</li>
 * 	<li><b>stripped_{name}_wood</b>: The stripped wood block</li>
 * 	<li><b>{name}_branch</b>: The branch block</li>
 * 	<li><b>stripped_{name}_branch</b>: The stripped branch block</li>
 * 	<li><b>{name}_planks</b>: The planks block</li>
 * 	<li><b>{name}_slab</b>: The slab block</li>
 * 	<li><b>{name}_stairs</b>: The stairs block</li>
 * 	<li><b>{name}_fence</b>: The fence block</li>
 * 	<li><b>{name}_fence_gate</b>: The fence gate block</li>
 * 	<li><b>{name}_door</b>: The door</li>
 * 	<li><b>{name}_trapdoor</b>: The trapdoor</li>
 * 	<li><b>{name}_button</b>: The button</li>
 * 	<li><b>{name}_pressure_plate</b>: The pressure plate</li>
 * 	<li><b>{name}_sign</b>: The sign and wall sign</li>
 * </ul>
 * <p> It requires the following textures for DataGen integration</p>
 * <ul>
 * 	<li><b>{name}_log</b>: The original log texture</li>
 * 	<li><b>{name}_log_top</b>: The original top log texture</li>
 * 	<li><b>stripped_{name}_log</b>: The stripped log texture</li>
 * 	<li><b>stripped_{name}_log_top</b>: The stripped top log texture</li>
 * 	<li><b>{name}_planks</b>: The planks texture</li>
 * 	<li><b>{name}_door_bottom</b>: The bottom door texture</li>
 * 	<li><b>{name}_door_top</b>: The top door texture</li>
 * 	<li><b>{name}_trapdoor</b>: The trapdoor texture</li>
 * 	<li><b>item/{name}_sign</b>: The sign item texture</li>
 * 	<li><b>entity/{name}_sign</b>: The sign entity texture</li>
 * </ul>
 * */
public class WoodSet implements DefaultSet {

	public final String name;
	public final WoodType woodType;
	
	public final RegistryObject<Block> log;
    public final RegistryObject<Block> wood;
    public final RegistryObject<Block> strippedLog;
    public final RegistryObject<Block> strippedWood;
    public final RegistryObject<Block> planks;
    public final RegistryObject<Block> slab;
    public final RegistryObject<Block> stairs;
    public final RegistryObject<Block> fence;
    public final RegistryObject<Block> fenceGate;
    public final RegistryObject<Block> door;
    public final RegistryObject<Block> trapdoor;
    public final RegistryObject<Block> button;
    public final RegistryObject<Block> pressurePlate;
    public final RegistryObject<StandingSignBlock> standingsign;
    public final RegistryObject<WallSignBlock> wallsign;
    
    public final RegistryObject<Item> logItem;
    public final RegistryObject<Item> woodItem;
    public final RegistryObject<Item> strippedLogItem;
    public final RegistryObject<Item> strippedWoodItem;
    public final RegistryObject<Item> planksItem;
    public final RegistryObject<Item> slabItem;
    public final RegistryObject<Item> stairsItem;
    public final RegistryObject<Item> fenceItem;
    public final RegistryObject<Item> fenceGateItem;
    public final RegistryObject<Item> doorItem;
    public final RegistryObject<Item> trapdoorItem;
    public final RegistryObject<Item> buttonItem;
    public final RegistryObject<Item> pressurePlateItem;
    public final RegistryObject<Item> signItem;
    
    public final RegistryObject<BlockEntityType<SignBlockEntity>> signBlockEntity;
    //public final RegistryObject<Block> boat;

    public WoodSet(DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES, DeferredRegister<Item> ITEMS, DeferredRegister<Block> BLOCKS, String woodName, float baseStrength,
            CreativeModeTab tab) {

    	woodType = WoodType.register(WoodType.create(CTBackport.MODID + ":" + woodName));
    	
    	name = woodName;

        log = CTBBlockFactory.makeLog(BLOCKS, name + "_log", baseStrength);
        strippedLog = CTBBlockFactory.makeLog(BLOCKS, "stripped_" + name + "_log", baseStrength);
        wood = CTBBlockFactory.makeWood(BLOCKS, name + "_wood", baseStrength);
        strippedWood = CTBBlockFactory.makeWood(BLOCKS, "stripped_" + name + "_wood", baseStrength);
        planks = CTBBlockFactory.makePlanks(BLOCKS, name + "_planks", baseStrength);

        slab = CTBBlockFactory.makeSlab(BLOCKS, name + "_slab", planks, baseStrength * 1.5f);
        stairs = CTBBlockFactory.makeStairs(BLOCKS, name + "_stairs", planks, baseStrength);
        fence = CTBBlockFactory.makeFence(BLOCKS, name + "_fence", planks, baseStrength * 1.5f);
        fenceGate = CTBBlockFactory.makeFenceGate(BLOCKS, name + "_fence_gate", planks, baseStrength * 1.5f);

        door = CTBBlockFactory.makeDoor(BLOCKS, name + "_door", planks, baseStrength);
        trapdoor = CTBBlockFactory.makeTrapdoor(BLOCKS, name + "_trapdoor", planks, baseStrength);
        button = CTBBlockFactory.makeButton(BLOCKS, name + "_button");
        pressurePlate = CTBBlockFactory.makePressurePlate(BLOCKS, name + "_pressure_plate", planks);
        
        //boat = CristalliteBlockFactory.makeBoat(name + "_boat", planks);
        standingsign = CTBBlockFactory.makeStandingSign(BLOCKS, name + "_sign", woodType);
        wallsign = CTBBlockFactory.makeWallSign(BLOCKS, name + "_wall_sign", woodType, standingsign);

        logItem = CTBBlockItemFactory.blockItem(ITEMS, tab, log);
        strippedLogItem = CTBBlockItemFactory.blockItem(ITEMS, tab, strippedLog);
        woodItem = CTBBlockItemFactory.blockItem(ITEMS, tab, wood);
        strippedWoodItem = CTBBlockItemFactory.blockItem(ITEMS, tab, strippedWood);
        planksItem = CTBBlockItemFactory.blockItem(ITEMS, tab, planks);
        slabItem = CTBBlockItemFactory.blockItem(ITEMS, tab, slab);
        stairsItem = CTBBlockItemFactory.blockItem(ITEMS, tab, stairs);
        fenceItem = CTBBlockItemFactory.blockItem(ITEMS, tab, fence);
        fenceGateItem = CTBBlockItemFactory.blockItem(ITEMS, tab, fenceGate);
        doorItem = CTBBlockItemFactory.blockItem(ITEMS, tab, door);
        trapdoorItem = CTBBlockItemFactory.blockItem(ITEMS, tab, trapdoor);
        buttonItem = CTBBlockItemFactory.blockItem(ITEMS, tab, button);
        pressurePlateItem = CTBBlockItemFactory.blockItem(ITEMS, tab, pressurePlate);
        signItem = CTBItemFactory.makeSign(ITEMS, name+"_sign", tab, wallsign, standingsign);
        
        signBlockEntity = CTBEntityFactory.makeSign(BLOCK_ENTITIES, name+"_sign_block_entity", standingsign, wallsign);
    }


    @Override
	public void addBlockTags(Function<TagKey<Block>, TagAppender<Block>> tag) {

		tag.apply(BlockTags.PLANKS).add(planks.get());


		tag.apply(BlockTags.LOGS).add(log.get(), strippedLog.get(), wood.get(), strippedWood.get());
		tag.apply(BlockTags.LOGS_THAT_BURN).add(log.get(), strippedLog.get(), wood.get(), strippedWood.get());
		tag.apply(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE).add(log.get(), strippedLog.get(), wood.get(), strippedWood.get());
		tag.apply(BlockTags.PARROTS_SPAWNABLE_ON).add(log.get(), strippedLog.get(), wood.get(), strippedWood.get());

		tag.apply(BlockTags.WOODEN_SLABS).add(slab.get());
		tag.apply(BlockTags.SLABS).add(slab.get());

		tag.apply(BlockTags.WOODEN_STAIRS).add(stairs.get());
		tag.apply(BlockTags.STAIRS).add(stairs.get());

		tag.apply(BlockTags.WOODEN_FENCES).add(fence.get());
		tag.apply(BlockTags.FENCES).add(fence.get());
		tag.apply(Tags.Blocks.FENCES).add(fence.get());
		tag.apply(Tags.Blocks.FENCES_WOODEN).add(fence.get());

		tag.apply(BlockTags.FENCE_GATES).add(fenceGate.get());
		tag.apply(BlockTags.UNSTABLE_BOTTOM_CENTER).add(fenceGate.get());
		tag.apply(Tags.Blocks.FENCE_GATES).add(fenceGate.get());
		tag.apply(Tags.Blocks.FENCE_GATES_WOODEN).add(fenceGate.get());

		tag.apply(BlockTags.WOODEN_DOORS).add(door.get());
		tag.apply(BlockTags.DOORS).add(door.get());

		tag.apply(BlockTags.WOODEN_TRAPDOORS).add(trapdoor.get());
		tag.apply(BlockTags.TRAPDOORS).add(trapdoor.get());

		tag.apply(BlockTags.WOODEN_BUTTONS).add(button.get());
		tag.apply(BlockTags.BUTTONS).add(button.get());

		tag.apply(BlockTags.WOODEN_PRESSURE_PLATES).add(pressurePlate.get());
		tag.apply(BlockTags.PRESSURE_PLATES).add(pressurePlate.get());
		tag.apply(BlockTags.WALL_POST_OVERRIDE).add(pressurePlate.get());

		tag.apply(BlockTags.SIGNS).add(standingsign.get());
		tag.apply(BlockTags.STANDING_SIGNS).add(standingsign.get());
		tag.apply(BlockTags.WALL_POST_OVERRIDE).add(standingsign.get());

		tag.apply(BlockTags.SIGNS).add(wallsign.get());
		tag.apply(BlockTags.WALL_SIGNS).add(wallsign.get());
		tag.apply(BlockTags.WALL_POST_OVERRIDE).add(wallsign.get());

		// Outil : hache
		tag.apply(BlockTags.MINEABLE_WITH_AXE).add(
				planks.get(),
				log.get(),
				strippedLog.get(),
				wood.get(),
				strippedWood.get(),
				slab.get(),
				fence.get(),
				stairs.get(),
				button.get(),
				door.get(),
				pressurePlate.get(),
				fenceGate.get(),
				trapdoor.get()
				);
	}


	@Override
	public void setRenderTypes() {
		BlockEntityRenderers.register(signBlockEntity.get(), SignRenderer::new);
    	//WoodType.register(WoodType.create(CTBackport.MODID + ":" + this.name));
	}


	public String getName() {
		return this.name;
	}
}