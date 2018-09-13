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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.alvin.tocorautils.convert.FromDbToCoraConverter;
import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.alvin.tocorautils.importing.CoraImporter;
import se.uu.ub.cora.client.CoraClient;
import se.uu.ub.cora.client.CoraClientConfig;
import se.uu.ub.cora.client.CoraClientFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.connection.ParameterConnectionProviderImp;
import se.uu.ub.cora.connection.SqlConnectionProvider;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.sqldatabase.RecordReaderFactoryImp;

import se.uu.ub.cora.alvin.tocorautils.convert.HistoricCountryFromDbToCoraConverter;

import java.lang.reflect.Field;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class HistoricCountryFromDbToCoraFactoryTest {

    private FromDbToCoraImp historicCountryToCora;
    private FromDbToCoraFactoryImp historicCountryToCoraFactory = new HistoricCountryFromDbToCoraFactory();
    private CoraClientConfig coraClientConfig;

    @BeforeMethod
    public void beforeMethod() {
        String userId = "someCoraUserId";
        String appToken = "someCoraAppToken";
        String appTokenVerifierUrl = "someCoraAppTokenVerifierUrl";
        String coraUrl = "someCoraUrl";
        coraClientConfig = new CoraClientConfig(userId, appToken, appTokenVerifierUrl, coraUrl);

        String dbUserId = "someDbUserId";
        String password = "someDbPassword";
        String url = "someDbUrl";
        DbConfig dbConfig = new DbConfig(dbUserId, password, url);

        CoraClientFactory coraClientFactory = new CoraClientFactorySpy();
        historicCountryToCora = (FromDbToCoraImp) historicCountryToCoraFactory.factorFromDbToCora(coraClientFactory,
                coraClientConfig, dbConfig);
    }

    @Test
    public void testInitCreatedRecordReaderFactory() throws Exception {
        RecordReaderFactoryImp createdRecordReaderFactory = (RecordReaderFactoryImp) historicCountryToCora
                .getRecordReaderFactory();
        assertNotNull(createdRecordReaderFactory);

        SqlConnectionProvider connectionProvider = createdRecordReaderFactory
                .getConnectionProvider();
        assertTrue(connectionProvider instanceof ParameterConnectionProviderImp);

        Field declaredUrlField = connectionProvider.getClass().getDeclaredField("url");
        declaredUrlField.setAccessible(true);
        String setUrl = (String) declaredUrlField.get(connectionProvider);
        assertEquals(setUrl, "someDbUrl");

        Field declaredUserField = connectionProvider.getClass().getDeclaredField("user");
        declaredUserField.setAccessible(true);
        String userId = (String) declaredUserField.get(connectionProvider);
        assertEquals(userId, "someDbUserId");

        Field declaredPasswordField = connectionProvider.getClass().getDeclaredField("password");
        declaredPasswordField.setAccessible(true);
        String password = (String) declaredPasswordField.get(connectionProvider);
        assertEquals(password, "someDbPassword");
    }

    @Test
    public void testInitFromDbToCoraConverter() {
        FromDbToCoraConverter createdConverter = historicCountryToCora.getFromDbToCoraConverter();
        assertNotNull(createdConverter);

        HistoricCountryFromDbToCoraConverter countryConverter = (HistoricCountryFromDbToCoraConverter) createdConverter;

        JsonBuilderFactory jsonBuilderFactory = countryConverter.getJsonBuilderFactory();
        assertTrue(jsonBuilderFactory instanceof OrgJsonBuilderFactoryAdapter);
        assertNotNull(jsonBuilderFactory);
        DataToJsonConverterFactory dataToJsonConverterFactory = countryConverter
                .getDataToJsonConverterFactory();
        assertTrue(dataToJsonConverterFactory instanceof DataToJsonConverterFactoryImp);
        assertNotNull(dataToJsonConverterFactory);
    }

    @Test
    public void testInitListImporter() {
        CoraImporter importer = (CoraImporter) historicCountryToCora.getListImporter();
        assertNotNull(importer);

        CoraClient coraClient = importer.getCoraClient();

        CoraClientFactorySpy coraClientFactory = (CoraClientFactorySpy) historicCountryToCoraFactory
                .getCoraClientFactory();
        assertNotNull(coraClientFactory);

        assertEquals(coraClient, coraClientFactory.factored);
        assertEquals(coraClientFactory.userId, coraClientConfig.userId);
        assertEquals(coraClientFactory.appToken, coraClientConfig.appToken);
    }
}
