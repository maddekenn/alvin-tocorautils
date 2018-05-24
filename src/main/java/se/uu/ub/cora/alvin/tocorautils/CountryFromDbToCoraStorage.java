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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.DataGroupToJsonConverter;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class CountryFromDbToCoraStorage implements FromDbToCoraStorage {

	private JsonBuilderFactory jsonFactory;

	private CountryFromDbToCoraStorage(JsonBuilderFactory jsonFactory) {
		this.jsonFactory = jsonFactory;
	}

	public static CountryFromDbToCoraStorage usingJsonFactory(JsonBuilderFactory jsonFactory) {
		return new CountryFromDbToCoraStorage(jsonFactory);
	}

	@Override
	public List<Map<String, String>> convertToJsonFromRowsFromDb(
			List<Map<String, String>> rowsFromDb) {
		List<Map<String, String>> convertedRows = new ArrayList<>();
		for (Map<String, String> rowFromDb : rowsFromDb) {
			convertToJsonFromRow(convertedRows, rowFromDb);
		}
		return convertedRows;
	}

	private void convertToJsonFromRow(List<Map<String, String>> convertedRows,
			Map<String, String> rowFromDb) {
		Map<String, String> convertedRow = new HashMap<>();
		convertTexts(rowFromDb, convertedRow);
		convertCountryItem(rowFromDb, convertedRow);
		convertedRows.add(convertedRow);
	}

	private void convertTexts(Map<String, String> rowFromDb, Map<String, String> convertedRow) {
		List<ClientDataGroup> texts = getConstructedTextDataGroupsToCreate(rowFromDb);
		for (ClientDataGroup text : texts) {
			String key = getKey(text);
			String json = convertText(text);
			convertedRow.put(key, json);
		}
	}

	private List<ClientDataGroup> getConstructedTextDataGroupsToCreate(
			Map<String, String> rowFromDb) {
		TextFromCountryConstructor textConstructor = new TextFromCountryConstructor();
		return textConstructor.constructFromDbRow(rowFromDb);
	}

	private String getKey(ClientDataGroup text) {
		String id = extractIdFromDataGroup(text);
		return id.endsWith("DefText") ? "defText" : "text";
	}

	private String extractIdFromDataGroup(ClientDataGroup text) {
		ClientDataGroup recordInfo = text.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private String convertText(ClientDataGroup text) {
		DataGroupToJsonConverter converter = DataGroupToJsonConverter
				.usingJsonFactoryForClientDataGroup(jsonFactory, text);
		return converter.toJson();
	}

	private void convertCountryItem(Map<String, String> rowFromDb,
			Map<String, String> convertedRow) {
		ClientDataGroup itemDataGroup = getConstructedCountryItemToCreate(rowFromDb);
		DataGroupToJsonConverter converter = DataGroupToJsonConverter
				.usingJsonFactoryForClientDataGroup(jsonFactory, itemDataGroup);
		String json = converter.toJson();
		convertedRow.put("countryCollectionItem", json);
	}

	private ClientDataGroup getConstructedCountryItemToCreate(Map<String, String> rowFromDb) {
		CountryCollectionItemConstructor itemConstructor = new CountryCollectionItemConstructor();
		return itemConstructor.convert(rowFromDb);
	}

}
