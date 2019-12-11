/*
 * Copyright 2019 Uppsala University Library
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
package se.uu.ub.cora.alvin.tocorautils.update;

import java.lang.reflect.InvocationTargetException;

import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientFactoryImp;

public class HistoricItemUpdaterRunner {

	protected static HistoricItemUpdater historicItemUpdater;

	private HistoricItemUpdaterRunner() {

	}

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException {
		// CoraClientDependencies coraClientDependencies = new CoraClientDependencies(null, null,
		// null,
		// null, null, null);

		String appTokenUrl = args[0];
		String coraUrl = args[1];
		String userId = args[2];
		String appToken = args[3];

		CoraClientFactoryImp clientFactoryImp = CoraClientFactoryImp
				.usingAppTokenVerifierUrlAndBaseUrl("", "");
		CoraClient coraClient = clientFactoryImp.factor(userId, appToken);
		// this.appTokenClientFactory = coraClientDependencies.appTokenClientFactory;
		// this.restClientFactory = coraClientDependencies.restClientFactory;
		// this.dataToJsonConverterFactory = coraClientDependencies.dataToJsonConverterFactory;
		// this.jsonToDataConverterFactory = coraClientDependencies.jsonToDataConverterFactory;
		// this.userId = coraClientDependencies.userId;
		// this.appToken = coraClientDependencies.appToken;

		// CoraClient coraClient = clientFactoryImp.factor("", null);
		historicItemUpdater = new HistoricItemUpdater(coraClient);
	}
}
