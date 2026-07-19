package ctbackport.gametest;

import java.util.Map;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.MaceItem;
import com.natsu.backport.common.registry.CTBEnchantments;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class MaceGameTests {

	private static final String EMPTY = "empty";

	@GameTest(template = EMPTY)
	public static void smashBonusMatchesVanillaTiers(GameTestHelper helper) {
		assertBonus(helper, MaceItem.smashBonus(3.0F, 0), 12.0F);
		assertBonus(helper, MaceItem.smashBonus(8.0F, 0), 22.0F);
		assertBonus(helper, MaceItem.smashBonus(10.0F, 0), 24.0F);
		// 4 blocks with density II : 14 + 0.5*2*4
		assertBonus(helper, MaceItem.smashBonus(4.0F, 2), 18.0F);
		helper.succeed();
	}

	private static void assertBonus(GameTestHelper helper, float actual, float expected) {
		if (Math.abs(actual - expected) > 0.01F) {
			helper.fail("smash bonus " + actual + ", expected " + expected);
		}
	}

	@GameTest(template = EMPTY, timeoutTicks = 40)
	public static void smashAttackHitsHard(GameTestHelper helper) {
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, 3, 1, 3);
		zombie.setNoAi(true);
		Player player = helper.makeMockPlayer();
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(CTBItems.MACE.get()));
		player.fallDistance = 6.0F;
		player.attack(zombie);
		// the flat smash bonus alone is 18, the zombie has to be near death
		helper.succeedWhen(() -> {
			if (zombie.isAlive() && zombie.getHealth() >= 8.0F) {
				helper.fail("no smash damage, health " + zombie.getHealth());
			}
		});
	}

	// mock players are not in the level's entity list, so the burst is
	// measured on a bystander pig instead of the attacker itself
	@GameTest(template = EMPTY, timeoutTicks = 40)
	public static void windBurstFiresOnSmash(GameTestHelper helper) {
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, 3, 1, 3);
		zombie.setNoAi(true);
		Pig pig = helper.spawn(EntityType.PIG, 4, 1, 3);
		Vec3 start = pig.position();
		Player player = helper.makeMockPlayer();
		ItemStack mace = new ItemStack(CTBItems.MACE.get());
		EnchantmentHelper.setEnchantments(Map.of(CTBEnchantments.WIND_BURST.get(), 1), mace);
		player.setItemInHand(InteractionHand.MAIN_HAND, mace);
		player.setPos(zombie.getX(), zombie.getY(), zombie.getZ());
		player.fallDistance = 3.0F;
		player.attack(zombie);
		helper.succeedWhen(() -> {
			if (pig.position().distanceTo(start) < 1.0) {
				helper.fail("no wind burst, pig did not move");
			}
		});
	}
}
