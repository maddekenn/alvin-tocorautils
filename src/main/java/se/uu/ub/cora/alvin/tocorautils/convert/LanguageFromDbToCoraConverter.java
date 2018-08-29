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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.RecordIdentifier;
import se.uu.ub.cora.clientdata.constructor.ItemCollectionConstructor;
import se.uu.ub.cora.clientdata.constructor.TextConstructor;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public final class LanguageFromDbToCoraConverter implements FromDbToCoraConverter {
	private static final String DATA_DIVIDER = "bibsys";
	private static final String LANGUAGE_COLLECTION_ITEM = "languageCollectionItem";
	private JsonBuilderFactory jsonFactory;
	private List<RecordIdentifier> collectionItems;
	private DataToJsonConverterFactory dataToJsonConverterFactory;

	private LanguageFromDbToCoraConverter(JsonBuilderFactory jsonFactory,
			DataToJsonConverterFactory dataToJsonConverterFactory) {
		this.jsonFactory = jsonFactory;
		this.dataToJsonConverterFactory = dataToJsonConverterFactory;
	}

	public static LanguageFromDbToCoraConverter usingJsonFactory(JsonBuilderFactory jsonFactory,
			DataToJsonConverterFactory dataToJsonConverterFactory) {
		return new LanguageFromDbToCoraConverter(jsonFactory, dataToJsonConverterFactory);
	}

	@Override
	public List<List<CoraJsonRecord>> convertToJsonFromRowsFromDb(
			List<Map<String, String>> rowsFromDb) {
		collectionItems = new ArrayList<>();

		List<List<CoraJsonRecord>> convertedData = convertAllRowsToJson(rowsFromDb);

		convertedData.add(createItemCollectionForCreatedCollectionItems());
		return convertedData;
	}

	private List<List<CoraJsonRecord>> convertAllRowsToJson(List<Map<String, String>> rowsFromDb) {
		List<List<CoraJsonRecord>> convertedData = new ArrayList<>();
		for (Map<String, String> rowFromDb : rowsFromDb) {
			convertedData.add(convertToJsonFromRow(rowFromDb));
		}
		return convertedData;
	}

	private List<CoraJsonRecord> convertToJsonFromRow(Map<String, String> rowFromDb) {
		List<CoraJsonRecord> convertedRow = new ArrayList<>();
		convertedRow.add(createTextAsJsonFromRow(rowFromDb));
		convertedRow.add(createDefTextAsJsonFromRow(rowFromDb));
		convertedRow.add(convertLanguageItem(rowFromDb));
		return convertedRow;
	}

	private CoraJsonRecord createTextAsJsonFromRow(Map<String, String> rowFromDb) {
		return constructTextAsJsonUsingRowAndTextType(rowFromDb, "Text");
	}

	private CoraJsonRecord constructTextAsJsonUsingRowAndTextType(Map<String, String> rowFromDb,
			String type) {
		String textId = constructTextIdUsingRowAndTextType(rowFromDb, type);
		String textAsJson = createTextFromDbRowWithIdEndingAndTextKey(rowFromDb, textId);
		return CoraJsonRecord.withRecordTypeAndJson("coraText", textAsJson);
	}

	private String constructTextIdUsingRowAndTextType(Map<String, String> rowFromDb, String type) {
		String code = rowFromDb.get("alpha3code");
		return code.toLowerCase() + "LanguageItem" + type;
	}

	private String createTextFromDbRowWithIdEndingAndTextKey(Map<String, String> rowFromDb,
			String textId) {
		String svText = rowFromDb.get("svText");
		Map<String, String> alternativeTexts = getAlternativeTexts(rowFromDb);

		ClientDataGroup text = constructText(textId, svText, alternativeTexts);
		return convertText(text);
	}

	private Map<String, String> getAlternativeTexts(Map<String, String> rowFromDb) {
		Map<String, String> alternativeTexts = new HashMap<>();
		if (nonEmptyValueExistsForKey(rowFromDb, "enText")) {
			String textValue = rowFromDb.get("enText");
			alternativeTexts.put("en", textValue);
		}
		return alternativeTexts;
	}

	private boolean nonEmptyValueExistsForKey(Map<String, String> rowFromDb, String key) {
		return rowFromDb.containsKey(key) && rowFromDb.get(key) != null
				&& !"".equals(rowFromDb.get(key));
	}

	private ClientDataGroup constructText(String textId, String svText,
			Map<String, String> alternativeTexts) {
		TextConstructor textConstructor = new TextConstructor(DATA_DIVIDER);
		return textConstructor.constructTextUsingTextIdAndDefaultSvTextAndAlternativeTexts(textId,
				svText, alternativeTexts);
	}

	private String convertText(ClientDataGroup text) {
		DataToJsonConverter converter = dataToJsonConverterFactory
				.createForClientDataElement(jsonFactory, text);
		return converter.toJson();
	}

	private CoraJsonRecord createDefTextAsJsonFromRow(Map<String, String> rowFromDb) {
		return constructTextAsJsonUsingRowAndTextType(rowFromDb, "DefText");
	}

	private CoraJsonRecord convertLanguageItem(Map<String, String> rowFromDb) {
		ClientDataGroup itemDataGroup = getConstructedLanguageItemToCreate(rowFromDb);

		DataToJsonConverter converter = dataToJsonConverterFactory
				.createForClientDataElement(jsonFactory, itemDataGroup);

		String id = itemDataGroup.getFirstGroupWithNameInData("recordInfo")
				.getFirstAtomicValueWithNameInData("id");
		collectionItems.add(RecordIdentifier.usingTypeAndId(LANGUAGE_COLLECTION_ITEM, id));
		String json = converter.toJson();
		return CoraJsonRecord.withRecordTypeAndJson(LANGUAGE_COLLECTION_ITEM, json);
	}

	private ClientDataGroup getConstructedLanguageItemToCreate(Map<String, String> rowFromDb) {
		CollectionItemConstructor itemConstructor = new LanguageCollectionItemConstructor();
		return itemConstructor.convert(rowFromDb);
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
				.withDataDivider(DATA_DIVIDER);
		ClientDataGroup itemCollectionDataGroup = createItemCollectionDataGroup(
				itemCollectionConstructor);
		DataToJsonConverter converter = dataToJsonConverterFactory
				.createForClientDataElement(jsonFactory, itemCollectionDataGroup);
		return converter.toJson();
	}

	private ClientDataGroup createItemCollectionDataGroup(
			ItemCollectionConstructor itemCollectionConstructor) {
		return itemCollectionConstructor.constructUsingIdAndNameInDataAndCollectionItems(
				"completeLanguageCollection", "language", collectionItems);
	}

	public JsonBuilderFactory getJsonBuilderFactory() {
		return jsonFactory;
	}

	public DataToJsonConverterFactory getDataToJsonConverterFactory() {
		return dataToJsonConverterFactory;
	}
}
