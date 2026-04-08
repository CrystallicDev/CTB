package com.natsu.backport.client.render;

import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.entity.model.CreakingModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CreakingRenderer extends GeoEntityRenderer<Creaking>{

	public CreakingRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new CreakingModel());
	}
	
}
