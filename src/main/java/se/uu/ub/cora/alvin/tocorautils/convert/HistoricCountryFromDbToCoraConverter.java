package se.uu.ub.cora.alvin.tocorautils.convert;

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoricCountryFromDbToCoraConverter {
    private JsonBuilderFactory jsonFactory;
    private DataToJsonConverterFactory dataToJsonConverterFactory;
    private HistoricCountryFromDbToCoraConverter(JsonBuilderFactory jsonFactory,
                                         DataToJsonConverterFactory dataToJsonConverterFactory) {
        this.jsonFactory = jsonFactory;
        this.dataToJsonConverterFactory = dataToJsonConverterFactory;
    }
    public static HistoricCountryFromDbToCoraConverter usingJsonFactoryAndConverterFactory(JsonBuilderFactory jsonFactory, DataToJsonConverterFactory dataToJsonConverterFactory) {
        return new HistoricCountryFromDbToCoraConverter(jsonFactory, dataToJsonConverterFactory);
    }

    public List<List<CoraJsonRecord>> convertToJsonFromRowsFromDb(List<Map<String,String>> rowsFromDb) {
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
        convertHistoricCountryItem(rowFromDb, convertedRow);
        convertedRows.add(convertedRow);
    }

    private void convertHistoricCountryItem(Map<String, String> rowFromDb,
                                    List<CoraJsonRecord> convertedRow) {
        ClientDataGroup itemDataGroup = getConstructedHistroricCountryItemToCreate(rowFromDb);

        DataToJsonConverter converter = getDataToJsonConverterFactory()
                .createForClientDataElement(jsonFactory, itemDataGroup);
        String json = converter.toJson();
        convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("historicCountryCollectionItem", json));
    }


    private ClientDataGroup getConstructedHistroricCountryItemToCreate(Map<String, String> rowFromDb) {
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
        return TextFromHistoricCountryConstructor.constructFromDbRow(rowFromDb);
    }

    private String convertText(ClientDataGroup text) {
        DataToJsonConverter converter = getDataToJsonConverterFactory()
                .createForClientDataElement(jsonFactory, text);
        return converter.toJson();
    }

    public DataToJsonConverterFactory getDataToJsonConverterFactory() {
        return dataToJsonConverterFactory;
    }
}
