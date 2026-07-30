package com.natsu.backport.common.item;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

/** The 1.21.6 happy ghast harness, one per dye color. */
public class HarnessItem extends Item {

	private final DyeColor color;

	public HarnessItem(DyeColor color, Properties properties) {
		super(properties);
		this.color = color;
	}

	public DyeColor getColor() {
		return this.color;
	}
}
