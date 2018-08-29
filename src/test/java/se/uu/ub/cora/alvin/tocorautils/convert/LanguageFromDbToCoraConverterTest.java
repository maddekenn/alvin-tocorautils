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

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class LanguageFromDbToCoraConverterTest {
	List<Map<String, String>> rowsFromDb = new ArrayList<Map<String, String>>();
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private LanguageFromDbToCoraConverter languageFromDbToCoraConverter;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactory;
	private String textJsonFromSpy = "{\"name\":\"text\"}";;
	private String metadataJsonFromSpy = "{\"name\":\"metadata\"}";

	@BeforeMethod
	public void beforeMethod() {
		rowsFromDb = new ArrayList<Map<String, String>>();
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha3code", "swe");
		rowFromDb.put("svText", "Svenska");

		rowsFromDb.add(rowFromDb);

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
		languageFromDbToCoraConverter = LanguageFromDbToCoraConverter.usingJsonFactory(jsonFactory,
				dataToJsonConverterFactory);

	}

	@Test
	public void testConvertLanguageOneRow() {
		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 4);
		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);

		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json, metadataJsonFromSpy);
		assertEquals(row.size(), 3);

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonItemCollection = row2.get(0);
		assertEquals(coraJsonItemCollection.recordType, "metadataItemCollection");
		assertEquals(coraJsonItemCollection.json, metadataJsonFromSpy);
		assertCorrectFactoryCallsForOneItemNoAlternativeLang(convertedRows);
	}

	private void assertCorrectFactoryCallsForOneItemNoAlternativeLang(
			List<List<CoraJsonRecord>> convertedRows) {
		int textParts = 1;
		int textChildren = 2;
		assertCorrectFactoryCallsForTextsAndFirstItem(textParts, textChildren);

		ClientDataGroup langCollection = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(3);
		assertCorrectCollectionWithOneRefSentToFactory(langCollection, "completeLanguageCollection",
				3, "language");
	}

	private void assertCorrectFactoryCallsForTextsAndFirstItem(int textParts, int textChildren) {

		ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(0);
		assertCorrectTextGroupSentToConverterFactory(text, "sweLanguageItemText", textParts,
				textChildren);

		ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(1);
		assertCorrectTextGroupSentToConverterFactory(defText, "sweLanguageItemDefText", textParts,
				textChildren);

		ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(2);
		assertCorrectLangItemSentToFactory(langItem, "sweLanguageItem", 3, "swe");
	}

	@Test
	public void testConvertLanguageOneRowNullAsAlternativeLangauge() {
		rowsFromDb.get(0).put("enText", null);
		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 4);
		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);
		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json, metadataJsonFromSpy);
		assertEquals(row.size(), 3);

		assertCorrectFactoryCallsForOneItemNoAlternativeLang(convertedRows);

	}

	private void assertCorrectCollectionWithOneRefSentToFactory(ClientDataGroup langCollection,
			String expectedId, int numOfChildren, String nameInData) {
		assertCorrectGroupSentToConverterFactory(langCollection, expectedId, numOfChildren);
		assertEquals(langCollection.getFirstAtomicValueWithNameInData("nameInData"), nameInData);
		String expectedItemId = "sweLanguageItem";
		assertCorrectChildReferencesWithOneItem(langCollection, expectedItemId);
	}

	private void assertCorrectChildReferencesWithOneItem(ClientDataGroup langCollection,
			String expectedItemId) {
		ClientDataGroup childReferences = langCollection
				.getFirstGroupWithNameInData("collectionItemReferences");
		assertEquals(childReferences.getAllChildrenWithNameInData("ref").size(), 1);
		ClientDataGroup ref = childReferences.getFirstGroupWithNameInData("ref");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"languageCollectionItem");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), expectedItemId);
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

	private void assertCorrectLangItemSentToFactory(ClientDataGroup langItem, String expectedId,
			int numOfChildren, String nameInData) {
		assertCorrectGroupSentToConverterFactory(langItem, expectedId, numOfChildren);
		assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"), nameInData);
		assertCorrectExtraData(langItem, nameInData);
	}

	private void assertCorrectExtraData(ClientDataGroup langItem, String partValue) {
		ClientDataGroup extraData = langItem.getFirstGroupWithNameInData("extraData");
		ClientDataGroup extraDataPart = extraData.getFirstGroupWithNameInData("extraDataPart");
		assertEquals(extraDataPart.getFirstAtomicValueWithNameInData("value"), partValue);
		assertEquals(extraDataPart.getAttributes().get("type"), "iso639Alpha3");
	}

	@Test
	public void testConvertLanguageOneRowEmptyStringAsAlternativeLangauge() {
		rowsFromDb.get(0).put("enText", "");
		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 4);
		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);
		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json, metadataJsonFromSpy);
		assertEquals(row.size(), 3);

		assertCorrectFactoryCallsForOneItemNoAlternativeLang(convertedRows);
	}

	@Test
	public void testConvertLanguageOneRowOneAlternativeLanguage() {
		rowsFromDb.get(0).put("enText", "Swedish");
		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);
		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 4);
		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json, metadataJsonFromSpy);

		assertCorrectFactoryCallsForTextsAndFirstItem(2, 3);
		ClientDataGroup langCollection = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(3);
		assertCorrectCollectionWithOneRefSentToFactory(langCollection, "completeLanguageCollection",
				3, "language");

	}

	@Test
	public void testConvertLanguageTwoRow() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha3code", "nor");
		rowFromDb.put("svText", "Norska");
		rowsFromDb.add(rowFromDb);

		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 7);
		assertEquals(convertedRows.size(), 3);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json, metadataJsonFromSpy);

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonRecordText2 = row2.get(0);
		assertEquals(coraJsonRecordText2.recordType, "coraText");
		assertEquals(coraJsonRecordText2.json, textJsonFromSpy);
		CoraJsonRecord coraJsonRecordDefText2 = row2.get(1);
		assertEquals(coraJsonRecordDefText2.recordType, "coraText");
		assertEquals(coraJsonRecordDefText2.json, textJsonFromSpy);
		CoraJsonRecord coraJsonRecordItem2 = row2.get(2);
		assertEquals(coraJsonRecordItem2.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem2.json, metadataJsonFromSpy);
		List<CoraJsonRecord> row3 = convertedRows.get(2);
		CoraJsonRecord coraJsonItemCollection = row3.get(0);
		assertEquals(coraJsonItemCollection.recordType, "metadataItemCollection");

		assertEquals(coraJsonItemCollection.json, metadataJsonFromSpy);
		assertCorrectFactoryCallsForTwoItemsNoAlternativeLang();
	}

	private void assertCorrectFactoryCallsForTwoItemsNoAlternativeLang() {
		assertCorrectFactoryCallsForTextsAndFirstItem(1, 2);

		ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(3);
		assertCorrectTextGroupSentToConverterFactory(text, "norLanguageItemText", 1, 2);

		ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(4);
		assertCorrectTextGroupSentToConverterFactory(defText, "norLanguageItemDefText", 1, 2);

		ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(5);
		assertCorrectLangItemSentToFactory(langItem, "norLanguageItem", 3, "nor");
		ClientDataGroup langCollection = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(6);
		assertCorrectGroupSentToConverterFactory(langCollection, "completeLanguageCollection", 3);
		assertEquals(langCollection.getFirstAtomicValueWithNameInData("nameInData"), "language");
		assertCorrectChildReferencesWithTwoItems(langCollection);
	}

	private void assertCorrectChildReferencesWithTwoItems(ClientDataGroup langCollection) {
		ClientDataGroup childReferences = langCollection
				.getFirstGroupWithNameInData("collectionItemReferences");
		List<ClientDataGroup> refs = childReferences.getAllGroupsWithNameInData("ref");
		assertEquals(refs.size(), 2);
		ClientDataGroup ref = refs.get(0);
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"languageCollectionItem");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), "sweLanguageItem");
		ClientDataGroup ref2 = refs.get(1);
		assertEquals(ref2.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"languageCollectionItem");
		assertEquals(ref2.getFirstAtomicValueWithNameInData("linkedRecordId"), "norLanguageItem");
	}
}
