package com.natsu.backport.client.render;

import com.natsu.backport.common.entity.AbstractNautilus;
import com.natsu.backport.common.entity.model.NautilusModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NautilusRenderer extends GeoEntityRenderer<AbstractNautilus> {

	public NautilusRenderer(EntityRendererProvider.Context context) {
		super(context, new NautilusModel());
		this.shadowRadius = 0.7F;
	}
}
