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
package se.uu.ub.cora.alvin.tocorautils.convert;

import se.uu.ub.cora.alvin.tocorautils.DbRowException;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class HistoricCountryCollectionItemConstructor extends CollectionItemConstructor {

	@Override
	protected String getSuffix() {
		return "HistoricCountryItem";
	}

	@Override
	protected String getId() {
		if (dbRowContainsNoCodeToUseAsId()) {
			throw DbRowException.withMessage("Could not find \"code\"");
		}
		return TextUtil.turnStringIntoCamelCase(rowFromDb.get("code"));
	}

	private boolean dbRowContainsNoCodeToUseAsId() {
		return !rowFromDb.containsKey("code") || "".equals(rowFromDb.get("code"));
	}

	@Override
	protected void addExtraData(String value, ClientDataGroup item) {
		// Not used in this item constructor
	}

	@Override
	protected String getNameInData() {
		return getId();
	}
}
