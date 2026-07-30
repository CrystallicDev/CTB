package com.natsu.backport.server.events;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.network.CTBNetwork;
import com.natsu.backport.common.registry.CTBItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

/**
 * The 1.21.5 farm animal variants : pigs, cows and chickens remember the
 * climate they spawned in, chickens lay the matching egg color.
 */
@Mod.EventBusSubscriber(modid = CTBackport.MODID)
public class AnimalVariants {

	public static final String TAG = "ctb_variant";
	public static final byte TEMPERATE = 0;
	public static final byte WARM = 1;
	public static final byte COLD = 2;

	/** The nine 1.20.5 wolf coats, indexed for VariantWolfRenderer. */
	public static byte wolfVariantForBiome(Level level, BlockPos pos) {
		String biome = level.getBiome(pos).unwrapKey()
				.map(key -> key.location().getPath()).orElse("");
		if (biome.equals("snowy_taiga")) return 1;
		if (biome.equals("old_growth_pine_taiga")) return 2;
		if (biome.equals("old_growth_spruce_taiga")) return 3;
		if (biome.contains("jungle")) return 4;
		if (biome.equals("grove") || biome.contains("peaks") || biome.equals("snowy_slopes")) return 5;
		if (biome.contains("savanna")) return 6;
		if (biome.contains("badlands") || biome.contains("desert")) return 7;
		if (biome.contains("forest")) return 8;
		return 0;
	}

	public static boolean isVariantAnimal(Entity entity) {
		return (entity instanceof Pig || entity instanceof Cow || entity instanceof Chicken)
				&& !(entity instanceof MushroomCow);
	}

	public static byte variantOf(Entity entity) {
		return entity.getPersistentData().getByte(TAG);
	}

	/** Marks freshly finalized spawns so onJoin can tell them from loaded entities. */
	@SubscribeEvent
	public static void onSpecialSpawn(net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn event) {
		if (event.getEntity() instanceof net.minecraft.world.entity.animal.Sheep
				&& event.getSpawnReason() != net.minecraft.world.entity.MobSpawnType.BREEDING) {
			event.getEntity().getPersistentData().putBoolean(SHEEP_COLOR_TAG, true);
		}
	}

	@SubscribeEvent
	public static void onJoin(EntityJoinWorldEvent event) {
		Level level = event.getWorld();
		if (level.isClientSide) {
			return;
		}
		Entity entity = event.getEntity();
		if (entity instanceof net.minecraft.world.entity.animal.Wolf wolf
				&& !wolf.getPersistentData().contains(TAG)) {
			wolf.getPersistentData().putByte(TAG, wolfVariantForBiome(level, wolf.blockPosition()));
		}
		if (entity instanceof net.minecraft.world.entity.animal.Sheep sheep
				&& sheep.getPersistentData().contains(SHEEP_COLOR_TAG)) {
			sheep.getPersistentData().remove(SHEEP_COLOR_TAG);
			sheep.setColor(sheepColorFor(variantForBiome(level, sheep.blockPosition()), sheep.getRandom()));
		}
		if (isVariantAnimal(entity)) {
			if (!entity.getPersistentData().contains(TAG)) {
				entity.getPersistentData().putByte(TAG, variantForBiome(level, entity.blockPosition()));
			}
			return;
		}
		// a freshly laid egg takes the color of the hen
		if (entity instanceof ItemEntity item && item.getThrower() == null
				&& item.getItem().is(Items.EGG)) {
			for (Chicken chicken : level.getEntitiesOfClass(Chicken.class,
					new AABB(entity.blockPosition()).inflate(1.0))) {
				byte variant = variantOf(chicken);
				if (variant == WARM || variant == COLD) {
					item.setItem(new ItemStack(variant == WARM
							? CTBItems.BROWN_EGG.get() : CTBItems.BLUE_EGG.get(),
							item.getItem().getCount()));
					return;
				}
			}
		}
	}

	private static final String SHEEP_COLOR_TAG = "ctb_sheep_color";

	/**
	 * The 1.21.5 SheepColorSpawnRules : 5/5/5/3 rare solid colors and an 82
	 * weight of the climate's common color, itself pink once in five hundred.
	 */
	private static net.minecraft.world.item.DyeColor sheepColorFor(byte climate, java.util.Random random) {
		net.minecraft.world.item.DyeColor[] rare = switch (climate) {
			case WARM -> new net.minecraft.world.item.DyeColor[] {
					net.minecraft.world.item.DyeColor.GRAY, net.minecraft.world.item.DyeColor.LIGHT_GRAY,
					net.minecraft.world.item.DyeColor.WHITE, net.minecraft.world.item.DyeColor.BLACK };
			case COLD -> new net.minecraft.world.item.DyeColor[] {
					net.minecraft.world.item.DyeColor.LIGHT_GRAY, net.minecraft.world.item.DyeColor.GRAY,
					net.minecraft.world.item.DyeColor.WHITE, net.minecraft.world.item.DyeColor.BROWN };
			default -> new net.minecraft.world.item.DyeColor[] {
					net.minecraft.world.item.DyeColor.BLACK, net.minecraft.world.item.DyeColor.GRAY,
					net.minecraft.world.item.DyeColor.LIGHT_GRAY, net.minecraft.world.item.DyeColor.BROWN };
		};
		net.minecraft.world.item.DyeColor common = switch (climate) {
			case WARM -> net.minecraft.world.item.DyeColor.BROWN;
			case COLD -> net.minecraft.world.item.DyeColor.BLACK;
			default -> net.minecraft.world.item.DyeColor.WHITE;
		};
		int roll = random.nextInt(100);
		if (roll < 5) {
			return rare[0];
		}
		if (roll < 10) {
			return rare[1];
		}
		if (roll < 15) {
			return rare[2];
		}
		if (roll < 18) {
			return rare[3];
		}
		return random.nextInt(500) == 0 ? net.minecraft.world.item.DyeColor.PINK : common;
	}

	private static byte variantForBiome(Level level, BlockPos pos) {
		float temperature = level.getBiome(pos).value().getBaseTemperature();
		if (temperature >= 0.95F) {
			return WARM;
		}
		return temperature <= 0.3F ? COLD : TEMPERATE;
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if (target instanceof net.minecraft.world.entity.animal.Wolf
				&& event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer wolfWatcher) {
			CTBNetwork.CHANNEL.sendTo(new CTBNetwork.AnimalVariantPacket(target.getId(), variantOf(target)),
					wolfWatcher.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
		if (isVariantAnimal(target) && event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer player) {
			CTBNetwork.CHANNEL.sendTo(new CTBNetwork.AnimalVariantPacket(target.getId(), variantOf(target)),
					player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
