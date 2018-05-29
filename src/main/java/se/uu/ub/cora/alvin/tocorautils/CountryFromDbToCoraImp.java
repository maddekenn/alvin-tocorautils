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

import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class CountryFromDbToCoraImp implements CountryFromDbToCora {

	private FromDbToCoraConverter fromDbToCoraConverter;
	private RecordReaderFactory recordReaderFactory;
	private ListImporter importer;

	public static CountryFromDbToCora usingRecordReaderFactoryAndDbToCoraConverterAndImporter(
			RecordReaderFactory recordReaderFactory, FromDbToCoraConverter fromDbToCoraConverter,
			ListImporter importer) {
		return new CountryFromDbToCoraImp(recordReaderFactory, fromDbToCoraConverter, importer);
	}

	private CountryFromDbToCoraImp(RecordReaderFactory recordReaderFactory,
			FromDbToCoraConverter fromDbToCoraConverter, ListImporter importer) {
		this.recordReaderFactory = recordReaderFactory;
		this.fromDbToCoraConverter = fromDbToCoraConverter;
		this.importer = importer;
	}

	@Override
	public ImportResult importCountries() {
		RecordReader recordReader = recordReaderFactory.factor();
		List<Map<String, String>> readAllFromTable = recordReader
				.readAllFromTable("completeCountry");

		List<Map<String, String>> convertedRows = fromDbToCoraConverter
				.convertToJsonFromRowsFromDb(readAllFromTable);
		return importer.createInCora(convertedRows);
	}

	RecordReaderFactory getRecordReaderFactory() {
		// needed for test
		return recordReaderFactory;
	}

	FromDbToCoraConverter getFromDbToCoraConverter() {
		// needed for test
		return fromDbToCoraConverter;
	}

	ListImporter getListImporter() {
		// needed for test
		return importer;
	}

}
