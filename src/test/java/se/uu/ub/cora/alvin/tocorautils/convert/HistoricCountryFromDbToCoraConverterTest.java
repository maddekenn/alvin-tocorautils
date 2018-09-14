package se.uu.ub.cora.alvin.tocorautils.convert;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class HistoricCountryFromDbToCoraConverterTest {
	List<Map<String, String>> rowsFromDb = new ArrayList<>();
	CoraClientSpy coraClient;
	private JsonBuilderFactory jsonFactory;
	private HistoricCountryFromDbToCoraConverter historicCountryFromDbToCoraConverter;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactory;

	@BeforeMethod
	public void beforeMethod() {
		rowsFromDb = new ArrayList<>();
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("code", "duchy_of_saxe-coburg-meiningen");
		rowFromDb.put("svText", "Hertigdömet Sachsen-Coburg-Meiningen");
		rowFromDb.put("enText", "Duchy of Saxe-Coburg-Meiningen");
		rowsFromDb.add(rowFromDb);

		coraClient = new CoraClientSpy();
		jsonFactory = new OrgJsonBuilderFactoryAdapter();
		dataToJsonConverterFactory = new DataToJsonConverterFactorySpy();
		historicCountryFromDbToCoraConverter = HistoricCountryFromDbToCoraConverter
				.usingJsonFactoryAndConverterFactory(jsonFactory, dataToJsonConverterFactory);
	}

	@Test
	public void testConvertCountryOneRow() {
		List<List<CoraJsonRecord>> convertedRows = historicCountryFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);
		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 4);
		assertEquals(convertedRows.size(), 2);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		String textJsonFromSpy = "{\"name\":\"text\"}";
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "genericCollectionItem");
		String collectionItemJsonFromSpy = "{\"name\":\"metadata\"}";
		assertEquals(coraJsonRecordItem.json, collectionItemJsonFromSpy);

		assertCorrectFirstTextsAndItem();

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonItemCollection = row2.get(0);
		assertEquals(coraJsonItemCollection.recordType, "metadataItemCollection");

		ClientDataGroup langCollection = (ClientDataGroup) dataToJsonConverterFactory.dataElements
				.get(3);
		assertCorrectCollectionWithOneRefSentToFactory(langCollection, "historicCountryCollection",
				3, "historicCountry");
	}

	private void assertCorrectFirstTextsAndItem() {
		ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(0);
		assertCorrectTextGroupSentToConverterFactory(text,
				"duchyOfSaxeCoburgMeiningenHistoricCountryItemText", 2, 3);

		ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(1);
		assertCorrectTextGroupSentToConverterFactory(defText,
				"duchyOfSaxeCoburgMeiningenHistoricCountryItemDefText", 2, 3);

		ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(2);
		assertCorrectGroupSentToConverterFactory(langItem,
				"duchyOfSaxeCoburgMeiningenHistoricCountryItem", 2);
		assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"),
				"duchyOfSaxeCoburgMeiningen");
	}

	private void assertCorrectTextGroupSentToConverterFactory(ClientDataGroup group,
			String expectedId, int numOfTextParts, int numOfChildren) {
		assertEquals(group.getAllGroupsWithNameInData("textPart").size(), numOfTextParts);
		assertCorrectGroupSentToConverterFactory(group, expectedId, numOfChildren);
	}

	private void assertCorrectGroupSentToConverterFactory(ClientDataGroup group, String expectedId,
			int numOfChildren) {
		String id = getIdFromDataGroup(group);
		assertEquals(id, expectedId);
		assertEquals(group.getChildren().size(), numOfChildren);
	}

	private String getIdFromDataGroup(ClientDataGroup group) {
		ClientDataGroup recordInfo = group.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void assertCorrectCollectionWithOneRefSentToFactory(ClientDataGroup langCollection,
			String expectedId, int numOfChildren, String nameInData) {
		assertCorrectGroupSentToConverterFactory(langCollection, expectedId, numOfChildren);
		assertEquals(langCollection.getFirstAtomicValueWithNameInData("nameInData"), nameInData);
		String expectedItemId = "duchyOfSaxeCoburgMeiningenHistoricCountryItem";
		assertCorrectChildReferencesWithOneItem(langCollection, expectedItemId);
	}

	private void assertCorrectChildReferencesWithOneItem(ClientDataGroup langCollection,
			String expectedItemId) {
		ClientDataGroup childReferences = langCollection
				.getFirstGroupWithNameInData("collectionItemReferences");
		assertEquals(childReferences.getAllChildrenWithNameInData("ref").size(), 1);
		ClientDataGroup ref = childReferences.getFirstGroupWithNameInData("ref");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"genericCollectionItem");
		assertEquals(ref.getFirstAtomicValueWithNameInData("linkedRecordId"), expectedItemId);
	}

	@Test
	public void testConvertCountryTwoRow() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("code", "archbishopric_salzburg");
		rowFromDb.put("svText", "Ärkebiskopsdömet Salzburg");
		rowFromDb.put("enText", "Archbishopric of Salzburg");
		rowsFromDb.add(rowFromDb);

		List<List<CoraJsonRecord>> convertedRows = historicCountryFromDbToCoraConverter
				.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 7);
		assertEquals(convertedRows.size(), 3);
		List<CoraJsonRecord> row = convertedRows.get(0);
		CoraJsonRecord coraJsonRecordText = row.get(0);
		assertEquals(coraJsonRecordText.recordType, "coraText");
		String textJsonFromSpy = "{\"name\":\"text\"}";
		assertEquals(coraJsonRecordText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText = row.get(1);
		assertEquals(coraJsonRecordDefText.recordType, "coraText");
		assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem = row.get(2);
		assertEquals(coraJsonRecordItem.recordType, "genericCollectionItem");
		String collectionItemJsonFromSpy = "{\"name\":\"metadata\"}";
		assertEquals(coraJsonRecordItem.json, collectionItemJsonFromSpy);

		List<CoraJsonRecord> row2 = convertedRows.get(1);
		CoraJsonRecord coraJsonRecordText2 = row2.get(0);
		assertEquals(coraJsonRecordText2.recordType, "coraText");
		assertEquals(coraJsonRecordText2.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordDefText2 = row2.get(1);
		assertEquals(coraJsonRecordDefText2.recordType, "coraText");
		assertEquals(coraJsonRecordDefText2.json, textJsonFromSpy);

		CoraJsonRecord coraJsonRecordItem2 = row2.get(2);
		assertEquals(coraJsonRecordItem2.recordType, "genericCollectionItem");
		assertEquals(coraJsonRecordItem2.json, collectionItemJsonFromSpy);
		assertCorrectFirstTextsAndItem();
		assertCorrectSecondTextsAndItem();
	}

	private void assertCorrectSecondTextsAndItem() {
		ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(3);
		assertCorrectTextGroupSentToConverterFactory(text,
				"archbishopricSalzburgHistoricCountryItemText", 2, 3);

		ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(4);
		assertCorrectTextGroupSentToConverterFactory(defText,
				"archbishopricSalzburgHistoricCountryItemDefText", 2, 3);

		ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(5);
		assertCorrectGroupSentToConverterFactory(langItem,
				"archbishopricSalzburgHistoricCountryItem", 2);
		assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"),
				"archbishopricSalzburg");
	}

	@Test
	public void testConvertCountryTwoRowCharactersShouldBeReplaced() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("code", "lordship_trcka_lípa");
		rowFromDb.put("svText", "Herradöme Trčka von Lípa");
		rowFromDb.put("enText", "Lordship of Trčka von Lípa");
		rowsFromDb.add(rowFromDb);

		historicCountryFromDbToCoraConverter.convertToJsonFromRowsFromDb(rowsFromDb);

		assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 7);
		ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(3);
		assertCorrectTextGroupSentToConverterFactory(text,
				"lordshipTrckaLipaHistoricCountryItemText", 2, 3);

		ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(4);
		assertCorrectTextGroupSentToConverterFactory(defText,
				"lordshipTrckaLipaHistoricCountryItemDefText", 2, 3);

		ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(5);
		assertCorrectGroupSentToConverterFactory(langItem, "lordshipTrckaLipaHistoricCountryItem",
				2);
		assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"), "lordshipTrckaLipa");
	}

}
