package se.uu.ub.cora.alvin.tocorautils.convert;

public class TextFromHistoricCountryConstructor extends TextFromCountryConstructor {

	@Override
	protected String modifyCodeString(String code) {
		return TextUtil.turnStringIntoCamelCase(code);
	}

	@Override
	protected String getDbKey() {
		return "code";

	}

	@Override
	protected String getItemNamePart() {
		return "HistoricCountryItem";
	}
}
