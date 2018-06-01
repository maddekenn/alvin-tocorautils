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

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.convert.TextFromCountryConstructor;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class TextFromCountryConstructorTest {
	private Map<String, String> rowFromDb;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "SE");
		rowFromDb.put("svText", "Sverige");

	}

	@Test
	public void testConstructTexts() {
		TextFromCountryConstructor textConstructor = new TextFromCountryConstructor();
		List<ClientDataGroup> texts = textConstructor.constructFromDbRow(rowFromDb);
		assertEquals(texts.size(), 2);

		ClientDataGroup text = texts.get(0);
		assertEquals(text.getNameInData(), "text");

		assertCorrectRecordInfo(text, "seCountryItemText");
		assertCorrectSwedishTextPart(text, "Sverige");
		assertNoEnglishTextPart(text);

		ClientDataGroup defText = texts.get(1);
		assertEquals(defText.getNameInData(), "text");

		assertCorrectRecordInfo(defText, "seCountryItemDefText");
		assertCorrectSwedishTextPart(defText, "Sverige");
		assertNoEnglishTextPart(defText);
	}

	private void assertNoEnglishTextPart(ClientDataGroup text) {
		ClientDataAttribute type = ClientDataAttribute.withNameInDataAndValue("type",
				"alternative");
		ClientDataAttribute lang = ClientDataAttribute.withNameInDataAndValue("lang", "en");
		List<ClientDataGroup> textParts = (List<ClientDataGroup>) text
				.getAllGroupsWithNameInDataAndAttributes("textPart", type, lang);
		assertEquals(textParts.size(), 0);
	}

	private void assertCorrectSwedishTextPart(ClientDataGroup text, String textValue) {
		ClientDataAttribute type = ClientDataAttribute.withNameInDataAndValue("type", "default");
		ClientDataAttribute lang = ClientDataAttribute.withNameInDataAndValue("lang", "sv");
		List<ClientDataGroup> textParts = (List<ClientDataGroup>) text
				.getAllGroupsWithNameInDataAndAttributes("textPart", type, lang);
		ClientDataGroup svTextPart = textParts.get(0);
		assertEquals(svTextPart.getFirstAtomicValueWithNameInData("text"), textValue);
	}

	private void assertCorrectRecordInfo(ClientDataGroup text, String textId) {
		ClientDataGroup recordInfo = text.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), textId);
		assertCorrectDataDivider(recordInfo);
	}

	private void assertCorrectDataDivider(ClientDataGroup recordInfo) {
		ClientDataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		String linkedRecordId = dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId");
		assertEquals(linkedRecordId, "bibsys");
		String linkedRecordType = dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType");
		assertEquals(linkedRecordType, "system");
	}

	@Test
	public void testConstructTextsWithEnglishParts() {
		rowFromDb.put("enText", "Sweden");
		TextFromCountryConstructor textConstructor = new TextFromCountryConstructor();
		List<ClientDataGroup> texts = textConstructor.constructFromDbRow(rowFromDb);
		assertEquals(texts.size(), 2);
		ClientDataGroup text = texts.get(0);
		assertCorrectSwedishTextPart(text, "Sverige");
		assertCorrectEnglishTextPart(text, "Sweden");
	}

	private void assertCorrectEnglishTextPart(ClientDataGroup text, String textValue) {
		ClientDataAttribute type = ClientDataAttribute.withNameInDataAndValue("type",
				"alternative");
		ClientDataAttribute lang = ClientDataAttribute.withNameInDataAndValue("lang", "en");
		List<ClientDataGroup> textParts = (List<ClientDataGroup>) text
				.getAllGroupsWithNameInDataAndAttributes("textPart", type, lang);
		ClientDataGroup svTextPart = textParts.get(0);
		assertEquals(svTextPart.getFirstAtomicValueWithNameInData("text"), textValue);
	}

	@Test
	public void testConstructTextsWithEnglishPartsNullValue() {
		rowFromDb.put("enText", null);
		TextFromCountryConstructor textConstructor = new TextFromCountryConstructor();
		List<ClientDataGroup> texts = textConstructor.constructFromDbRow(rowFromDb);
		assertEquals(texts.size(), 2);
		ClientDataGroup text = texts.get(0);
		assertEquals(text.getAllGroupsWithNameInData("textPart").size(), 1);

		ClientDataGroup defText = texts.get(1);
		assertEquals(defText.getAllGroupsWithNameInData("textPart").size(), 1);
	}

	@Test
	public void testConstructTextsWithEnglishPartsEmptyValue() {
		rowFromDb.put("enText", "");
		TextFromCountryConstructor textConstructor = new TextFromCountryConstructor();
		List<ClientDataGroup> texts = textConstructor.constructFromDbRow(rowFromDb);
		assertEquals(texts.size(), 2);
		ClientDataGroup text = texts.get(0);
		assertEquals(text.getAllGroupsWithNameInData("textPart").size(), 1);

		ClientDataGroup defText = texts.get(1);
		assertEquals(defText.getAllGroupsWithNameInData("textPart").size(), 1);
	}

}
