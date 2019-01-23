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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.convert.LanguageFromDbToCoraConverter;
import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.client.CoraClient;
import se.uu.ub.cora.client.CoraClientConfig;
import se.uu.ub.cora.client.CoraClientFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.tocorautils.DbConfig;
import se.uu.ub.cora.tocorautils.FromDbToCoraImp;
import se.uu.ub.cora.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.tocorautils.importing.CoraImporter;

public class LanguageFromDbToCaraFactoryTest {
	private CoraClientConfig coraClientConfig;
	private DbConfig dbConfig;
	private FromDbToCoraImp languageToCora;
	private LanguageFromDbToCoraFactory languageToCoraFactory = new LanguageFromDbToCoraFactory();

	@BeforeMethod
	public void beforeMethod() {

		String userId = "someCoraUserId";
		String appToken = "someCoraAppToken";
		String appTokenVerifierUrl = "someCoraAppTokenVierifierUrl";
		String coraUrl = "someCoraUrl";
		coraClientConfig = new CoraClientConfig(userId, appToken, appTokenVerifierUrl, coraUrl);

		String dbUserId = "someDbUserId";
		String password = "someDbPassword";
		String url = "someDbUrl";
		dbConfig = new DbConfig(dbUserId, password, url);

		CoraClientFactory coraClientFactory = new CoraClientFactorySpy();
		languageToCora = (FromDbToCoraImp) languageToCoraFactory
				.factorFromDbToCora(coraClientFactory, coraClientConfig, dbConfig);
	}

	@Test
	public void testInitFromDbToCoraConverter() throws Exception {
		FromDbToCoraConverter createdConverter = languageToCora.getFromDbToCoraConverter();
		assertTrue(createdConverter instanceof LanguageFromDbToCoraConverter);

		LanguageFromDbToCoraConverter languageConverter = (LanguageFromDbToCoraConverter) createdConverter;

		JsonBuilderFactory jsonBuilderFactory = languageConverter.getJsonBuilderFactory();
		assertTrue(jsonBuilderFactory instanceof OrgJsonBuilderFactoryAdapter);
		assertNotNull(jsonBuilderFactory);
		DataToJsonConverterFactory dataToJsonConverterFactory = languageConverter
				.getDataToJsonConverterFactory();
		assertTrue(dataToJsonConverterFactory instanceof DataToJsonConverterFactoryImp);
		assertNotNull(jsonBuilderFactory);
	}

	@Test
	public void testInitListImporter() throws Exception {
		CoraImporter importer = (CoraImporter) languageToCora.getImporter();
		assertTrue(importer instanceof CoraImporter);

		CoraClient coraClient = importer.getCoraClient();

		CoraClientFactorySpy coraClientFactory = (CoraClientFactorySpy) languageToCoraFactory
				.getCoraClientFactory();
		assertTrue(coraClientFactory instanceof CoraClientFactorySpy);

		assertEquals(coraClient, coraClientFactory.factored);
		assertEquals(coraClientFactory.userId, coraClientConfig.userId);
		assertEquals(coraClientFactory.appToken, coraClientConfig.appToken);
	}
}
