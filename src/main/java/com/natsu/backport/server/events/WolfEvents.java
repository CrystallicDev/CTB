package com.natsu.backport.server.events;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.WolfArmorItem;
import com.natsu.backport.common.network.CTBNetwork;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

/**
 * The 1.20.5 wolf armor on the vanilla wolf : the armor lives in persistent
 * data (no body slot in 1.18.2), soaks every blockable hit and cracks apart.
 */
@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class WolfEvents {

	public static final String ARMOR_TAG = "ctb_wolf_armor";
	public static final int MAX_DURABILITY = 64;

	public static boolean hasArmor(Wolf wolf) {
		return wolf.getPersistentData().contains(ARMOR_TAG);
	}

	public static int getArmorDurability(Wolf wolf) {
		return wolf.getPersistentData().getInt(ARMOR_TAG);
	}

	private static void setArmor(Wolf wolf, int durability) {
		if (durability < 0) {
			wolf.getPersistentData().remove(ARMOR_TAG);
		} else {
			wolf.getPersistentData().putInt(ARMOR_TAG, durability);
		}
		sync(wolf);
	}

	private static void sync(Wolf wolf) {
		int value = hasArmor(wolf) ? getArmorDurability(wolf) : -1;
		if (wolf.level instanceof net.minecraft.server.level.ServerLevel server) {
			for (ServerPlayer player : server.players()) {
				CTBNetwork.CHANNEL.sendTo(new CTBNetwork.WolfArmorPacket(wolf.getId(), value),
						player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
			}
		}
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		if (event.getTarget() instanceof Wolf wolf && hasArmor(wolf)
				&& event.getPlayer() instanceof ServerPlayer player) {
			CTBNetwork.CHANNEL.sendTo(new CTBNetwork.WolfArmorPacket(wolf.getId(), getArmorDurability(wolf)),
					player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent
	public static void onInteract(PlayerInteractEvent.EntityInteract event) {
		if (!(event.getTarget() instanceof Wolf wolf)) {
			return;
		}
		Player player = event.getPlayer();
		ItemStack stack = event.getItemStack();

		if (stack.getItem() instanceof WolfArmorItem && wolf.isTame() && !wolf.isBaby()
				&& wolf.isOwnedBy(player) && !hasArmor(wolf)) {
			if (!wolf.level.isClientSide) {
				setArmor(wolf, MAX_DURABILITY - stack.getDamageValue());
				wolf.level.playSound(null, wolf, CTBSounds.WOLF_ARMOR_EQUIP.get(),
						SoundSource.NEUTRAL, 1.0F, 1.0F);
				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}
			}
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.sidedSuccess(wolf.level.isClientSide));
			return;
		}
		if (stack.is(Items.SHEARS) && hasArmor(wolf) && wolf.isOwnedBy(player)) {
			if (!wolf.level.isClientSide) {
				ItemStack armor = new ItemStack(CTBItems.WOLF_ARMOR.get());
				armor.setDamageValue(MAX_DURABILITY - getArmorDurability(wolf));
				wolf.spawnAtLocation(armor);
				setArmor(wolf, -1);
				wolf.level.playSound(null, wolf, CTBSounds.WOLF_ARMOR_UNEQUIP.get(),
						SoundSource.NEUTRAL, 1.0F, 1.0F);
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
			}
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.sidedSuccess(wolf.level.isClientSide));
			return;
		}
		if (stack.is(CTBItems.ARMADILLO_SCUTE.get()) && hasArmor(wolf)
				&& getArmorDurability(wolf) < MAX_DURABILITY && wolf.isOwnedBy(player)) {
			if (!wolf.level.isClientSide) {
				setArmor(wolf, Math.min(MAX_DURABILITY, getArmorDurability(wolf) + 8));
				wolf.level.playSound(null, wolf, CTBSounds.WOLF_ARMOR_REPAIR.get(),
						SoundSource.NEUTRAL, 1.0F, 1.0F);
				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}
			}
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.sidedSuccess(wolf.level.isClientSide));
		}
	}

	@SubscribeEvent
	public static void onHurt(LivingHurtEvent event) {
		if (!(event.getEntityLiving() instanceof Wolf wolf) || wolf.level.isClientSide
				|| !hasArmor(wolf) || event.getSource().isBypassArmor()) {
			return;
		}
		int durability = getArmorDurability(wolf) - Math.max(1, (int) Math.ceil(event.getAmount()));
		if (durability <= 0) {
			setArmor(wolf, -1);
			wolf.level.playSound(null, wolf, CTBSounds.WOLF_ARMOR_BREAK.get(),
					SoundSource.NEUTRAL, 1.0F, 1.0F);
		} else {
			setArmor(wolf, durability);
			wolf.level.playSound(null, wolf, CTBSounds.WOLF_ARMOR_DAMAGE.get(),
					SoundSource.NEUTRAL, 1.0F, 1.0F);
		}
		// the armor soaks the whole hit
		event.setCanceled(true);
	}
}
