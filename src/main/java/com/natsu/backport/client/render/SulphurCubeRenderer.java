package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.SulphurCube;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * The 26.2 look : a translucent outer shell, and inside either the yellow
 * core or the swallowed item.
 */
public class SulphurCubeRenderer extends MobRenderer<SulphurCube, SulphurCubeModel> {

	private static final ResourceLocation OUTER =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_outer.png");
	private static final ResourceLocation OUTER_SMALL =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_outer_small.png");
	private static final ResourceLocation INNER =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_inner.png");
	private static final ResourceLocation INNER_SMALL =
			new ResourceLocation(CTBackport.MODID, "textures/entity/sulfur_cube/sulfur_cube_inner_small.png");

	private final SulphurCubeModel outerModel;
	private final SulphurCubeModel innerModel;
	private final SulphurCubeModel smallOuterModel;
	private final SulphurCubeModel smallInnerModel;

	public SulphurCubeRenderer(EntityRendererProvider.Context context) {
		super(context, new SulphurCubeModel(SulphurCubeModel.outer().bakeRoot()), 0.625F);
		this.outerModel = this.model;
		this.innerModel = new SulphurCubeModel(SulphurCubeModel.inner().bakeRoot());
		this.smallOuterModel = new SulphurCubeModel(SulphurCubeModel.smallOuter().bakeRoot());
		this.smallInnerModel = new SulphurCubeModel(SulphurCubeModel.smallInner().bakeRoot());
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
		float size = cube.getSize() <= 1 ? 1.0F : 1.0F;
		float squish = Mth.lerp(partialTick, cube.oSquish, cube.squish) / (cube.getSize() * 0.5F + 1.0F);
		float stretch = 1.0F / (squish + 1.0F);
		poseStack.scale(stretch * size, 1.0F / stretch * size, stretch * size);
	}

	@Override
	public void render(SulphurCube cube, float yaw, float partialTick, PoseStack poseStack,
			MultiBufferSource buffers, int packedLight) {
		boolean small = cube.getSize() <= 1;
		this.model = small ? this.smallOuterModel : this.outerModel;

		// the inner core or the item render first, the shell wraps them
		if (!cube.isInvisible()) {
			poseStack.pushPose();
			if (cube.hasBodyItem()) {
				ItemStack body = cube.getBodyItem();
				poseStack.pushPose();
				poseStack.translate(0.0, small ? 0.28 : 0.55, 0.0);
				float itemScale = small ? 0.5F : 0.95F;
				poseStack.scale(itemScale, itemScale, itemScale);
				Minecraft.getInstance().getItemRenderer().renderStatic(body, ItemTransforms.TransformType.GROUND,
						packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers, cube.getId());
				poseStack.popPose();
			} else {
				SulphurCubeModel inner = small ? this.smallInnerModel : this.innerModel;
				ResourceLocation texture = small ? INNER_SMALL : INNER;
				poseStack.pushPose();
				// the model space flip the dispatcher applies around super.render
				poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(180.0F - yaw));
				poseStack.scale(-1.0F, -1.0F, 1.0F);
				poseStack.translate(0.0, -1.501, 0.0);
				VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(texture));
				inner.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
				poseStack.popPose();
			}
			poseStack.popPose();
		}
		super.render(cube, yaw, partialTick, poseStack, buffers, packedLight);
	}
}
