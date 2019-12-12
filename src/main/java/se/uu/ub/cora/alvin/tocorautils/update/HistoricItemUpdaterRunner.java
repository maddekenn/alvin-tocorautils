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
import java.lang.reflect.Method;

import se.uu.ub.cora.javaclient.cora.CoraClient;
import se.uu.ub.cora.javaclient.cora.CoraClientFactoryImp;

public class HistoricItemUpdaterRunner {

	static ClientUpdater historicItemUpdater;

	private HistoricItemUpdaterRunner() {

	}

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		String appTokenUrl = args[0];
		String baseUrl = args[1];
		String userId = args[2];
		String appToken = args[3];
		String updaterClassName = args[4];

		CoraClientFactoryImp clientFactoryImp = CoraClientFactoryImp
				.usingAppTokenVerifierUrlAndBaseUrl(appTokenUrl, baseUrl);
		CoraClient coraClient = clientFactoryImp.factor(userId, appToken);

		historicItemUpdater = createClientUpdater(updaterClassName, coraClient);
		historicItemUpdater.update();
	}

	private static ClientUpdater createClientUpdater(String updaterClassName, CoraClient coraClient)
			throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
			InvocationTargetException {
		Class<?>[] cArg = new Class[1];
		cArg[0] = CoraClient.class;
		Method constructor = Class.forName(updaterClassName).getMethod("usingCoraClient", cArg);
		return (ClientUpdater) constructor.invoke(null, coraClient);
	}
}
