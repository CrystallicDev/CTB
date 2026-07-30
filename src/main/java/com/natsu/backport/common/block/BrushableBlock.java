package com.natsu.backport.common.block;

import java.util.Random;
import java.util.function.Supplier;

import com.natsu.backport.common.block.entity.BrushableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** The 1.20 suspicious sand and gravel : brush away four layers of dust. */
public class BrushableBlock extends BaseEntityBlock {

	public static final IntegerProperty DUSTED = IntegerProperty.create("dusted", 0, 3);

	private final Supplier<Block> turnsInto;
	private final Supplier<SoundEvent> brushSound;
	private final Supplier<SoundEvent> brushCompletedSound;

	public BrushableBlock(Supplier<Block> turnsInto, Supplier<SoundEvent> brushSound,
			Supplier<SoundEvent> brushCompletedSound, Properties properties) {
		super(properties);
		this.turnsInto = turnsInto;
		this.brushSound = brushSound;
		this.brushCompletedSound = brushCompletedSound;
		this.registerDefaultState(this.stateDefinition.any().setValue(DUSTED, 0));
	}

	public Block getTurnsInto() {
		return this.turnsInto.get();
	}

	public SoundEvent getBrushSound() {
		return this.brushSound.get();
	}

	public SoundEvent getBrushCompletedSound() {
		return this.brushCompletedSound.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DUSTED);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BrushableBlockEntity(pos, state);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		level.scheduleTick(pos, this, 2);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState,
			LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
		level.scheduleTick(pos, this, 2);
		return super.updateShape(state, direction, neighbourState, level, pos, neighbourPos);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (level.getBlockEntity(pos) instanceof BrushableBlockEntity brushable) {
			brushable.checkReset(level);
		}
		// falls like sand, the buried treasure does not survive the drop
		if (FallingBlock.isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
			level.setBlock(pos, this.getTurnsInto().defaultBlockState(), 3);
			FallingBlockEntity.fall(level, pos, this.getTurnsInto().defaultBlockState());
		}
	}
}
