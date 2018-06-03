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
import se.uu.ub.cora.clientdata.constructor.TextConstructor;
import se.uu.ub.cora.clientdata.converter.javatojson.DataGroupToJsonConverter;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class LanguageFromDbToCoraConverter implements FromDbToCoraConverter {
	private static final String LANGUAGE_COLLECTION_ITEM = "languageCollectionItem";
	private JsonBuilderFactory jsonFactory;

	private LanguageFromDbToCoraConverter(JsonBuilderFactory jsonFactory) {
		this.jsonFactory = jsonFactory;
	}

	public static LanguageFromDbToCoraConverter usingJsonFactory(JsonBuilderFactory jsonFactory) {
		return new LanguageFromDbToCoraConverter(jsonFactory);
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
		convertLanguageItem(rowFromDb, convertedRow);
		convertedRows.add(convertedRow);
	}

	private void convertTexts(Map<String, String> rowFromDb, List<CoraJsonRecord> convertedRow) {
		String textAsJson = createTextFromDbRowWithIdEndingAndTextKey(rowFromDb, "Text", "svText");
		convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("coraText", textAsJson));
		String defTextAsJson = createTextFromDbRowWithIdEndingAndTextKey(rowFromDb, "DefText",
				"svText");
		convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("coraText", defTextAsJson));

	}

	private String createTextFromDbRowWithIdEndingAndTextKey(Map<String, String> rowFromDb,
			String idEnding, String textToExtract) {
		String textId = constructIdFromCodeWithEnding(rowFromDb, idEnding);
		String svText = rowFromDb.get(textToExtract);
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
		TextConstructor textConstructor = new TextConstructor("bibsys");
		return textConstructor.constructTextUsingTextIdAndDefaultSvTextAndAlternativeTexts(textId,
				svText, alternativeTexts);
	}

	private String convertText(ClientDataGroup text) {
		DataGroupToJsonConverter converter = DataGroupToJsonConverter
				.usingJsonFactoryForClientDataGroup(jsonFactory, text);
		return converter.toJson();
	}

	private String constructIdFromCodeWithEnding(Map<String, String> rowFromDb, String ending) {
		String code = rowFromDb.get("alpha3code");
		return code.toLowerCase() + "LanguageItem" + ending;
	}

	private void convertLanguageItem(Map<String, String> rowFromDb,
			List<CoraJsonRecord> convertedRow) {
		ClientDataGroup itemDataGroup = getConstructedLanguageItemToCreate(rowFromDb);
		DataGroupToJsonConverter converter = DataGroupToJsonConverter
				.usingJsonFactoryForClientDataGroup(jsonFactory, itemDataGroup);
		String json = converter.toJson();
		convertedRow.add(CoraJsonRecord.withRecordTypeAndJson(LANGUAGE_COLLECTION_ITEM, json));
	}

	private ClientDataGroup getConstructedLanguageItemToCreate(Map<String, String> rowFromDb) {
		CollectionItemConstructor itemConstructor = new LanguageCollectionItemConstructor();
		return itemConstructor.convert(rowFromDb);
	}
}
