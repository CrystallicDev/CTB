package com.natsu.backport.common.item;

import java.util.function.Consumer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/** The statue item renders the actual statue model instead of a placeholder cube. */
public class CopperGolemStatueItem extends BlockItem implements IAnimatable {

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public CopperGolemStatueItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void registerControllers(AnimationData data) {
		// static pose, the model as authored
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return com.natsu.backport.client.render.CopperGolemStatueItemRenderer.INSTANCE.get();
			}
		});
	}
}
