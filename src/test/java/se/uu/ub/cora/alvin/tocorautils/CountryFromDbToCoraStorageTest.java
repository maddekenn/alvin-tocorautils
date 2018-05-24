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
 */package se.uu.ub.cora.alvin.tocorautils;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class CountryFromDbToCoraStorageTest {
	List<Map<String, String>> rowsFromDb = new ArrayList<Map<String, String>>();
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private CountryFromDbToCoraStorage toCoraStorage;

	@BeforeMethod
	public void beforeMethod() {
		rowsFromDb = new ArrayList<Map<String, String>>();
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "SE");
		rowFromDb.put("svText", "Sverige");

		rowsFromDb.add(rowFromDb);

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		toCoraStorage = CountryFromDbToCoraStorage.usingJsonFactory(jsonFactory);

	}

	@Test
	public void testConvertCountryOneRow() {
		List<Map<String, String>> convertToJsonFromRowsFromDb = toCoraStorage
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertToJsonFromRowsFromDb.size(), 1);
		Map<String, String> row = convertToJsonFromRowsFromDb.get(0);
		String jsonForText = row.get("text");
		assertEquals(jsonForText,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Sverige\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		String jsonForDefText = row.get("defText");
		assertEquals(jsonForDefText,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Sverige\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		String jsonForItem = row.get("countryCollectionItem");
		assertEquals(jsonForItem,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"SE\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"SE\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso31661Alpha2\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");

	}

	@Test
	public void testConvertCountryTwoRow() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "NO");
		rowFromDb.put("svText", "Norge");
		rowsFromDb.add(rowFromDb);

		List<Map<String, String>> convertToJsonFromRowsFromDb = toCoraStorage
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertToJsonFromRowsFromDb.size(), 2);
		Map<String, String> row = convertToJsonFromRowsFromDb.get(0);
		String jsonForText = row.get("text");
		assertEquals(jsonForText,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Sverige\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		String jsonForDefText = row.get("defText");
		assertEquals(jsonForDefText,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Sverige\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		String jsonForItem = row.get("countryCollectionItem");
		assertEquals(jsonForItem,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"SE\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"SE\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso31661Alpha2\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");

		Map<String, String> secondRow = convertToJsonFromRowsFromDb.get(1);
		String jsonForText2 = secondRow.get("text");
		assertEquals(jsonForText2,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"noCountryItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Norge\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");
		String jsonForDefText2 = secondRow.get("defText");
		assertEquals(jsonForDefText2,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"noCountryItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Norge\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");
		String jsonForItem2 = secondRow.get("countryCollectionItem");
		assertEquals(jsonForItem2,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"noCountryItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"NO\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"NO\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso31661Alpha2\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");

	}
}
