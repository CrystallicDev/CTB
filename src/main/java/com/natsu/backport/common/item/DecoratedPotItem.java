package com.natsu.backport.common.item;

import java.util.function.Consumer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

public class DecoratedPotItem extends BlockItem {

	public DecoratedPotItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return com.natsu.backport.client.render.DecoratedPotItemRenderer.INSTANCE.get();
			}
		});
	}
}
