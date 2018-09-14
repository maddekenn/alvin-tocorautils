package se.uu.ub.cora.alvin.tocorautils.convert;

import java.text.Normalizer;

public class TextUtil {

	private TextUtil() {
	}

	public static String normalizeString(String stringToNormalize) {
		return Normalizer.normalize(stringToNormalize, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

}
