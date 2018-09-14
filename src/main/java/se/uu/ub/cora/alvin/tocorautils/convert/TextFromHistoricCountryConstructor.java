package se.uu.ub.cora.alvin.tocorautils.convert;

import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataGroup;

public class TextFromHistoricCountryConstructor extends TextFromCountryConstructor {

    public TextFromHistoricCountryConstructor(){}

	private TextFromHistoricCountryConstructor(Map<String, String> rowFromDb) {
//		super(rowFromDb);
		this.dbKey = "code";
		this.coraInfix = "HistoricCountryItem";
	}

	@Override
	protected String possiblyMangleId(String code) {
        return TextUtil.turnStringIntoCamelCase(code);
	}

//	public static List<ClientDataGroup> constructFromDbRow(Map<String, String> rowFromDb) {
//		TextFromCountryConstructor textConstructor = new TextFromHistoricCountryConstructor(
//				rowFromDb);
//		return textConstructor.getTexts();
//	}
}
