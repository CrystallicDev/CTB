package ctbackport.gametest;

import java.util.Optional;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.entity.TrialSpawnerBlockEntity;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class TrialChambersStructureGameTests {

	private static StructureTemplate load(GameTestHelper helper, String id) {
		Optional<StructureTemplate> template = helper.getLevel().getStructureManager()
				.get(new ResourceLocation(CTBackport.MODID, "trial_chambers/" + id));
		if (template.isEmpty()) {
			throw new GameTestAssertException("template trial_chambers/" + id + " did not load");
		}
		return template.get();
	}

	private static void place(GameTestHelper helper, StructureTemplate template, BlockPos rel) {
		BlockPos origin = helper.absolutePos(rel);
		template.placeInWorld(helper.getLevel(), origin, origin,
				new StructurePlaceSettings().setBoundingBox(BoundingBox.fromCorners(origin, origin.offset(60, 30, 60))),
				helper.getLevel().getRandom(), 2);
	}

	// a mistyped block id in the palette remap would silently turn into air
	@GameTest(template = "empty")
	public static void spawnerTemplatePlacesRealBlocks(GameTestHelper helper) {
		StructureTemplate template = load(helper, "spawner/ranged/skeleton");
		place(helper, template, new BlockPos(1, 1, 1));

		boolean foundSpawner = false;
		boolean foundConfig = false;
		for (BlockPos pos : BlockPos.betweenClosed(helper.absolutePos(new BlockPos(0, 0, 0)), helper.absolutePos(new BlockPos(6, 6, 6)))) {
			if (helper.getLevel().getBlockState(pos).is(CTBBlocks.TRIAL_SPAWNER.get())) {
				foundSpawner = true;
				BlockEntity be = helper.getLevel().getBlockEntity(pos);
				if (be instanceof TrialSpawnerBlockEntity spawner
						&& !spawner.getTrialSpawner().normalConfig().spawnPotentials().isEmpty()) {
					foundConfig = true;
				}
			}
		}
		if (!foundSpawner) {
			throw new GameTestAssertException("no trial spawner placed from the template");
		}
		if (!foundConfig) {
			throw new GameTestAssertException("trial spawner config_id did not resolve to a config");
		}
		helper.succeed();
	}

	@GameTest(template = "empty")
	public static void chamberTemplateUsesModBlocks(GameTestHelper helper) {
		StructureTemplate template = load(helper, "corridor/first_plate");
		place(helper, template, new BlockPos(0, 1, 0));

		int modBlocks = 0;
		int vanillaCopper = 0;
		for (BlockPos pos : BlockPos.betweenClosed(helper.absolutePos(new BlockPos(0, 0, 0)), helper.absolutePos(new BlockPos(6, 3, 6)))) {
			var state = helper.getLevel().getBlockState(pos);
			String ns = state.getBlock().getRegistryName().getNamespace();
			if (ns.equals(CTBackport.MODID)) {
				modBlocks++;
			}
			if (state.is(Blocks.WAXED_OXIDIZED_COPPER) || state.is(Blocks.WAXED_COPPER_BLOCK)) {
				vanillaCopper++;
			}
		}
		if (modBlocks == 0 && vanillaCopper == 0) {
			throw new GameTestAssertException("template placed neither mod nor vanilla copper blocks");
		}
		helper.succeed();
	}
}
