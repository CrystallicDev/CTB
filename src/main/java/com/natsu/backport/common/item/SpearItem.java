package com.natsu.backport.common.item;

import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.natsu.backport.common.registry.CTBEnchantments;
import com.natsu.backport.common.registry.CTBSounds;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The 1.21.11 kinetic weapon: holding use couches the lance, riding or
 * sprinting into something converts speed into damage. Melee stabs never
 * sweep, and the Lunge enchantment dashes the attacker forward on foot.
 */
public class SpearItem extends TieredItem implements Vanishable {

	/** The vanilla per tier tuning, all times in seconds and speeds in blocks per second. */
	public record KineticParams(float attackDuration, float damageMultiplier, float delay,
			float dismountTime, float dismountThreshold, float knockbackTime, float knockbackThreshold,
			float damageTime, float damageThreshold) {
	}

	private static final int CONTACT_COOLDOWN_TICKS = 10;
	private static final float LUNGE_IMPULSE_PER_LEVEL = 0.458F;
	private static final double KINETIC_REACH = 3.5;

	// recently stabbed targets per attacker, vanilla contact cooldown
	private static final WeakHashMap<LivingEntity, Object2IntOpenHashMap<Entity>> RECENT_STABS = new WeakHashMap<>();
	// the server wipes xOld through absMoveTo for packet driven entities (riders,
	// vehicles, remote players), so speeds are tracked from our own tick samples
	private static final WeakHashMap<Entity, Vec3> LAST_SAMPLED_POS = new WeakHashMap<>();

	private final KineticParams params;
	private final boolean woodSounds;
	// built lazily, the forge reach attribute does not exist yet during item registration
	private Multimap<Attribute, AttributeModifier> defaultModifiers;

