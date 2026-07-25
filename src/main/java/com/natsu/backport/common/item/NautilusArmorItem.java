package com.natsu.backport.common.item;

import net.minecraft.world.item.Item;

/** Body armor for the nautilus, protection from the matching armor material. */
public class NautilusArmorItem extends Item {

	private final int protection;

	public NautilusArmorItem(int protection, Properties properties) {
		super(properties.stacksTo(1));
		this.protection = protection;
	}

	public int getProtection() {
		return this.protection;
	}
}
