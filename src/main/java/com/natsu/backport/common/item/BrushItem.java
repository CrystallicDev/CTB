package com.natsu.backport.common.item;

import com.natsu.backport.common.block.BrushableBlock;
import com.natsu.backport.common.block.entity.BrushableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** The 1.20 brush : hold use on a suspicious block to sweep the dust away. */
public class BrushItem extends Item {

	private static final int USE_DURATION = 200;

	public BrushItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player != null && this.calculateHitResult(player).getType() == HitResult.Type.BLOCK) {
			player.startUsingItem(context.getHand());
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return USE_DURATION;
	}

	@Override
	public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remaining) {
		if (!(user instanceof Player player)) {
			user.releaseUsingItem();
			return;
		}
		HitResult hit = this.calculateHitResult(player);
		if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
			user.releaseUsingItem();
			return;
		}
		int elapsed = this.getUseDuration(stack) - remaining + 1;
		if (elapsed % 10 != 5) {
			return;
		}
		BlockPos pos = blockHit.getBlockPos();
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof BrushableBlock brushable)) {
			user.releaseUsingItem();
			return;
		}
		this.spawnDustParticles(level, blockHit, state, user.getViewVector(0.0F));
		level.playSound(player, pos, brushable.getBrushSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
		if (level instanceof ServerLevel server
				&& level.getBlockEntity(pos) instanceof BrushableBlockEntity entity) {
			boolean complete = entity.brush(level.getGameTime(), server, user, blockHit.getDirection(), stack);
			if (complete) {
				level.playSound(player, pos, brushable.getBrushCompletedSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
				InteractionHand hand = user.getUsedItemHand();
				stack.hurtAndBreak(1, user, u -> u.broadcastBreakEvent(hand));
			}
		}
	}

	private HitResult calculateHitResult(Player player) {
		Vec3 eye = player.getEyePosition();
		Vec3 look = player.getViewVector(0.0F);
		Vec3 end = eye.add(look.scale(5.0));
		return player.level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE, player));
	}

	private void spawnDustParticles(Level level, BlockHitResult hit, BlockState state, Vec3 look) {
		int count = 6 + level.random.nextInt(6);
		Vec3 center = hit.getLocation();
		for (int i = 0; i < count; i++) {
			level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
					center.x, center.y, center.z,
					(level.random.nextDouble() - 0.5) * 0.2 + look.x * -0.1,
					level.random.nextDouble() * 0.1,
					(level.random.nextDouble() - 0.5) * 0.2 + look.z * -0.1);
		}
	}
}
