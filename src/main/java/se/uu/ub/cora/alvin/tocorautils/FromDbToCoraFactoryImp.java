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

import se.uu.ub.cora.alvin.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.alvin.tocorautils.importing.CoraImporter;
import se.uu.ub.cora.alvin.tocorautils.importing.Importer;
import se.uu.ub.cora.client.CoraClient;
import se.uu.ub.cora.client.CoraClientConfig;
import se.uu.ub.cora.client.CoraClientFactory;
import se.uu.ub.cora.connection.ParameterConnectionProviderImp;
import se.uu.ub.cora.connection.SqlConnectionProvider;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;
import se.uu.ub.cora.sqldatabase.RecordReaderFactoryImp;

public abstract class FromDbToCoraFactoryImp implements FromDbToCoraFactory {

	protected CoraClientFactory coraClientFactory;

	protected final RecordReaderFactory createRecordReaderFactory(DbConfig dbConfig) {
		SqlConnectionProvider connectionProvider = ParameterConnectionProviderImp
				.usingUriAndUserAndPassword(dbConfig.url, dbConfig.userId, dbConfig.password);
		return new RecordReaderFactoryImp(connectionProvider);
	}

	protected final JsonBuilderFactory createJsonBuilderFactory() {
		return new OrgJsonBuilderFactoryAdapter();
	}

	protected final CoraClient createCoraClient(CoraClientFactory coraClientFactory,
			CoraClientConfig coraClientConfig) {
		return coraClientFactory.factor(coraClientConfig.userId, coraClientConfig.appToken);
	}

	@Override
	public final FromDbToCora factorFromDbToCora(CoraClientFactory coraClientFactory,
			CoraClientConfig coraClientConfig, DbConfig dbConfig) {
		this.coraClientFactory = coraClientFactory;

		RecordReaderFactory recordReaderFactory = createRecordReaderFactory(dbConfig);
		JsonBuilderFactory jsonFactory = createJsonBuilderFactory();
		FromDbToCoraConverter fromDbToCoraConverter = createConverter(jsonFactory);

		CoraClient coraClient = createCoraClient(coraClientFactory, coraClientConfig);
		Importer importer = createImporter(coraClient);

		return FromDbToCoraImp.usingRecordReaderFactoryAndDbToCoraConverterAndImporter(
				recordReaderFactory, fromDbToCoraConverter, importer);
	}

	protected Importer createImporter(CoraClient coraClient) {
		return CoraImporter.usingCoraClient(coraClient);
	}

	abstract FromDbToCoraConverter createConverter(JsonBuilderFactory jsonFactory);

	protected CoraClientFactory getCoraClientFactory() {
		// needed for test
		return coraClientFactory;
	}

}
