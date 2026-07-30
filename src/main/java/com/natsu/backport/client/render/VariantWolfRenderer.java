package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.client.ClientVariantCache;
import com.natsu.backport.server.events.WolfEvents;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** The 1.20.5 wolves : nine coats picked by biome, armor painted on top. */
@OnlyIn(value = Dist.CLIENT)
public class VariantWolfRenderer extends WolfRenderer {

	public static final String[] VARIANTS =
			{ "pale", "ashen", "black", "chestnut", "rusty", "snowy", "spotted", "striped", "woods" };

	public VariantWolfRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.addLayer(new WolfArmorLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(Wolf wolf) {
		byte variant = ClientVariantCache.get(wolf.getId());
		String base = variant > 0 && variant < VARIANTS.length ? "wolf_" + VARIANTS[variant] : "wolf";
		String state = wolf.isTame() ? "_tame" : wolf.isAngry() ? "_angry" : "";
		return new ResourceLocation(CTBackport.MODID, "textures/entity/wolf/" + base + state + ".png");
	}

	private static class WolfArmorLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {

		private static final ResourceLocation ARMOR =
				new ResourceLocation(CTBackport.MODID, "textures/entity/wolf/wolf_armor.png");

		WolfArmorLayer(VariantWolfRenderer renderer) {
			super(renderer);
		}

		@Override
		public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, Wolf wolf,
				float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
				float headPitch) {
			int durability = ClientVariantCache.getWolfArmor(wolf.getId());
			if (durability < 0 || wolf.isInvisible()) {
				return;
			}
			this.getParentModel().renderToBuffer(poseStack,
					buffers.getBuffer(RenderType.entityCutoutNoCull(ARMOR)), packedLight,
					OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			float ratio = durability / (float) WolfEvents.MAX_DURABILITY;
			String crackiness = ratio > 0.75F ? null : ratio > 0.5F ? "low" : ratio > 0.25F ? "medium" : "high";
			if (crackiness != null) {
				ResourceLocation cracks = new ResourceLocation(CTBackport.MODID,
						"textures/entity/wolf/wolf_armor_crackiness_" + crackiness + ".png");
				this.getParentModel().renderToBuffer(poseStack,
						buffers.getBuffer(RenderType.entityTranslucent(cracks)), packedLight,
						OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}
}
