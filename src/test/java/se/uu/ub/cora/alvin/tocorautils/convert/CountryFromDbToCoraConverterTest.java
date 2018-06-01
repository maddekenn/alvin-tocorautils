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
import se.uu.ub.cora.alvin.tocorautils.convert.CountryFromDbToCoraConverter;
import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class CountryFromDbToCoraConverterTest {
	List<Map<String, String>> rowsFromDb = new ArrayList<Map<String, String>>();
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private CountryFromDbToCoraConverter countryFromDbToCoraConverter;

	@BeforeMethod
	public void beforeMethod() {
		rowsFromDb = new ArrayList<Map<String, String>>();
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "SE");
		rowFromDb.put("svText", "Sverige");

		rowsFromDb.add(rowFromDb);

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		countryFromDbToCoraConverter = CountryFromDbToCoraConverter.usingJsonFactory(jsonFactory);

	}

	@Test
	public void testConvertCountryOneRow() {
		List<List<CoraJsonRecord>> convertedRows = countryFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 1);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Sverige\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Sverige\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "countryCollectionItem");
		assertEquals(coraJsonRecordItem.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"SE\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"SE\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso31661Alpha2\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");
	}

	@Test
	public void testConvertCountryTwoRow() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "NO");
		rowFromDb.put("svText", "Norge");
		rowsFromDb.add(rowFromDb);

		List<List<CoraJsonRecord>> convertedRows = countryFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		assertEquals(coraJsonRecordText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Sverige\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Sverige\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "countryCollectionItem");
		assertEquals(coraJsonRecordItem.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"seCountryItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"SE\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"SE\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso31661Alpha2\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonRecordText2 = row2.get(0);
		assertEquals(coraJsonRecordText2.recordType, "coraText");
		assertEquals(coraJsonRecordText2.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"noCountryItemText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Norge\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordDefText2 = row2.get(1);
		assertEquals(coraJsonRecordDefText2.recordType, "coraText");
		assertEquals(coraJsonRecordDefText2.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"noCountryItemDefText\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"text\",\"value\":\"Norge\"}],\"name\":\"textPart\",\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}}],\"name\":\"text\"}");

		CoraJsonRecord coraJsonRecordItem2 = row2.get(2);
		assertEquals(coraJsonRecordItem2.recordType, "countryCollectionItem");
		assertEquals(coraJsonRecordItem2.json,
				"{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"noCountryItem\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"bibsys\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"NO\"},{\"children\":[{\"children\":[{\"name\":\"value\",\"value\":\"NO\"}],\"name\":\"extraDataPart\",\"attributes\":{\"type\":\"iso31661Alpha2\"}}],\"name\":\"extraData\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"collectionItem\"}}");

	}
}
