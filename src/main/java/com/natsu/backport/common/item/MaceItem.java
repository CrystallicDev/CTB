package com.natsu.backport.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.natsu.backport.common.registry.CTBEnchantments;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.utils.WindChargeHelper;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class MaceItem extends Item {

	public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
	private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
	private static final float KNOCKBACK_RADIUS = 3.5F;
	private static final float KNOCKBACK_POWER = 0.7F;
	private static final double[] WIND_BURST_STRENGTH = {1.2, 1.75, 2.2};

	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public MaceItem(Properties props) {
		super(props);
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 5.0, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.4, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();
	}

	public static boolean canSmashAttack(LivingEntity attacker) {
		return attacker.fallDistance > SMASH_ATTACK_FALL_THRESHOLD && !attacker.isFallFlying();
	}

	// vanilla tiers : 4 damage per block for 3 blocks, then 2, then 1,
	// plus half a point per block per density level
	public static float smashBonus(float fallDistance, int densityLevel) {
		double damage;
		if (fallDistance <= 3.0) {
			damage = 4.0 * fallDistance;
		} else if (fallDistance <= 8.0) {
			damage = 12.0 + 2.0 * (fallDistance - 3.0);
		} else {
			damage = 22.0 + (fallDistance - 8.0);
		}
		return (float) (damage + 0.5 * densityLevel * fallDistance);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));

		if (canSmashAttack(attacker) && attacker.level instanceof ServerLevel level) {
			float fallDistance = attacker.fallDistance;
			attacker.setDeltaMovement(attacker.getDeltaMovement().x, 0.01, attacker.getDeltaMovement().z);
			attacker.fallDistance = 0.0F;

			SoundEvent sound;
			if (!target.isOnGround()) {
				sound = CTBSounds.MACE_SMASH_AIR.get();
			} else {
				sound = fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD
						? CTBSounds.MACE_SMASH_GROUND_HEAVY.get() : CTBSounds.MACE_SMASH_GROUND.get();
			}
			level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), sound, attacker.getSoundSource(), 1.0F, 1.0F);

			knockbackWave(level, attacker, target, fallDistance);

			int windBurst = EnchantmentHelper.getItemEnchantmentLevel(CTBEnchantments.WIND_BURST.get(), stack);
			if (windBurst > 0) {
				double strength = WIND_BURST_STRENGTH[Math.min(windBurst, WIND_BURST_STRENGTH.length) - 1];
				WindChargeHelper.explodeWind(level, attacker.position(), 3.5F, strength);
			}

			if (attacker instanceof ServerPlayer player) {
				player.connection.send(new ClientboundSetEntityMotionPacket(player));
			}
		}
		return true;
	}

	private static void knockbackWave(ServerLevel level, LivingEntity attacker, LivingEntity target, float fallDistance) {
		level.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY(), target.getZ(), 1, 0.0, 0.0, 0.0, 0.0);

		for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(KNOCKBACK_RADIUS))) {
			if (nearby == attacker || nearby == target || nearby.isSpectator()
					|| attacker.isAlliedTo(nearby)
					|| (nearby instanceof TamableAnimal tamed && tamed.isTame() && tamed.isOwnedBy(attacker))
					|| (nearby instanceof ArmorStand stand && stand.isMarker())
					|| (nearby instanceof Player p && p.isCreative() && p.getAbilities().flying)
					|| target.distanceToSqr(nearby) > KNOCKBACK_RADIUS * KNOCKBACK_RADIUS) {
				continue;
			}
			Vec3 dir = nearby.position().subtract(target.position());
			double power = (KNOCKBACK_RADIUS - dir.length()) * KNOCKBACK_POWER
					* (fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD ? 2 : 1)
					* (1.0 - nearby.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			if (power > 0.0) {
				Vec3 kb = dir.normalize().scale(power);
				nearby.push(kb.x, 0.7, kb.z);
				nearby.hurtMarked = true;
			}
		}
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
		return repair.is(CTBItems.BREEZE_ROD.get());
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public boolean canAttackBlock(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, Player player) {
		return !player.isCreative();
	}
}
