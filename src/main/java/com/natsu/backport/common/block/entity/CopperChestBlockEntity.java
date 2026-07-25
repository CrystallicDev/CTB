package com.natsu.backport.common.block.entity;

import com.natsu.backport.common.registry.CTBBlockEntities;
import com.natsu.backport.common.registry.CTBSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

/**
 * A vanilla chest block entity with the copper sounds. The parent's private
 * openers counter goes unused, ours drives the lid events and sounds instead.
 */
public class CopperChestBlockEntity extends ChestBlockEntity {

	private final ContainerOpenersCounter copperOpenersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			playCopperSound(level, pos, state, CTBSounds.COPPER_CHEST_OPEN.get());
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			playCopperSound(level, pos, state, CTBSounds.COPPER_CHEST_CLOSE.get());
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int previous, int current) {
			level.blockEvent(pos, state.getBlock(), 1, current);
		}

		@Override
		protected boolean isOwnContainer(Player player) {
			if (player.containerMenu instanceof ChestMenu menu) {
				Container container = menu.getContainer();
				return container == CopperChestBlockEntity.this
						|| container instanceof CompoundContainer compound
								&& compound.contains(CopperChestBlockEntity.this);
			}
			return false;
		}
	};

	public CopperChestBlockEntity(BlockPos pos, BlockState state) {
		super(CTBBlockEntities.COPPER_CHEST.get(), pos, state);
	}

	/** Vanilla plays chest sounds at the double chest center. */
	private static void playCopperSound(Level level, BlockPos pos, BlockState state, SoundEvent sound) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		ChestType type = state.hasProperty(ChestBlock.TYPE) ? state.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
		if (type != ChestType.SINGLE) {
			net.minecraft.core.Direction direction = ChestBlock.getConnectedDirection(state);
			x += direction.getStepX() * 0.5;
			z += direction.getStepZ() * 0.5;
		}
		level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.ctbackport.copper_chest");
	}

	@Override
	public void startOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			this.copperOpenersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	public void stopOpen(Player player) {
		if (!this.remove && !player.isSpectator()) {
			this.copperOpenersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	@Override
	public void recheckOpen() {
		if (!this.remove) {
			this.copperOpenersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
		}
	}

	public int getOpenerCount() {
		return this.copperOpenersCounter.getOpenerCount();
	}
}
