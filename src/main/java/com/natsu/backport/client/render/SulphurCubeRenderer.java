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
 * The 26.2 ordering : the main pass draws the inner core or the swallowed
 * block, then the translucent shell layers over it, exactly like the vanilla
 * inner layer submitting at order minus one.
 */
public class SulphurCubeRenderer extends MobRenderer<SulphurCube, SulphurCubeModel> {

	private static final ResourceLocation INNER =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_inner.png");
	private static final ResourceLocation INNER_SMALL =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_inner_small.png");

	private final SulphurCubeModel innerModel;
	private final SulphurCubeModel smallInnerModel;

	public SulphurCubeRenderer(EntityRendererProvider.Context context) {
		super(context, new SulphurCubeModel(SulphurCubeModel.inner().bakeRoot()), 0.625F);
		this.innerModel = this.model;
		this.smallInnerModel = new SulphurCubeModel(SulphurCubeModel.smallInner().bakeRoot());
		this.addLayer(new SulphurCubeBodyItemLayer(this));
		this.addLayer(new SulphurCubeOuterLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(SulphurCube cube) {
		return cube.getSize() <= 1 ? INNER_SMALL : INNER;
	}

	@Override
	protected RenderType getRenderType(SulphurCube cube, boolean visible, boolean invisibleToPlayer, boolean glowing) {
		// the core hides when a block sits inside, the layers do the rest
		return cube.hasBodyItem() ? null : RenderType.entityTranslucent(this.getTextureLocation(cube));
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
		this.model = cube.getSize() <= 1 ? this.smallInnerModel : this.innerModel;
		super.render(cube, yaw, partialTick, poseStack, buffers, packedLight);
	}
}
