package com.natsu.backport.server.events;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.utils.WindChargeHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// vanilla wires these through MobEffect#onMobHurt/onMobRemoved, 1.18.2 has no
// such hooks so the forge events do the job
@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class EffectListener {

	private static final float INFESTED_SPAWN_CHANCE = 0.1F;
	private static final int OOZING_SLIME_COUNT = 2;
	private static final int OOZING_SLIME_RADIUS = 2;

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (!(entity.level instanceof ServerLevel level)) {
			return;
		}

		if (entity.hasEffect(CTBEffects.WIND_CHARGED.get())) {
			float gustStrength = 3.0F + entity.getRandom().nextFloat() * 2.0F;
			WindChargeHelper.explodeWind(level,
					new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2.0F, entity.getZ()),
					gustStrength);
		}

		if (entity.hasEffect(CTBEffects.WEAVING.get())
				&& (entity instanceof Player || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))) {
			spawnCobwebsRandomlyAround(level, entity.getRandom(), entity.blockPosition());
		}

		if (entity.hasEffect(CTBEffects.OOZING.get())) {
			// capped by the cramming budget, minus the slimes already around
			int maxCramming = level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
			int toSpawn = OOZING_SLIME_COUNT;
			if (maxCramming >= 1) {
				int nearby = level.getEntitiesOfClass(Slime.class,
						entity.getBoundingBox().inflate(OOZING_SLIME_RADIUS)).size();
				toSpawn = Mth.clamp(maxCramming - nearby, 0, OOZING_SLIME_COUNT);
			}
			for (int i = 0; i < toSpawn; i++) {
				spawnSlimeOffspring(level, entity.getX(), entity.getY() + 0.5, entity.getZ());
			}
		}
	}

	@SubscribeEvent
	public static void onMobHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (!(entity.level instanceof ServerLevel level) || !entity.hasEffect(CTBEffects.INFESTED.get())) {
			return;
		}

		if (entity.getRandom().nextFloat() <= INFESTED_SPAWN_CHANCE) {
			int count = 1 + entity.getRandom().nextInt(2);
			for (int i = 0; i < count; i++) {
				spawnSilverfish(level, entity);
			}
		}
	}

	private static void spawnSilverfish(ServerLevel level, LivingEntity entity) {
		Silverfish silverfish = EntityType.SILVERFISH.create(level);
		if (silverfish == null) {
			return;
		}
		Random random = entity.getRandom();
		// flung out of the body, roughly along the look direction
		float angle = Mth.randomBetween(random, -Mth.PI / 2.0F, Mth.PI / 2.0F);
		Vec3 push = entity.getLookAngle().scale(0.3).multiply(1.0, 1.5, 1.0).yRot(angle);
		silverfish.moveTo(entity.getX(), entity.getY() + entity.getBbHeight() / 2.0, entity.getZ(),
				random.nextFloat() * 360.0F, 0.0F);
		silverfish.setDeltaMovement(push);
		level.addFreshEntity(silverfish);
		silverfish.playSound(SoundEvents.SILVERFISH_HURT, 1.0F, 1.0F);
	}

	private static void spawnSlimeOffspring(ServerLevel level, double x, double y, double z) {
		Slime slime = EntityType.SLIME.create(level);
		if (slime != null) {
			// Slime#setSize is protected here, the nbt route does the same
			CompoundTag tag = new CompoundTag();
			tag.putInt("Size", 1);
			slime.readAdditionalSaveData(tag);
			slime.setHealth(slime.getMaxHealth());
			slime.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
			level.addFreshEntity(slime);
		}
	}

	private static void spawnCobwebsRandomlyAround(ServerLevel level, Random random, BlockPos pos) {
		Set<BlockPos> positions = Sets.newHashSet();
		int cobwebCount = 2 + random.nextInt(2);

		for (BlockPos candidate : BlockPos.randomInCube(random, 15, pos, 1)) {
			BlockPos below = candidate.below();
			if (!positions.contains(candidate)
					&& level.getBlockState(candidate).getMaterial().isReplaceable()
					&& level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
				positions.add(candidate.immutable());
				if (positions.size() >= cobwebCount) {
					break;
				}
			}
		}

		for (BlockPos webPos : positions) {
			level.setBlock(webPos, Blocks.COBWEB.defaultBlockState(), 3);
		}
	}
}
