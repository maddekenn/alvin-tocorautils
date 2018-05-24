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

import se.uu.ub.cora.client.CoraClientConfig;
import se.uu.ub.cora.connection.SqlConnectionProvider;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;
import se.uu.ub.cora.sqldatabase.RecordReaderFactoryImp;

public class FromDbToCoraFactoryImp implements FromDbToCoraFactory {

	@Override
	public CountryToCora factorForCountryItems(CoraClientConfig coraClientConfig,
			DbConfig dbConfig) {
		// TODO: should be a ParameterConnectionProviderImp
		SqlConnectionProvider connectionProvider = null;
		RecordReaderFactory recordReaderFactory = new RecordReaderFactoryImp(connectionProvider);

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		FromDbToCoraConverter fromDbToCoraConverter = CountryFromDbToCoraConverter
				.usingJsonFactory(jsonFactory);
		// FromDbToCoraCo();
		ListImporter importer = null;
		return CountryToCora.usingRecordReaderFactoryAndDbToCoraConverterAndImporter(
				recordReaderFactory, fromDbToCoraConverter, importer);
	}

}
