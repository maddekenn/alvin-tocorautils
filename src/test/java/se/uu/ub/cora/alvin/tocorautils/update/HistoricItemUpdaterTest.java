package se.uu.ub.cora.alvin.tocorautils.update;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.doubles.CoraClientFactorySpy;
import se.uu.ub.cora.javaclient.cora.CoraClientFactory;

public class HistoricItemUpdaterTest {

	@Test
	public void testGivenCoraClientIsUsed() throws Exception {
		String appTokenVerifierURL = "http://appTokenVerifierURL";
		String baseURL = "http://baseURL";
		String userId = "someUserId";
		String appToken = "someApptoken";

		CoraClientFactory coraClientFactory = new CoraClientFactorySpy();

		CoraClientSpy coraClientSpy = new CoraClientSpy();
		HistoricItemUpdater historicItemUpdater = new HistoricItemUpdater(coraClientSpy);

		assertEquals(coraClientSpy.readAsRecordRecordType.get(0), "metadataItemCollection");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(0), "historicCountryCollection");

		assertEquals(coraClientSpy.readAsRecordRecordType.get(1), "genericCollectionItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(1), "gaulHistoricCountryItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(2), "britainHistoricCountryItem");

		assertEquals(coraClientSpy.readAsRecordRecordId.size(), 3);

	}

	@Test
	public void testGivenCoraClientIsUsed2() throws Exception {
		String appTokenVerifierURL = "http://appTokenVerifierURL";
		String baseURL = "http://baseURL";
		String userId = "someUserId";
		String appToken = "someApptoken";

		CoraClientFactory coraClientFactory = new CoraClientFactorySpy();

		CoraClientSpy coraClientSpy = new CoraClientSpy();
		coraClientSpy.extraLastItem = true;
		HistoricItemUpdater historicItemUpdater = new HistoricItemUpdater(coraClientSpy);

		assertEquals(coraClientSpy.readAsRecordRecordType.get(0), "metadataItemCollection");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(0), "historicCountryCollection");

		assertEquals(coraClientSpy.readAsRecordRecordType.get(1), "genericCollectionItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(1), "gaulHistoricCountryItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(2), "britainHistoricCountryItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(3), "extraLastItemHistoricCountryItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.size(), 4);

	}
}
