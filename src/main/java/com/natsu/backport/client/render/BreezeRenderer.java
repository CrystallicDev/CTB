package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.common.entity.Breeze;
import com.natsu.backport.common.entity.model.BreezeModel;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@OnlyIn(value = Dist.CLIENT)
public class BreezeRenderer extends GeoEntityRenderer<Breeze> {

    public BreezeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BreezeModel());
        this.shadowRadius = 0.5f;
        this.addLayer(new BreezeWindLayer(this));
    }

    @Override
    public void render(Breeze breeze, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight) {
        // the wind rings only render through the scrolling layer
        GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelLocation(breeze));
        for (GeoBone bone : model.topLevelBones) {
            if (bone.getName().equals(BreezeWindLayer.WIND_BONE)) {
                BreezeWindLayer.setHiddenRecursive(bone, true);
            }
        }
        super.render(breeze, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
