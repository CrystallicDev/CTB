package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.SulphurCube;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** The translucent shell, drawn last so the inside blends through it. */
public class SulphurCubeOuterLayer extends RenderLayer<SulphurCube, SulphurCubeModel> {

	private static final ResourceLocation OUTER =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_outer.png");
	private static final ResourceLocation OUTER_SMALL =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_outer_small.png");

	private final SulphurCubeModel outerModel;
	private final SulphurCubeModel smallOuterModel;

	public SulphurCubeOuterLayer(RenderLayerParent<SulphurCube, SulphurCubeModel> parent) {
		super(parent);
		this.outerModel = new SulphurCubeModel(SulphurCubeModel.outer().bakeRoot());
		this.smallOuterModel = new SulphurCubeModel(SulphurCubeModel.smallOuter().bakeRoot());
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, SulphurCube cube,
			float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
			float netHeadYaw, float headPitch) {
		if (cube.isInvisible()) {
			return;
		}
		boolean small = cube.getSize() <= 1;
		SulphurCubeModel model = small ? this.smallOuterModel : this.outerModel;
		ResourceLocation texture = small ? OUTER_SMALL : OUTER;
		VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(texture));
		model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
