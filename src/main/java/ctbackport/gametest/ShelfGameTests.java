package ctbackport.gametest;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.CTBSideChainPart;
import com.natsu.backport.common.block.ShelfBlock;
import com.natsu.backport.common.block.entity.ShelfBlockEntity;
import com.natsu.backport.common.registry.CTBBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class ShelfGameTests {

	// items on a broken shelf spill on the ground
	@GameTest(template = "empty")
	public static void shelfDropsContents(GameTestHelper helper) {
		BlockPos pos = new BlockPos(3, 2, 3);
		helper.setBlock(pos, CTBBlocks.OAK_SHELF.get().defaultBlockState()
				.setValue(ShelfBlock.FACING, Direction.NORTH));
		ShelfBlockEntity shelf = (ShelfBlockEntity) helper.getBlockEntity(pos);
		shelf.setItem(1, new ItemStack(Items.DIAMOND, 3));
		helper.destroyBlock(pos);
		helper.succeedWhen(() -> helper.assertItemEntityPresent(Items.DIAMOND, pos, 2.0));
	}

	// three powered shelves settle into a left center right chain
	@GameTest(template = "empty")
	public static void poweredShelvesChain(GameTestHelper helper) {
		for (int x = 2; x <= 4; x++) {
			helper.setBlock(new BlockPos(x, 3, 3), CTBBlocks.OAK_SHELF.get().defaultBlockState()
					.setValue(ShelfBlock.FACING, Direction.NORTH));
		}
		for (int x = 2; x <= 4; x++) {
			helper.setBlock(new BlockPos(x, 2, 3), Blocks.REDSTONE_BLOCK);
		}
		helper.succeedWhen(() -> {
			int connected = 0;
			int centers = 0;
			for (int x = 2; x <= 4; x++) {
				BlockState state = helper.getBlockState(new BlockPos(x, 3, 3));
				if (!state.getValue(ShelfBlock.POWERED)) {
					throw new GameTestAssertException("shelf at x=" + x + " should be powered");
				}
				CTBSideChainPart part = state.getValue(ShelfBlock.SIDE_CHAIN_PART);
				if (part.isConnected()) {
					connected++;
				}
				if (part == CTBSideChainPart.CENTER) {
					centers++;
				}
			}
			if (connected != 3 || centers != 1) {
				throw new GameTestAssertException("expected a full chain, connected " + connected + " centers " + centers);
			}
		});
	}
}
