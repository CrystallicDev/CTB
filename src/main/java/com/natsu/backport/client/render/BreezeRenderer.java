package com.natsu.backport.client.render;

import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.model.BreezeModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@OnlyIn(value = Dist.CLIENT)
public class BreezeRenderer extends GeoEntityRenderer<Breeze> {

    public BreezeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BreezeModel());
        this.shadowRadius = 0.5f;
    }
}