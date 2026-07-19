package ctbackport.gametest;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class CompostFlammableGameTests {

	// common setup ran before the tests, the vanilla maps must contain our entries
	@GameTest(template = "empty")
	public static void compostablesAreRegistered(GameTestHelper helper) {
		assertCompost(helper, CTBBlocks.PALE_OAK_LEAVES.leavesItem.get(), 0.3F);
		assertCompost(helper, CTBBlocks.PALE_MOSS.mossItem.get(), 0.65F);
		assertCompost(helper, CTBBlocks.OPEN_EYEBLOSSOM.get().asItem(), 0.65F);
		assertCompost(helper, CTBBlocks.PALE_OAK_SAPLING.get().asItem(), 0.3F);
		helper.succeed();
	}

	private static void assertCompost(GameTestHelper helper, net.minecraft.world.level.ItemLike item, float expected) {
		float actual = ComposterBlock.COMPOSTABLES.getOrDefault(item.asItem(), -1.0F);
		if (Math.abs(actual - expected) > 0.001F) {
			helper.fail("compost " + item + " = " + actual + ", expected " + expected);
		}
	}
}
