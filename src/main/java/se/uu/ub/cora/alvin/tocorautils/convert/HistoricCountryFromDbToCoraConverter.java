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

import se.uu.ub.cora.alvin.tocorautils.CoraJsonRecord;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HistoricCountryFromDbToCoraConverter implements FromDbToCoraConverter{
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

    public JsonBuilderFactory getJsonBuilderFactory() {
        // needed for tests
        return jsonFactory;
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
        ClientDataGroup itemDataGroup = getConstructedHistoricCountryItemToCreate(rowFromDb);

        DataToJsonConverter converter = getDataToJsonConverterFactory()
                .createForClientDataElement(jsonFactory, itemDataGroup);
        String json = converter.toJson();
        convertedRow.add(CoraJsonRecord.withRecordTypeAndJson("historicCountryCollectionItem", json));
    }


    private ClientDataGroup getConstructedHistoricCountryItemToCreate(Map<String, String> rowFromDb) {
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
