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

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.constructor.ItemCollectionConstructor;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public final class HistoricCountryFromDbToCoraConverter extends CountryFromDbToCoraConverter {

	private HistoricCountryFromDbToCoraConverter(JsonBuilderFactory jsonFactory,
			DataToJsonConverterFactory dataToJsonConverterFactory) {
		super(jsonFactory, dataToJsonConverterFactory);
	}

	public static HistoricCountryFromDbToCoraConverter usingJsonFactoryAndConverterFactory(
			JsonBuilderFactory jsonFactory, DataToJsonConverterFactory dataToJsonConverterFactory) {
		return new HistoricCountryFromDbToCoraConverter(jsonFactory, dataToJsonConverterFactory);
	}

	@Override
	protected void handleRow(List<List<CoraJsonRecord>> convertedRows,
			Map<String, String> rowFromDb) {
		normalizeCodeString(rowFromDb);
		convertToJsonFromRow(convertedRows, rowFromDb);
	}

	private void normalizeCodeString(Map<String, String> rowFromDb) {
		String code = rowFromDb.get("code");
		String replacedCode = TextUtil.normalizeString(code);
		rowFromDb.put("code", replacedCode);
	}

	@Override
	protected String getItemType() {
		return "genericCollectionItem";
	}

	@Override
	protected List<ClientDataGroup> getConstructedTextDataGroupsToCreate(
			Map<String, String> rowFromDb) {
		TextFromHistoricCountryConstructor textConstructor = new TextFromHistoricCountryConstructor();
		return textConstructor.constructFromDbRow(rowFromDb);
	}

	@Override
	protected ClientDataGroup getConstructedItemToCreate(Map<String, String> rowFromDb) {
		CollectionItemConstructor itemConstructor = new HistoricCountryCollectionItemConstructor();
		return itemConstructor.convert(rowFromDb);
	}

	@Override
	protected ClientDataGroup createItemCollectionDataGroup(
			ItemCollectionConstructor itemCollectionConstructor) {
		return itemCollectionConstructor.constructUsingIdAndNameInDataAndCollectionItems(
				"historicCountryCollection", "historicCountry", collectionItems);
	}
}
