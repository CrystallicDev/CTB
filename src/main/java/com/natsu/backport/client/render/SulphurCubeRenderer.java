package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.SulphurCube;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.block.state.BlockState;

public class SulphurCubeRenderer extends SlimeRenderer {
	 
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CTBackport.MODID, "textures/entity/sulphur_cube.png");
 
    public SulphurCubeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
 
    @Override
    public ResourceLocation getTextureLocation(Slime slime) {
        return TEXTURE;
    }
 
    @Override
    public void render(Slime slime, float yaw, float partialTicks,
                       PoseStack pose, MultiBufferSource buffers, int packedLight) {
        super.render(slime, yaw, partialTicks, pose, buffers, packedLight);
 
        if (slime instanceof SulphurCube sulphur && sulphur.hasBlock()) {
            BlockState held = sulphur.getHeldBlock();
            int size = slime.getSize();
            pose.pushPose();
            float scale = 0.4F * size;
            pose.translate(0.0D, 0.05D * size, 0.0D);
            pose.scale(scale, scale, scale);
            pose.translate(-0.5D, 0.0D, -0.5D);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(held, pose, buffers, packedLight, OverlayTexture.NO_OVERLAY);
 
            pose.popPose();
        }
    }
}