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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringJoiner;

import se.uu.ub.cora.client.CoraClientConfig;
import se.uu.ub.cora.client.CoraClientFactory;
import se.uu.ub.cora.client.CoraClientFactoryImp;

public class AlvinCountryListImporter {

	static FromDbToCoraFactory fromDbToCoraFactory = null;

	private FromDbToCoraFactoryImp k;

	private AlvinCountryListImporter(String[] args) {
		try {
			String fromDbToCoraFactoryClassName = getFromDbToCoraFactoryClassName(args);
			fromDbToCoraFactory = tryToCreateFromDbToCoraFactory(fromDbToCoraFactoryClassName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static void main(String[] args) {
		AlvinCountryListImporter listImporter = new AlvinCountryListImporter(args);

		listImporter.importCountries(args);
	}

	private static String getFromDbToCoraFactoryClassName(String[] args) {
		return args[0];
	}

	private FromDbToCoraFactory tryToCreateFromDbToCoraFactory(String fromDbToCoraFactoryClassName)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Constructor<?> constructor = Class.forName(fromDbToCoraFactoryClassName).getConstructor();
		return (FromDbToCoraFactory) constructor.newInstance();
	}

	private void importCountries(String[] args) {
		CountryFromDbToCora countryFromDbToCora = createFromDbToCora(args);
		ImportResult importResult = countryFromDbToCora.importCountries();
		throwErrorWithFailMessageIfFailsDuringImport(importResult);
	}

	private CountryFromDbToCora createFromDbToCora(String[] args) {
		CoraClientConfig coraClientConfig = createCoraClientConfig(args);
		DbConfig dbConfig = createDbConfig(args);

		CoraClientFactory coraClientFactory = CoraClientFactoryImp
				.usingAppTokenVerifierUrlAndBaseUrl(coraClientConfig.appTokenVerifierUrl,
						coraClientConfig.coraUrl);
		return fromDbToCoraFactory.factorForCountryItems(coraClientFactory, coraClientConfig,
				dbConfig);
	}

	private static CoraClientConfig createCoraClientConfig(String[] args) {
		String userId = args[1];
		String appToken = args[2];
		String appTokenVerifierUrl = args[3];
		String coraUrl = args[4];
		return new CoraClientConfig(userId, appToken, appTokenVerifierUrl, coraUrl);
	}

	private static DbConfig createDbConfig(String[] args) {
		String dbUserId = args[5];
		String dbPassword = args[6];
		String dbUrl = args[7];
		return new DbConfig(dbUserId, dbPassword, dbUrl);
	}

	private static void throwErrorWithFailMessageIfFailsDuringImport(ImportResult importResult) {
		if (failsDuringImport(importResult)) {
			StringJoiner stringJoiner = composeMessageFromImportResult(importResult);
			throw new RuntimeException(stringJoiner.toString());
		}
	}

	private static boolean failsDuringImport(ImportResult importResult) {
		return !importResult.listOfFails.isEmpty();
	}

	private static StringJoiner composeMessageFromImportResult(ImportResult importResult) {
		StringJoiner stringJoiner = new StringJoiner("\nERROR: ");
		for (String fail : importResult.listOfFails) {
			stringJoiner.add(fail);
		}
		return stringJoiner;
	}

}
