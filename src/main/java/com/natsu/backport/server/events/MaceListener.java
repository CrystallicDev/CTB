package com.natsu.backport.server.events;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.item.MaceItem;
import com.natsu.backport.common.registry.CTBEnchantments;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class MaceListener {

	// hurt (pre armor) and damage (post armor) run nested in the same call
	private record PendingBreach(LivingEntity target, float preArmor, int level) {}
	private static final ThreadLocal<PendingBreach> PENDING = new ThreadLocal<>();

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof LivingEntity attacker)
				|| event.getSource().getEntity() != attacker) {
			return;
		}
		ItemStack weapon = attacker.getMainHandItem();
		if (!(weapon.getItem() instanceof MaceItem)) {
			return;
		}

		if (MaceItem.canSmashAttack(attacker)) {
			int density = EnchantmentHelper.getItemEnchantmentLevel(CTBEnchantments.DENSITY.get(), weapon);
			event.setAmount(event.getAmount() + MaceItem.smashBonus(attacker.fallDistance, density));
		}

		int breach = EnchantmentHelper.getItemEnchantmentLevel(CTBEnchantments.BREACH.get(), weapon);
		if (breach > 0) {
			PENDING.set(new PendingBreach(event.getEntityLiving(), event.getAmount(), breach));
		}
	}

	// breach : give back 15% per level of what the armor absorbed
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		PendingBreach pending = PENDING.get();
		if (pending == null || pending.target() != event.getEntityLiving()) {
			return;
		}
		PENDING.remove();

		float absorbed = pending.preArmor() - event.getAmount();
		if (absorbed > 0) {
			float pierce = Math.min(1.0F, 0.15F * pending.level());
			event.setAmount(event.getAmount() + absorbed * pierce);
		}
	}
}
