package se.uu.ub.cora.alvin.tocorautils.doubles;

import se.uu.ub.cora.client.CoraClient;
import se.uu.ub.cora.client.CoraClientFactory;

public class CoraClientFactorySpy implements CoraClientFactory {

	public CoraClientSpy factored;

	public CoraClientFactorySpy(String appTokenVerifierUrl, String baseUrl) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CoraClient factor(String userId, String appToken) {
		factored = new CoraClientSpy();
		return factored;
	}

}
