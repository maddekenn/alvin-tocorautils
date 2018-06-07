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
package se.uu.ub.cora.alvin.tocorautils.importing;

import java.util.List;

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.client.CoraClient;

public final class CoraImporter implements Importer {

	private CoraClient coraClient;
	private ImportResult importResult;

	public static CoraImporter usingCoraClient(CoraClient coraClient) {
		return new CoraImporter(coraClient);
	}

	private CoraImporter(CoraClient coraClient) {
		this.coraClient = coraClient;
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

	public CoraClient getCoraClient() {
		// needed for test
		return coraClient;
	}

	@Override
	public ImportResult createInCora(List<List<CoraJsonRecord>> listOfConvertedRows) {
		importResult = new ImportResult();
		createRecordsForRows(listOfConvertedRows);
		return importResult;
	}

	private void createRecordsForRows(List<List<CoraJsonRecord>> listOfConvertedRows) {
		for (List<CoraJsonRecord> listWithConvertedRow : listOfConvertedRows) {
			createRecordForRow(listWithConvertedRow);
		}
	}

	private void createRecordForRow(List<CoraJsonRecord> listWithConvertedRow) {
		for (CoraJsonRecord coraJsonRecord : listWithConvertedRow) {
			tryToCreateRecordForJson(coraJsonRecord);
		}
	}

	private void tryToCreateRecordForJson(CoraJsonRecord coraJsonRecord) {
		try {
			createRecordForJson(coraJsonRecord.recordType, coraJsonRecord.json);
		} catch (Exception e) {
			addErrorImportResult(coraJsonRecord.json, e);
		}
	}
}
