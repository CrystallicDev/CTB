package com.natsu.backport.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class OminousBottleItem extends Item {

	private static final int DRINK_DURATION = 32;
	public static final int EFFECT_DURATION = 120000; // 100 minutes
	public static final String TAG_AMPLIFIER = "OminousBottleAmplifier";

	public OminousBottleItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide) {
			int amplifier = Math.min(4, Math.max(0, stack.getOrCreateTag().getInt(TAG_AMPLIFIER)));
			entity.removeEffect(MobEffects.BAD_OMEN);
			entity.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, EFFECT_DURATION, amplifier, false, false, true));
		}

		// the bottle shatters, nothing comes back
		if (entity instanceof Player player && !player.getAbilities().instabuild) {
			stack.shrink(1);
		}
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return DRINK_DURATION;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(level, player, hand);
	}
}
