package com.natsu.backport.client.render;

import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.entity.model.CreakingModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@OnlyIn(value = Dist.CLIENT)
public class CreakingRenderer extends GeoEntityRenderer<Creaking> {

    public CreakingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CreakingModel());
        this.shadowRadius = 0.5f;
    }

    // vanilla creakings never tip over on death, they twitch then crumble
    @Override
    protected float getDeathMaxRotation(Creaking entity) {
        return 0.0F;
    }
}