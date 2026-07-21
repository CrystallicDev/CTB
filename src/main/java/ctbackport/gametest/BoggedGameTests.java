package ctbackport.gametest;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Bogged;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class BoggedGameTests {

	@GameTest(template = "empty", timeoutTicks = 100)
	public static void boggedShearsIntoMushrooms(GameTestHelper helper) {
		BlockPos pos = new BlockPos(3, 2, 3);
		Bogged bogged = helper.spawn(CTBEntities.BOGGED.get(), pos);

		if (bogged.getMaxHealth() != 16.0F) {
			throw new GameTestAssertException("bogged should have 16 max health, got " + bogged.getMaxHealth());
		}
		if (bogged.isSheared()) {
			throw new GameTestAssertException("freshly spawned bogged should not be sheared");
		}

		bogged.shear(SoundSource.PLAYERS);

		helper.succeedWhen(() -> {
			if (!bogged.isSheared()) {
				throw new GameTestAssertException("bogged should be sheared");
			}
			AABB box = new AABB(helper.absolutePos(pos)).inflate(3.0);
			long mushrooms = helper.getLevel().getEntitiesOfClass(ItemEntity.class, box).stream()
					.filter(item -> item.getItem().is(Items.RED_MUSHROOM) || item.getItem().is(Items.BROWN_MUSHROOM))
					.count();
			if (mushrooms == 0) {
				throw new GameTestAssertException("shearing should drop mushrooms");
			}
		});
	}
}
