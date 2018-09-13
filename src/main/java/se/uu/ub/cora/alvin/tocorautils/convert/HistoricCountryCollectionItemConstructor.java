package se.uu.ub.cora.alvin.tocorautils.convert;

import se.uu.ub.cora.clientdata.ClientDataGroup;

public class HistoricCountryCollectionItemConstructor extends CollectionItemConstructor {

    @Override
    protected String getSuffix() {
        return "HistoricCountryItem";
    }

    @Override
    protected String getId() {
        assert(rowFromDb.containsKey("code"));
        String id = cleanCamelCase(rowFromDb.get("code"));
        assert(!"".equals(id));
        return id;
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

    @Override
    protected void addExtraData(String value, ClientDataGroup item) {
    }
}
