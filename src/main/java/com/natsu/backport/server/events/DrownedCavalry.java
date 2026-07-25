package com.natsu.backport.server.events;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.ZombieNautilus;
import com.natsu.backport.common.entity.goal.SpearUseGoal;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The 1.21.11 spear side of the zombies : rare iron spears in hand, the
 * SpearUseGoal charge, and trident drowned riding zombie nautilus.
 */
@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class DrownedCavalry {

	private static final String CHECKED_TAG = "ctb_cavalry_checked";

	@SubscribeEvent
	public static void onJoin(EntityJoinWorldEvent event) {
		Level level = event.getWorld();
		if (level.isClientSide || !(event.getEntity() instanceof Zombie zombie)) {
			return;
		}
		// every zombie knows how to couch a spear when it holds one
		zombie.goalSelector.addGoal(2, new SpearUseGoal<>((Monster) zombie, 1.0, 1.0, 10.0F, 2.0F));

		if (zombie.getPersistentData().getBoolean(CHECKED_TAG)) {
			return;
		}
		zombie.getPersistentData().putBoolean(CHECKED_TAG, true);

		// the vanilla zombie equipment roll : rarely an iron spear
		if (zombie.getMainHandItem().isEmpty()
				&& level.random.nextFloat() < (level.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)
				&& level.random.nextInt(6) == 1) {
			zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(CTBItems.IRON_SPEAR.get()));
		}

		// a trident drowned has an even chance to ride in on a zombie nautilus
		if (zombie instanceof Drowned drowned && !drowned.isBaby() && !drowned.isPassenger()
				&& drowned.getMainHandItem().is(Items.TRIDENT)
				&& level.random.nextFloat() < 0.5F) {
			ZombieNautilus mount = CTBEntities.ZOMBIE_NAUTILUS.get().create(level);
			if (mount != null) {
				mount.moveTo(drowned.getX(), drowned.getY(), drowned.getZ(), drowned.getYRot(), 0.0F);
				level.addFreshEntity(mount);
				drowned.startRiding(mount, true);
			}
		}
	}
}
