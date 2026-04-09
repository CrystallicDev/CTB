package com.natsu.backport.common.block;

import java.util.Random;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.natsu.backport.common.block.entity.CreakingHeartBlockEntity;
import com.natsu.backport.common.block.state.CreakingHeartState;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CreakingHeartBlock extends BaseEntityBlock {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty<CreakingHeartState> STATE = EnumProperty.create("state", CreakingHeartState.class);
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
 

    // =========================================================
    //  isNaturalNight : level.isMoonVisible() n'existe pas en 1.18.2
    // =========================================================
 
    public static boolean isNaturalNight(Level level) {
        if (level.isClientSide) return false;
        return !level.isDay() && !level.isRaining()
            || (!level.isDay() && level.getMoonBrightness() > 0.0f);
    }
 

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(level.getBlockEntity(pos) instanceof CreakingHeartBlockEntity heart)) return;
 
        // getDamageSource() n'existe pas sur Explosion en 1.18.2, on passe null
        heart.removeProtector(null);
 
        // ServerExplosion et getIndirectSourceEntity n'existent pas en 1.18.2
        // On récupère l'instigateur via getSourceMob() ou getExploder()
        Entity source = explosion.getExploder();
        if (source instanceof Player player) {
            BlockState state = level.getBlockState(pos);
            this.tryAwardExperience(player, state, level, pos);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof CreakingHeartBlockEntity heart) {
            // En 1.18.2 : DamageSource.playerAttack(player) directement accessible
            heart.removeProtector(DamageSource.playerAttack(player));
            this.tryAwardExperience(player, state, level, pos);
        }
        super.playerWillDestroy(level, pos, state, player);
    }
 
    private void tryAwardExperience(Player player, BlockState state, Level level, BlockPos pos) {
        if (!player.isCreative()
            && !player.isSpectator()
            && state.getValue(NATURAL)
            && level instanceof ServerLevel serverLevel) {
            // nextIntBetweenInclusive(20, 24) → nextInt(5) + 20
            this.popExperience(serverLevel, pos, level.random.nextInt(5) + 20);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
 
    
    

    public CreakingHeartBlock(BlockBehaviour.Properties p_366361_) {
        super(p_366361_);
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(STATE, CreakingHeartState.UPROOTED).setValue(NATURAL, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_361541_, BlockState p_365645_) {
        return new CreakingHeartBlockEntity(p_361541_, p_365645_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_363998_, BlockState p_362026_, BlockEntityType<T> p_362183_) {
        if (p_363998_.isClientSide) {
            return null;
        } else {
            return p_362026_.getValue(STATE) != CreakingHeartState.UPROOTED
                ? createTickerHelper(p_362183_, CTBBlockEntities.CREAKING_HEART.get(), CreakingHeartBlockEntity::serverTick)
                : null;
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    	if (!state.is(newState.getBlock())) {
    		if (level.getBlockEntity(pos) instanceof CreakingHeartBlockEntity heart) {
    			heart.removeProtector(null);
    		}
    	}
    	super.onRemove(state, level, pos, newState, isMoving);
    }

    

    @Override
    public void animateTick(BlockState p_363486_, Level p_367731_, BlockPos p_364380_, Random p_362325_) {
        if (isNaturalNight(p_367731_)) {
            if (p_363486_.getValue(STATE) != CreakingHeartState.UPROOTED) {
                if (p_362325_.nextInt(16) == 0 && isSurroundedByLogs(p_367731_, p_364380_)) {
                    p_367731_.playLocalSound(
                        p_364380_.getX(), p_364380_.getY(), p_364380_.getZ(), CTBSounds.CREAKING_HEART_IDLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false
                    );
                }
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                   LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public static boolean hasRequiredLogs(BlockState state, LevelReader level, BlockPos pos) {
        Direction.Axis axis = state.getValue(AXIS);

        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != axis) continue;

            BlockState neighbor = level.getBlockState(pos.relative(direction));
            if (!neighbor.is(CTBTags.Blocks.PALE_OAK_LOGS) || neighbor.getValue(AXIS) != axis) {
                return false;
            }
        }
        return true;
    }

    @Override
	public void tick(BlockState p_396773_, ServerLevel p_396152_, BlockPos p_394279_, Random p_392431_) {
        BlockState blockstate = updateState(p_396773_, p_396152_, p_394279_);
        if (blockstate != p_396773_) {
            p_396152_.setBlock(p_394279_, blockstate, 3);
        }
    }

    private static BlockState updateState(BlockState p_366979_, Level p_397672_, BlockPos p_368789_) {
        boolean flag = hasRequiredLogs(p_366979_, p_397672_, p_368789_);
        boolean flag1 = p_366979_.getValue(STATE) == CreakingHeartState.UPROOTED;
        return flag && flag1 ? p_366979_.setValue(STATE, isNaturalNight(p_397672_) ? CreakingHeartState.AWAKE : CreakingHeartState.DORMANT) : p_366979_;
    }

    

    private static boolean isSurroundedByLogs(LevelAccessor p_369449_, BlockPos p_360949_) {
        for (Direction direction : Direction.values()) {
            BlockPos blockpos = p_360949_.relative(direction);
            BlockState blockstate = p_369449_.getBlockState(blockpos);
            if (!blockstate.is(CTBTags.Blocks.PALE_OAK_LOGS)) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_368175_) {
        return updateState(this.defaultBlockState().setValue(AXIS, p_368175_.getClickedFace().getAxis()), p_368175_.getLevel(), p_368175_.getClickedPos());
    }

    @Override
	public BlockState rotate(BlockState p_364749_, Rotation p_361524_) {
        return RotatedPillarBlock.rotatePillar(p_364749_, p_361524_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_365552_) {
        p_365552_.add(AXIS, STATE, NATURAL);
    }

    

    @Override
	public boolean hasAnalogOutputSignal(BlockState p_369932_) {
        return true;
    }

    @Override
	public int getAnalogOutputSignal(BlockState p_360933_, Level p_366654_, BlockPos p_366296_) {
        if (p_360933_.getValue(STATE) == CreakingHeartState.UPROOTED) {
            return 0;
        } else {
            return p_366654_.getBlockEntity(p_366296_) instanceof CreakingHeartBlockEntity creakingheartblockentity ? creakingheartblockentity.getAnalogOutputSignal() : 0;
        }
    }
}
