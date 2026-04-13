package com.natsu.backport.common.block;

import java.util.Random;

import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class EyeblossomBlock extends FlowerBlock {

	private static final int EYEBLOSSOM_XZ_RANGE = 3;
    private static final int EYEBLOSSOM_Y_RANGE = 2;
    private final EyeblossomBlock.Type type;
 
    
    
    public EyeblossomBlock(EyeblossomBlock.Type type, BlockBehaviour.Properties properties) {
        super(() -> type.effect, (int) type.effectDuration, properties);
        this.type = type;
    }
 
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        if (this.type.emitSounds() && random.nextInt(700) == 0) {
            BlockState below = level.getBlockState(pos.below());
            if (below.is(CTBBlocks.PALE_MOSS.moss.get())) {
            	level.playSound(null, pos, CTBSounds.EYEBLOSSOM_IDLE.get(), SoundSource.AMBIENT,  1.0F, 1.0F);
            }
        }
    }
 
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (this.tryChangingState(state, level, pos, random)) {
            level.playSound(null, pos, this.type.transform().longSwitchSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        super.randomTick(state, level, pos, random);
    }
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (this.tryChangingState(state, level, pos, random)) {
            level.playSound(null, pos, this.type.transform().shortSwitchSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        super.tick(state, level, pos, random);
    }
 
    private boolean tryChangingState(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!level.dimensionType().natural()) return false;
 
        if (CreakingHeartBlock.isNaturalNight(level) == this.type.open) return false;
 
        EyeblossomBlock.Type nextType = this.type.transform();
        level.setBlock(pos, nextType.state(), 3);
        nextType.spawnTransformParticle(level, pos, random);
        BlockPos.betweenClosed(
            pos.offset(-EYEBLOSSOM_XZ_RANGE, -EYEBLOSSOM_Y_RANGE, -EYEBLOSSOM_XZ_RANGE),
            pos.offset( EYEBLOSSOM_XZ_RANGE,  EYEBLOSSOM_Y_RANGE,  EYEBLOSSOM_XZ_RANGE)
        ).forEach(neighbor -> {
            BlockState neighborState = level.getBlockState(neighbor);
            if (neighborState.getBlock() == state.getBlock()) {
                double dist = Math.sqrt(pos.distSqr(neighbor));
                int min = (int)(dist * 5.0);
                int max = (int)(dist * 10.0);
                int delay = (min >= max) ? min : random.nextInt(max - min + 1) + min;
                level.scheduleTick(BlockPos.of(neighbor.asLong()), state.getBlock(), delay);
            }
        });
 
        return true;
    }
 
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide()
            && level.getDifficulty() != Difficulty.PEACEFUL
            && entity instanceof Bee bee
            && !bee.hasEffect(MobEffects.POISON)) {
            bee.addEffect(this.getBeeInteractionEffect());
        }
    }
 
    public MobEffectInstance getBeeInteractionEffect() {
        return new MobEffectInstance(MobEffects.POISON, 25);
    }

    public enum Type implements StringRepresentable {
        OPEN(true, MobEffects.BLINDNESS, 11.0F, CTBSounds.EYEBLOSSOM_OPEN_LONG, CTBSounds.EYEBLOSSOM_OPEN, 16545810),
        CLOSED(false, MobEffects.CONFUSION, 7.0F, CTBSounds.EYEBLOSSOM_CLOSE_LONG, CTBSounds.EYEBLOSSOM_CLOSE, 6250335);

        final boolean open;
        final MobEffect effect;
        final float effectDuration;
        final RegistryObject<SoundEvent> longSwitchSound;   // ← RegistryObject
        final RegistryObject<SoundEvent> shortSwitchSound;  // ← RegistryObject
        private final int particleColor;

        Type(boolean open, MobEffect effect, float effectDuration,
             RegistryObject<SoundEvent> longSwitchSound,    // ← RegistryObject
             RegistryObject<SoundEvent> shortSwitchSound,   // ← RegistryObject
             int particleColor) {
            this.open = open;
            this.effect = effect;
            this.effectDuration = effectDuration;
            this.longSwitchSound = longSwitchSound;
            this.shortSwitchSound = shortSwitchSound;
            this.particleColor = particleColor;
        }
	 
	    public Block block() {
	        return this.open ? CTBBlocks.OPEN_EYEBLOSSOM.get() : CTBBlocks.CLOSED_EYEBLOSSOM.get();
	    }
	 
	    public BlockState state() {
	        return this.block().defaultBlockState();
	    }
	 
	    public EyeblossomBlock.Type transform() {
	        return fromBoolean(!this.open);
	    }
	 
	    public boolean emitSounds() {
	        return this.open;
	    }
	 
	    public static EyeblossomBlock.Type fromBoolean(boolean open) {
	        return open ? OPEN : CLOSED;
	    }
	 

	    public void spawnTransformParticle(ServerLevel level, BlockPos pos, Random random) {
	        float r = ((this.particleColor >> 16) & 0xFF) / 255.0f;
	        float g = ((this.particleColor >> 8)  & 0xFF) / 255.0f;
	        float b = ( this.particleColor        & 0xFF) / 255.0f;
	 
	        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.5f);
	 
	        Vec3 center = Vec3.atCenterOf(pos);
	 
	        double spread = 0.5 + random.nextDouble();
	        double ox = (random.nextDouble() - 0.5) * spread;
	        double oy = (random.nextDouble() + 1.0) * spread;
	        double oz = (random.nextDouble() - 0.5) * spread;
	 
	        level.sendParticles(dust,
	            center.x, center.y, center.z, 3, ox, oy, oz, 0.05
	        );
	    }
	 
	    public SoundEvent longSwitchSound() {
	        return this.longSwitchSound.get();
	    }
	    
	    public SoundEvent shortSwitchSound() {
	        return this.shortSwitchSound.get();
	    }
	 
	    @Override
	    public String getSerializedName() {
	        return this.open ? "open" : "closed";
	    }
	}
}