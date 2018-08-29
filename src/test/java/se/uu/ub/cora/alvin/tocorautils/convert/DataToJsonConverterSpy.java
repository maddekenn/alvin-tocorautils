package se.uu.ub.cora.alvin.tocorautils.convert;

import se.uu.ub.cora.clientdata.ClientDataElement;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataToJsonConverterSpy extends DataToJsonConverter {
	public ClientDataElement clientDataElement;

	public DataToJsonConverterSpy(ClientDataElement clientDataElement) {
		this.clientDataElement = clientDataElement;
	}

	@Override
	protected JsonObjectBuilder toJsonObjectBuilder() {
		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		jsonObjectBuilder.addKeyString("name", clientDataElement.getNameInData());
		return jsonObjectBuilder;
	}

}
