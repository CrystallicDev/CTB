package com.natsu.backport.common.entity;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.natsu.backport.common.entity.goal.TransportItemsGoal;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/** The tireless little sorter of the copper age, oxidizes into a statue. */
public class CopperGolem extends AbstractGolem implements IAnimatable {

	public static final long IGNORE_WEATHERING_TICK = -2L;
	public static final long UNSET_WEATHERING_TICK = -1L;
	private static final int WEATHERING_TICK_FROM = 504000;
	private static final int WEATHERING_TICK_TO = 552000;
	private static final float TURN_TO_STATUE_CHANCE = 0.0058F;

	// 0 unaffected, 1 exposed, 2 weathered, 3 oxidized
	private static final EntityDataAccessor<Byte> DATA_WEATHER = SynchedEntityData.defineId(CopperGolem.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> DATA_STATE = SynchedEntityData.defineId(CopperGolem.class, EntityDataSerializers.BYTE);

	public enum GolemState {
		IDLE, GETTING_ITEM, GETTING_NO_ITEM, DROPPING_ITEM, DROPPING_NO_ITEM
	}

	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	public long nextWeatheringTick = UNSET_WEATHERING_TICK;
	@Nullable
	private UUID lastLightningBoltUUID;

	public CopperGolem(EntityType<? extends CopperGolem> type, Level level) {
		super(type, level);
		this.setPersistenceRequired();
		this.maxUpStep = 1.0F;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.MAX_HEALTH, 12.0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new TransportItemsGoal(this));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_WEATHER, (byte) 0);
		this.entityData.define(DATA_STATE, (byte) 0);
	}

	public int getWeatherLevel() {
		return this.entityData.get(DATA_WEATHER);
	}

	public void setWeatherLevel(int level) {
		this.entityData.set(DATA_WEATHER, (byte) net.minecraft.util.Mth.clamp(level, 0, 3));
	}

	public GolemState getState() {
		return GolemState.values()[this.entityData.get(DATA_STATE)];
	}

	public void setState(GolemState state) {
		this.entityData.set(DATA_STATE, (byte) state.ordinal());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putLong("next_weather_age", this.nextWeatheringTick);
		tag.putByte("weather_state", (byte) this.getWeatherLevel());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.nextWeatheringTick = tag.contains("next_weather_age") ? tag.getLong("next_weather_age") : UNSET_WEATHERING_TICK;
		this.setWeatherLevel(tag.getByte("weather_state"));
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level.isClientSide) {
			this.updateWeathering((ServerLevel) this.level, this.random, this.level.getGameTime());
		}
	}

	private void updateWeathering(ServerLevel level, Random random, long gameTime) {
		if (this.nextWeatheringTick == IGNORE_WEATHERING_TICK) {
			return;
		}
		if (this.nextWeatheringTick == UNSET_WEATHERING_TICK) {
			this.nextWeatheringTick = gameTime + WEATHERING_TICK_FROM + random.nextInt(WEATHERING_TICK_TO - WEATHERING_TICK_FROM + 1);
			return;
		}

		boolean fullyOxidized = this.getWeatherLevel() >= 3;
		if (gameTime >= this.nextWeatheringTick && !fullyOxidized) {
			this.setWeatherLevel(this.getWeatherLevel() + 1);
			this.nextWeatheringTick = this.getWeatherLevel() >= 3 ? 0L
					: this.nextWeatheringTick + WEATHERING_TICK_FROM + random.nextInt(WEATHERING_TICK_TO - WEATHERING_TICK_FROM + 1);
		}

		if (fullyOxidized && level.getBlockState(this.blockPosition()).isAir() && random.nextFloat() <= TURN_TO_STATUE_CHANCE) {
			this.turnToStatue(level);
		}
	}

	private void turnToStatue(ServerLevel level) {
		BlockPos pos = this.blockPosition();
		BlockState statue = com.natsu.backport.common.registry.CTBBlocks.COPPER_GOLEM_STATUE.get().defaultBlockState()
				.setValue(com.natsu.backport.common.block.CopperGolemStatueBlock.FACING,
						net.minecraft.core.Direction.fromYRot(this.getYRot()));
		level.setBlock(pos, statue, Block.UPDATE_ALL);
		if (level.getBlockEntity(pos) instanceof com.natsu.backport.common.block.entity.CopperGolemStatueBlockEntity be) {
			be.createStatue(this);
			ItemStack carried = this.getMainHandItem();
			if (!carried.isEmpty()) {
				this.spawnAtLocation(carried);
			}
			this.playSound(SoundEvents.IRON_GOLEM_DEATH, 1.0F, 1.6F);
			this.discard();
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		if (held.isEmpty()) {
			ItemStack carried = this.getMainHandItem();
			if (!carried.isEmpty()) {
				// hands the carried stack over instead of keeping it hostage
				this.spawnAtLocation(carried);
				this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
				return InteractionResult.sidedSuccess(this.level.isClientSide);
			}
		}

		if (this.level.isClientSide) {
			return InteractionResult.PASS;
		}

		if (held.is(Items.HONEYCOMB) && this.nextWeatheringTick != IGNORE_WEATHERING_TICK) {
			this.level.levelEvent(null, 3003, this.blockPosition(), 0); // wax on particles
			this.nextWeatheringTick = IGNORE_WEATHERING_TICK;
			if (!player.getAbilities().instabuild) {
				held.shrink(1);
			}
			return InteractionResult.SUCCESS;
		}

		if (held.getItem() instanceof net.minecraft.world.item.AxeItem) {
			if (this.nextWeatheringTick == IGNORE_WEATHERING_TICK) {
				this.level.playSound(null, this, SoundEvents.AXE_SCRAPE, this.getSoundSource(), 1.0F, 1.0F);
				this.level.levelEvent(null, 3004, this.blockPosition(), 0); // wax off particles
				this.nextWeatheringTick = UNSET_WEATHERING_TICK;
				held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				return InteractionResult.SUCCESS;
			}
			if (this.getWeatherLevel() > 0) {
				this.level.playSound(null, this, SoundEvents.AXE_SCRAPE, this.getSoundSource(), 1.0F, 1.0F);
				this.level.levelEvent(null, 3005, this.blockPosition(), 0); // scrape particles
				this.nextWeatheringTick = UNSET_WEATHERING_TICK;
				this.setWeatherLevel(this.getWeatherLevel() - 1);
				held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				return InteractionResult.SUCCESS;
			}
		}

		return super.mobInteract(player, hand);
	}

	@Override
	public void thunderHit(ServerLevel level, LightningBolt bolt) {
		super.thunderHit(level, bolt);
		if (!bolt.getUUID().equals(this.lastLightningBoltUUID)) {
			this.lastLightningBoltUUID = bolt.getUUID();
			if (this.getWeatherLevel() > 0) {
				this.nextWeatheringTick = UNSET_WEATHERING_TICK;
				this.setWeatherLevel(this.getWeatherLevel() - 1);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		boolean hurt = super.hurt(source, amount);
		if (hurt && !this.level.isClientSide) {
			this.setState(GolemState.IDLE);
		}
		return hurt;
	}

	public void playSpawnSound() {
		this.playSound(CTBSounds.COPPER_GOLEM_SPAWN.get(), 1.0F, 1.0F);
	}

	private String soundSet() {
		int weather = this.getWeatherLevel();
		return weather >= 3 ? "oxidized" : weather == 2 ? "weathered" : "regular";
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return switch (this.soundSet()) {
			case "oxidized" -> CTBSounds.COPPER_GOLEM_OXIDIZED_HURT.get();
			case "weathered" -> CTBSounds.COPPER_GOLEM_WEATHERED_HURT.get();
			default -> CTBSounds.COPPER_GOLEM_REGULAR_HURT.get();
		};
	}

	@Override
	protected SoundEvent getDeathSound() {
		return switch (this.soundSet()) {
			case "oxidized" -> CTBSounds.COPPER_GOLEM_OXIDIZED_DEATH.get();
			case "weathered" -> CTBSounds.COPPER_GOLEM_WEATHERED_DEATH.get();
			default -> CTBSounds.COPPER_GOLEM_REGULAR_DEATH.get();
		};
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		SoundEvent step = switch (this.soundSet()) {
			case "oxidized" -> CTBSounds.COPPER_GOLEM_OXIDIZED_STEP.get();
			case "weathered" -> CTBSounds.COPPER_GOLEM_WEATHERED_STEP.get();
			default -> CTBSounds.COPPER_GOLEM_REGULAR_STEP.get();
		};
		this.playSound(step, 1.0F, 1.0F);
	}

	@Override
	public Vec3 getLeashOffset() {
		return new Vec3(0.0, 0.55, 0.0);
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return air;
	}

	// --- geckolib ---

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		AnimationBuilder builder = new AnimationBuilder();
		switch (this.getState()) {
			case GETTING_ITEM -> builder.addAnimation("interact.chest_noitem_get", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
			case GETTING_NO_ITEM -> builder.addAnimation("interact.chest_noitem_noget", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
			case DROPPING_ITEM -> builder.addAnimation("interact.chest_item_drop", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
			case DROPPING_NO_ITEM -> builder.addAnimation("interact.chest_item_nodrop", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
			default -> {
				if (event.isMoving()) {
					builder.addAnimation(this.getMainHandItem().isEmpty() ? "moove.walk" : "moove.walk_item", ILoopType.EDefaultLoopTypes.LOOP);
				} else {
					builder.addAnimation("moove.idle", ILoopType.EDefaultLoopTypes.LOOP);
				}
			}
		}
		event.getController().setAnimation(builder);
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 4, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
