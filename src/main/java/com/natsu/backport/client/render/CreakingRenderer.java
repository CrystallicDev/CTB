package com.natsu.backport.client.render;

import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.entity.model.CreakingModel;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.LayerGlowingAreasGeo;

@OnlyIn(value = Dist.CLIENT)
public class CreakingRenderer extends GeoEntityRenderer<Creaking> {

    public CreakingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CreakingModel());
        this.shadowRadius = 0.5f;
        // eyes and mouth cracks glow in the dark (zones from creaking.png.mcmeta)
        this.addLayer(new LayerGlowingAreasGeo<>(this, this::getTextureLocation,
            e -> this.getGeoModelProvider().getModelLocation(e), RenderType::eyes));
    }

    // vanilla creakings never tip over on death, they twitch then crumble
    @Override
    protected float getDeathMaxRotation(Creaking entity) {
        return 0.0F;
    }
}