package ctbackport.gametest;

import java.util.Random;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBWeatheringCopper;
import com.natsu.backport.common.block.WeatheringCopperChestBlock;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class CopperAgeGameTests {

	// the weathering map is filled at common setup, and randomTick walks it
	@GameTest(template = "empty")
	public static void copperChestWeathers(GameTestHelper helper) {
		if (CTBWeatheringCopper.getNextBlock(CTBBlocks.COPPER_CHEST.get()).orElse(null) != CTBBlocks.EXPOSED_COPPER_CHEST.get()) {
			throw new GameTestAssertException("copper chest should weather into the exposed chest");
		}
		BlockPos pos = new BlockPos(2, 2, 2);
		helper.setBlock(pos, CTBBlocks.COPPER_CHEST.get());
		BlockPos absolute = helper.absolutePos(pos);
		WeatheringCopperChestBlock block = (WeatheringCopperChestBlock) CTBBlocks.COPPER_CHEST.get();
		Random random = new Random(1234L);
		for (int i = 0; i < 10000 && helper.getLevel().getBlockState(absolute).is(CTBBlocks.COPPER_CHEST.get()); i++) {
			block.randomTick(helper.getLevel().getBlockState(absolute), helper.getLevel(), absolute, random);
		}
		if (!helper.getLevel().getBlockState(absolute).is(CTBBlocks.EXPOSED_COPPER_CHEST.get())) {
			throw new GameTestAssertException("repeated random ticks should oxidize the chest one stage");
		}
		helper.succeed();
	}

	// oxidation of one half spreads to the partner through updateShape
	@GameTest(template = "empty")
	public static void doubleCopperChestSyncs(GameTestHelper helper) {
		BlockPos left = new BlockPos(1, 2, 1);
		BlockPos right = new BlockPos(2, 2, 1);
		BlockState leftState = CTBBlocks.COPPER_CHEST.get().defaultBlockState()
				.setValue(ChestBlock.FACING, Direction.NORTH).setValue(ChestBlock.TYPE, ChestType.LEFT);
		BlockState rightState = CTBBlocks.EXPOSED_COPPER_CHEST.get().defaultBlockState()
				.setValue(ChestBlock.FACING, Direction.NORTH).setValue(ChestBlock.TYPE, ChestType.RIGHT);
		helper.setBlock(left, leftState);
		helper.setBlock(right, rightState);
		helper.runAfterDelay(5, () -> {
			BlockState a = helper.getLevel().getBlockState(helper.absolutePos(left));
			BlockState b = helper.getLevel().getBlockState(helper.absolutePos(right));
			if (a.getBlock() != b.getBlock()) {
				throw new GameTestAssertException("connected halves should settle on the same block, got "
						+ a.getBlock() + " and " + b.getBlock());
			}
			helper.succeed();
		});
	}

	// the ritual mapping keeps the oxidation and drops the wax
	@GameTest(template = "empty")
	public static void ritualChestMapping(GameTestHelper helper) {
		BlockState fromWeathered = com.natsu.backport.common.block.CopperChestBlock
				.getFromCopperBlock(Blocks.WEATHERED_COPPER, Direction.EAST);
		if (!fromWeathered.is(CTBBlocks.WEATHERED_COPPER_CHEST.get())
				|| fromWeathered.getValue(ChestBlock.FACING) != Direction.EAST) {
			throw new GameTestAssertException("weathered copper should give the weathered chest facing east");
		}
		BlockState fromWaxed = com.natsu.backport.common.block.CopperChestBlock
				.getFromCopperBlock(Blocks.WAXED_OXIDIZED_COPPER, Direction.NORTH);
		if (!fromWaxed.is(CTBBlocks.OXIDIZED_COPPER_CHEST.get())) {
			throw new GameTestAssertException("waxed bases should give the unwaxed chest, like vanilla");
		}
		helper.succeed();
	}
}
