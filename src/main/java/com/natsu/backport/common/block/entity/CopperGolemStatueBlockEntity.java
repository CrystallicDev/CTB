package com.natsu.backport.common.block.entity;

import com.natsu.backport.common.block.CopperGolemStatueBlock;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CopperGolemStatueBlockEntity extends BlockEntity implements software.bernie.geckolib3.core.IAnimatable {

	private final software.bernie.geckolib3.core.manager.AnimationFactory factory =
			software.bernie.geckolib3.util.GeckoLibUtil.createFactory(this);

	private <E extends software.bernie.geckolib3.core.IAnimatable> software.bernie.geckolib3.core.PlayState statuePose(
			software.bernie.geckolib3.core.event.predicate.AnimationEvent<E> event) {
		CopperGolemStatueBlock.Pose pose = this.getBlockState().hasProperty(CopperGolemStatueBlock.POSE)
				? this.getBlockState().getValue(CopperGolemStatueBlock.POSE) : CopperGolemStatueBlock.Pose.STANDING;
		if (pose == CopperGolemStatueBlock.Pose.STANDING) {
			return software.bernie.geckolib3.core.PlayState.STOP;
		}
		String anim = switch (pose) {
			case SITTING -> "pose.sitting";
			case RUNNING -> "pose.running";
			default -> "pose.star";
		};
		event.getController().setAnimation(new software.bernie.geckolib3.core.builder.AnimationBuilder()
				.addAnimation(anim, software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
		return software.bernie.geckolib3.core.PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(software.bernie.geckolib3.core.manager.AnimationData data) {
		data.addAnimationController(new software.bernie.geckolib3.core.controller.AnimationController<>(this, "pose", 0, this::statuePose));
	}

	@Override
	public software.bernie.geckolib3.core.manager.AnimationFactory getFactory() {
		return this.factory;
	}

	private int weatherLevel = 3;
	private boolean waxed;
	private CompoundTag golemData = new CompoundTag();

	public CopperGolemStatueBlockEntity(BlockPos pos, BlockState state) {
		super(CTBBlockEntities.COPPER_GOLEM_STATUE.get(), pos, state);
	}

	/** Snapshot of the oxidized golem, restored when the statue is scraped clean. */
	public void createStatue(CopperGolem golem) {
		this.weatherLevel = golem.getWeatherLevel();
		this.waxed = false;
		this.golemData = new CompoundTag();
		golem.saveWithoutId(this.golemData);
		this.golemData.remove("Pos");
		this.golemData.remove("UUID");
		this.setChanged();
	}

	public void releaseGolem(Level level, BlockPos pos, BlockState state) {
		if (level.isClientSide) {
			return;
		}
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
		CopperGolem golem = CTBEntities.COPPER_GOLEM.get().create(level);
		if (golem != null) {
			if (!this.golemData.isEmpty()) {
				golem.load(this.golemData.copy());
			}
			golem.setWeatherLevel(0);
			golem.nextWeatheringTick = CopperGolem.UNSET_WEATHERING_TICK;
			golem.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
					state.getValue(CopperGolemStatueBlock.FACING).toYRot(), 0.0F);
			level.addFreshEntity(golem);
			golem.playSpawnSound();
		}
	}

	public int getWeatherLevel() {
		return this.weatherLevel;
	}

	public void setWeatherLevel(int level) {
		this.weatherLevel = net.minecraft.util.Mth.clamp(level, 0, 3);
		this.markUpdated();
	}

	public boolean isWaxed() {
		return this.waxed;
	}

	public void setWaxed(boolean waxed) {
		this.waxed = waxed;
		this.markUpdated();
	}

	private void markUpdated() {
		this.setChanged();
		if (this.level != null) {
			this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("weather_level", this.weatherLevel);
		tag.putBoolean("waxed", this.waxed);
		if (!this.golemData.isEmpty()) {
			tag.put("golem_data", this.golemData);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.weatherLevel = tag.getInt("weather_level");
		this.waxed = tag.getBoolean("waxed");
		this.golemData = tag.getCompound("golem_data");
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("weather_level", this.weatherLevel);
		tag.putBoolean("waxed", this.waxed);
		return tag;
	}
}
