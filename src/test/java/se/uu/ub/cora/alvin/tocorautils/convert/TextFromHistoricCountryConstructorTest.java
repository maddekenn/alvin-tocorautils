/*
 * Copyright 2018 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.alvin.tocorautils.convert;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static se.uu.ub.cora.alvin.tocorautils.convert.ConverterTestHelpers.*;

public class TextFromHistoricCountryConstructorTest {
	private Map<String, String> rowFromDb;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("code", "duchy_of_saxe-coburg-meiningen");
		rowFromDb.put("svText", "Hertigdömet Sachsen-Coburg-Meiningen");
		rowFromDb.put("enText", "Duchy of Saxe-Coburg-Meiningen");
	}

	@Test
	public void testConstructTexts() {
		rowFromDb.remove("enText");
		List<ClientDataGroup> texts = TextFromHistoricCountryConstructor.constructFromDbRow(rowFromDb);
		assertEquals(texts.size(), 2);

		ClientDataGroup text = texts.get(0);
		assertEquals(text.getNameInData(), "text");

		assertCorrectRecordInfo(text, "duchyOfSaxeCoburgMeiningenHistoricCountryText");
		assertCorrectSwedishTextPart(text, "Hertigdömet Sachsen-Coburg-Meiningen");
		assertNoEnglishTextPart(text);

		ClientDataGroup defText = texts.get(1);
		assertEquals(defText.getNameInData(), "text");

		assertCorrectRecordInfo(defText, "duchyOfSaxeCoburgMeiningenHistoricCountryDefText");
		assertCorrectSwedishTextPart(defText, "Hertigdömet Sachsen-Coburg-Meiningen");
		assertNoEnglishTextPart(defText);
	}

	private void assertCorrectRecordInfo(ClientDataGroup text, String textId) {
		ClientDataGroup recordInfo = text.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), textId);
		assertCorrectDataDivider(recordInfo);
	}

	@Test
	public void testConstructTextsWithEnglishParts() {
        List<ClientDataGroup> texts = TextFromHistoricCountryConstructor.constructFromDbRow(rowFromDb);
		assertEquals(texts.size(), 2);
		ClientDataGroup text = texts.get(0);
		assertCorrectSwedishTextPart(text, "Hertigdömet Sachsen-Coburg-Meiningen");
		assertCorrectEnglishTextPart(text, "Duchy of Saxe-Coburg-Meiningen");
	}

	@Test
	public void testConstructTextsWithEnglishPartsNullValue() {
        rowFromDb.replace("enText", null);
        List<ClientDataGroup> texts = TextFromHistoricCountryConstructor.constructFromDbRow(rowFromDb);
		assertEquals(texts.size(), 2);
		ClientDataGroup text = texts.get(0);
		assertEquals(text.getAllGroupsWithNameInData("textPart").size(), 1);

		ClientDataGroup defText = texts.get(1);
		assertEquals(defText.getAllGroupsWithNameInData("textPart").size(), 1);
	}

	@Test
	public void testConstructTextsWithEnglishPartsEmptyValue() {
		rowFromDb.replace("enText", "");
        List<ClientDataGroup> texts = TextFromHistoricCountryConstructor.constructFromDbRow(rowFromDb);
		assertEquals(texts.size(), 2);
		ClientDataGroup text = texts.get(0);
		assertEquals(text.getAllGroupsWithNameInData("textPart").size(), 1);

		ClientDataGroup defText = texts.get(1);
		assertEquals(defText.getAllGroupsWithNameInData("textPart").size(), 1);
	}
}
