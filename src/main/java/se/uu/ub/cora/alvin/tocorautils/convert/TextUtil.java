package se.uu.ub.cora.alvin.tocorautils.convert;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TextUtil {

	private TextUtil() {
	}

	public static String normalizeString(String stringToNormalize) {
		return Normalizer.normalize(stringToNormalize, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	public static String turnStringIntoCamelCase(String stringToTurnIntoCamelCase) {
		String[] stringSplitByNonAlpha = stringToTurnIntoCamelCase.split("\\P{Alpha}+");

		String camelCased = Arrays.stream(stringSplitByNonAlpha)
				.map(TextUtil::turnFirstCharacterIntoUpperCase).collect(Collectors.joining(""));
		return turnFirstCharacterIntoLowerCase(camelCased);
	}

	private static final String turnFirstCharacterIntoUpperCase(String stringToModify) {
		return stringToModify.substring(0, 1).toUpperCase() + stringToModify.substring(1);
	};

	private static final String turnFirstCharacterIntoLowerCase(String stringToModify) {
		return stringToModify.substring(0, 1).toLowerCase() + stringToModify.substring(1);
	};
}
