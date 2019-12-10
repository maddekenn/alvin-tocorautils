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
package se.uu.ub.cora.alvin.tocorautils.doubles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.alvin.tocorautils.convert.FromDbToCoraConverter;

public class FromDbToCoraConverterSpy implements FromDbToCoraConverter {

	public List<Map<String, Object>> rowsFromDb;
	public List<Map<String, String>> returnedList;
	public List<List<CoraJsonRecord>> returnedList2;

	// @Override
	// public List<Map<String, String>> convertToJsonFromRowsFromDb(
	// List<Map<String, String>> rowsFromDb) {
	// this.rowsFromDb = rowsFromDb;
	//
	// returnedList = new ArrayList<>();
	// Map<String, String> returnedMap = new HashMap<>();
	// returnedMap.put("keyFromDbToCoraStorageSpy", "valueFromCoraStorageSpy");
	// returnedList.add(returnedMap);
	// return returnedList;
	// }

	@Override
	public List<List<CoraJsonRecord>> convertToJsonFromRowsFromDb(
			List<Map<String, Object>> rowsFromDb) {
		this.rowsFromDb = rowsFromDb;

		returnedList2 = new ArrayList<>();
		List<CoraJsonRecord> returnedInnerList = new ArrayList<>();
		returnedInnerList.add(CoraJsonRecord.withRecordTypeAndJson(
				"recordTypeFromDbToCoraConverterSpy", "jsonFromDbToCoraConverterSpy"));
		returnedList2.add(returnedInnerList);
		return returnedList2;
	}

}
