package com.natsu.backport.common.block.entity.trialspawner;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface PlayerDetector {

	PlayerDetector NO_CREATIVE_PLAYERS = (level, selector, pos, range, requireLineOfSight) -> selector.getPlayers(
				level, p -> p.blockPosition().closerThan(pos, range) && !p.isCreative() && !p.isSpectator())
			.stream()
			.filter(player -> !requireLineOfSight || inLineOfSight(level, Vec3.atCenterOf(pos), player.getEyePosition()))
			.map(Entity::getUUID)
			.toList();

	PlayerDetector INCLUDING_CREATIVE_PLAYERS = (level, selector, pos, range, requireLineOfSight) -> selector.getPlayers(
				level, p -> p.blockPosition().closerThan(pos, range) && !p.isSpectator())
			.stream()
			.filter(player -> !requireLineOfSight || inLineOfSight(level, Vec3.atCenterOf(pos), player.getEyePosition()))
			.map(Entity::getUUID)
			.toList();

	List<UUID> detect(ServerLevel level, EntitySelector selector, BlockPos pos, double range, boolean requireLineOfSight);

	private static boolean inLineOfSight(Level level, Vec3 origin, Vec3 dest) {
		BlockHitResult hit = level.clip(new ClipContext(dest, origin, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
		return hit.getBlockPos().equals(new BlockPos(origin)) || hit.getType() == HitResult.Type.MISS;
	}

	/** Indirection needed by gametests : mock players are not in the level's player list. */
	interface EntitySelector {

		EntitySelector SELECT_FROM_LEVEL = (level, selector) -> level.getPlayers(selector::test);

		List<? extends Player> getPlayers(ServerLevel level, Predicate<? super Player> selector);

		static EntitySelector onlySelectPlayer(Player player) {
			return onlySelectPlayers(List.of(player));
		}

		static EntitySelector onlySelectPlayers(List<Player> players) {
			return (level, selector) -> players.stream().filter(selector).filter(Objects::nonNull).toList();
		}
	}
}
