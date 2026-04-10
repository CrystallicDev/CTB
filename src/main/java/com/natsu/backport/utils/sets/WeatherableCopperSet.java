package com.natsu.backport.utils.sets;

import java.lang.reflect.Constructor;
import java.util.function.Function;
import java.util.function.Supplier;

import com.natsu.salm.block.cristallite.vine.VineHeadBase;

import net.minecraft.core.Direction;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class WeatherableCopperSet<Weatherable extends Block, Waxed extends Block> implements DefaultSet {

	public final RegistryObject<Block> block;
	public final RegistryObject<Block> exposedBlock;
	public final RegistryObject<Block> weatheredBlock;
	public final RegistryObject<Block> oxidizedBlock;

	public final RegistryObject<Block> blockWaxed;
	public final RegistryObject<Block> exposedBlockWaxed;
	public final RegistryObject<Block> weatheredBlockWaxed;
	public final RegistryObject<Block> oxidizedBlockWaxed;
	
	public WeatherableCopperSet(DeferredRegister<Block> BLOCKS, String blockName, float strength, Class<Weatherable> weatherable, Class<Waxed> waxed) {
		block = BLOCKS.register(blockName, () -> createWeatherable(weatherable, WeatherState.UNAFFECTED, BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).strength(strength)));
		exposedBlock = BLOCKS.register("exposed"+blockName, () -> createWeatherable(weatherable, WeatherState.EXPOSED, BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).strength(strength)));
		weatheredBlock = BLOCKS.register("weathered"+blockName, () -> createWeatherable(weatherable, WeatherState.WEATHERED, BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).strength(strength)));
		oxidizedBlock = BLOCKS.register("oxidized"+blockName, () -> createWeatherable(weatherable, WeatherState.OXIDIZED, BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).strength(strength)));

		blockWaxed = BLOCKS.register("waxed_"+blockName, () -> createWaxed(waxed, WeatherState.UNAFFECTED, BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).strength(strength)));
		exposedBlockWaxed = BLOCKS.register("waxed_exposed_"+blockName, () -> createWaxed(waxed, WeatherState.EXPOSED, BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).strength(strength)));
		weatheredBlockWaxed = BLOCKS.register("waxed_weathered_"+blockName, () -> createWaxed(waxed, WeatherState.WEATHERED, BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).strength(strength)));
		oxidizedBlockWaxed = BLOCKS.register("waxed_oxidized_"+blockName, () -> createWaxed(waxed, WeatherState.OXIDIZED, BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).strength(strength)));
	
		WeatheringCopper.NEXT_BY_BLOCK.get().put(block.get(), exposedBlock.get());
		WeatheringCopper.NEXT_BY_BLOCK.get().put(exposedBlock.get(), weatheredBlock.get());
		WeatheringCopper.NEXT_BY_BLOCK.get().put(weatheredBlock.get(), oxidizedBlock.get());
		
		WeatheringCopper.PREVIOUS_BY_BLOCK.get().put(oxidizedBlock.get(), weatheredBlock.get());
		WeatheringCopper.PREVIOUS_BY_BLOCK.get().put(weatheredBlock.get(), exposedBlock.get());
		WeatheringCopper.PREVIOUS_BY_BLOCK.get().put(exposedBlock.get(), block.get());
	
	}
	
	
	public Block createWeatherable(Class<Weatherable> clazz, WeatherState state, Properties properties) {
        try {
            Constructor<Weatherable> ctor = clazz.getConstructor(WeatherState.class, Properties.class);
            return ctor.newInstance(state, properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public Block createWaxed(Class<Waxed> clazz, WeatherState state, Properties properties) {
        try {
            Constructor<Waxed> ctor = clazz.getConstructor(WeatherState.class, Properties.class);
            return ctor.newInstance(state, properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	
	@Override
	public void addBlockTags(Function<TagKey<Block>, TagAppender<Block>> tag) {
		
	}

	@Override
	public void setRenderTypes() {
		
	}

}
