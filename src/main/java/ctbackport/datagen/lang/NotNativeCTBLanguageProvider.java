package ctbackport.datagen.lang;

public interface NotNativeCTBLanguageProvider {

	/**
	 * <p>This method is supposed to be called for default sets, to translate
	 * their key like <b>"cold_pine"</b> to <b>"pin_froid"</b> in french. It is a shit way to do
	 * this, but still works. The most important part is that i don't have to write
	 * every translation for every block, so this is still "efficient", and it only
	 * run once during datagen anyway.</p>
	 * */
	public String translateSetName(String setName) throws MissingTranslationException;

}
