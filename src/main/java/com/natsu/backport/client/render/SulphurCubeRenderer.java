package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.SulphurCube;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;

/** The 26.2 look : outer jelly texture with the swallowed item floating inside. */
public class SulphurCubeRenderer extends SlimeRenderer {

	private static final ResourceLocation OUTER =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_outer.png");
	private static final ResourceLocation OUTER_SMALL =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_outer_small.png");

	public SulphurCubeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(Slime slime) {
		return slime.getSize() <= 1 ? OUTER_SMALL : OUTER;
	}

	@Override
	public void render(Slime slime, float yaw, float partialTicks,
			PoseStack pose, MultiBufferSource buffers, int packedLight) {
		super.render(slime, yaw, partialTicks, pose, buffers, packedLight);

		if (slime instanceof SulphurCube cube && cube.hasBodyItem()) {
			ItemStack body = cube.getBodyItem();
			int size = slime.getSize();
			pose.pushPose();
			float scale = 0.5F * size;
			pose.translate(0.0D, 0.28D * size, 0.0D);
			pose.scale(scale, scale, scale);
			Minecraft.getInstance().getItemRenderer().renderStatic(body, ItemTransforms.TransformType.GROUND,
					packedLight, OverlayTexture.NO_OVERLAY, pose, buffers, cube.getId());
			pose.popPose();
		}
	}
}
