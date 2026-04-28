package com.natsu.backport.common.entity;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SulphurCube extends Slime implements Bucketable {
	 
    private static final EntityDataAccessor<Optional<BlockState>> DATA_HELD_BLOCK =
            SynchedEntityData.defineId(SulphurCube.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> DATA_FROM_BUCKET =
            SynchedEntityData.defineId(SulphurCube.class, EntityDataSerializers.BOOLEAN);
 
    public static final int ADULT_SIZE = 2;
    public static final int BABY_SIZE = 1;
    public static final int DEFAULT_GROW_UP_TIME = 24000; 
 
    private int growUpTime = 0;
 
    public SulphurCube(EntityType<? extends SulphurCube> type, Level level) {
        super(type, level);
    }
 
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)      
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)     
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }
 

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HELD_BLOCK, Optional.empty());
        this.entityData.define(DATA_FROM_BUCKET, false);
    }
 
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.hasBlock()) {
            tag.put("HeldBlock", NbtUtils.writeBlockState(this.getHeldBlock()));
        }
        tag.putBoolean("FromBucket", this.fromBucket());
        tag.putInt("GrowUpTime", this.growUpTime);
    }
 
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HeldBlock")) {
            this.setHeldBlock(NbtUtils.readBlockState(tag.getCompound("HeldBlock")));
        } else {
            this.setHeldBlock(Blocks.AIR.defaultBlockState());
        }
        this.setFromBucket(tag.getBoolean("FromBucket"));
        this.growUpTime = tag.getInt("GrowUpTime");
    }
 

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.removeAllGoals();
    }
 
    @Override
    public boolean isPushable() {
        return !this.hasBlock(); 
    }
 

    public BlockState getHeldBlock() {
        return this.entityData.get(DATA_HELD_BLOCK).orElse(Blocks.AIR.defaultBlockState());
    }
 
    public void setHeldBlock(BlockState state) {
        this.entityData.set(DATA_HELD_BLOCK, state.isAir() ? Optional.empty() : Optional.of(state));
    }
 
    public boolean hasBlock() {
        return !this.getHeldBlock().isAir();
    }
 
    public boolean isAdult() {
        return this.getSize() >= ADULT_SIZE;
    }
 
    public boolean canAbsorb() {
        return this.isAdult() && !this.hasBlock();
    }
 
    public static boolean isBlockAbsorbable(BlockState state) {
        if (state.isAir()) return false;
        if (state.hasBlockEntity()) return false;
        return Block.isShapeFullBlock(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
    }
 
    private void dropHeldBlock() {
        BlockState state = this.getHeldBlock();
        if (!state.isAir()) {
            ItemStack stack = new ItemStack(state.getBlock().asItem());
            if (!stack.isEmpty()) this.spawnAtLocation(stack);
            this.setHeldBlock(Blocks.AIR.defaultBlockState());
        }
    }
 
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
 
        if (stack.is(Items.WATER_BUCKET) && this.isAlive() && !this.hasBlock()) {
            return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
        }
 
        if (stack.is(Items.SHEARS) && this.hasBlock()) {
            this.dropHeldBlock();
            this.level.playSound(null, this, SoundEvents.SHEEP_SHEAR, this.getSoundSource(), 1.0F, 1.0F);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
 
        if (this.canAbsorb() && stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (isBlockAbsorbable(state)) {
                if (!this.level.isClientSide) {
                    this.setHeldBlock(state);
                    this.level.playSound(null, this, SoundEvents.SLIME_SQUISH, this.getSoundSource(), 1.0F, 0.8F);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }
 
        return super.mobInteract(player, hand);
    }
 
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.hasBlock() && !source.isBypassInvul() && !source.isBypassMagic() && !source.isCreativePlayer()) {
            SoundEvent hurtSound = this.getHurtSound(source);
            if (hurtSound != null) this.playSound(hurtSound, this.getSoundVolume(), this.getVoicePitch());
            this.level.broadcastEntityEvent(this, (byte) 2);
 
            if (source.getEntity() instanceof LivingEntity attacker) {
                double dx = attacker.getX() - this.getX();
                double dz = attacker.getZ() - this.getZ();
                this.knockback(1.5D, dx, dz);
            }
            return false;
        }
        return super.hurt(source, amount);
    }
 
    @Override
    protected void jumpFromGround() {
        if (this.hasBlock()) return;
        super.jumpFromGround();
    }
 
    public int getGrowUpTime() { return this.growUpTime; }
    public void setGrowUpTime(int t) { this.growUpTime = t; }
 
    @Override
    public void aiStep() {
        super.aiStep();
 
        // Croissance côté serveur
        if (!this.level.isClientSide && this.isAlive() && !this.isAdult() && this.growUpTime > 0) {
            this.growUpTime--;
            if (this.growUpTime <= 0) {
                this.setSize(ADULT_SIZE, false);
                this.level.broadcastEntityEvent(this, (byte) 18);
            }
        }

        if (this.level.isClientSide && this.tickCount % 4 == 0 && !this.isDeadOrDying()) {
            float r = this.getBbWidth() * 0.5F;
            this.level.addParticle(ParticleTypes.FALLING_NECTAR,
                    this.getX() + (this.random.nextDouble() - 0.5) * r,
                    this.getY() + this.getBbHeight() * 0.7,
                    this.getZ() + (this.random.nextDouble() - 0.5) * r,
                    0.0, 0.02, 0.0);
        }
    }
 
    @Override
    public void setSize(int size, boolean resetHealth) {
        super.setSize(size <= 1 ? BABY_SIZE : ADULT_SIZE, resetHealth);
    }
 
    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag dataTag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.SPAWNER ||
            reason == MobSpawnType.CHUNK_GENERATION || reason == MobSpawnType.SPAWN_EGG) {
            this.setSize(ADULT_SIZE, true);
            this.growUpTime = 0;
        }
        return result;
    }
 
    @Override
    public void remove(Entity.RemovalReason reason) {
        int size = this.getSize();
        if (!this.level.isClientSide && size > 1 && this.isDeadOrDying()) {
            this.dropHeldBlock();
            spawnBabies(size);
            this.setSize(BABY_SIZE, false);
        }
        super.remove(reason);
    }
 
    private void spawnBabies(int parentSize) {
        Component name = this.getCustomName();
        boolean noAi = this.isNoAi();
        float halfWidth = parentSize / 4.0F;
        int childSize = parentSize / 2;
 
        for (int j = 0; j < 2; j++) {
            float xOff = ((float) (j % 2) - 0.5F) * halfWidth;
            float zOff = ((float) (j / 2) - 0.5F) * halfWidth;
            if (this.getType().create(this.level) instanceof SulphurCube child) {
                if (this.isPersistenceRequired()) child.setPersistenceRequired();
                child.setCustomName(name);
                child.setNoAi(noAi);
                child.setInvulnerable(this.isInvulnerable());
                child.setSize(childSize, true);
                child.setGrowUpTime(DEFAULT_GROW_UP_TIME);
                child.moveTo(this.getX() + xOff, this.getY() + 0.5, this.getZ() + zOff,
                        this.random.nextFloat() * 360.0F, 0.0F);
                this.level.addFreshEntity(child);
            }
        }
    }
 

    @Override
    public boolean fromBucket() {
        return this.entityData.get(DATA_FROM_BUCKET);
    }
 
    @Override
    public void setFromBucket(boolean fromBucket) {
        this.entityData.set(DATA_FROM_BUCKET, fromBucket);
    }
 
    @Override
    public void saveToBucketTag(ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("Size", this.getSize() - 1); 
        tag.putInt("GrowUpTime", this.growUpTime);
        if (this.hasBlock()) {
            tag.put("HeldBlock", NbtUtils.writeBlockState(this.getHeldBlock()));
        }
    }
 
    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        if (tag.contains("Size")) this.setSize(tag.getInt("Size") + 1, true);
        if (tag.contains("GrowUpTime")) this.growUpTime = tag.getInt("GrowUpTime");
        if (tag.contains("HeldBlock")) {
            this.setHeldBlock(NbtUtils.readBlockState(tag.getCompound("HeldBlock")));
        }
    }
 
    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.AXOLOTL_BUCKET);
    }
 
    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }
 
    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }
 
    @Override
    public boolean removeWhenFarAway(double distance) {
        return !this.fromBucket() && !this.hasCustomName();
    }
}