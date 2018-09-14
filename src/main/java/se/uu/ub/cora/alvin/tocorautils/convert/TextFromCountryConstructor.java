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

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class TextFromCountryConstructor {

	private Map<String, String> rowFromDb;

	private List<ClientDataGroup> texts;

	protected String dbKey = "alpha2code";

	protected String coraInfix = "CountryItem";

	protected TextFromCountryConstructor(Map<String,String> rowFromDb) {
        this.rowFromDb = rowFromDb;
    }

    protected List<ClientDataGroup> getTexts() {
		texts = new ArrayList<>();
		createText(texts);
		createDefText(texts);
		return texts;
	}

	public static List<ClientDataGroup> constructFromDbRow(Map<String, String> rowFromDb) {
        TextFromCountryConstructor textConstructor = new TextFromCountryConstructor(rowFromDb);
		return textConstructor.getTexts();
	}

	private void createText(List<ClientDataGroup> texts) {
		createAndAddTextWithIdEnding(texts, "Text");
	}

	private void createAndAddTextWithIdEnding(List<ClientDataGroup> texts, String textIdEnding) {
		ClientDataGroup text = ClientDataGroup.withNameInData("text");
		addRecordInfo(textIdEnding, text);
		addTextParts(text);
		texts.add(text);
	}

	private void addRecordInfo(String textIdEnding, ClientDataGroup text) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		String id = constructIdFromCodeWithEnding(textIdEnding);
		addId(id, recordInfo);
		addDataDivider(recordInfo);
		text.addChild(recordInfo);
	}

	private String constructIdFromCodeWithEnding(String ending) {
		String code = rowFromDb.get(dbKey);
		return possiblyMangleId(code) + coraInfix + ending;
	}

	protected String possiblyMangleId(String oldId) {
		return oldId.toLowerCase();
	}

	private void addId(String id, ClientDataGroup recordInfo) {
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", id));
	}

	private void addDataDivider(ClientDataGroup recordInfo) {
		ClientDataGroup dataDivider = ClientDataGroup.withNameInData("dataDivider");
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId", "bibsys"));
		recordInfo.addChild(dataDivider);
	}

	private void addTextParts(ClientDataGroup text) {
		String textValue = rowFromDb.get("svText");
		addTextPartUsingValueLangAttributeAndTypeAttribute(textValue, "sv", "default", text);
		possiblyAddEnglishTextPart(text);
	}

	private void addTextPartUsingValueLangAttributeAndTypeAttribute(String textValue,
			String langAttribute, String typeAttribute, ClientDataGroup text) {
		ClientDataGroup textPart = ClientDataGroup.withNameInData("textPart");
		textPart.addAttributeByIdWithValue("type", typeAttribute);
		textPart.addAttributeByIdWithValue("lang", langAttribute);
		textPart.addChild(ClientDataAtomic.withNameInDataAndValue("text", textValue));
		text.addChild(textPart);
	}

	private void possiblyAddEnglishTextPart(ClientDataGroup text) {
		if (nonEmptyValueExistsForKey("enText")) {
			String enTextValue = rowFromDb.get("enText");
			addTextPartUsingValueLangAttributeAndTypeAttribute(enTextValue, "en", "alternative",
					text);
		}
	}

	private boolean nonEmptyValueExistsForKey(String key) {
		return rowFromDb.containsKey(key) && rowFromDb.get(key) != null
				&& !"".equals(rowFromDb.get(key));
	}

	private void createDefText(List<ClientDataGroup> texts) {
		createAndAddTextWithIdEnding(texts, "DefText");
	}
}
