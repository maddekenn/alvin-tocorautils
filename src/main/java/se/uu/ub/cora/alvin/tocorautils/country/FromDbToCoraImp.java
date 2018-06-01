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

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.alvin.tocorautils.FromDbToCoraConverter;
import se.uu.ub.cora.alvin.tocorautils.ImportResult;
import se.uu.ub.cora.alvin.tocorautils.ListImporter;
import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public final class FromDbToCoraImp implements FromDbToCora {

	private FromDbToCoraConverter fromDbToCoraConverter;
	private RecordReaderFactory recordReaderFactory;
	private ListImporter importer;

	public static FromDbToCora usingRecordReaderFactoryAndDbToCoraConverterAndImporter(
			RecordReaderFactory recordReaderFactory, FromDbToCoraConverter fromDbToCoraConverter,
			ListImporter importer) {
		return new FromDbToCoraImp(recordReaderFactory, fromDbToCoraConverter, importer);
	}

	private FromDbToCoraImp(RecordReaderFactory recordReaderFactory,
			FromDbToCoraConverter fromDbToCoraConverter, ListImporter importer) {
		this.recordReaderFactory = recordReaderFactory;
		this.fromDbToCoraConverter = fromDbToCoraConverter;
		this.importer = importer;
	}

	@Override
	public ImportResult importFromTable(String tableName) {
		RecordReader recordReader = recordReaderFactory.factor();
		List<Map<String, String>> readAllFromTable = recordReader.readAllFromTable(tableName);

		List<Map<String, String>> convertedRows = fromDbToCoraConverter
				.convertToJsonFromRowsFromDb(readAllFromTable);
		return importer.createInCora(convertedRows);
	}

	public RecordReaderFactory getRecordReaderFactory() {
		// needed for test
		return recordReaderFactory;
	}

	public FromDbToCoraConverter getFromDbToCoraConverter() {
		// needed for test
		return fromDbToCoraConverter;
	}

	public ListImporter getListImporter() {
		// needed for test
		return importer;
	}

}
