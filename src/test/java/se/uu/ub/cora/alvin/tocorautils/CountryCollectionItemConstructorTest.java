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
package se.uu.ub.cora.alvin.tocorautils;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class CountryCollectionItemConstructorTest {
	private Map<String, String> rowFromDb;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "SE");
		rowFromDb.put("lastupdated", "2014-04-17 10:12:48.8");
	}

	@Test
	public void testConvertCountry() {
		CountryCollectionItemConstructor countryItemCounstructor = new CountryCollectionItemConstructor();
		ClientDataGroup countryItem = countryItemCounstructor.convert(rowFromDb);
		assertEquals(countryItem.getNameInData(), "metadata");
		assertEquals(countryItem.getAttributes().get("type"), "collectionItem");

		ClientDataGroup recordInfo = countryItem.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "seCountryItem");
		assertCorrectDataDivider(recordInfo);

		assertEquals(countryItem.getFirstAtomicValueWithNameInData("nameInData"), "SE");
	}

	private void assertCorrectDataDivider(ClientDataGroup recordInfo) {
		ClientDataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		String linkedRecordId = dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId");
		assertEquals(linkedRecordId, "bibsys");
		String linkedRecordType = dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType");
		assertEquals(linkedRecordType, "system");
	}

	@Test
	public void testConvertCountryExtraDataOnlyIso2() {
		CountryCollectionItemConstructor countryFromDbToCoraStorageConverter = new CountryCollectionItemConstructor();
		ClientDataGroup countryItem = countryFromDbToCoraStorageConverter.convert(rowFromDb);

		ClientDataGroup extraData = countryItem.getFirstGroupWithNameInData("extraData");
		assertCorrectExtraDataPartGroup(extraData, "iso31661Alpha2", "SE");
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
	public void testConvertCountryExtraDataAllValues() {
		rowFromDb.put("alpha3code", "SWE");
		rowFromDb.put("numericalcode", "752");
		rowFromDb.put("marccode", "sw");
		CountryCollectionItemConstructor countryFromDbToCoraStorageConverter = new CountryCollectionItemConstructor();
		ClientDataGroup countryItem = countryFromDbToCoraStorageConverter.convert(rowFromDb);

		ClientDataGroup extraData = countryItem.getFirstGroupWithNameInData("extraData");
		assertCorrectExtraDataPartGroup(extraData, "iso31661Alpha2", "SE");
		assertCorrectExtraDataPartGroup(extraData, "iso31661Alpha3", "SWE");
		assertCorrectExtraDataPartGroup(extraData, "numericalcode", "752");
		assertCorrectExtraDataPartGroup(extraData, "marccode", "sw");
		assertEquals(extraData.getAllGroupsWithNameInData("extraDataPart").size(), 4);
	}
}
