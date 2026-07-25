package com.natsu.backport.client.render;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * The 1.21.5 farm animal meshes : same shapes as before but 64x64 textures for
 * pig and cow, plus the fluffy cold variants. The variant textures from the
 * 1.21.11 jar only map onto these.
 */
public final class VariantAnimalModels {

	private VariantAnimalModels() {
	}

	// --- pig, vanilla PigModel.createBasePigModel at 64x64 ---

	private static MeshDefinition basePigMesh() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F)
				.texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F),
				PartPose.offset(0.0F, 12.0F, -6.0F));
		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F),
				PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
		CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F);
		root.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-3.0F, 18.0F, 7.0F));
		root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(3.0F, 18.0F, 7.0F));
		root.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-3.0F, 18.0F, -5.0F));
		root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(3.0F, 18.0F, -5.0F));
		return mesh;
	}

	public static ModelPart bakePig() {
		return LayerDefinition.create(basePigMesh(), 64, 64).bakeRoot();
	}

	/** The cold pig grows a fur shell around the body. */
	public static ModelPart bakeColdPig() {
		MeshDefinition mesh = basePigMesh();
		mesh.getRoot().addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F)
				.texOffs(28, 32).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, new CubeDeformation(0.5F)),
				PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 64).bakeRoot();
	}

	// --- cow, vanilla CowModel.createBaseCowModel at 64x64 ---

	private static MeshDefinition baseCowMesh() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
				.texOffs(1, 33).addBox(-3.0F, 1.0F, -7.0F, 6.0F, 3.0F, 1.0F)
				.texOffs(22, 0).addBox("right_horn", -5.0F, -5.0F, -5.0F, 1.0F, 3.0F, 1.0F)
				.texOffs(22, 0).addBox("left_horn", 4.0F, -5.0F, -5.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offset(0.0F, 4.0F, -8.0F));
		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(18, 4).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F)
				.texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F),
				PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
		CubeListBuilder rightLeg = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
		CubeListBuilder leftLeg = CubeListBuilder.create().mirror().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
		root.addOrReplaceChild("right_hind_leg", rightLeg, PartPose.offset(-4.0F, 12.0F, 7.0F));
		root.addOrReplaceChild("left_hind_leg", leftLeg, PartPose.offset(4.0F, 12.0F, 7.0F));
		root.addOrReplaceChild("right_front_leg", rightLeg, PartPose.offset(-4.0F, 12.0F, -5.0F));
		root.addOrReplaceChild("left_front_leg", leftLeg, PartPose.offset(4.0F, 12.0F, -5.0F));
		return mesh;
	}

	public static ModelPart bakeCow() {
		return LayerDefinition.create(baseCowMesh(), 64, 64).bakeRoot();
	}

	/** The cold cow : woolly body and long horns attached to the head. */
	public static ModelPart bakeColdCow() {
		MeshDefinition mesh = baseCowMesh();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(20, 32).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, new CubeDeformation(0.5F))
				.texOffs(18, 4).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F)
				.texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F),
				PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
		// vanilla hangs the long horns from the root, we hang them from the head
		// so the head parts iterator renders them ; poses made head relative
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F)
				.texOffs(9, 33).addBox(-3.0F, 1.0F, -7.0F, 6.0F, 3.0F, 1.0F),
				PartPose.offset(0.0F, 4.0F, -8.0F));
		head.addOrReplaceChild("cold_right_horn", CubeListBuilder.create()
				.texOffs(0, 40).addBox(-1.5F, -4.5F, -0.5F, 2.0F, 6.0F, 2.0F),
				PartPose.offsetAndRotation(-4.5F, -6.5F, 4.5F, 1.5708F, 0.0F, 0.0F));
		head.addOrReplaceChild("cold_left_horn", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-1.5F, -3.0F, -0.5F, 2.0F, 6.0F, 2.0F),
				PartPose.offsetAndRotation(5.5F, -6.5F, 3.0F, 1.5708F, 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 64).bakeRoot();
	}

	// --- chicken, the 1.18.2 layout with the cold crest and tail feathers ---

	public static ModelPart bakeColdChicken() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F)
				.texOffs(44, 0).addBox(-3.0F, -7.0F, -2.015F, 6.0F, 3.0F, 4.0F),
				PartPose.offset(0.0F, 15.0F, -4.0F));
		root.addOrReplaceChild("beak", CubeListBuilder.create()
				.texOffs(14, 0).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F),
				PartPose.offset(0.0F, 15.0F, -4.0F));
		root.addOrReplaceChild("red_thing", CubeListBuilder.create()
				.texOffs(14, 4).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F),
				PartPose.offset(0.0F, 15.0F, -4.0F));
		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 9).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F)
				.texOffs(38, 9).addBox(0.0F, 3.0F, -1.0F, 0.0F, 3.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
		CubeListBuilder leg = CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
		root.addOrReplaceChild("right_leg", leg, PartPose.offset(-2.0F, 19.0F, 1.0F));
		root.addOrReplaceChild("left_leg", leg, PartPose.offset(1.0F, 19.0F, 1.0F));
		root.addOrReplaceChild("right_wing", CubeListBuilder.create()
				.texOffs(24, 13).addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), PartPose.offset(-4.0F, 13.0F, 0.0F));
		root.addOrReplaceChild("left_wing", CubeListBuilder.create()
				.texOffs(24, 13).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), PartPose.offset(4.0F, 13.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 32).bakeRoot();
	}
}
