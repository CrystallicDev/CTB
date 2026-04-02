package ctbackport.datagen.lang;

import com.natsu.backport.CTBackport;
import com.natsu.backport.common.registry.CTBBlocks;
import com.natsu.backport.common.registry.CTBEffects;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class CTBLangEN extends LanguageProvider{

	public CTBLangEN(DataGenerator gen) {
		super(gen, CTBackport.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add(CTBEffects.INFESTED.get(), "Infested");
		add(CTBEffects.OOZING.get(), "Oozing");
		add(CTBEffects.TRIAL_OMEN.get(), "Trial Omen");
		add(CTBEffects.WEAVING.get(), "Weaving");
		add(CTBEffects.WIND_CHARGED.get(), "Wind Charged");
	}

}
