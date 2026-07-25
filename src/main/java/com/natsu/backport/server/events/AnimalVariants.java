package com.natsu.backport.server.events;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.network.CTBNetwork;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

/**
 * The 1.21.5 farm animal variants : pigs, cows and chickens remember the
 * climate they spawned in, chickens lay the matching egg color.
 */
@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class AnimalVariants {

	public static final String TAG = "ctb_variant";
	public static final byte TEMPERATE = 0;
	public static final byte WARM = 1;
	public static final byte COLD = 2;

	public static boolean isVariantAnimal(Entity entity) {
		return (entity instanceof Pig || entity instanceof Cow || entity instanceof Chicken)
				&& !(entity instanceof MushroomCow);
	}

	public static byte variantOf(Entity entity) {
		return entity.getPersistentData().getByte(TAG);
	}

	@SubscribeEvent
	public static void onJoin(EntityJoinWorldEvent event) {
		Level level = event.getWorld();
		if (level.isClientSide) {
			return;
		}
		Entity entity = event.getEntity();
		if (isVariantAnimal(entity)) {
			if (!entity.getPersistentData().contains(TAG)) {
				entity.getPersistentData().putByte(TAG, variantForBiome(level, entity.blockPosition()));
			}
			return;
		}
		// a freshly laid egg takes the color of the hen
		if (entity instanceof ItemEntity item && item.getThrower() == null
				&& item.getItem().is(Items.EGG)) {
			for (Chicken chicken : level.getEntitiesOfClass(Chicken.class,
					new AABB(entity.blockPosition()).inflate(1.0))) {
				byte variant = variantOf(chicken);
				if (variant == WARM || variant == COLD) {
					item.setItem(new ItemStack(variant == WARM
							? CTBItems.BROWN_EGG.get() : CTBItems.BLUE_EGG.get(),
							item.getItem().getCount()));
					return;
				}
			}
		}
	}

	private static byte variantForBiome(Level level, BlockPos pos) {
		float temperature = level.getBiome(pos).value().getBaseTemperature();
		if (temperature >= 0.95F) {
			return WARM;
		}
		return temperature <= 0.3F ? COLD : TEMPERATE;
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if (isVariantAnimal(target) && event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer player) {
			CTBNetwork.CHANNEL.sendTo(new CTBNetwork.AnimalVariantPacket(target.getId(), variantOf(target)),
					player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
