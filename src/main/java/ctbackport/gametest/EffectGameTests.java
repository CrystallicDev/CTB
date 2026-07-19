package ctbackport.gametest;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.WindChargeEntity;
import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CTBackport.MODID)
@PrefixGameTestTemplate(false)
public class EffectGameTests {

	private static final String EMPTY = "empty";

	@GameTest(template = EMPTY)
	public static void oozingSpawnsSlimes(GameTestHelper helper) {
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, 3, 1, 3);
		zombie.addEffect(new MobEffectInstance(CTBEffects.OOZING.get(), 1200));
		zombie.hurt(DamageSource.OUT_OF_WORLD, 1000.0F);
		helper.succeedWhen(() -> helper.assertEntityPresent(EntityType.SLIME));
	}

	@GameTest(template = EMPTY)
	public static void weavingSpawnsCobwebs(GameTestHelper helper) {
		// the placement rolls are random, three deaths make a miss astronomical
		for (int i = 0; i < 3; i++) {
			Zombie zombie = helper.spawn(EntityType.ZOMBIE, 3, 1, 3);
			zombie.addEffect(new MobEffectInstance(CTBEffects.WEAVING.get(), 1200));
			zombie.hurt(DamageSource.OUT_OF_WORLD, 1000.0F);
		}
		helper.succeedWhen(() -> {
			for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(1, 1, 1), new BlockPos(5, 3, 5))) {
				if (helper.getBlockState(pos).is(Blocks.COBWEB)) {
					return;
				}
			}
			helper.fail("no cobweb spawned");
		});
	}

	// short timeout so a wandering pig can't fake the knockback
	@GameTest(template = EMPTY, timeoutTicks = 20)
	public static void windChargedPushesNearbyEntities(GameTestHelper helper) {
		Pig pig = helper.spawn(EntityType.PIG, 4, 1, 3);
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, 3, 1, 3);
		zombie.addEffect(new MobEffectInstance(CTBEffects.WIND_CHARGED.get(), 1200));
		Vec3 start = pig.position();
		zombie.hurt(DamageSource.OUT_OF_WORLD, 1000.0F);
		helper.succeedWhen(() -> {
			if (pig.position().distanceTo(start) < 1.0) {
				helper.fail("pig was not pushed");
			}
		});
	}

	@GameTest(template = EMPTY, timeoutTicks = 40)
	public static void windChargeKnocksBack(GameTestHelper helper) {
		Pig pig = helper.spawn(EntityType.PIG, 3, 1, 3);
		Vec3 start = pig.position();
		WindChargeEntity charge = new WindChargeEntity(CTBEntities.WIND_CHARGE_ENTITY.get(), helper.getLevel());
		Vec3 spawn = helper.absoluteVec(new Vec3(3.5, 3.5, 3.5));
		charge.setPos(spawn.x, spawn.y, spawn.z);
		charge.shoot(0, -1, 0, 1.0F, 0.0F);
		helper.getLevel().addFreshEntity(charge);
		helper.succeedWhen(() -> {
			if (pig.position().distanceTo(start) < 1.0) {
				helper.fail("pig was not knocked back");
			}
		});
	}

	@GameTest(template = EMPTY, timeoutTicks = 200)
	public static void infestedSpawnsSilverfish(GameTestHelper helper) {
		Zombie zombie = helper.spawn(EntityType.ZOMBIE, 3, 1, 3);
		zombie.setNoAi(true);
		zombie.addEffect(new MobEffectInstance(CTBEffects.INFESTED.get(), 12000));
		// 10% per hit, a hundred hits make it a statistical certainty
		helper.onEachTick(() -> {
			if (zombie.isAlive() && helper.getTick() < 100) {
				zombie.invulnerableTime = 0;
				zombie.hurt(DamageSource.GENERIC, 0.01F);
			}
		});
		helper.succeedWhen(() -> helper.assertEntityPresent(EntityType.SILVERFISH));
	}
}
