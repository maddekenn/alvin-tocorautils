package se.uu.ub.cora.alvin.tocorautils.convert;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientSpy;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

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
        assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 3);
        assertEquals(convertedRows.size(), 1);
        List<CoraJsonRecord> row = convertedRows.get(0);
        CoraJsonRecord coraJsonRecordText = row.get(0);
        assertEquals(coraJsonRecordText.recordType, "coraText");
        String textJsonFromSpy = "{\"name\":\"text\"}";
        assertEquals(coraJsonRecordText.json, textJsonFromSpy);

        CoraJsonRecord coraJsonRecordDefText = row.get(1);
        assertEquals(coraJsonRecordDefText.recordType, "coraText");
        assertEquals(coraJsonRecordDefText.json, textJsonFromSpy);

        CoraJsonRecord coraJsonRecordItem = row.get(2);
        assertEquals(coraJsonRecordItem.recordType, "historicCountryCollectionItem");
        String collectionItemJsonFromSpy = "{\"name\":\"metadata\"}";
        assertEquals(coraJsonRecordItem.json, collectionItemJsonFromSpy);

        assertCorrectFirstTextsAndItem();
    }

    private void assertCorrectFirstTextsAndItem() {
        ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(0);
        assertCorrectTextGroupSentToConverterFactory(text, "duchyOfSaxeCoburgMeiningenHistoricCountryText", 2, 3);

        ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(1);
        assertCorrectTextGroupSentToConverterFactory(defText, "duchyOfSaxeCoburgMeiningenHistoricCountryDefText", 2, 3);

        ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(2);
        assertCorrectGroupSentToConverterFactory(langItem, "duchyOfSaxeCoburgMeiningenHistoricCountryItem", 2);
        assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"), "duchyOfSaxeCoburgMeiningen");
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

    @Test
    public void testConvertCountryTwoRow() {
        Map<String, String> rowFromDb = new HashMap<>();
        rowFromDb.put("code", "archbishopric_salzburg");
        rowFromDb.put("svText", "Ärkebiskopsdömet Salzburg");
        rowFromDb.put("enText", "Archbishopric of Salzburg");
        rowsFromDb.add(rowFromDb);

        List<List<CoraJsonRecord>> convertedRows = historicCountryFromDbToCoraConverter
                .convertToJsonFromRowsFromDb(rowsFromDb);

        assertEquals(dataToJsonConverterFactory.calledNumOfTimes, 6);
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
        assertEquals(coraJsonRecordItem.recordType, "historicCountryCollectionItem");
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
        assertEquals(coraJsonRecordItem2.recordType, "historicCountryCollectionItem");
        assertEquals(coraJsonRecordItem2.json, collectionItemJsonFromSpy);
        assertCorrectFirstTextsAndItem();
        assertCorrectSecondTextsAndItem();
    }

    private void assertCorrectSecondTextsAndItem() {
        ClientDataGroup text = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(3);
        assertCorrectTextGroupSentToConverterFactory(text, "archbishopricSalzburgHistoricCountryText", 2, 3);

        ClientDataGroup defText = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(4);
        assertCorrectTextGroupSentToConverterFactory(defText, "archbishopricSalzburgHistoricCountryDefText", 2, 3);

        ClientDataGroup langItem = (ClientDataGroup) dataToJsonConverterFactory.dataElements.get(5);
        assertCorrectGroupSentToConverterFactory(langItem, "archbishopricSalzburgHistoricCountryItem", 2);
        assertEquals(langItem.getFirstAtomicValueWithNameInData("nameInData"), "archbishopricSalzburg");
    }
}
