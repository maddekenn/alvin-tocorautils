package se.uu.ub.cora.alvin.tocorautils.convert;

import se.uu.ub.cora.clientdata.ClientDataGroup;

import java.util.List;
import java.util.Map;

public class TextFromHistoricCountryConstructor extends TextFromCountryConstructor {

    private TextFromHistoricCountryConstructor(Map<String, String> rowFromDb) {
        super(rowFromDb);
        this.dbKey = "code";
        this.coraInfix = "HistoricCountry";
    }

    private String cleanCamelCase(String text) {
        StringBuilder sb = new StringBuilder();
        boolean toUpper = false;
        for(char symbol : text.toCharArray()) {
            if(Character.isAlphabetic(symbol)) {
                if(toUpper) {
                    sb.append(Character.toUpperCase(symbol));
                    toUpper = false;
                } else {
                    sb.append(Character.toLowerCase(symbol));
                }
            } else {
                toUpper = true;
            }
        }
        return sb.toString();
    }

    protected String possiblyMangleId(String oldId) {
        return cleanCamelCase(oldId);
    }

    public static List<ClientDataGroup> constructFromDbRow(Map<String, String> rowFromDb) {
        TextFromCountryConstructor textConstructor =
                new TextFromHistoricCountryConstructor(rowFromDb);
        return textConstructor.getTexts();
    }
}
