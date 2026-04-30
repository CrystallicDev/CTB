package ctbackport.datagen.lang;

public class MissingTranslationException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissingTranslationException(String message) {
		super(message);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
