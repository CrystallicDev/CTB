package com.natsu.backport.common.registry;

import net.minecraft.world.level.block.entity.BannerPattern;

/** The 1.21 loom patterns, added through the forge extensible enum. */
public class CTBBannerPatterns {

	public static final BannerPattern FLOW = BannerPattern.create("CTB_FLOW", "flow", "flw", true);
	public static final BannerPattern GUSTER = BannerPattern.create("CTB_GUSTER", "guster", "gus", true);

	public static void init() {
		// classloading is the registration
	}
}
