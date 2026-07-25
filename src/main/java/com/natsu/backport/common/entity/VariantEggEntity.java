package com.natsu.backport.common.entity;

import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBItems;
import com.natsu.backport.server.events.AnimalVariants;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** A thrown blue or brown egg : hatches chicks of the matching climate variant. */
public class VariantEggEntity extends ThrowableItemProjectile {

	public VariantEggEntity(EntityType<? extends VariantEggEntity> type, Level level) {
		super(type, level);
	}

	public VariantEggEntity(Level level, LivingEntity thrower) {
		super(CTBEntities.VARIANT_EGG.get(), thrower, level);
	}

	@Override
	protected Item getDefaultItem() {
		return CTBItems.BLUE_EGG.get();
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; i++) {
				this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
						this.getX(), this.getY(), this.getZ(),
						(this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08,
						(this.random.nextFloat() - 0.5) * 0.08);
			}
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		result.getEntity().hurt(net.minecraft.world.damagesource.DamageSource.thrown(this, this.getOwner()), 0.0F);
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!this.level.isClientSide) {
			if (this.random.nextInt(8) == 0) {
				int count = this.random.nextInt(32) == 0 ? 4 : 1;
				byte variant = this.getItem().is(CTBItems.BROWN_EGG.get())
						? AnimalVariants.WARM : AnimalVariants.COLD;
				for (int i = 0; i < count; i++) {
					Chicken chicken = EntityType.CHICKEN.create(this.level);
					if (chicken != null) {
						chicken.setAge(-24000);
						chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
						chicken.getPersistentData().putByte(AnimalVariants.TAG, variant);
						this.level.addFreshEntity(chicken);
					}
				}
			}
			this.level.broadcastEntityEvent(this, (byte) 3);
			this.discard();
		}
	}
}
