package com.natsu.backport.common.registry;

import net.minecraftforge.common.util.ForgeSoundType;

/** The 26.2 stone sound types, fall reuses step like most vanilla types. */
public final class CTBSoundTypes {

	public static final ForgeSoundType SULFUR = new ForgeSoundType(1.0F, 1.0F,
			CTBSounds.SULFUR_BREAK, CTBSounds.SULFUR_STEP, CTBSounds.SULFUR_PLACE,
			CTBSounds.SULFUR_HIT, CTBSounds.SULFUR_STEP);
	public static final ForgeSoundType CINNABAR = new ForgeSoundType(1.0F, 1.0F,
			CTBSounds.CINNABAR_BREAK, CTBSounds.CINNABAR_STEP, CTBSounds.CINNABAR_PLACE,
			CTBSounds.CINNABAR_HIT, CTBSounds.CINNABAR_STEP);

	public static final ForgeSoundType POTENT_SULFUR = new ForgeSoundType(1.0F, 1.0F,
			CTBSounds.POTENT_SULFUR_BREAK, CTBSounds.POTENT_SULFUR_STEP, CTBSounds.POTENT_SULFUR_PLACE,
			CTBSounds.POTENT_SULFUR_HIT, CTBSounds.POTENT_SULFUR_STEP);

	private CTBSoundTypes() {
	}
}
