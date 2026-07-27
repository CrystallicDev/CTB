package com.natsu.backport.common.worldgen;

import java.util.List;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.natsu.backport.CTBackport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * The 26.2 sulfur_spring feature : a weighted pick of four sizes, each a patch
 * of tuff scattered over the floor then a spring structure template sunk seven
 * blocks under the origin, with a random rotation around its center.
 */
public class SulfurSpringFeature extends Feature<NoneFeatureConfiguration> {

	private record SpringSize(int weight, int tuffCount, int tuffSpread, List<String> templates) {
	}

	private static final List<SpringSize> SIZES = List.of(
			new SpringSize(200, 64, 7, List.of("sulfur_spring_small_1", "sulfur_spring_small_2",
					"sulfur_spring_small_3", "sulfur_spring_small_4")),
			new SpringSize(90, 80, 8, List.of("sulfur_spring_medium_1", "sulfur_spring_medium_2",
					"sulfur_spring_medium_3")),
			new SpringSize(20, 96, 9, List.of("sulfur_spring_large_1", "sulfur_spring_large_2")),
			new SpringSize(5, 128, 10, List.of("sulfur_spring_extra_large_1")));
	private static final int TOTAL_WEIGHT = SIZES.stream().mapToInt(SpringSize::weight).sum();
	private static final int TEMPLATE_SINK = -7;

	public SulfurSpringFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		Random random = context.random();
		BlockPos origin = context.origin();
		SpringSize size = pickSize(random);

		for (int i = 0; i < size.tuffCount; i++) {
			BlockPos target = origin.offset(triangular(random, size.tuffSpread), triangular(random, 3),
					triangular(random, size.tuffSpread));
			BlockPos solid = scanDownToSolid(level, target, 4);
			if (solid != null) {
				level.setBlock(solid, Blocks.TUFF.defaultBlockState(), 2);
			}
		}

		String template = size.templates.get(random.nextInt(size.templates.size()));
		return placeTemplate(level, random, origin.offset(0, TEMPLATE_SINK, 0), template);
	}

	private static SpringSize pickSize(Random random) {
		int roll = random.nextInt(TOTAL_WEIGHT);
		for (SpringSize size : SIZES) {
			roll -= size.weight;
			if (roll < 0) {
				return size;
			}
		}
		return SIZES.get(0);
	}

	/** The trapezoid spread with no plateau of the vanilla data, a triangular distribution. */
	private static int triangular(Random random, int spread) {
		return (random.nextInt(2 * spread + 1) - spread + random.nextInt(2 * spread + 1) - spread) / 2;
	}

	private static BlockPos scanDownToSolid(WorldGenLevel level, BlockPos origin, int maxSteps) {
		BlockPos.MutableBlockPos pos = origin.mutable();
		for (int i = 0; i <= maxSteps; i++) {
			BlockState state = level.getBlockState(pos);
			if (state.getMaterial().isSolidBlocking()) {
				return pos.immutable();
			}
			pos.move(Direction.DOWN);
		}
		return null;
	}

	private boolean placeTemplate(WorldGenLevel level, Random random, BlockPos origin, String name) {
		StructureManager manager = level.getLevel().getServer().getStructureManager();
		StructureTemplate template = manager
				.getOrCreate(new ResourceLocation(CTBackport.MODID, "spring/" + name));
		Rotation rotation = Rotation.getRandom(random);
		BlockPos pos = origin.offset(rotatedOffset(rotation, Direction.Axis.X, template))
				.offset(rotatedOffset(rotation, Direction.Axis.Z, template));
		StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setRandom(random);
		return template.placeInWorld(level, pos, pos, settings, random, 3);
	}

	/** Centers the rotated template on the origin, the 26.2 TemplateFeature offset. */
	private static Vec3i rotatedOffset(Rotation rotation, Direction.Axis axis, StructureTemplate template) {
		Direction negative = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
		return rotation.rotate(negative).getNormal().multiply(template.getSize().get(axis) / 2);
	}
}
