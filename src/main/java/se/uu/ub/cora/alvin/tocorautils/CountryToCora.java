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

import javax.naming.InitialContext;
import javax.naming.NamingException;

import se.uu.ub.cora.client.CoraClient;
import se.uu.ub.cora.client.CoraClientFactoryImp;
import se.uu.ub.cora.connection.ContextConnectionProviderImp;
import se.uu.ub.cora.connection.SqlConnectionProvider;
import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactoryImp;

public class CountryToCora {

	private FromDbToCoraStorage fromDbToCoraStorage;

	public CountryToCora(FromDbToCoraStorage fromDbToCoraStorage) {
		this.fromDbToCoraStorage = fromDbToCoraStorage;
		// TODO Auto-generated constructor stub
	}

	public void doStuff() throws NamingException {

		SqlConnectionProvider sqlConnectionProvider = createConnectionProvider();

		RecordReaderFactoryImp recordReaderFactory = new RecordReaderFactoryImp(
				sqlConnectionProvider);

		RecordReader recordReader = recordReaderFactory.factor();
		List<Map<String, String>> readAllFromTable = recordReader.readAllFromTable("someTable");
		List<Map<String, String>> readAllFromOtherTable = recordReader
				.readAllFromTable("someOtherTable");

		// joinTables()

		// CountryFromDbToCoraStorage toCoraStorage = CountryFromDbToCoraStorage
		// .usingCoraClientAndJsonFactory(null, null);

		List<Map<String, String>> convertedRows = fromDbToCoraStorage
				.convertToJsonFromRowsFromDb(readAllFromTable);

		// create coraClient
		String appTokenVerifierUrl = "someAppTokenVerifierUrl";
		String baseUrl = "someBaseUrl";
		CoraClientFactoryImp coraClientFactory = new CoraClientFactoryImp(appTokenVerifierUrl,
				baseUrl);
		CoraClient coraClient = coraClientFactory.factor("someUserId", "someAppToken");

		// import
		CountryImporter importer = CountryImporter.usingCoraClient(coraClient);
		ImportResult importResult = importer.createInCora(convertedRows);

	}

	private SqlConnectionProvider createConnectionProvider() throws NamingException {
		InitialContext context = new InitialContext();
		String databaseLookupName = "";
		return ContextConnectionProviderImp.usingInitialContextAndName(context, databaseLookupName);
	}

	public static CountryToCora usingFromDbToCoraStorage(FromDbToCoraStorage fromDbToCoraStorage) {
		return new CountryToCora(fromDbToCoraStorage);
	}

}
