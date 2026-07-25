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

	public static void clear() {
		VARIANTS.clear();
	}
}
