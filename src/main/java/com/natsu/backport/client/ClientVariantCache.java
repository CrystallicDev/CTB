package com.natsu.backport.client;

import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;

/** Client side view of the farm animal variants, filled by the tracking packets. */
public final class ClientVariantCache {

	private static final Int2ByteMap VARIANTS = new Int2ByteOpenHashMap();

	private ClientVariantCache() {
	}

	public static void put(int entityId, byte variant) {
		VARIANTS.put(entityId, variant);
	}

	public static byte get(int entityId) {
		return VARIANTS.get(entityId);
	}

	private static final it.unimi.dsi.fastutil.ints.Int2IntMap WOLF_ARMOR =
			new it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap();
	static {
		WOLF_ARMOR.defaultReturnValue(-1);
	}

	public static void putWolfArmor(int entityId, int durability) {
		if (durability < 0) {
			WOLF_ARMOR.remove(entityId);
		} else {
			WOLF_ARMOR.put(entityId, durability);
		}
	}

	public static int getWolfArmor(int entityId) {
		return WOLF_ARMOR.get(entityId);
	}

	public static void clear() {
		VARIANTS.clear();
		WOLF_ARMOR.clear();
	}
}
