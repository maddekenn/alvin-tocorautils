package se.uu.ub.cora.alvin.tocorautils.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

public class TextUtilTest {

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<TextUtil> constructor = TextUtil.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test
	public void testRemoveDiacriticsFromString() {
		String stringToNormalize = "aAáâåäÄąbBcCćčçdDeEéèfFgGhHiIíjJkKlLmMnNńoOóöÖpPqQrRsSśtTuUüÜvVwWxyYzZžż";
		String normalizedString = TextUtil.normalizeString(stringToNormalize);
		assertEquals(normalizedString,
				"aAaaaaAabBcCcccdDeEeefFgGhHiIijJkKlLmMnNnoOooOpPqQrRsSstTuUuUvVwWxyYzZzz");
	}
}
