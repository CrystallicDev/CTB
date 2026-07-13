package com.natsu.backport.common.block.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Vector3f;
import com.natsu.backport.common.block.CreakingHeartBlock;
import com.natsu.backport.common.block.state.CreakingHeartState;
import com.natsu.backport.common.entity.Creaking;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEntities;
import com.natsu.backport.common.registry.CTBSounds;
import com.natsu.backport.common.registry.CTBTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CreakingHeartBlockEntity extends BlockEntity {
    private static final int PLAYER_DETECTION_RANGE = 32;
    public static final int CREAKING_ROAMING_RADIUS = 32;
    private static final int DISTANCE_CREAKING_TOO_FAR = 34;
    private static final int SPAWN_RANGE_XZ = 16;
    private static final int SPAWN_RANGE_Y = 8;
    private static final int ATTEMPTS_PER_SPAWN = 5;
    private static final int UPDATE_TICKS = 20;
    private static final int UPDATE_TICKS_VARIANCE = 5;
    private static final int HURT_CALL_TOTAL_TICKS = 100;
    private static final int NUMBER_OF_HURT_CALLS = 10;
    private static final int HURT_CALL_INTERVAL = 10;
    private static final int HURT_CALL_PARTICLE_TICKS = 50;
    private static final int MAX_COUNT = 64;
    private static final int TICKS_GRACE_PERIOD = 30;

    public static final int CREAKING_ORANGE = 16545810;
    public static final int CREAKING_GRAY = 6250335;

    private static final Optional<Creaking> NO_CREAKING = Optional.empty();
    @Nullable
    private Either<Creaking, UUID> creakingInfo;
    private long ticksExisted;
    private int ticker;
    private int emitter;
    @Nullable
    private Vec3 emitterTarget;
    private int outputSignal;

    public CreakingHeartBlockEntity(BlockPos p_369235_, BlockState p_367834_) {
        super(CTBBlockEntities.CREAKING_HEART.get(), p_369235_, p_367834_);
    }

    public static void serverTick(Level p_360952_, BlockPos p_367184_, BlockState p_365574_, CreakingHeartBlockEntity p_366884_) {
        p_366884_.ticksExisted++;
        if (p_360952_ instanceof ServerLevel serverlevel) {
            int $$6 = p_366884_.computeAnalogOutputSignal();
            if (p_366884_.outputSignal != $$6) {
                p_366884_.outputSignal = $$6;
                p_360952_.updateNeighbourForOutputSignal(p_367184_, CTBBlocks.CREAKING_HEART.get());
            }

            if (p_366884_.emitter > 0) {
                if (p_366884_.emitter > 50) {
                    p_366884_.emitParticles(serverlevel, 1, true);
                    p_366884_.emitParticles(serverlevel, 1, false);
                }

                if (p_366884_.emitter % 10 == 0 && p_366884_.emitterTarget != null) {
                    p_366884_.getCreakingProtector().ifPresent(p_376513_ -> p_366884_.emitterTarget = p_376513_.getBoundingBox().getCenter());
                    Vec3 vec3 = Vec3.atCenterOf(p_367184_);
                    float f = 0.2F + 0.8F * (100 - p_366884_.emitter) / 100.0F;
                    Vec3 vec31 = vec3.subtract(p_366884_.emitterTarget).scale(f).add(p_366884_.emitterTarget);
                    BlockPos blockpos = new BlockPos(vec31);
                    float f1 = p_366884_.emitter / 2.0F / 100.0F + 0.5F;
                    serverlevel.playSound(null, blockpos, CTBSounds.CREAKING_HEART_HURT.get(), SoundSource.BLOCKS, f1, 1.0F);
                }

                p_366884_.emitter--;
            }

            if (p_366884_.ticker-- < 0) {
                p_366884_.ticker = p_366884_.level == null ? UPDATE_TICKS : p_366884_.level.random.nextInt(UPDATE_TICKS_VARIANCE) + UPDATE_TICKS;
                BlockState blockstate = updateCreakingState(p_360952_, p_365574_, p_367184_, p_366884_);
                if (blockstate != p_365574_) {
                    p_360952_.setBlock(p_367184_, blockstate, 3);
                    if (blockstate.getValue(CreakingHeartBlock.STATE) == CreakingHeartState.UPROOTED) {
                        return;
                    }
                }

                if (p_366884_.creakingInfo == null) {
                    if (blockstate.getValue(CreakingHeartBlock.STATE) == CreakingHeartState.AWAKE) {
                        if (p_360952_.getDifficulty() != Difficulty.PEACEFUL) {
                            if (serverlevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                                Player player = p_360952_.getNearestPlayer(p_367184_.getX(), p_367184_.getY(), p_367184_.getZ(), PLAYER_DETECTION_RANGE, true);
                                if (player != null) {
                                    Creaking creaking1 = spawnProtector(serverlevel, p_366884_);
                                    if (creaking1 != null) {
                                        p_366884_.setCreakingInfo(creaking1);
                                        creaking1.makeSound(CTBSounds.CREAKING_SPAWN.get());
                                        p_360952_.playSound(null, p_366884_.getBlockPos(), CTBSounds.CREAKING_HEART_SPAWN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Optional<Creaking> optional = p_366884_.getCreakingProtector();
                    if (optional.isPresent()) {
                        Creaking creaking = optional.get();
                        if (!CreakingHeartBlock.isNaturalNight(p_360952_) && !creaking.isPersistenceRequired() || p_366884_.distanceToCreaking() > DISTANCE_CREAKING_TOO_FAR) {
                            p_366884_.removeProtector(null);
                        }
                    }
                }
            }
        }
    }

    private static BlockState updateCreakingState(Level p_391469_, BlockState p_392285_, BlockPos p_394743_, CreakingHeartBlockEntity p_397498_) {
        if (!CreakingHeartBlock.hasRequiredLogs(p_392285_, p_391469_, p_394743_) && p_397498_.creakingInfo == null) {
            return p_392285_.setValue(CreakingHeartBlock.STATE, CreakingHeartState.UPROOTED);
        } else {
            boolean flag = CreakingHeartBlock.isNaturalNight(p_391469_);
            return p_392285_.setValue(CreakingHeartBlock.STATE, flag ? CreakingHeartState.AWAKE : CreakingHeartState.DORMANT);
        }
    }

    private double distanceToCreaking() {
        return this.getCreakingProtector().map(p_390963_ -> Math.sqrt(p_390963_.distanceToSqr(Vec3.atBottomCenterOf(this.getBlockPos())))).orElse(0.0);
    }

    private void clearCreakingInfo() {
        this.creakingInfo = null;
        this.setChanged();
    }

    public void setCreakingInfo(Creaking p_376531_) {
        this.creakingInfo = Either.left(p_376531_);
        this.setChanged();
    }

    public void setCreakingInfo(UUID p_376550_) {
        this.creakingInfo = Either.right(p_376550_);
        this.ticksExisted = 0L;
        this.setChanged();
    }

    private Optional<Creaking> getCreakingProtector() {
        if (this.creakingInfo == null) {
            return NO_CREAKING;
        } else {
            if (this.creakingInfo.left().isPresent()) {
            	Creaking creaking = this.creakingInfo.left().get();
                if (!creaking.isRemoved()) {
                    return Optional.of(creaking);
                }

                this.setCreakingInfo(creaking.getUUID());
            }

            if (this.level instanceof ServerLevel serverlevel && this.creakingInfo.right().isPresent()) {
                UUID uuid = this.creakingInfo.right().get();
                if (serverlevel.getEntity(uuid) instanceof Creaking creaking1) {
                    this.setCreakingInfo(creaking1);
                    return Optional.of(creaking1);
                } else {
                    if (this.ticksExisted >= TICKS_GRACE_PERIOD) {
                        this.clearCreakingInfo();
                    }

                    return NO_CREAKING;
                }
            } else {
                return NO_CREAKING;
            }
        }
    }

    @Nullable
    private static Creaking spawnProtector(ServerLevel level, CreakingHeartBlockEntity blockEntity) {
        BlockPos blockPos = blockEntity.getBlockPos();
        for (int attempt = 0; attempt < ATTEMPTS_PER_SPAWN; attempt++) {
            int dx = level.random.nextInt(SPAWN_RANGE_XZ * 2 + 1) - SPAWN_RANGE_XZ;
            int dy = level.random.nextInt(SPAWN_RANGE_Y * 2 + 1) - SPAWN_RANGE_Y;
            int dz = level.random.nextInt(SPAWN_RANGE_XZ * 2 + 1) - SPAWN_RANGE_XZ;
            BlockPos spawnPos = blockPos.offset(dx, dy, dz);

            if (!level.getBlockState(spawnPos).isCollisionShapeFullBlock(level, spawnPos)
                && level.getBlockState(spawnPos.below()).isFaceSturdy(level, spawnPos.below(), Direction.UP)) {

                Creaking creaking = CTBEntities.CREAKING.get().create(level);
                if (creaking == null) {
					return null;
				}

                creaking.moveTo(spawnPos, level.random.nextFloat() * 360f, 0f);
                if (!level.noCollision(creaking, creaking.getBoundingBox())) {
					continue;
				}

                creaking.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                    MobSpawnType.SPAWNER, null, null);
                level.addFreshEntityWithPassengers(creaking);
                level.broadcastEntityEvent(creaking, (byte) 60);
                creaking.setTransient(blockPos);

                return creaking;
            }
        }
        return null;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void creakingHurt() {
        Optional<Creaking> opt = this.getCreakingProtector();
        if (opt.isEmpty() || !(this.level instanceof ServerLevel serverlevel) || (this.emitter > 0)) {
			return;
		}

        Creaking creaking = opt.get();
        this.emitParticles(serverlevel, 20, false);

        if (this.getBlockState().getValue(CreakingHeartBlock.STATE) == CreakingHeartState.AWAKE) {
            int j = this.level.getRandom().nextInt(2) + 2;
            for (int i = 0; i < j; i++) {
                this.spreadResin().ifPresent(pos -> {
                    this.level.playSound(null, pos, CTBSounds.RESIN_PLACE.get(),
                        SoundSource.BLOCKS, 1.0F, 1.0F);
                });
            }
        }

        this.emitter = 100;
        this.emitterTarget = creaking.getBoundingBox().getCenter();
    }

	private Optional<BlockPos> spreadResin() {
		Queue<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(this.worldPosition);
		visited.add(this.worldPosition);

		while (!queue.isEmpty() && visited.size() < MAX_COUNT) {
			BlockPos current = queue.poll();
			BlockState currentState = this.level.getBlockState(current);
			if (!currentState.is(CTBTags.Blocks.PALE_OAK_LOGS)) {
				List<Direction> directions = shuffledDirections();
				for (Direction direction : directions) {
					BlockPos neighbor = current.relative(direction);
					if (!visited.contains(neighbor) && this.level.getBlockState(neighbor).is(CTBTags.Blocks.PALE_OAK_LOGS)) {
						visited.add(neighbor);
						queue.add(neighbor);
					}
				}
				continue;
			}
			for (Direction direction : shuffledDirections()) {
				BlockPos blockpos = current.relative(direction);
				BlockState blockstate = this.level.getBlockState(blockpos);
				Direction opposite = direction.getOpposite();

				if (blockstate.isAir()) {
					blockstate = CTBBlocks.RESIN.block.get().defaultBlockState();
				} else if (blockstate.is(Blocks.WATER) && blockstate.getFluidState().isSource()) {
					blockstate = CTBBlocks.RESIN.block.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
				}

				if (blockstate.is(CTBBlocks.RESIN.block.get()) && !blockstate.getValue(MultifaceBlock.getFaceProperty(opposite))) {
					this.level.setBlock(blockpos, blockstate.setValue(MultifaceBlock.getFaceProperty(opposite), true),
							3);
					return Optional.of(blockpos);
				}
			}
			List<Direction> directions = shuffledDirections();
			for (Direction direction : directions) {
				BlockPos neighbor = current.relative(direction);
				if (!visited.contains(neighbor) && this.level.getBlockState(neighbor).is(CTBTags.Blocks.PALE_OAK_LOGS)) {
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		}

		return Optional.empty();
	}

	private List<Direction> shuffledDirections() {
		List<Direction> dirs = new ArrayList<>(Arrays.asList(Direction.values()));
		Collections.shuffle(dirs, new Random(this.level.random.nextLong()));
		return dirs;
	}

	private void emitParticles(ServerLevel level, int count, boolean reverse) {
	    Optional<Creaking> opt = this.getCreakingProtector();
	    if (opt.isEmpty()) {
			return;
		}
	    Creaking creaking = opt.get();

	    Vec3 color = reverse
	        ? new Vec3(((CREAKING_ORANGE >> 16) & 0xFF) / 255.0,
	                   ((CREAKING_ORANGE >>  8) & 0xFF) / 255.0,
	                   ( CREAKING_ORANGE        & 0xFF) / 255.0)
	        : new Vec3(((CREAKING_GRAY   >> 16) & 0xFF) / 255.0,
	                   ((CREAKING_GRAY   >>  8) & 0xFF) / 255.0,
	                   ( CREAKING_GRAY          & 0xFF) / 255.0);

	    DustParticleOptions dust = new DustParticleOptions(
	        new Vector3f((float) color.x, (float) color.y, (float) color.z), 1.0F);
	    Random random = level.random;

	    for (int i = 0; i < count; i++) {
	        AABB aabb = creaking.getBoundingBox();
	        Vec3 fromCreaking = new Vec3(
	            aabb.minX + random.nextDouble() * aabb.getXsize(),
	            aabb.minY + random.nextDouble() * aabb.getYsize(),
	            aabb.minZ + random.nextDouble() * aabb.getZsize());
	        Vec3 fromBlock = new Vec3(
	            this.getBlockPos().getX() + random.nextDouble(),
	            this.getBlockPos().getY() + random.nextDouble(),
	            this.getBlockPos().getZ() + random.nextDouble());

	        Vec3 origin = reverse ? fromBlock : fromCreaking;
	        level.sendParticles(dust, origin.x, origin.y, origin.z, 1, 0.0, 0.0, 0.0, 0.0);
	    }
	}

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("creaking")) {
        	this.setCreakingInfo(tag.getUUID("creaking"));
        } else {
        	this.clearCreakingInfo();
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.creakingInfo != null) {
        	UUID uuid = this.creakingInfo.map(Entity::getUUID, existingUuid -> existingUuid);
        	tag.putUUID("creaking", uuid);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }

    public void removeProtector(@Nullable DamageSource source) {
        this.getCreakingProtector().ifPresent(creaking -> {
            if (source == null) {
                creaking.tearDown();
            } else {
                creaking.creakingDeathEffects(source);
                creaking.setTearingDown();
                creaking.setHealth(0.0F);
            }
            this.clearCreakingInfo();
        });
    }



    public boolean isProtector(Creaking p_367915_) {
        return this.getCreakingProtector().map(p_375974_ -> p_375974_ == p_367915_).orElse(false);
    }

    public int getAnalogOutputSignal() {
        return this.outputSignal;
    }

    public int computeAnalogOutputSignal() {
        if (this.creakingInfo != null && !this.getCreakingProtector().isEmpty()) {
            double d0 = this.distanceToCreaking();
            double d1 = Mth.clamp(d0, 0.0, 32.0) / 32.0;
            return 15 - (int)Math.floor(d1 * 15.0);
        } else {
            return 0;
        }
    }


}