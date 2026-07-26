package com.natsu.backport.common.block;

import net.minecraft.util.StringRepresentable;

/** The 26.2 potent sulfur states, dry land to full geyser. */
public enum CTBPotentSulfurState implements StringRepresentable {
	DRY("dry"),
	WET("wet"),
	DORMANT("dormant"),
	ERUPTING("erupting"),
	CONTINUOUS("continuous");

	private final String name;

	CTBPotentSulfurState(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
