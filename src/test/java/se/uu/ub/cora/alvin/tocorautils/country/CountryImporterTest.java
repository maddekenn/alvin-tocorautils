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
package se.uu.ub.cora.alvin.tocorautils.country;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.ImportResult;
import se.uu.ub.cora.alvin.tocorautils.country.CountryImporter;
import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientSpy;

public class CountryImporterTest {

	private CoraClientSpy coraClient;
	private CountryImporter importer;
	private List<Map<String, String>> listOfConvertedRows;

	@BeforeMethod
	public void beforeMethod() {
		coraClient = new CoraClientSpy();
		importer = CountryImporter.usingCoraClient(coraClient);

		listOfConvertedRows = new ArrayList<>();
	}

	@Test
	public void testImport() {
		createAndAddRowUsingSuffix("");

		ImportResult importResult = importer.createInCora(listOfConvertedRows);
		assertEquals(coraClient.createdRecordTypes.size(), 3);
		assertEquals(importResult.noOfImportedOk, 3);
		String suffix = "";
		int index = 0;

		assertCorrectCreatedTextsAndItemUsingSuffixAndGroupNo(suffix, index);
	}

	@Test
	public void testImportTwoRows() {
		createAndAddRowUsingSuffix("");
		createAndAddRowUsingSuffix("2");

		ImportResult importResult = importer.createInCora(listOfConvertedRows);
		assertEquals(coraClient.createdRecordTypes.size(), 6);
		assertEquals(importResult.noOfImportedOk, 6);

		assertCorrectCreatedTextsAndItemUsingSuffixAndGroupNo("", 0);
		assertCorrectCreatedTextsAndItemUsingSuffixAndGroupNo("2", 1);
	}

	@Test
	public void testFailedImport() throws Exception {
		coraClient = new CoraClientSpy();
		importer = CountryImporter.usingCoraClient(coraClient);

		createAndAddRowUsingSuffix("FAIL");

		ImportResult importResult = importer.createInCora(listOfConvertedRows);

		assertEquals(importResult.noOfImportedOk, 0);
		List<String> listOfFails = importResult.listOfFails;
		assertEquals(listOfFails.size(), 3);
		assertEquals(listOfFails.get(0),
				"Failed to create record" + " json that failed: " + "json textFAIL");
		assertEquals(listOfFails.get(1),
				"Failed to create record" + " json that failed: " + "json def textFAIL");
		assertEquals(listOfFails.get(2),
				"Failed to create record" + " json that failed: " + "json itemFAIL");
	}

	@Test
	public void testFailedSomeImport() throws Exception {
		coraClient = new CoraClientSpy();
		importer = CountryImporter.usingCoraClient(coraClient);

		createAndAddRowUsingSuffix("");

		Map<String, String> convertedRow = new HashMap<>();
		listOfConvertedRows.add(convertedRow);
		String jsonText = "json text" + "FAIL";
		convertedRow.put("text", jsonText);
		String jsonDefText = "json def text" + "2";
		convertedRow.put("defText", jsonDefText);
		String jsonItem = "json item" + "FAIL";
		convertedRow.put("countryCollectionItem", jsonItem);

		createAndAddRowUsingSuffix("");

		ImportResult importResult = importer.createInCora(listOfConvertedRows);

		assertEquals(importResult.noOfImportedOk, 7);
		List<String> listOfFails = importResult.listOfFails;
		assertEquals(listOfFails.size(), 2);
		assertEquals(listOfFails.get(0),
				"Failed to create record" + " json that failed: " + "json textFAIL");
		assertEquals(listOfFails.get(1),
				"Failed to create record" + " json that failed: " + "json itemFAIL");
	}

	private void assertCorrectCreatedTextsAndItemUsingSuffixAndGroupNo(String suffix, int groupNo) {
		int stride = 3;
		int baseIndex = groupNo * stride;
		assertEquals(coraClient.createdRecordTypes.get(0 + baseIndex), "coraText");
		assertEquals(coraClient.jsonStrings.get(0 + baseIndex), "json text" + suffix);

		assertEquals(coraClient.createdRecordTypes.get(1 + baseIndex), "coraText");
		assertEquals(coraClient.jsonStrings.get(1 + baseIndex), "json def text" + suffix);

		assertEquals(coraClient.createdRecordTypes.get(2 + baseIndex), "countryCollectionItem");
		assertEquals(coraClient.jsonStrings.get(2 + baseIndex), "json item" + suffix);
	}

	private void createAndAddRowUsingSuffix(String suffix) {
		Map<String, String> convertedRow = new HashMap<>();
		listOfConvertedRows.add(convertedRow);
		String jsonText = "json text" + suffix;
		convertedRow.put("text", jsonText);
		String jsonDefText = "json def text" + suffix;
		convertedRow.put("defText", jsonDefText);
		String jsonItem = "json item" + suffix;
		convertedRow.put("countryCollectionItem", jsonItem);
	}
}
