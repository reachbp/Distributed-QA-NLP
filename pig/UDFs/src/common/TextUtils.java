package common;

public abstract class TextUtils {
	private static final String beforeWord = ".*\\b(?i)";
	private static final String afterWord = "\\b.*";

	public static String cleaned(String text) {
		return text.toLowerCase().replaceAll("[^\\p{L}\\p{Z}]", " ");
	}

	public static String getRegex(String word) {
		return beforeWord + word + afterWord;
	}
}
