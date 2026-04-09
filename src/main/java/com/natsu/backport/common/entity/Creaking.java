package com.natsu.backport.common.entity;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.natsu.backport.common.block.CreakingHeartBlock;
import com.natsu.backport.common.block.entity.CreakingHeartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class Creaking extends Monster implements IAnimatable {
	protected static final AnimationBuilder FLY_ANIM = new AnimationBuilder().addAnimation("move.fly", true);
	
    @Nullable
    private BlockPos homePos = null;         // linked CreakingHeart 
    private boolean isTransient = false;     // when true, disapear if heart unlinked
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final String TAG_HOME_POS = "CreakingHomePos";
    private static final String TAG_IS_TRANSIENT = "IsTransient";
 
    public Creaking(EntityType<? extends Creaking> type, Level level) {
        super(type, level);
    }
 
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 1.0)      
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ATTACK_DAMAGE, 3.0)
            .add(Attributes.FOLLOW_RANGE, 32.0);
    }
 
    /**
     * Called by {@link CreakingHeartBlockEntity}, link this Creaking to the
     * {@link CreakingHeartBlock} at the position
     */
    public void setTransient(BlockPos blockPos) {
        this.homePos = blockPos;
        this.isTransient = true;
    }
 
    public boolean isTransient() {
        return this.isTransient;
    }
 
    @Nullable
    public BlockPos getHomePos() {
        return this.homePos;
    }
 
    /**
     * Checks if the Creaking Heart exists still.
     */
    public boolean hasValidHome() {
        if (!this.isTransient || this.homePos == null) return false;
        if (!(this.level instanceof ServerLevel serverLevel)) return false;
 
        BlockEntity be = serverLevel.getBlockEntity(this.homePos);
        return be instanceof CreakingHeartBlockEntity;
    }
 
    public void detachFromHeart() {
        this.homePos = null;
        this.isTransient = false;
        if (!this.level.isClientSide) {
            this.discard();
        }
    }
 

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && this.isTransient && this.homePos != null) {
            BlockEntity be = this.level.getBlockEntity(this.homePos);
            if (be instanceof CreakingHeartBlockEntity heart) {
                heart.creakingHurt();
                return false;
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.homePos != null) {
            tag.putInt(TAG_HOME_POS + "X", this.homePos.getX());
            tag.putInt(TAG_HOME_POS + "Y", this.homePos.getY());
            tag.putInt(TAG_HOME_POS + "Z", this.homePos.getZ());
        }
        tag.putBoolean(TAG_IS_TRANSIENT, this.isTransient);
    }
 
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_HOME_POS + "X")) {
            this.homePos = new BlockPos(
                tag.getInt(TAG_HOME_POS + "X"),
                tag.getInt(TAG_HOME_POS + "Y"),
                tag.getInt(TAG_HOME_POS + "Z")
            );
        }
        this.isTransient = tag.getBoolean(TAG_IS_TRANSIENT);
    }
 

    @Override
    public void registerControllers(final AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "Anim_name", 5, this::flyAnimController));
    }
 
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
    
    protected <E extends Creaking> PlayState flyAnimController(final AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(FLY_ANIM);

            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

	public void tearDown() {
		// TODO Auto-generated method stub
		
	}

	public void creakingDeathEffects(DamageSource p_364053_) {
		// TODO Auto-generated method stub
		
	}

	public void setTearingDown() {
		// TODO Auto-generated method stub
		
	}

	public void makeSound(@NotNull SoundEvent soundEvent) {
		if (soundEvent != null) {
			this.getLevel().playSound(null, this.getOnPos(), soundEvent, SoundSource.HOSTILE, 1.0f, 1.0f);
		}
	}
}
