package se.uu.ub.cora.alvin.tocorautils.convert;

import java.text.Normalizer;

public class TextUtil {


	private TextUtil() {
	}

	public static String normalizeString(String stringToNormalize) {
		return Normalizer.normalize(stringToNormalize, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	public static String turnStringIntoCamelCase(String stringToTurnIntoCamelCase) {
		String[] splitText = stringToTurnIntoCamelCase.split("\\P{Alpha}+");
		String camelCasedString = splitText[0];
		for(int i=1; i< splitText.length; i++){
			String camelCasedWord = turnFirstCharacterIntoUpperCase(splitText[i]);
			camelCasedString+=camelCasedWord;
		}

		return camelCasedString;
	}

	private static final String turnFirstCharacterIntoUpperCase(String input) {
		return input.substring(0,  1).toUpperCase() + input.substring(1);
	};
}
