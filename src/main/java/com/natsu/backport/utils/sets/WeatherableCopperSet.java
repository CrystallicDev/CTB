package com.natsu.backport.utils.sets;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.natsu.backport.common.item.CTBBlockItemFactory;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class WeatherableCopperSet<Weatherable extends Block, Waxed extends Block> implements DefaultSet {

	public final String name;

	public final RegistryObject<Block> block;
	public final RegistryObject<Block> exposedBlock;
	public final RegistryObject<Block> weatheredBlock;
	public final RegistryObject<Block> oxidizedBlock;

	public final RegistryObject<Block> blockWaxed;
	public final RegistryObject<Block> exposedBlockWaxed;
	public final RegistryObject<Block> weatheredBlockWaxed;
	public final RegistryObject<Block> oxidizedBlockWaxed;

	public final RegistryObject<Item> blockItem;
	public final RegistryObject<Item> exposedBlockItem;
	public final RegistryObject<Item> weatheredBlockItem;
	public final RegistryObject<Item> oxidizedBlockItem;

	public final RegistryObject<Item> blockWaxedItem;
	public final RegistryObject<Item> exposedBlockWaxedItem;
	public final RegistryObject<Item> weatheredBlockWaxedItem;
	public final RegistryObject<Item> oxidizedBlockWaxedItem;

	public WeatherableCopperSet(DeferredRegister<Block> BLOCKS, DeferredRegister<Item> ITEMS, String blockName, float strength, Class<Weatherable> weatherable, Class<Waxed> waxed) {
		name = blockName;

		block = BLOCKS.register(blockName, () -> createWeatherableState(weatherable, WeatherState.UNAFFECTED, BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).strength(strength)));
		exposedBlock = BLOCKS.register("exposed_"+blockName, () -> createWeatherableState(weatherable, WeatherState.EXPOSED, BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).strength(strength)));
		weatheredBlock = BLOCKS.register("weathered_"+blockName, () -> createWeatherableState(weatherable, WeatherState.WEATHERED, BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).strength(strength)));
		oxidizedBlock = BLOCKS.register("oxidized_"+blockName, () -> createWeatherableState(weatherable, WeatherState.OXIDIZED, BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).strength(strength)));

		blockWaxed = BLOCKS.register("waxed_"+blockName, () -> createWaxedState(waxed, WeatherState.UNAFFECTED, BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).strength(strength)));
		exposedBlockWaxed = BLOCKS.register("waxed_exposed_"+blockName, () -> createWaxedState(waxed, WeatherState.EXPOSED, BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).strength(strength)));
		weatheredBlockWaxed = BLOCKS.register("waxed_weathered_"+blockName, () -> createWaxedState(waxed, WeatherState.WEATHERED, BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).strength(strength)));
		oxidizedBlockWaxed = BLOCKS.register("waxed_oxidized_"+blockName, () -> createWaxedState(waxed, WeatherState.OXIDIZED, BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).strength(strength)));

		blockItem = CTBBlockItemFactory.blockItem(ITEMS, block);
		exposedBlockItem = CTBBlockItemFactory.blockItem(ITEMS, exposedBlock);
		weatheredBlockItem = CTBBlockItemFactory.blockItem(ITEMS, weatheredBlock);
		oxidizedBlockItem = CTBBlockItemFactory.blockItem(ITEMS, oxidizedBlock);

		blockWaxedItem = CTBBlockItemFactory.blockItem(ITEMS, blockWaxed);
		exposedBlockWaxedItem = CTBBlockItemFactory.blockItem(ITEMS, exposedBlockWaxed);
		weatheredBlockWaxedItem = CTBBlockItemFactory.blockItem(ITEMS, weatheredBlockWaxed);
		oxidizedBlockWaxedItem = CTBBlockItemFactory.blockItem(ITEMS, oxidizedBlockWaxed);

		

	}


	public Block createWeatherable(Class<Weatherable> clazz, WeatherState state, Properties properties) {
        try {
            Constructor<Weatherable> ctor = clazz.getConstructor(Properties.class);
            return ctor.newInstance(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	public Block createWaxed(Class<Waxed> clazz, WeatherState state, Properties properties) {
        try {
            Constructor<Waxed> ctor = clazz.getConstructor(Properties.class);
            return ctor.newInstance(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	public Block createWeatherableState(Class<Weatherable> clazz, WeatherState state, Properties properties) {
        try {
            Constructor<Weatherable> ctor = clazz.getConstructor(WeatherState.class, Properties.class);
            return ctor.newInstance(state, properties);
        } catch (Exception e) {
        	return createWeatherable(clazz, state, properties);
        }
    }

	public Block createWaxedState(Class<Waxed> clazz, WeatherState state, Properties properties) {
        try {
            Constructor<Waxed> ctor = clazz.getConstructor(WeatherState.class, Properties.class);
            return ctor.newInstance(state, properties);
        } catch (Exception e) {
            return createWaxed(clazz, state, properties);
        }
    }


	@Override
	public void addBlockTags(Function<TagKey<Block>, TagAppender<Block>> tag) {

	}

	@Override
	public void setRenderTypes() {

	}


	@Override
	public void setFlammables(Map<Block, Integer> flameOdds, Map<Block, Integer> burnOdds) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setCompostables(Object2FloatMap<ItemLike> compostables) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setWeatherable(BiMap<Block, Block> nextByBlock, BiMap<Block, Block> previousByBlock) {
		nextByBlock.put(block.get(), exposedBlock.get());
		nextByBlock.put(exposedBlock.get(), weatheredBlock.get());
		nextByBlock.put(weatheredBlock.get(), oxidizedBlock.get());

		previousByBlock.put(oxidizedBlock.get(), weatheredBlock.get());
		previousByBlock.put(weatheredBlock.get(), exposedBlock.get());
		previousByBlock.put(exposedBlock.get(), block.get());
	}

	@Override
	public void setWaxables(BiMap<Block, Block> waxables) {
		waxables.put(block.get(), blockWaxed.get());
		waxables.put(exposedBlock.get(), exposedBlockWaxed.get());
		waxables.put(weatheredBlock.get(), weatheredBlockWaxed.get());
		waxables.put(oxidizedBlock.get(), oxidizedBlockWaxed.get());
	}

}
