package com.natsu.backport.server.events;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.CopperGolem;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** A carved pumpkin on a copper block wakes a golem, like the vanilla ritual. */
@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class CopperGolemBuilding {

	@SubscribeEvent
	public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
		if (!(event.getWorld() instanceof Level level) || level.isClientSide) {
			return;
		}
		if (!event.getPlacedBlock().is(Blocks.CARVED_PUMPKIN)) {
			return;
		}

		BlockPos pumpkinPos = event.getPos();
		BlockPos basePos = pumpkinPos.below();
		int weather = weatherLevelOf(level.getBlockState(basePos));
		if (weather < 0) {
			return;
		}

		BlockState baseState = level.getBlockState(basePos);
		net.minecraft.core.Direction facing = event.getPlacedBlock()
				.getOptionalValue(net.minecraft.world.level.block.CarvedPumpkinBlock.FACING)
				.orElse(net.minecraft.core.Direction.NORTH);
		level.removeBlock(pumpkinPos, false);
		// vanilla turns the copper base block into the matching copper chest
		level.setBlockAndUpdate(basePos, com.natsu.backport.common.block.CopperChestBlock
				.getFromCopperBlock(baseState.getBlock(), facing));
		CopperGolem golem = CTBEntities.COPPER_GOLEM.get().create(level);
		if (golem != null) {
			golem.setWeatherLevel(weather);
			golem.moveTo(pumpkinPos.getX() + 0.5, pumpkinPos.getY(), pumpkinPos.getZ() + 0.5,
					event.getEntity() instanceof Player player ? player.getYRot() + 180.0F : 0.0F, 0.0F);
			level.addFreshEntity(golem);
			golem.playSpawnSound();
		}
	}

	/** -1 when the block is not a copper golem base, else the matching oxidation. */
	private static int weatherLevelOf(BlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.COPPER_BLOCK || block == Blocks.WAXED_COPPER_BLOCK) {
			return 0;
		}
		if (block == Blocks.EXPOSED_COPPER || block == Blocks.WAXED_EXPOSED_COPPER) {
			return 1;
		}
		if (block == Blocks.WEATHERED_COPPER || block == Blocks.WAXED_WEATHERED_COPPER) {
			return 2;
		}
		if (block == Blocks.OXIDIZED_COPPER || block == Blocks.WAXED_OXIDIZED_COPPER) {
			return 3;
		}
		return -1;
	}
}
