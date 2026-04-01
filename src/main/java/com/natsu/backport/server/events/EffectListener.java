package com.natsu.backport.server.events;

import java.util.Random;
import java.util.Set;

import org.joml.Vector3f;

import com.google.common.collect.Sets;
import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBEffects;
import com.natsu.backport.utils.WindChargeHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class EffectListener {

	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntityLiving().hasEffect(CTBEffects.WIND_CHARGED.get())) {
			LivingEntity entity = event.getEntityLiving();
			double d2 = entity.getX();
            double d0 = entity.getY() + (double)(entity.getBbHeight() / 2.0F);
            double d1 = entity.getZ();
            float f = 3.0F + entity.level.getRandom().nextFloat() * 2.0F;
            if (entity.level instanceof ServerLevel server) {
                WindChargeHelper.explodeWind(server, new Vec3(d2, d0, d1));
            }
		}
		
		if (event.getEntityLiving().hasEffect(CTBEffects.WEAVING.get())) {
			LivingEntity entity = event.getEntityLiving();
			spawnCobwebsRandomlyAround(entity.level, entity.level.random, entity.blockPosition());
		}
		
		if (event.getEntityLiving().hasEffect(CTBEffects.OOZING.get())) {
			LivingEntity entity = event.getEntityLiving();
			int slimeCount = new Random().nextInt(3) + event.getEntityLiving().getEffect(CTBEffects.INFESTED.get()).getAmplifier();
			for (int i = 0; i < slimeCount; i++) {
				spawnSlimeOffspring(entity.level, entity.getX(), entity.getY(), entity.getZ());
			}
		}
	}
	
	@SubscribeEvent
	public static void onMobHurt(LivingHurtEvent event) {
		if (event.getEntityLiving().hasEffect(CTBEffects.INFESTED.get())) {
			LivingEntity entity = event.getEntityLiving();
			if (new Random().nextFloat() >= 0.1f * (event.getEntityLiving().getEffect(CTBEffects.INFESTED.get()).getAmplifier()+1)) {
				spawnSilverfish(entity.level, entity);
			}
		}
	}
	
   
	private static void spawnSilverfish(Level level, LivingEntity entity) {
        Silverfish silverfish = EntityType.SILVERFISH.create(level);
        if (silverfish != null) {
        	silverfish.moveTo(entity.position());
        	level.addFreshEntity(silverfish);
            silverfish.playSound(SoundEvents.SILVERFISH_HURT, 1.0f, 1.0f);
        }
    }
	
	private static void spawnSlimeOffspring(Level level, double x, double y, double z) {
        Slime slime = EntityType.SLIME.create(level);
        if (slime != null) {
            slime.load(new CompoundTag() {{
            	putInt("Size", 2);
            }});
            slime.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(slime);
        }
    }
	
	private static void spawnCobwebsRandomlyAround(Level level, Random random, BlockPos pos) {
		Set<BlockPos> set = Sets.newHashSet();
		int i = random.nextInt(10);

		for (BlockPos blockpos : BlockPos.randomInCube(random, 15, pos, 1)) {
			BlockPos blockpos1 = blockpos.below();
			if (!set.contains(blockpos) && level.getBlockState(blockpos).getBlock() == Blocks.AIR
					&& level.getBlockState(blockpos1).isFaceSturdy(level, blockpos1, Direction.UP)) {
				set.add(blockpos.immutable());
				if (set.size() >= i) {
					break;
				}
			}
		}

		for (BlockPos blockpos2 : set) {
			level.setBlock(blockpos2, Blocks.COBWEB.defaultBlockState(), 3);
			level.levelEvent(3018, blockpos2, 0);
		}
	}
}
