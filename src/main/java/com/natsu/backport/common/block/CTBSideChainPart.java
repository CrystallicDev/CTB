package com.natsu.backport.common.block;

import net.minecraft.util.StringRepresentable;

/** The 1.21.9 SideChainPart : where a block sits in a horizontal chain. */
public enum CTBSideChainPart implements StringRepresentable {
	UNCONNECTED("unconnected"),
	RIGHT("right"),
	CENTER("center"),
	LEFT("left");

	private final String name;

	CTBSideChainPart(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.getSerializedName();
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public boolean isConnected() {
		return this != UNCONNECTED;
	}

	public boolean isConnectionTowards(CTBSideChainPart endPart) {
		return this == CENTER || this == endPart;
	}

	public boolean isChainEnd() {
		return this != CENTER;
	}

	public CTBSideChainPart whenConnectedToTheRight() {
		return switch (this) {
			case UNCONNECTED, LEFT -> LEFT;
			case RIGHT, CENTER -> CENTER;
		};
	}

	public CTBSideChainPart whenConnectedToTheLeft() {
		return switch (this) {
			case UNCONNECTED, RIGHT -> RIGHT;
			case CENTER, LEFT -> CENTER;
		};
	}

	public CTBSideChainPart whenDisconnectedFromTheRight() {
		return switch (this) {
			case UNCONNECTED, LEFT -> UNCONNECTED;
			case RIGHT, CENTER -> RIGHT;
		};
	}

	public CTBSideChainPart whenDisconnectedFromTheLeft() {
		return switch (this) {
			case UNCONNECTED, RIGHT -> UNCONNECTED;
			case CENTER, LEFT -> LEFT;
		};
	}
}
