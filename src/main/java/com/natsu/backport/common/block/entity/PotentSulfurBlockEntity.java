package com.natsu.backport.common.block.entity;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.natsu.backport.common.block.CTBPotentSulfurState;
import com.natsu.backport.common.block.PotentSulfurBlock;
import com.natsu.backport.common.particle.GeyserParticleOptions;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * Port of the 26.2 PotentSulfurBlockEntity : the state driven tickers for the
 * noxious gas, the eruption countdown and the entity launching column.
 */
public class PotentSulfurBlockEntity extends BlockEntity {

	private static final Predicate<Entity> EFFECT_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isAlive);
	private static final float GEYSER_BASE_LAUNCH_SPEED = 0.3F;
	private static final float GEYSER_LAUNCH_FORCE = 0.2F;
	private static final long GEYSER_SALT = -904011478L;

	public int waitingCountdown = -1;
	public long eruptionTick = -1L;

	public PotentSulfurBlockEntity(BlockPos pos, BlockState state) {
		super(CTBBlockEntities.POTENT_SULFUR.get(), pos, state);
	}

	public static BlockEntityTicker<PotentSulfurBlockEntity> andThen(
			BlockEntityTicker<PotentSulfurBlockEntity> first, BlockEntityTicker<PotentSulfurBlockEntity> second) {
		return (level, pos, state, entity) -> {
			first.tick(level, pos, state, entity);
			second.tick(level, pos, state, entity);
		};
	}

	/** Every ten ticks, nausea to whoever the gas can reach around the surface. */
	public static final BlockEntityTicker<PotentSulfurBlockEntity> SERVER_NAUSEA_EFFECT_TICKER = (level, pos, state, entity) -> {
		if (level.getGameTime() % 10L != 0L) {
			return;
		}
		BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
		if (sourceBlock == null) {
			return;
		}
		for (LivingEntity living : getNearbyLivingEntities(level, sourceBlock)) {
			if (canBeReachedByNoxiousGas(level, sourceBlock, living.getEyePosition(), living)) {
				living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, true, true));
			}
		}
	};

	public static final BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_NOXIOUS_GAS_TICKER = (level, pos, state, entity) -> {
		if (level.getGameTime() % 20L != 0L) {
			return;
		}
		BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
		if (sourceBlock != null) {
			Vec3 center = Vec3.atCenterOf(sourceBlock);
			level.addParticle(CTBParticles.NOXIOUS_GAS.get(), center.x, center.y, center.z, 0.0, 0.0, 0.0);
		}
	};

	public static BlockEntityTicker<PotentSulfurBlockEntity> clientGeyserPlumeTicker(SoundEvent sound) {
		return (level, pos, state, entity) -> {
			BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
			if (sourceBlock == null) {
				return;
			}
			long eruptionTime = level.getGameTime() - entity.eruptionTick;
			if (eruptionTime % 20L == 0L) {
				int waterBlocks = sourceBlock.getY() - pos.getY() - 1;
				level.addParticle(new GeyserParticleOptions(CTBParticles.GEYSER.get(), Math.max(1, waterBlocks)),
						sourceBlock.getX() + 0.5, sourceBlock.getY(), sourceBlock.getZ() + 0.5, 0.0, 0.0, 0.0);
			}
			if (eruptionTime % 40L == 0L) {
				level.playLocalSound(sourceBlock.getX() + 0.5, sourceBlock.getY() + 0.5, sourceBlock.getZ() + 0.5,
						sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
			}
		};
	}

	/** The dormant and erupting cycle, seeded per position like vanilla. */
	public static final BlockEntityTicker<PotentSulfurBlockEntity> SERVER_WAITING_COUNTDOWN_TICKER = (level, pos, state, entity) -> {
		if (level.getGameTime() % 20L != 0L) {
			return;
		}
		BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
		if (sourceBlock == null) {
			return;
		}
		if (entity.waitingCountdown <= 0) {
			int waterBlocks = sourceBlock.getY() - pos.getY() - 1;
			var random = geyserPositional((ServerLevel) level, pos);
			if (state.getValue(PotentSulfurBlock.STATE) == CTBPotentSulfurState.DORMANT) {
				entity.waitingCountdown = 10 * (waterBlocks - 1) + 15 + random.nextInt(16);
			} else {
				random.nextInt();
				entity.waitingCountdown = waterBlocks - 1 + 1 + random.nextInt(2);
			}
		}
		if (entity.waitingCountdown > 0) {
			entity.waitingCountdown--;
		}
		if (entity.waitingCountdown == 0) {
			CTBPotentSulfurState stateToSet = state.getValue(PotentSulfurBlock.STATE) == CTBPotentSulfurState.DORMANT
					? CTBPotentSulfurState.ERUPTING : CTBPotentSulfurState.DORMANT;
			level.setBlock(pos, state.setValue(PotentSulfurBlock.STATE, stateToSet), 3);
		}
	};

	/** Anything in the unobstructed water column gets pushed upwards. */
	public static final BlockEntityTicker<PotentSulfurBlockEntity> LAUNCH_ENTITY_TICKER = (level, pos, state, entity) -> {
		BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
		if (sourceBlock == null) {
			return;
		}
		int waterBlocks = sourceBlock.getY() - pos.getY() - 1;
		int geyserForceHeight = getUnobstructedBlockCount(level, pos.above(), waterBlocks);
		AABB aabb = new AABB(pos.above()).expandTowards(0.0, geyserForceHeight - 1, 0.0);
		List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb, EFFECT_PREDICATE);
		for (Entity launched : entities) {
			Vec3 velocity = launched.getDeltaMovement();
			if (launched instanceof Player player && player.getAbilities().flying) {
				continue;
			}
			if (launched.isPassenger() || velocity.y >= GEYSER_BASE_LAUNCH_SPEED + waterBlocks * 0.1) {
				continue;
			}
			launched.setDeltaMovement(velocity.add(0.0, GEYSER_LAUNCH_FORCE, 0.0));
			launched.hurtMarked = true;
		}
	};

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("countdown", this.waitingCountdown);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("countdown")) {
			this.waitingCountdown = tag.getInt("countdown");
		}
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		if (this.eruptionTick == -1L) {
			this.eruptionTick = level.getGameTime();
		}
	}

	public void resetCountdown() {
		this.waitingCountdown = -1;
	}

	private static List<LivingEntity> getNearbyLivingEntities(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(2.5, 0.0, 2.5), EFFECT_PREDICATE);
	}

	private static XoroshiroRandomSource geyserPositional(ServerLevel level, BlockPos pos) {
		long seed = level.getSeed() ^ GEYSER_SALT;
		return new XoroshiroRandomSource(seed ^ pos.getX() * 341873128712L ^ pos.getY() * 132897987541L ^ pos.getZ());
	}

	private static int getUnobstructedBlockCount(Level level, BlockPos pos, int waterBlocks) {
		int geyserForceHeight = 6 * waterBlocks;
		for (int i = 0; i < geyserForceHeight; i++) {
			BlockPos currentPos = pos.above(i);
			if (!isGeyserPassableBlock(level.getBlockState(currentPos), level, currentPos)) {
				return i;
			}
		}
		return geyserForceHeight;
	}

	private static boolean isGeyserPassableBlock(BlockState state, Level level, BlockPos pos) {
		if (state.isAir() || state.is(net.minecraft.world.level.block.Blocks.WATER)) {
			return true;
		}
		return state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty();
	}

	/** Walks up through the allowed water column to the surface block. */
	@Nullable
	private static BlockPos findNoxiousGasSourceBlock(Level level, BlockPos origin) {
		int maxY = origin.getY() + PotentSulfurBlock.ALLOWED_WATER_BLOCKS_ABOVE + 1;
		BlockPos.MutableBlockPos pos = origin.above(1).mutable();
		while (pos.getY() <= maxY) {
			BlockState state = level.getBlockState(pos);
			boolean isWaterSource = level.getFluidState(pos).isSource()
					&& level.getFluidState(pos).getType() == Fluids.WATER;
			if (!isWaterSource || !state.is(net.minecraft.world.level.block.Blocks.WATER)
					&& !isGeyserPassableBlock(state, level, pos)) {
				if (!state.isAir() && !isGeyserPassableBlock(state, level, pos)) {
					break;
				}
				return pos.immutable();
			}
			pos.move(Direction.UP);
		}
		return null;
	}

	private static boolean canBeReachedByNoxiousGas(Level level, BlockPos sourceBlock, Vec3 pos, Entity context) {
		BlockPos blockPos = new BlockPos(pos);
		if (!isGeyserPassableBlock(level.getBlockState(blockPos), level, blockPos)) {
			return false;
		}
		if (pos.distanceToSqr(Vec3.atCenterOf(sourceBlock)) > 9.0) {
			return false;
		}
		Vec3 belowSource = Vec3.atCenterOf(sourceBlock.below());
		Vec3 belowPos = new Vec3(pos.x, pos.y - 1.0, pos.z);
		return isWater(level, belowPos) && haveLineOfSight(level, belowSource, belowPos, context);
	}

	private static boolean isWater(Level level, Vec3 pos) {
		var fluid = level.getFluidState(new BlockPos(pos));
		return fluid.isSource() && fluid.getType() == Fluids.WATER;
	}

	private static boolean haveLineOfSight(Level level, Vec3 a, Vec3 b, Entity context) {
		BlockHitResult hit = level.clip(new ClipContext(a, b,
				ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, context));
		return hit.getType() != HitResult.Type.BLOCK;
	}
}
