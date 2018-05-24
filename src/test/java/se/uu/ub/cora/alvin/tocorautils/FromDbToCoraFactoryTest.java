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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.client.CoraClientConfig;
import se.uu.ub.cora.connection.SqlConnectionProvider;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.sqldatabase.RecordReaderFactoryImp;

public class FromDbToCoraFactoryTest {

	private CountryToCora countryToCora;

	@BeforeMethod
	public void beforeMethod() {
		FromDbToCoraFactoryImp c = new FromDbToCoraFactoryImp();

		String userId = "";
		String appToken = "";
		String appTokenVerifierUrl = "";
		String coraUrl = "";
		CoraClientConfig coraClientConfig = new CoraClientConfig(userId, appToken,
				appTokenVerifierUrl, coraUrl);

		String password = "";
		String url = "";
		DbConfig dbConfig = new DbConfig(userId, password, url);

		countryToCora = c.factorForCountryItems(coraClientConfig, dbConfig);

	}

	@Test
	public void testInitCreatedRecordReaderFactory() throws Exception {
		RecordReaderFactoryImp createdRecordReaderFactory = (RecordReaderFactoryImp) countryToCora
				.getRecordReaderFactory();
		assertTrue(createdRecordReaderFactory instanceof RecordReaderFactoryImp);

		SqlConnectionProvider connectionProvider = createdRecordReaderFactory
				.getConnectionProvider();
		// TODO: check this when class is added to sqldatabase, and fully implemented
		// assertTrue(connectionProvider instanceof ParameterConnectionProviderImp);

		// assertNotNull(connectionProvider);
	}

	@Test
	public void testInitFromDbToCoraConverter() throws Exception {
		FromDbToCoraConverter createdConverter = countryToCora.getFromDbToCoraConverter();
		assertTrue(createdConverter instanceof CountryFromDbToCoraConverter);

		CountryFromDbToCoraConverter c = (CountryFromDbToCoraConverter) createdConverter;

		JsonBuilderFactory j = c.getJsonBuilderFactory();
		assertTrue(j instanceof OrgJsonBuilderFactoryAdapter);
		assertNotNull(j);
	}
}
