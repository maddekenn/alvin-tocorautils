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
 */package se.uu.ub.cora.alvin.tocorautils.convert;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.tocorautils.CoraJsonRecord;

public class CountryFromDbToCoraConverterTest {
	List<Map<String, String>> rowsFromDb = new ArrayList<Map<String, String>>();
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private CountryFromDbToCoraConverter countryFromDbToCoraConverter;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactory;

	@BeforeMethod
	public void beforeMethod() {
		rowsFromDb = new ArrayList<Map<String, String>>();
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "SE");
		rowFromDb.put("svText", "Sverige");

		rowsFromDb.add(rowFromDb);

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
		countryFromDbToCoraConverter = CountryFromDbToCoraConverter
				.usingJsonFactoryAndConverterFactory(jsonFactory, dataToJsonConverterFactory);

	}

	@Test
	public void testConvertCountryOneRow() {
		List<List<CoraJsonRecord>> convertedRows = countryFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 4);
		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		String textJsonFromSpy = "{\"name\":\"text\"}";
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "countryCollectionItem");
		String collectionItemJsonFromSpy = "{\"name\":\"metadata\"}";
		assertEquals(coraJsonRecordItem.json, collectionItemJsonFromSpy);

		assertCorrectFirstTextsAndItem();

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonItemCollection = row2.get(0);
		assertEquals(coraJsonItemCollection.recordType, "metadataItemCollection");

		ClientDataGroup countryCollection = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(3);
		assertCorrectCollectionWithOneRefSentToFactory(countryCollection,
				"completeCountryCollection", 3, "completeCountry");
	}

	private void assertCorrectFirstTextsAndItem() {
		ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(0);
		assertCorrectTextGroupSentToConverterFactory(text, "seCountryItemText", 1, 2);

		ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(1);
		assertCorrectTextGroupSentToConverterFactory(defText, "seCountryItemDefText", 1, 2);

		ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(2);
		assertCorrectGroupSentToConverterFactory(langItem, "seCountryItem", 3);
		assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"), "SE");
		assertCorrectExtraData(langItem, "SE");
	}

	private void assertCorrectTextGroupSentToConverterFactory(ClientDataGroup group,
			String expectedId, int numOfTextParts, int numOfChildren) {
		assertEquals(group.getAllGroupsWithNameInData("textPart").size(), numOfTextParts);
		assertCorrectGroupSentToConverterFactory(group, expectedId, numOfChildren);
	}

	private void assertCorrectGroupSentToConverterFactory(ClientDataGroup group, String expectedId,
			int numOfChildren) {
		String id = getIdFromDataGroup(group);
		assertEquals(id, expectedId);
		assertEquals(group.getChildren().size(), numOfChildren);
	}

	private String getIdFromDataGroup(ClientDataGroup group) {
		ClientDataGroup recordInfo = group.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void assertCorrectExtraData(ClientDataGroup langItem, String partValue) {
		ClientDataGroup extraData = langItem.getFirstGroupWithNameInData("extraData");
		ClientDataGroup extraDataPart = extraData.getFirstGroupWithNameInData("extraDataPart");
		assertEquals(extraDataPart.getFirstAtomicValueWithNameInData("value"), partValue);
		assertEquals(extraDataPart.getAttributes().get("type"), "iso31661Alpha2");
	}

	private void assertCorrectCollectionWithOneRefSentToFactory(ClientDataGroup langCollection,
			String expectedId, int numOfChildren, String nameInData) {
		assertCorrectGroupSentToConverterFactory(langCollection, expectedId, numOfChildren);
		assertEquals(langCollection.getFirstAtomicValueWithNameInData("nameInData"), nameInData);
		String expectedItemId = "seCountryItem";
		assertCorrectChildReferencesWithOneItem(langCollection, expectedItemId);
	}

	private void assertCorrectChildReferencesWithOneItem(ClientDataGroup langCollection,
			String expectedItemId) {
		ClientDataGroup childReferences = langCollection
				.getFirstGroupWithNameInData("collectionItemReferences");
		assertEquals(childReferences.getAllChildrenWithNameInData("ref").size(), 1);
		ClientDataGroup ref = childReferences.getFirstGroupWithNameInData("ref");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"countryCollectionItem");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), expectedItemId);
	}

	@Test
	public void testConvertCountryTwoRow() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "NO");
		rowFromDb.put("svText", "Norge");
		rowsFromDb.add(rowFromDb);

		List<List<CoraJsonRecord>> convertedRows = countryFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 7);
		assertEquals(convertedRows.size(), 3);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		String textJsonFromSpy = "{\"name\":\"text\"}";
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "countryCollectionItem");
		String collectionItemJsonFromSpy = "{\"name\":\"metadata\"}";
		assertEquals(coraJsonRecordItem.json, collectionItemJsonFromSpy);

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonRecordText2 = row2.get(0);
		assertEquals(coraJsonRecordText2.recordType, "coraText");
		assertEquals(coraJsonRecordText2.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText2 = row2.get(1);
		assertEquals(coraJsonRecordDefText2.recordType, "coraText");
		assertEquals(coraJsonRecordDefText2.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem2 = row2.get(2);
		assertEquals(coraJsonRecordItem2.recordType, "countryCollectionItem");
		assertEquals(coraJsonRecordItem2.json, collectionItemJsonFromSpy);
		assertCorrectFirstTextsAndItem();
		assertCorrectSecondTextsAndItem();

	}

	private void assertCorrectSecondTextsAndItem() {
		ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(3);
		assertCorrectTextGroupSentToConverterFactory(text, "noCountryItemText", 1, 2);

		ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(4);
		assertCorrectTextGroupSentToConverterFactory(defText, "noCountryItemDefText", 1, 2);

		ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(5);
		assertCorrectGroupSentToConverterFactory(langItem, "noCountryItem", 3);
		assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"), "NO");
		assertCorrectExtraData(langItem, "NO");
	}
}
