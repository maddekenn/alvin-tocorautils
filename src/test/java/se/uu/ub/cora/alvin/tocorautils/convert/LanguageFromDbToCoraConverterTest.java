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
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class LanguageFromDbToCoraConverterTest {
	List<Map<String, String>> rowsFromDb = new ArrayList<Map<String, String>>();
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private LanguageFromDbToCoraConverter languageFromDbToCoraConverter;

	@BeforeMethod
	public void beforeMethod() {
		rowsFromDb = new ArrayList<Map<String, String>>();
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha3code", "swe");
		rowFromDb.put("svText", "Svenska");

		rowsFromDb.add(rowFromDb);

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		languageFromDbToCoraConverter = LanguageFromDbToCoraConverter.usingJsonFactory(jsonFactory);

	}

	@Test
	public void testConvertLanguageOneRow() {
		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");
		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"swe\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"swe\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso639Alpha3\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");
		assertEquals(row.size(), 3);

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonItemCollection = row2.get(0);
		assertEquals(coraJsonItemCollection.recordType, "metadataItemCollection");

		String expectedJson = "{\"children\":[{\"name\":\"nameInData\",\"value\":\"language\"},{\"children\":[{\"name\":\"id\",\"value\":\"completeLanguageCollection\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"repeatId\":\"0\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"languageCollectionItem\"},{\"name\":\"linkedRecordId\",\"value\":\"sweLanguageItem\"}],\"name\":\"ref\"}],\"name\":\"collectionItemReferences\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"itemCollection\"}}";
		assertEquals(coraJsonItemCollection.json, expectedJson);
	}

	@Test
	public void testConvertLanguageOneRowNullAsAlternativeLangauge() {
		rowsFromDb.get(0).put("enText", null);
		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");
		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"swe\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"swe\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso639Alpha3\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");
		assertEquals(row.size(), 3);
	}

	@Test
	public void testConvertLanguageOneRowEmptyStringAsAlternativeLangauge() {
		rowsFromDb.get(0).put("enText", "");
		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");
		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"swe\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"swe\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso639Alpha3\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");
		assertEquals(row.size(), 3);
	}

	@Test
	public void testConvertLanguageOneRowOneAlternativeLanguage() {
		rowsFromDb.get(0).put("enText", "Swedish");
		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}},{\"children\":[{\"name\":\"text\",\"value\":\"Swedish\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"alternative\",\"lang\":\"en\"}}],\"name\":\"text\"}");
		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}},{\"children\":[{\"name\":\"text\",\"value\":\"Swedish\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"alternative\",\"lang\":\"en\"}}],\"name\":\"text\"}");
		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"swe\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"swe\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso639Alpha3\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");

	}

	@Test
	public void testConvertLanguageTwoRow() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha3code", "nor");
		rowFromDb.put("svText", "Norska");
		rowsFromDb.add(rowFromDb);

		List<List<CoraJsonRecord>> convertedRows = languageFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 3);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Svenska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"sweLanguageItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"swe\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"swe\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso639Alpha3\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonRecordText2 = row2.get(0);
		assertEquals(coraJsonRecordText2.recordType, "coraText");
		assertEquals(coraJsonRecordText2.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"norLanguageItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Norska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordDefText2 = row2.get(1);
		assertEquals(coraJsonRecordDefText2.recordType, "coraText");
		assertEquals(coraJsonRecordDefText2.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"norLanguageItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Norska\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordItem2 = row2.get(2);
		assertEquals(coraJsonRecordItem2.recordType, "languageCollectionItem");
		assertEquals(coraJsonRecordItem2.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"norLanguageItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"nor\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"nor\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso639Alpha3\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");

		List<CoraJsonRecord> row3 = convertedRows.get(2);
		CoraJsonRecord coraJsonItemCollection = row3.get(0);
		assertEquals(coraJsonItemCollection.recordType, "metadataItemCollection");

		String expectedJson = "{\"children\":[{\"name\":\"nameInData\",\"value\":\"language\"},{\"children\":[{\"name\":\"id\",\"value\":\"completeLanguageCollection\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"repeatId\":\"0\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"languageCollectionItem\"},{\"name\":\"linkedRecordId\",\"value\":\"sweLanguageItem\"}],\"name\":\"ref\"},{\"repeatId\":\"1\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"languageCollectionItem\"},{\"name\":\"linkedRecordId\",\"value\":\"norLanguageItem\"}],\"name\":\"ref\"}],\"name\":\"collectionItemReferences\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"itemCollection\"}}";
		assertEquals(coraJsonItemCollection.json, expectedJson);
	}
}
