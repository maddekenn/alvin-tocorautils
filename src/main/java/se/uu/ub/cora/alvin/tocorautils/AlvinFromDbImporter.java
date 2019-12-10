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
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import se.uu.ub.cora.alvin.tocorautils.importing.ImportResult;
import se.uu.ub.cora.javaclient.CoraClientConfig;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;
import se.uu.ub.cora.javaclient.cora.CoraClientFactoryImp;

public final class AlvinFromDbImporter {
	private static List<AlvinFromDbImporter> instances = new ArrayList<>();
	private FromDbToCoraFactory fromDbToCoraFactory = null;
	String tableName;

	public static FromDbToCoraFactory getInstance() {
		return instances.get(0).fromDbToCoraFactory;
	}

	public static void main(String[] args) {
		new AlvinFromDbImporter(args);
	}

	private AlvinFromDbImporter(String[] args) {
		tableName = args[7];
		importFromDbToCora(args);
	}

	private void importFromDbToCora(String[] args) {
		enableAccessToCurrentInstanceForTesting();
		tryToCreateFromDbToCoraFactory(args[8]);
		FromDbToCora fromDbToCora = createFromDbToCora(args);
		importRowsUsing(fromDbToCora);
	}

	private void enableAccessToCurrentInstanceForTesting() {
		instances.add(this);
	}

	private void tryToCreateFromDbToCoraFactory(String fromDbToCoraFactoryClassName) {
		try {
			fromDbToCoraFactory = createFromDbToCoraFactory(fromDbToCoraFactoryClassName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private FromDbToCoraFactory createFromDbToCoraFactory(String fromDbToCoraFactoryClassName)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Constructor<?> constructor = Class.forName(fromDbToCoraFactoryClassName).getConstructor();
		return (FromDbToCoraFactory) constructor.newInstance();
	}

	private FromDbToCora createFromDbToCora(String[] args) {
		CoraClientConfig coraClientConfig = createCoraClientConfig(args);
		DbConfig dbConfig = createDbConfig(args);

		CoraClientFactory coraClientFactory = CoraClientFactoryImp
				.usingAppTokenVerifierUrlAndBaseUrl(coraClientConfig.appTokenVerifierUrl,
						coraClientConfig.coraUrl);

		return fromDbToCoraFactory.factorFromDbToCora(coraClientFactory, coraClientConfig,
				dbConfig);
	}

	private CoraClientConfig createCoraClientConfig(String[] args) {
		String userId = args[0];
		String appToken = args[1];
		String appTokenVerifierUrl = args[2];
		String coraUrl = args[3];
		return new CoraClientConfig(userId, appToken, appTokenVerifierUrl, coraUrl);
	}

	private DbConfig createDbConfig(String[] args) {
		String dbUserId = args[4];
		String dbPassword = args[5];
		String dbUrl = args[6];
		return new DbConfig(dbUserId, dbPassword, dbUrl);
	}

	private void importRowsUsing(FromDbToCora fromDbToCora) {
		ImportResult importResult = fromDbToCora.importFromTable(tableName);
		throwErrorWithFailMessageIfFailsDuringImport(importResult);
	}

	private void throwErrorWithFailMessageIfFailsDuringImport(ImportResult importResult) {
		if (failsDuringImport(importResult)) {
			StringJoiner stringJoiner = composeMessageFromImportResult(importResult);
			throw new RuntimeException(stringJoiner.toString());
		}
	}

	private boolean failsDuringImport(ImportResult importResult) {
		return !importResult.listOfFails.isEmpty();
	}

	private StringJoiner composeMessageFromImportResult(ImportResult importResult) {
		StringJoiner stringJoiner = new StringJoiner("\nERROR: ");
		for (String fail : importResult.listOfFails) {
			stringJoiner.add(fail);
		}
		return stringJoiner;
	}
}
