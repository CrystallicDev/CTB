package ctbackport.gametest;

import java.util.List;
import java.util.Optional;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.block.TrialSpawnerBlock;
import com.natsu.backport.common.block.VaultBlock;
import com.natsu.backport.common.block.entity.TrialSpawnerBlockEntity;
import com.natsu.backport.common.block.entity.trialspawner.PlayerDetector;
import com.natsu.backport.common.block.entity.trialspawner.TrialSpawnerState;
import com.natsu.backport.common.block.entity.vault.VaultBlockEntity;
import com.natsu.backport.common.block.entity.vault.VaultConfig;
import com.natsu.backport.common.block.entity.vault.VaultState;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class TrialChamberGameTests {

	private static final BlockPos SPAWNER_POS = new BlockPos(3, 2, 3);

	private static Player installMockPlayer(GameTestHelper helper, TrialSpawnerBlockEntity spawner) {
		Player player = helper.makeMockPlayer();
		player.setPos(helper.absoluteVec(new Vec3(SPAWNER_POS.getX() + 0.5, SPAWNER_POS.getY(), SPAWNER_POS.getZ() - 2.5)));
		spawner.getTrialSpawner().setPlayerDetector((level, selector, pos, range, los) -> List.of(player.getUUID()));
		spawner.getTrialSpawner().setEntitySelector(PlayerDetector.EntitySelector.onlySelectPlayer(player));
		spawner.getTrialSpawner().overridePeacefulAndMobSpawnRule();
		return player;
	}

	@GameTest(template = "empty", timeoutTicks = 200)
	public static void trialSpawnerStartsWave(GameTestHelper helper) {
		helper.setBlock(SPAWNER_POS, CTBBlocks.TRIAL_SPAWNER.get());
		TrialSpawnerBlockEntity spawner = (TrialSpawnerBlockEntity) helper.getBlockEntity(SPAWNER_POS);
		spawner.getTrialSpawner().overrideEntityToSpawn(EntityType.ZOMBIE, helper.getLevel());
		installMockPlayer(helper, spawner);

		helper.succeedWhen(() -> {
			helper.assertBlockProperty(SPAWNER_POS, TrialSpawnerBlock.STATE, TrialSpawnerState.ACTIVE);
			AABB box = new AABB(helper.absolutePos(SPAWNER_POS)).inflate(5.0);
			if (helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.monster.Zombie.class, box).isEmpty()) {
				throw new net.minecraft.gametest.framework.GameTestAssertException("no zombie spawned yet");
			}
		});
	}

	@GameTest(template = "empty", timeoutTicks = 200)
	public static void vaultActivatesAndRejectsRewardedPlayer(GameTestHelper helper) {
		BlockPos vaultPos = new BlockPos(3, 2, 3);
		helper.setBlock(vaultPos, CTBBlocks.VAULT.get());
		VaultBlockEntity vault = (VaultBlockEntity) helper.getBlockEntity(vaultPos);

		Player player = helper.makeMockPlayer();
		player.setPos(helper.absoluteVec(new Vec3(vaultPos.getX() + 0.5, vaultPos.getY(), vaultPos.getZ() - 2.5)));
		VaultConfig defaults = VaultConfig.defaultConfig();
		vault.setConfig(new VaultConfig(defaults.lootTable(), defaults.activationRange(), defaults.deactivationRange(),
				defaults.keyItem(), Optional.empty(),
				(level, selector, pos, range, los) -> List.of(player.getUUID()),
				PlayerDetector.EntitySelector.onlySelectPlayer(player)));

		helper.succeedWhen(() -> {
			helper.assertBlockProperty(vaultPos, VaultBlock.STATE, VaultState.ACTIVE);

			// a valid key unlocks once, then the same player gets rejected
			ItemStack key = new ItemStack(CTBItems.TRIAL_KEY.get(), 1);
			VaultBlockEntity.Server.tryInsertKey(helper.getLevel(), helper.absolutePos(vaultPos),
					helper.getBlockState(vaultPos), vault.getConfig(), vault.getServerData(), vault.getSharedData(), player, key);
			if (!key.isEmpty()) {
				throw new net.minecraft.gametest.framework.GameTestAssertException("key was not consumed");
			}
			if (!vault.getServerData().hasRewardedPlayer(player)) {
				throw new net.minecraft.gametest.framework.GameTestAssertException("player not marked as rewarded");
			}

			ItemStack secondKey = new ItemStack(CTBItems.TRIAL_KEY.get(), 1);
			VaultBlockEntity.Server.tryInsertKey(helper.getLevel(), helper.absolutePos(vaultPos),
					helper.getBlockState(vaultPos), vault.getConfig(), vault.getServerData(), vault.getSharedData(), player, secondKey);
			if (secondKey.isEmpty()) {
				throw new net.minecraft.gametest.framework.GameTestAssertException("rewarded player got a second unlock");
			}
		});
	}

	@GameTest(template = "empty", timeoutTicks = 400)
	public static void vaultEjectsLoot(GameTestHelper helper) {
		BlockPos vaultPos = new BlockPos(3, 2, 3);
		helper.setBlock(vaultPos, CTBBlocks.VAULT.get());
		VaultBlockEntity vault = (VaultBlockEntity) helper.getBlockEntity(vaultPos);

		Player player = helper.makeMockPlayer();
		player.setPos(helper.absoluteVec(new Vec3(vaultPos.getX() + 0.5, vaultPos.getY(), vaultPos.getZ() - 2.5)));
		VaultConfig defaults = VaultConfig.defaultConfig();
		vault.setConfig(new VaultConfig(defaults.lootTable(), defaults.activationRange(), defaults.deactivationRange(),
				defaults.keyItem(), Optional.empty(),
				(level, selector, pos, range, los) -> List.of(player.getUUID()),
				PlayerDetector.EntitySelector.onlySelectPlayer(player)));

		helper.runAfterDelay(60, () -> {
			ItemStack key = new ItemStack(CTBItems.TRIAL_KEY.get(), 1);
			VaultBlockEntity.Server.tryInsertKey(helper.getLevel(), helper.absolutePos(vaultPos),
					helper.getBlockState(vaultPos), vault.getConfig(), vault.getServerData(), vault.getSharedData(), player, key);
		});

		helper.succeedWhen(() -> {
			AABB box = new AABB(helper.absolutePos(vaultPos)).inflate(3.0);
			if (helper.getLevel().getEntitiesOfClass(ItemEntity.class, box).isEmpty()) {
				throw new net.minecraft.gametest.framework.GameTestAssertException("no loot ejected yet");
			}
		});
	}
}
