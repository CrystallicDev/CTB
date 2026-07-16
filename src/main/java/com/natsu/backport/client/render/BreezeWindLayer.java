package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Breeze;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

/**
 * The wind rings are hidden from the base pass and re-rendered here with a
 * scrolling texture, both faces visible — the vanilla breeze wind look.
 */
@OnlyIn(value = Dist.CLIENT)
public class BreezeWindLayer extends AbstractLayerGeo<Breeze> {

	public static final String WIND_BONE = "wind_body";

	// no lit shader with texture scrolling in 1.18, so the scroll is prebaked
	private static final int FRAMES = 16;
	private static final ResourceLocation[] WIND_TEXTURES = new ResourceLocation[FRAMES];
	static {
		for (int i = 0; i < FRAMES; i++) {
			WIND_TEXTURES[i] = new ResourceLocation(CTBackport.MODID, "textures/entity/breeze/breeze_wind_" + i + ".png");
		}
	}

	public BreezeWindLayer(GeoEntityRenderer<Breeze> renderer) {
		super(renderer, renderer::getTextureLocation, e -> renderer.getGeoModelProvider().getModelLocation(e));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Breeze breeze,
			float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
			float headPitch) {
		GeoModel model = getEntityModel().getModel(this.funcGetCurrentModel.apply(breeze));

		setHiddenExcept(model, WIND_BONE);
		float scroll = (breeze.tickCount + partialTick) * 0.02F;
		int frame = (int) (scroll * FRAMES) % FRAMES;
		reRenderCurrentModelInRenderer(breeze, partialTick, poseStack, bufferSource, packedLight,
				RenderType.entityTranslucent(WIND_TEXTURES[frame]));
		showAll(model);
	}

	private static void setHiddenExcept(GeoModel model, String keep) {
		for (GeoBone bone : model.topLevelBones) {
			setHiddenRecursive(bone, !bone.getName().equals(keep));
		}
	}

	private static void showAll(GeoModel model) {
		for (GeoBone bone : model.topLevelBones) {
			setHiddenRecursive(bone, false);
		}
	}

	static void setHiddenRecursive(GeoBone bone, boolean hidden) {
		bone.setHidden(hidden);
		for (GeoBone child : bone.childBones) {
			setHiddenRecursive(child, hidden);
		}
	}
}
