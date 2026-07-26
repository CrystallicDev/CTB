package com.natsu.backport.client.render;

import com.natsu.backport.common.entity.SulphurCube;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * The 26.2 sulfur cube meshes : a translucent outer shell around an inner
 * core, adult on a 128 sheet and small on a 64 one. Boxes sit on the feet
 * like the 1.18.2 model space expects.
 */
public class SulphurCubeModel extends EntityModel<SulphurCube> {

	private final ModelPart cube;

	public SulphurCubeModel(ModelPart root) {
		this.cube = root.getChild("cube");
	}

	public static LayerDefinition outer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("cube", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0F, 6.0F, -9.0F, 18.0F, 18.0F, 18.0F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition inner() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("cube", CubeListBuilder.create()
				.texOffs(0, 36).addBox(-8.0F, 7.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition smallOuter() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("cube", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-5.0F, 14.0F, -5.0F, 10.0F, 10.0F, 10.0F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 64);
	}

	public static LayerDefinition smallInner() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("cube", CubeListBuilder.create()
				.texOffs(0, 20).addBox(-4.0F, 15.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 64);
	}

	@Override
	public void setupAnim(SulphurCube cube, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack,
			com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		this.cube.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
