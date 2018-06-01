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

import se.uu.ub.cora.alvin.tocorautils.country.FromDbToCora;
import se.uu.ub.cora.alvin.tocorautils.country.CountryFromDbToCoraConverter;
import se.uu.ub.cora.alvin.tocorautils.country.FromDbToCoraImp;
import se.uu.ub.cora.alvin.tocorautils.country.CountryImporter;
import se.uu.ub.cora.client.CoraClient;
import se.uu.ub.cora.client.CoraClientConfig;
import se.uu.ub.cora.client.CoraClientFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class CountryFromDbToCoraFactory extends FromDbToCoraFactoryImp
		implements FromDbToCoraFactory {
	private CoraClientFactory coraClientFactory;

	@Override
	public FromDbToCora factorFromDbToCora(CoraClientFactory coraClientFactory,
			CoraClientConfig coraClientConfig, DbConfig dbConfig) {
		this.coraClientFactory = coraClientFactory;

		RecordReaderFactory recordReaderFactory = createRecordReaderFactory(dbConfig);
		FromDbToCoraConverter fromDbToCoraConverter = createConverter();

		ListImporter importer = createImporter(coraClientFactory, coraClientConfig);

		return FromDbToCoraImp.usingRecordReaderFactoryAndDbToCoraConverterAndImporter(
				recordReaderFactory, fromDbToCoraConverter, importer);
	}

	private FromDbToCoraConverter createConverter() {
		JsonBuilderFactory jsonFactory = createJsonBuilderFactory();
		return CountryFromDbToCoraConverter.usingJsonFactory(jsonFactory);
	}

	private ListImporter createImporter(CoraClientFactory coraClientFactory,
			CoraClientConfig coraClientConfig) {
		CoraClient coraClient = createCoraClient(coraClientFactory, coraClientConfig);
		return CountryImporter.usingCoraClient(coraClient);
	}

	CoraClientFactory getCoraClientFactory() {
		// needed for test
		return coraClientFactory;
	}

}
