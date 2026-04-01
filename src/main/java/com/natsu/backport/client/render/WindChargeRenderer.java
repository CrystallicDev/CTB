package com.natsu.backport.client.render;

import com.natsu.backport.common.entity.WindChargeEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT)
public class WindChargeRenderer extends ThrownItemRenderer<WindChargeEntity>{

	public WindChargeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	
}
