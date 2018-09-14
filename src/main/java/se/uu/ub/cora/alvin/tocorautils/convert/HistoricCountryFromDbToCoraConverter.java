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
import se.uu.ub.cora.clientdata.RecordIdentifier;
import se.uu.ub.cora.clientdata.constructor.ItemCollectionConstructor;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public final class HistoricCountryFromDbToCoraConverter implements FromDbToCoraConverter {
	private JsonBuilderFactory jsonFactory;
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private List<RecordIdentifier> collectionItems;

	private HistoricCountryFromDbToCoraConverter(JsonBuilderFactory jsonFactory,
			DataToJsonConverterFactory dataToJsonConverterFactory) {
		this.jsonFactory = jsonFactory;
		this.dataToJsonConverterFactory = dataToJsonConverterFactory;
	}

	public static HistoricCountryFromDbToCoraConverter usingJsonFactoryAndConverterFactory(
			JsonBuilderFactory jsonFactory, DataToJsonConverterFactory dataToJsonConverterFactory) {
		return new HistoricCountryFromDbToCoraConverter(jsonFactory, dataToJsonConverterFactory);
	}

	public JsonBuilderFactory getJsonBuilderFactory() {
		// needed for tests
		return jsonFactory;
	}

	@Override
	public List<List<CoraJsonRecord>> convertToJsonFromRowsFromDb(
			List<Map<String, String>> rowsFromDb) {
		collectionItems = new ArrayList<>();
		List<List<CoraJsonRecord>> convertedRows = new ArrayList<>();
		for (Map<String, String> rowFromDb : rowsFromDb) {
			normalizeCodeString(rowFromDb);
			convertToJsonFromRow(convertedRows, rowFromDb);
		}
		convertedRows.add(createItemCollectionForCreatedCollectionItems());
		return convertedRows;
	}

	private void normalizeCodeString(Map<String, String> rowFromDb) {
		String code = rowFromDb.get("code");
		String replacedCode = TextUtil.normalizeString(code);
		rowFromDb.put("code", replacedCode);
	}

	private void convertToJsonFromRow(List<List<CoraJsonRecord>> convertedRows,
			Map<String, String> rowFromDb) {
		List<CoraJsonRecord> convertedRow = new ArrayList<>();
		convertTexts(rowFromDb, convertedRow);
		convertHistoricCountryItem(rowFromDb, convertedRow);
		convertedRows.add(convertedRow);
	}

	private void convertHistoricCountryItem(Map<String, String> rowFromDb,
			List<CoraJsonRecord> convertedRow) {
		ClientDataGroup itemDataGroup = getConstructedHistoricCountryItemToCreate(rowFromDb);

		String id = itemDataGroup.getFirstGroupWithNameInData("recordInfo")
				.getFirstAtomicValueWithNameInData("id");
		collectionItems.add(RecordIdentifier.usingTypeAndId("genericCollectionItem", id));

		DataToJsonConverter converter = getDataToJsonConverterFactory()
				.createForClientDataElement(jsonFactory, itemDataGroup);
		String json = converter.toJson();
		convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("genericCollectionItem", json));
	}

	private ClientDataGroup getConstructedHistoricCountryItemToCreate(
			Map<String, String> rowFromDb) {
		CollectionItemConstructor itemConstructor = new HistoricCountryCollectionItemConstructor();
		return itemConstructor.convert(rowFromDb);
	}

	private void convertTexts(Map<String, String> rowFromDb, List<CoraJsonRecord> convertedRow) {
		List<ClientDataGroup> texts = getConstructedTextDataGroupsToCreate(rowFromDb);
		for (ClientDataGroup text : texts) {
			String json = convertText(text);
			convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("coraText", json));
		}
	}

	private List<ClientDataGroup> getConstructedTextDataGroupsToCreate(
			Map<String, String> rowFromDb) {
		TextFromHistoricCountryConstructor textConstructor = new TextFromHistoricCountryConstructor();
		return textConstructor.constructFromDbRow(rowFromDb);
//		return TextFromHistoricCountryConstructor.constructFromDbRow(rowFromDb);
	}

	private String convertText(ClientDataGroup text) {
		DataToJsonConverter converter = getDataToJsonConverterFactory()
				.createForClientDataElement(jsonFactory, text);
		return converter.toJson();
	}

	public DataToJsonConverterFactory getDataToJsonConverterFactory() {
		return dataToJsonConverterFactory;
	}

	private List<CoraJsonRecord> createItemCollectionForCreatedCollectionItems() {
		List<CoraJsonRecord> itemCollectionHolderList = new ArrayList<>();
		String itemCollectionJson = createItemCollectionAsJson();

		CoraJsonRecord coraRecordJsonItemCollection = CoraJsonRecord
				.withRecordTypeAndJson("metadataItemCollection", itemCollectionJson);
		itemCollectionHolderList.add(coraRecordJsonItemCollection);
		return itemCollectionHolderList;
	}

	private String createItemCollectionAsJson() {
		ItemCollectionConstructor itemCollectionConstructor = ItemCollectionConstructor
				.withDataDivider("cora");
		ClientDataGroup itemCollectionDataGroup = createItemCollectionDataGroup(
				itemCollectionConstructor);
		DataToJsonConverter converter = dataToJsonConverterFactory
				.createForClientDataElement(jsonFactory, itemCollectionDataGroup);
		return converter.toJson();
	}

	private ClientDataGroup createItemCollectionDataGroup(
			ItemCollectionConstructor itemCollectionConstructor) {
		return itemCollectionConstructor.constructUsingIdAndNameInDataAndCollectionItems(
				"historicCountryCollection", "historicCountry", collectionItems);
	}
}
