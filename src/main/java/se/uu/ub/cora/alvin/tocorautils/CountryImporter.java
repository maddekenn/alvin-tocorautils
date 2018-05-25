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

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.client.CoraClient;

public final class CountryImporter implements ListImporter {

	private CoraClient coraClient;
	private ImportResult importResult;

	public static CountryImporter usingCoraClient(CoraClient coraClient) {
		return new CountryImporter(coraClient);
	}

	private CountryImporter(CoraClient coraClient) {
		this.coraClient = coraClient;
	}

	@Override
	public ImportResult createInCora(List<Map<String, String>> listOfConvertedRows) {
		importResult = new ImportResult();
		for (Map<String, String> convertedRow : listOfConvertedRows) {
			createRecordsForRow(convertedRow);
		}
		return importResult;
	}

	private void createRecordsForRow(Map<String, String> convertedRow) {
		createConvertedRowUsingRecordTypeAndKey(convertedRow, "coraText", "text");
		createConvertedRowUsingRecordTypeAndKey(convertedRow, "coraText", "defText");
		createConvertedRowUsingRecordTypeAndKey(convertedRow, "countryCollectionItem",
				"countryCollectionItem");
	}

	private void createConvertedRowUsingRecordTypeAndKey(Map<String, String> convertedRow,
			String recordType, String key) {
		String jsonText = "";
		try {
			jsonText = convertedRow.get(key);
			createRecordForJson(recordType, jsonText);
		} catch (Exception e) {
			addErrorImportResult(jsonText, e);
		}
	}

	private void createRecordForJson(String recordType, String jsonText) {
		coraClient.create(recordType, jsonText);
		importResult.noOfImportedOk++;
	}

	private void addErrorImportResult(String jsonText, Exception e) {
		String message = e.getMessage();
		message += " json that failed: ";
		message += jsonText;
		importResult.listOfFails.add(message);
	}

	CoraClient getCoraClient() {
		// needed for test
		return coraClient;
	}
}
