package ctbackport.gametest;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.entity.CopperChestBlockEntity;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class CopperGolemGameTests {

	/** The stroll goal can walk a golem off the platform, fence it in. */
	private static void fence(GameTestHelper helper) {
		for (int x = 0; x <= 8; x++) {
			for (int z = 0; z <= 8; z++) {
				if (x == 0 || x == 8 || z == 0 || z == 8) {
					for (int y = 2; y <= 4; y++) {
						helper.setBlock(new BlockPos(x, y, z), Blocks.BARRIER);
					}
				}
			}
		}
	}

	// full loop: the golem empties the copper chest into the plain chest
	@GameTest(template = "empty", timeoutTicks = 1200)
	public static void golemSortsItems(GameTestHelper helper) {
		fence(helper);
		BlockPos copperPos = new BlockPos(1, 2, 3);
		BlockPos chestPos = new BlockPos(5, 2, 3);
		helper.setBlock(copperPos, CTBBlocks.COPPER_CHEST.get());
		helper.setBlock(chestPos, Blocks.CHEST);

		CopperChestBlockEntity copperChest = (CopperChestBlockEntity) helper.getBlockEntity(copperPos);
		copperChest.setItem(0, new ItemStack(Items.IRON_INGOT, 8));
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(chestPos);
		chest.setItem(0, new ItemStack(Items.IRON_INGOT, 1));

		CopperGolem golem = helper.spawn(CTBEntities.COPPER_GOLEM.get(), new BlockPos(3, 2, 3));
		if (golem.getWeatherLevel() != 0) {
			throw new GameTestAssertException("fresh golem should be unaffected");
		}

		helper.succeedWhen(() -> {
			int inChest = chest.getItem(0).getCount();
			boolean copperEmpty = copperChest.getItem(0).isEmpty();
			if (!copperEmpty || inChest < 9) {
				throw new GameTestAssertException("expected the ingots moved, chest has " + inChest + " copper empty " + copperEmpty);
			}
		});
	}

	@GameTest(template = "empty")
	public static void statueScrapesBackToGolem(GameTestHelper helper) {
		fence(helper);
		BlockPos pos = new BlockPos(3, 2, 3);
		helper.setBlock(pos, CTBBlocks.COPPER_GOLEM_STATUE.get());
		com.natsu.backport.common.block.entity.CopperGolemStatueBlockEntity statue =
				(com.natsu.backport.common.block.entity.CopperGolemStatueBlockEntity) helper.getBlockEntity(pos);
		statue.setWeatherLevel(0);
		statue.releaseGolem(helper.getLevel(), helper.absolutePos(pos), helper.getBlockState(pos));

		helper.succeedWhen(() -> {
			if (!helper.getLevel().getBlockState(helper.absolutePos(pos)).isAir()) {
				throw new GameTestAssertException("statue block should be gone");
			}
			if (helper.getLevel().getEntitiesOfClass(CopperGolem.class,
					new net.minecraft.world.phys.AABB(helper.absolutePos(pos)).inflate(2.0)).isEmpty()) {
				throw new GameTestAssertException("a golem should have been released");
			}
		});
	}
}
