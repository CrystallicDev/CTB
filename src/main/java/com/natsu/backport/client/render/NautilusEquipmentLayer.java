package com.natsu.backport.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.AbstractNautilus;
import com.natsu.backport.common.item.NautilusArmorItem;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

/** Saddle and armor overlays, textures authored for the custom nautilus UV. */
@OnlyIn(value = Dist.CLIENT)
public class NautilusEquipmentLayer extends AbstractLayerGeo<AbstractNautilus> {

	private static final ResourceLocation SADDLE = new ResourceLocation(CTBackport.MODID, "textures/entity/nautilus/saddle.png");

	public NautilusEquipmentLayer(GeoEntityRenderer<AbstractNautilus> renderer) {
		super(renderer, renderer::getTextureLocation, e -> renderer.getGeoModelProvider().getModelLocation(e));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractNautilus nautilus,
			float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
			float headPitch) {
		if (nautilus.isInvisible()) {
			return;
		}
		ItemStack armor = nautilus.getArmor();
		if (armor.getItem() instanceof NautilusArmorItem) {
			ResourceLocation id = ForgeRegistries.ITEMS.getKey(armor.getItem());
			String material = id.getPath().replace("_nautilus_armor", "");
			reRenderCurrentModelInRenderer(nautilus, partialTick, poseStack, bufferSource, packedLight,
					RenderType.entityCutoutNoCull(new ResourceLocation(CTBackport.MODID,
							"textures/entity/nautilus/armor_" + material + ".png")));
		}
		if (nautilus.isSaddled()) {
			reRenderCurrentModelInRenderer(nautilus, partialTick, poseStack, bufferSource, packedLight,
					RenderType.entityCutoutNoCull(SADDLE));
		}
	}
}
