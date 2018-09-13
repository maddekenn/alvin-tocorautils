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
import static org.testng.Assert.assertNull;
import static se.uu.ub.cora.alvin.tocorautils.convert.ConverterTestHelpers.assertCorrectDataDivider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class LanguageCollectionItemConstructorTest {
	private static final String ALPHA2_ATTRIBUTE = "iso639Alpha2";
	private static final String ALPHA3_ATTRIBUTE = "iso639Alpha3";
	private Map<String, String> rowFromDb;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("alpha3code", "swe");
	}

	@Test
	public void testConvertLanguage() {
		LanguageCollectionItemConstructor languageItemCounstructor = new LanguageCollectionItemConstructor();
		ClientDataGroup langaugeItem = languageItemCounstructor.convert(rowFromDb);
		assertEquals(langaugeItem.getNameInData(), "metadata");
		assertEquals(langaugeItem.getAttributes().get("type"), "collectionItem");

		ClientDataGroup recordInfo = langaugeItem.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "sweLanguageItem");
		assertCorrectDataDivider(recordInfo);

		assertEquals(langaugeItem.getFirstAtomicValueWithNameInData("nameInData"), "swe");
	}

	@Test
	public void testUnusedGetSuffixIsNull() {
		LanguageCollectionItemConstructor languageItemCounstructor = new LanguageCollectionItemConstructor();
		assertNull(languageItemCounstructor.getSuffix());
	}

	@Test
	public void testConvertCountryExtraDataOnlyIso3() {
		CollectionItemConstructor languageItemConstructor = new LanguageCollectionItemConstructor();
		ClientDataGroup languageItem = languageItemConstructor.convert(rowFromDb);

		ClientDataGroup extraData = languageItem.getFirstGroupWithNameInData("extraData");
		assertCorrectExtraDataPartGroup(extraData, ALPHA3_ATTRIBUTE, "swe");
		assertEquals(extraData.getAllGroupsWithNameInData("extraDataPart").size(), 1);
	}

	private void assertCorrectExtraDataPartGroup(ClientDataGroup extraData, String attribute,
			String value) {
		ClientDataAttribute dataAttribute = ClientDataAttribute.withNameInDataAndValue("type",
				attribute);
		List<ClientDataGroup> extraParts = (List<ClientDataGroup>) extraData
				.getAllGroupsWithNameInDataAndAttributes("extraDataPart", dataAttribute);
		ClientDataGroup extraPart = extraParts.get(0);
		assertEquals(extraPart.getFirstAtomicValueWithNameInData("value"), value);
	}

	@Test
	public void testConvertLanguageExtraDataAllValues() {
		rowFromDb.put("alpha2code", "sw");
		CollectionItemConstructor languageItemConstructor = new LanguageCollectionItemConstructor();
		ClientDataGroup languageItem = languageItemConstructor.convert(rowFromDb);

		ClientDataGroup extraData = languageItem.getFirstGroupWithNameInData("extraData");
		assertCorrectExtraDataPartGroup(extraData, ALPHA3_ATTRIBUTE, "swe");
		assertCorrectExtraDataPartGroup(extraData, ALPHA2_ATTRIBUTE, "sw");
		assertEquals(extraData.getAllGroupsWithNameInData("extraDataPart").size(), 2);
	}

	@Test
	public void testConvertLanguageExtraDataNullValues() {
		rowFromDb.put("alpha2code", null);
		CollectionItemConstructor languageItemConstructor = new LanguageCollectionItemConstructor();
		ClientDataGroup languageItem = languageItemConstructor.convert(rowFromDb);

		ClientDataGroup extraData = languageItem.getFirstGroupWithNameInData("extraData");
		assertCorrectExtraDataPartGroup(extraData, ALPHA3_ATTRIBUTE, "swe");
		assertEquals(extraData.getAllGroupsWithNameInData("extraDataPart").size(), 1);
	}

	@Test
	public void testConvertLanguageExtraDataEmptyValues() {
		rowFromDb.put("alpha2code", "");
		CollectionItemConstructor languageItemConstructor = new LanguageCollectionItemConstructor();
		ClientDataGroup languageItem = languageItemConstructor.convert(rowFromDb);

		ClientDataGroup extraData = languageItem.getFirstGroupWithNameInData("extraData");
		assertCorrectExtraDataPartGroup(extraData, ALPHA3_ATTRIBUTE, "swe");
		assertEquals(extraData.getAllGroupsWithNameInData("extraDataPart").size(), 1);
	}

	@Test
	public void testConvertLanguageExtraDataAllValuesTrailingWhitespaces() {
		rowFromDb.put("alpha3code", " swe ");
		rowFromDb.put("alpha2code", " sw ");
		CollectionItemConstructor languageItemConstructor = new LanguageCollectionItemConstructor();
		ClientDataGroup languageItem = languageItemConstructor.convert(rowFromDb);

		ClientDataGroup extraData = languageItem.getFirstGroupWithNameInData("extraData");
		assertCorrectExtraDataPartGroup(extraData, ALPHA3_ATTRIBUTE, "swe");
		assertCorrectExtraDataPartGroup(extraData, ALPHA2_ATTRIBUTE, "sw");
		assertEquals(extraData.getAllGroupsWithNameInData("extraDataPart").size(), 2);
	}
}
