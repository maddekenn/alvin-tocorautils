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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public final class CountryFromDbToCoraConverter implements FromDbToCoraConverter {

	private JsonBuilderFactory jsonFactory;
	private DataToJsonConverterFactory dataToJsonConverterFactory;

	private CountryFromDbToCoraConverter(JsonBuilderFactory jsonFactory,
			DataToJsonConverterFactory dataToJsonConverterFactory) {
		this.jsonFactory = jsonFactory;
		this.dataToJsonConverterFactory = dataToJsonConverterFactory;
	}

	public static CountryFromDbToCoraConverter usingJsonFactoryAndConverterFactory(
			JsonBuilderFactory jsonFactory, DataToJsonConverterFactory dataToJsonConverterFactory) {
		return new CountryFromDbToCoraConverter(jsonFactory, dataToJsonConverterFactory);
	}

	@Override
	public List<List<CoraJsonRecord>> convertToJsonFromRowsFromDb(
			List<Map<String, String>> rowsFromDb) {
		List<List<CoraJsonRecord>> convertedRows = new ArrayList<>();

		for (Map<String, String> rowFromDb : rowsFromDb) {
			convertToJsonFromRow(convertedRows, rowFromDb);
		}
		return convertedRows;
	}

	private void convertToJsonFromRow(List<List<CoraJsonRecord>> convertedRows,
									  Map<String, String> rowFromDb) {
		List<CoraJsonRecord> convertedRow = new ArrayList<>();
		convertTexts(rowFromDb, convertedRow);
		convertCountryItem(rowFromDb, convertedRow);
		convertedRows.add(convertedRow);
	}

	private List<ClientDataGroup> getConstructedTextDataGroupsToCreate(
			Map<String, String> rowFromDb) {
		return TextFromCountryConstructor.constructFromDbRow(rowFromDb);
	}

	private String convertText(ClientDataGroup text) {
		DataToJsonConverter converter = getDataToJsonConverterFactory()
				.createForClientDataElement(jsonFactory, text);
		return converter.toJson();
	}

	private ClientDataGroup getConstructedCountryItemToCreate(Map<String, String> rowFromDb) {
		CollectionItemConstructor itemConstructor = new CountryCollectionItemConstructor();
		return itemConstructor.convert(rowFromDb);
	}

	public JsonBuilderFactory getJsonBuilderFactory() {
		// needed for tests
		return jsonFactory;
	}



	private void convertTexts(Map<String, String> rowFromDb, List<CoraJsonRecord> convertedRow) {
		List<ClientDataGroup> texts = getConstructedTextDataGroupsToCreate(rowFromDb);
		for (ClientDataGroup text : texts) {
			String json = convertText(text);
			convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("coraText", json));
		}
	}

	private void convertCountryItem(Map<String, String> rowFromDb,
			List<CoraJsonRecord> convertedRow) {
		ClientDataGroup itemDataGroup = getConstructedCountryItemToCreate(rowFromDb);

		DataToJsonConverter converter = getDataToJsonConverterFactory()
				.createForClientDataElement(jsonFactory, itemDataGroup);
		String json = converter.toJson();
		convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("countryCollectionItem", json));
	}

	public DataToJsonConverterFactory getDataToJsonConverterFactory() {
		return dataToJsonConverterFactory;
	}

}
