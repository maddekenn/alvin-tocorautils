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

import org.testng.annotations.Test;

import se.uu.ub.cora.client.CoraClientConfig;

public class CountryFromDbToCoraServerTest {
	@Test
	public void testMain() throws Exception {
		String fromDbToCoraFactoryClassName = "se.uu.ub.cora.alvin.tocorautils.FromDbToCoraFactorySpy";

		String args[] = new String[] { fromDbToCoraFactoryClassName, "someUserId", "someAppToken",
				"appTokenVerifierUrl", "baseUrl", "dbUrl", "dbUser", "dbPassword" };

		CountryFromDbToCoraServer.main(args);
		FromDbToCoraFactorySpy fromDbToCoraFactory = (FromDbToCoraFactorySpy) CountryFromDbToCoraServer.fromDbToCoraFactory;
		assertTrue(fromDbToCoraFactory instanceof FromDbToCoraFactorySpy);

		assertEquals("se.uu.ub.cora.client.CoraClientFactoryImp",
				fromDbToCoraFactory.coraClientFactoryClassName);

		CoraClientConfig coraClientConfig = fromDbToCoraFactory.coraClientConfig;
		assertEquals(coraClientConfig.userId, args[1]);
		assertEquals(coraClientConfig.appToken, args[2]);
		assertEquals(coraClientConfig.appTokenVerifierUrl, args[3]);
		assertEquals(coraClientConfig.coraUrl, args[4]);

		DbConfig dbConfig = fromDbToCoraFactory.dbConfig;
		assertNotNull(dbConfig);
		assertEquals(dbConfig.userId, args[5]);
		assertEquals(dbConfig.password, args[6]);
		assertEquals(dbConfig.url, args[7]);

	}

}