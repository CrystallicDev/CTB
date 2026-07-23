package com.natsu.backport.server.events;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.entity.Nautilus;
import com.natsu.backport.common.registry.CTBEntities;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class NautilusSpawns {

	public static void registerPlacements() {
		SpawnPlacements.register(CTBEntities.NAUTILUS.get(), SpawnPlacements.Type.IN_WATER,
				Heightmap.Types.OCEAN_FLOOR, Nautilus::checkNautilusSpawnRules);
		SpawnPlacements.register(CTBEntities.ZOMBIE_NAUTILUS.get(), SpawnPlacements.Type.IN_WATER,
				Heightmap.Types.OCEAN_FLOOR, Nautilus::checkNautilusSpawnRules);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBiomeLoad(BiomeLoadingEvent event) {
		if (event.getName() == null) {
			return;
		}

		if (event.getCategory() == Biome.BiomeCategory.DESERT) {
			// vanilla splits the desert skeletons half and half with the parched
			java.util.List<MobSpawnSettings.SpawnerData> monsters = event.getSpawns().getSpawner(MobCategory.MONSTER);
			for (int i = 0; i < monsters.size(); i++) {
				MobSpawnSettings.SpawnerData data = monsters.get(i);
				if (data.type == net.minecraft.world.entity.EntityType.SKELETON) {
					monsters.set(i, new MobSpawnSettings.SpawnerData(net.minecraft.world.entity.EntityType.SKELETON,
							Math.max(1, data.getWeight().asInt() / 2), data.minCount, data.maxCount));
				}
			}
			event.getSpawns().addSpawn(MobCategory.MONSTER,
					new MobSpawnSettings.SpawnerData(CTBEntities.PARCHED.get(), 50, 4, 4));
			return;
		}

		if (event.getCategory() != Biome.BiomeCategory.OCEAN) {
			return;
		}

		String path = event.getName().getPath();
		boolean warm = path.contains("warm") || path.contains("lukewarm");
		if (warm && !path.contains("deep")) {
			event.getSpawns().addSpawn(MobCategory.WATER_CREATURE,
					new MobSpawnSettings.SpawnerData(CTBEntities.NAUTILUS.get(), 4, 1, 2));
		}
		if (path.contains("cold") || path.contains("deep") || path.equals("ocean")) {
			event.getSpawns().addSpawn(MobCategory.WATER_CREATURE,
					new MobSpawnSettings.SpawnerData(CTBEntities.ZOMBIE_NAUTILUS.get(), 2, 1, 1));
		}
	}
}
