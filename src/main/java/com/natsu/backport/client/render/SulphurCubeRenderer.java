package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.SulphurCube;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The 26.2 look : the translucent outer shell, its inner content drawn by the
 * layer so both follow the exact same pose.
 */
public class SulphurCubeRenderer extends MobRenderer<SulphurCube, SulphurCubeModel> {

	private static final ResourceLocation OUTER =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_outer.png");
	private static final ResourceLocation OUTER_SMALL =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_outer_small.png");

	private final SulphurCubeModel outerModel;
	private final SulphurCubeModel smallOuterModel;

	public SulphurCubeRenderer(EntityRendererProvider.Context context) {
		super(context, new SulphurCubeModel(SulphurCubeModel.outer().bakeRoot()), 0.625F);
		this.outerModel = this.model;
		this.smallOuterModel = new SulphurCubeModel(SulphurCubeModel.smallOuter().bakeRoot());
		this.addLayer(new SulphurCubeInnerLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(SulphurCube cube) {
		return cube.getSize() <= 1 ? OUTER_SMALL : OUTER;
	}

	@Override
	protected RenderType getRenderType(SulphurCube cube, boolean visible, boolean invisibleToPlayer, boolean glowing) {
		return RenderType.entityTranslucent(this.getTextureLocation(cube));
	}

	@Override
	protected void scale(SulphurCube cube, PoseStack poseStack, float partialTick) {
		float shadowScale = 0.999F;
		poseStack.scale(shadowScale, shadowScale, shadowScale);
		poseStack.translate(0.0, 0.001, 0.0);
		float squish = Mth.lerp(partialTick, cube.oSquish, cube.squish) / (cube.getSize() * 0.5F + 1.0F);
		float stretch = 1.0F / (squish + 1.0F);
		poseStack.scale(stretch, 1.0F / stretch, stretch);
	}

	@Override
	public void render(SulphurCube cube, float yaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffers, int packedLight) {
		this.model = cube.getSize() <= 1 ? this.smallOuterModel : this.outerModel;
		super.render(cube, yaw, partialTick, poseStack, buffers, packedLight);
	}
}
