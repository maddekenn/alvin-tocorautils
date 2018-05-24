package se.uu.ub.cora.alvin.tocorautils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class TextFromCountryConstructor {

	private Map<String, String> rowFromDb;

	List<ClientDataGroup> constructFromDbRow(Map<String, String> rowFromDb) {
		List<ClientDataGroup> texts = new ArrayList<>();
		this.rowFromDb = rowFromDb;
		createText(texts);
		createDefText(texts);
		return texts;
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
		String code = rowFromDb.get("alpha2code");
		return code.toLowerCase() + "CountryItem" + ending;
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
		if (rowFromDb.containsKey("enText")) {
			String enTextValue = rowFromDb.get("enText");
			addTextPartUsingValueLangAttributeAndTypeAttribute(enTextValue, "en", "alternative",
					text);
		}
	}

	private void createDefText(List<ClientDataGroup> texts) {
		createAndAddTextWithIdEnding(texts, "DefText");
	}
}
