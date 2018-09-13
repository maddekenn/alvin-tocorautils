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
		if (rowFromDb.containsKey("code")) {
			return possiblyExtractCamelCaseId();
		}
		throw DbRowException.withMessage("Could not find \"code\"");
	}

	private String possiblyExtractCamelCaseId() {
		String id = cleanCamelCase(rowFromDb.get("code"));
		if ("".equals(id)) {
			throw DbRowException.withMessage("Could not extract valid ID from \"code\"");
		}
		return id;
	}

	// private String sdf(String s) {
	// Arrays.asList(s.toCharArray()).stream().collect();
	//
	// return "";
	// }
	//
	// private Optional<Character> handleSymbol(Character symbol) {
	// if(Character.isAlphabetic(symbol)) {
	// return Optional.of(symbol);
	// }
	// return Optional.empty();
	// }

	private String cleanCamelCase(String text) {
		StringBuilder sb = new StringBuilder();
		boolean toUpper = false;
		for (char symbol : text.toCharArray()) {
			toUpper = handleCharacter(sb, toUpper, symbol);
		}
		return sb.toString();
	}

	private boolean handleCharacter(StringBuilder sb, boolean toUpper, char symbol) {
		if (Character.isAlphabetic(symbol)) {
			return handleUpperOrLowerCase(sb, toUpper, symbol);
		}
		return true;
	}

	private boolean handleUpperOrLowerCase(StringBuilder sb, boolean toUpper, char symbol) {
		if (toUpper) {
			sb.append(Character.toUpperCase(symbol));
			return false;
		}
		sb.append(Character.toLowerCase(symbol));
		return toUpper;
	}

	@Override
	protected void addExtraData(String value, ClientDataGroup item) {
		// Not used in this item constructor
	}
}
