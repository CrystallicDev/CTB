package com.natsu.backport.common.item;

import com.natsu.backport.common.entity.WindChargeEntity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WindChargeItem extends Item {

	public WindChargeItem(Properties pros) {
		super(pros);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat()* 0.4f + 0.8f));

		if (!world.isClientSide()) {
			WindChargeEntity charge = new WindChargeEntity(world, player);
			charge.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 1.0f);
			world.addFreshEntity(charge);

			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}

		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
	}

}
