package se.uu.ub.cora.alvin.tocorautils.convert;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataElement;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class DataToJsonConverterFactorySpy implements DataToJsonConverterFactory {

	public int calledNumOfTimes = 0;
	public List<ClientDataElement> dataElements = new ArrayList<>();

	@Override
	public DataToJsonConverter createForClientDataElement(JsonBuilderFactory factory,
			ClientDataElement clientDataElement) {
		calledNumOfTimes++;
		dataElements.add(clientDataElement);
		return new DataToJsonConverterSpy(clientDataElement);
	}

	@Override
	public DataToJsonConverter createForClientDataElementIncludingActionLinks(
			JsonBuilderFactory factory, ClientDataElement clientDataElement,
			boolean includeActionLinks) {
		// TODO Auto-generated method stub
		return null;
	}

}
