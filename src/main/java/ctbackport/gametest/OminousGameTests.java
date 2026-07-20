package ctbackport.gametest;

import java.util.List;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.TrialSpawnerBlock;
import com.natsu.backport.common.block.entity.TrialSpawnerBlockEntity;
import com.natsu.backport.common.block.entity.trialspawner.PlayerDetector;
import com.natsu.backport.common.item.OminousBottleItem;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class OminousGameTests {

	@GameTest(template = "empty")
	public static void ominousBottleGivesBadOmen(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		ItemStack bottle = new ItemStack(CTBItems.OMINOUS_BOTTLE.get());
		bottle.getOrCreateTag().putInt(OminousBottleItem.TAG_AMPLIFIER, 3);

		bottle.getItem().finishUsingItem(bottle, helper.getLevel(), player);

		MobEffectInstance badOmen = player.getEffect(MobEffects.BAD_OMEN);
		if (badOmen == null || badOmen.getAmplifier() != 3) {
			throw new GameTestAssertException("expected bad omen III after drinking, got " + badOmen);
		}
		if (!bottle.isEmpty()) {
			throw new GameTestAssertException("the bottle should shatter");
		}
		helper.succeed();
	}

	@GameTest(template = "empty", timeoutTicks = 200)
	public static void spawnerBecomesOminousFromBadOmen(GameTestHelper helper) {
		BlockPos spawnerPos = new BlockPos(3, 2, 3);
		helper.setBlock(spawnerPos, CTBBlocks.TRIAL_SPAWNER.get());
		TrialSpawnerBlockEntity spawner = (TrialSpawnerBlockEntity) helper.getBlockEntity(spawnerPos);
		spawner.getTrialSpawner().overrideEntityToSpawn(EntityType.ZOMBIE, helper.getLevel());

		Player player = helper.makeMockPlayer();
		player.setPos(helper.absoluteVec(new Vec3(spawnerPos.getX() + 0.5, spawnerPos.getY(), spawnerPos.getZ() - 2.5)));
		player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, 0));
		spawner.getTrialSpawner().setPlayerDetector((level, selector, pos, range, los) -> List.of(player.getUUID()));
		spawner.getTrialSpawner().setEntitySelector(PlayerDetector.EntitySelector.onlySelectPlayer(player));
		spawner.getTrialSpawner().overridePeacefulAndMobSpawnRule();

		helper.succeedWhen(() -> {
			helper.assertBlockProperty(spawnerPos, TrialSpawnerBlock.OMINOUS, true);
			if (player.hasEffect(MobEffects.BAD_OMEN)) {
				throw new GameTestAssertException("bad omen should be consumed by the conversion");
			}
			if (!player.hasEffect(CTBEffects.TRIAL_OMEN.get())) {
				throw new GameTestAssertException("player should have trial omen");
			}
		});
	}
}