	public SpearItem(Tier tier, KineticParams params, boolean woodSounds, Properties properties) {
		super(tier, properties.defaultDurability(tier.getUses()));
		this.params = params;
		this.woodSounds = woodSounds;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		if (slot != EquipmentSlot.MAINHAND) {
			return super.getDefaultAttributeModifiers(slot);
		}
		if (this.defaultModifiers == null) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
					this.getTier().getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
					1.0F / this.params.attackDuration() - 4.0F, AttributeModifier.Operation.ADDITION));
			builder.put(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get(),
					new AttributeModifier(UUID.fromString("6f7cbe08-8f0c-4a30-8b96-454c12b915dd"), "Spear reach", 1.0, AttributeModifier.Operation.ADDITION));
			this.defaultModifiers = builder.build();
		}
		return this.defaultModifiers;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		// SPEAR is the trident throw windup in 1.18.2, the tip would point backwards
		return UseAnim.NONE;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		level.playSound(null, player, this.useSound(), player.getSoundSource(), 1.0F, 1.0F);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity user, int ticksRemaining) {
		if (!user.level.isClientSide) {
			this.kineticTick(user, stack, this.getUseDuration(stack) - ticksRemaining);
		}
	}

	/** One tick of couched lance contact checks, vanilla KineticWeapon logic. */
	public void kineticTick(LivingEntity user, ItemStack stack, int ticksUsed) {
		int delayTicks = (int) (this.params.delay() * 20.0F);
		Vec3 attackerMotion = sampleMotion(user.isPassenger() ? user.getRootVehicle() : user);
		if (ticksUsed < delayTicks) {
			return;
		}
		int sinceDelay = ticksUsed - delayTicks;

		Vec3 look = user.getLookAngle();
		double attackerSpeed = look.dot(attackerMotion);
		if (attackerSpeed <= 0.0) {
			return;
		}

		Vec3 reach = look.scale(KINETIC_REACH);
		List<Entity> targets = user.level.getEntities(user, user.getBoundingBox().expandTowards(reach).inflate(0.5),
				e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator()
						&& !user.isPassengerOfSameVehicle(e) && !e.hasPassenger(user));
		Object2IntOpenHashMap<Entity> recent = RECENT_STABS.computeIfAbsent(user, k -> new Object2IntOpenHashMap<>());
		recent.object2IntEntrySet().removeIf(e -> user.tickCount - e.getIntValue() > CONTACT_COOLDOWN_TICKS);

		boolean affected = false;
		for (Entity target : targets) {
			if (recent.containsKey(target)) {
				continue;
			}
			recent.put(target, user.tickCount);

			double targetSpeed = look.dot(sampleMotion(target.isPassenger() ? target.getRootVehicle() : target));
			double relativeSpeed = Math.max(0.0, attackerSpeed - targetSpeed);
			boolean dismount = sinceDelay <= this.params.dismountTime() * 20.0F && attackerSpeed >= this.params.dismountThreshold();
			boolean knockback = sinceDelay <= this.params.knockbackTime() * 20.0F && attackerSpeed >= this.params.knockbackThreshold();
			boolean damage = sinceDelay <= this.params.damageTime() * 20.0F && relativeSpeed >= this.params.damageThreshold();
			if (!dismount && !knockback && !damage) {
				continue;
			}

			float dealt = Mth.floor(relativeSpeed * this.params.damageMultiplier());
			DamageSource source = user instanceof Player player ? DamageSource.playerAttack(player) : DamageSource.mobAttack(user);
			if (dealt > 0.0F && damage) {
				target.hurt(source, dealt);
			}
			if (knockback && target instanceof LivingEntity living) {
				living.knockback(0.8F, -look.x, -look.z);
			}
			if (dismount && target.isPassenger()) {
				target.stopRiding();
			}
			stack.hurtAndBreak(1, user, u -> u.broadcastBreakEvent(u.getUsedItemHand()));
			user.level.playSound(null, target, this.hitSound(), user.getSoundSource(), 1.0F, 1.0F);
			affected = true;
		}

		if (affected && user instanceof Player player) {
			player.causeFoodExhaustion(0.1F);
		}
	}

	/** Blocks per second from consecutive samples, zero on the first sighting. */
	private static Vec3 sampleMotion(Entity mover) {
		Vec3 current = mover.position();
		Vec3 previous = LAST_SAMPLED_POS.put(mover, current);
		if (previous == null) {
			return Vec3.ZERO;
		}
		Vec3 perTick = current.subtract(previous);
		// a stale sample from a past use would read as a huge teleport
		return perTick.lengthSqr() > 25.0 ? Vec3.ZERO : perTick.scale(20.0);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, a -> a.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		attacker.level.playSound(null, attacker, this.attackSound(), attacker.getSoundSource(), 1.0F, 1.0F);

		int lunge = EnchantmentHelper.getItemEnchantmentLevel(CTBEnchantments.LUNGE.get(), stack);
		if (lunge > 0 && !attacker.isPassenger()) {
			Vec3 dir = new Vec3(attacker.getLookAngle().x, 0.0, attacker.getLookAngle().z).normalize();
			attacker.push(dir.x * LUNGE_IMPULSE_PER_LEVEL * lunge, 0.0, dir.z * LUNGE_IMPULSE_PER_LEVEL * lunge);
			attacker.hurtMarked = true;
			stack.hurtAndBreak(1, attacker, a -> a.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			attacker.level.playSound(null, attacker, CTBSounds.SPEAR_LUNGE.get(), attacker.getSoundSource(), 1.0F, 1.0F);
			if (attacker instanceof Player player) {
				player.causeFoodExhaustion(4.0F * lunge);
			}
		}
		return true;
	}

	@Override
	public boolean canAttackBlock(net.minecraft.world.level.block.state.BlockState state, Level level, net.minecraft.core.BlockPos pos, Player player) {
		return !player.isCreative();
	}

	private SoundEvent useSound() {
		return this.woodSounds ? CTBSounds.SPEAR_WOOD_USE.get() : CTBSounds.SPEAR_USE.get();
	}

	private SoundEvent hitSound() {
		return this.woodSounds ? CTBSounds.SPEAR_WOOD_HIT.get() : CTBSounds.SPEAR_HIT.get();
	}

	private SoundEvent attackSound() {
		return this.woodSounds ? CTBSounds.SPEAR_WOOD_ATTACK.get() : CTBSounds.SPEAR_ATTACK.get();
	}

	@Override
	public int getEnchantmentValue() {
		return this.getTier().getEnchantmentValue();
	}
}
