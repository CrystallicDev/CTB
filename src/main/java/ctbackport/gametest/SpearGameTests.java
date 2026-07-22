package ctbackport.gametest;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.SpearItem;
import com.natsu.backport.common.registry.CTBEnchantments;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;

import java.util.Map;

@GameTestHolder(CTBackport.MODID)
@net.minecraftforge.gametest.PrefixGameTestTemplate(false)
public class SpearGameTests {

	// a couched iron spear at nautilus dash speed must hurt and knock the target
	@GameTest(template = "empty")
	public static void kineticStabHurtsAtSpeed(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		Vec3 pos = helper.absoluteVec(new Vec3(1.5, 2.0, 3.5));
		player.setPos(pos);
		player.setYRot(-90.0F); // looking towards +x
		player.setXRot(0.0F);
		player.setDeltaMovement(0.7, 0.0, 0.0);
		player.xOld = player.getX() - 0.7;
		player.yOld = player.getY();
		player.zOld = player.getZ();

		Pig pig = helper.spawn(EntityType.PIG, new BlockPos(3, 2, 3));
		float before = pig.getHealth();

		ItemStack spear = new ItemStack(CTBItems.IRON_SPEAR.get());
		((SpearItem) spear.getItem()).kineticTick(player, spear, 40);

		if (pig.getHealth() >= before) {
			throw new GameTestAssertException("pig should take kinetic damage, health " + before + " -> " + pig.getHealth());
		}
		if (spear.getDamageValue() == 0) {
			throw new GameTestAssertException("spear should lose durability on a stab");
		}
		helper.succeed();
	}

	// stationary attacker deals nothing, the spear is a speed weapon
	@GameTest(template = "empty")
	public static void kineticStabNeedsSpeed(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		player.setPos(helper.absoluteVec(new Vec3(1.5, 2.0, 3.5)));
		player.setYRot(-90.0F);
		player.setDeltaMovement(Vec3.ZERO);
		player.xOld = player.getX();
		player.yOld = player.getY();
		player.zOld = player.getZ();

		Pig pig = helper.spawn(EntityType.PIG, new BlockPos(3, 2, 3));
		float before = pig.getHealth();

		ItemStack spear = new ItemStack(CTBItems.IRON_SPEAR.get());
		((SpearItem) spear.getItem()).kineticTick(player, spear, 40);

		if (pig.getHealth() < before) {
			throw new GameTestAssertException("a stationary couched spear should not deal damage");
		}
		helper.succeed();
	}

	@GameTest(template = "empty")
	public static void lungeDashesTheAttacker(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		player.setPos(helper.absoluteVec(new Vec3(2.5, 2.0, 3.5)));
		player.setYRot(-90.0F);
		player.setXRot(0.0F);
		player.setDeltaMovement(Vec3.ZERO);

		Pig pig = helper.spawn(EntityType.PIG, new BlockPos(4, 2, 3));

		ItemStack spear = new ItemStack(CTBItems.DIAMOND_SPEAR.get());
		net.minecraft.world.item.enchantment.EnchantmentHelper.setEnchantments(
				Map.of(CTBEnchantments.LUNGE.get(), 2), spear);
		spear.getItem().hurtEnemy(spear, pig, player);

		if (player.getDeltaMovement().x <= 0.5) {
			throw new GameTestAssertException("lunge should push the attacker forward, got " + player.getDeltaMovement());
		}
		helper.succeed();
	}
}
