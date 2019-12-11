package se.uu.ub.cora.alvin.tocorautils.update;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;

public class HistoricItemUpdaterTest {
	String appTokenVerifierURL = "http://appTokenVerifierURL";
	String baseURL = "http://baseURL";
	String userId = "someUserId";
	String appToken = "someApptoken";
	private CoraClientSpy coraClientSpy;

	@BeforeMethod
	public void beforeMethod() {
		coraClientSpy = new CoraClientSpy();
	}

	@Test
	public void testGivenCoraClientIsUsed() throws Exception {
		new HistoricItemUpdater(coraClientSpy);
		assertCorrectRead();
		assertEquals(coraClientSpy.readAsRecordRecordId.size(), 4);
	}

	private void assertCorrectRead() {
		assertEquals(coraClientSpy.readAsRecordRecordType.get(0), "metadataItemCollection");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(0), "historicCountryCollection");
		assertEquals(coraClientSpy.readAsRecordRecordType.get(1), "genericCollectionItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(1), "gaulHistoricCountryItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(2), "romanRepublicHistoricCountryItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.get(3),
				"kingdomOfTheNorthHistoricCountryItem");
	}

	@Test
	public void testGivenCoraClientIsUsed2() throws Exception {
		coraClientSpy.extraLastItem = true;
		new HistoricItemUpdater(coraClientSpy);
		assertCorrectRead();
		assertEquals(coraClientSpy.readAsRecordRecordId.get(4), "extraLastItemHistoricCountryItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.size(), 5);
	}

	@Test
	public void testUpdate() {
		new HistoricItemUpdater(coraClientSpy);
		ClientDataGroup firstUpdatedItem = coraClientSpy.dataGroupsSentToUpdate.get(0);
		String firstUpdatedType = coraClientSpy.updateRecordTypes.get(0);
		String firstUpdatedId = coraClientSpy.updateRecordIds.get(0);

		ClientDataGroup secondUpdatedItem = coraClientSpy.dataGroupsSentToUpdate.get(1);
		String secondUpdatedType = coraClientSpy.updateRecordTypes.get(1);
		String secondUpdatedId = coraClientSpy.updateRecordIds.get(1);

		assertSame(coraClientSpy.recordsReturnedFromRead.get(1).getClientDataGroup(),
				firstUpdatedItem);
		assertEquals(firstUpdatedType, "genericCollectionItem");
		assertEquals(firstUpdatedId, "gaulHistoricCountryItem");

		assertSame(coraClientSpy.recordsReturnedFromRead.get(2).getClientDataGroup(),
				secondUpdatedItem);
		assertEquals(secondUpdatedType, "genericCollectionItem");
		assertEquals(secondUpdatedId, "romanRepublicHistoricCountryItem");

		assertEquals(coraClientSpy.updateRecordTypes.size(), 3);

	}

	@Test
	public void testTransformationOCodeNoTransformationNeeded() throws Exception {
		new HistoricItemUpdater(coraClientSpy);
		ClientDataGroup updatedItem = coraClientSpy.dataGroupsSentToUpdate.get(0);
		String code = updatedItem.getFirstAtomicValueWithNameInData("nameInData");
		assertEquals(code, "gaul");
	}

	@Test
	public void testTransformationOCodeCamelCase() throws Exception {
		new HistoricItemUpdater(coraClientSpy);
		ClientDataGroup updatedItem = coraClientSpy.dataGroupsSentToUpdate.get(1);
		String code = updatedItem.getFirstAtomicValueWithNameInData("nameInData");
		assertEquals(code, "roman_republic");
	}

	@Test
	public void testTransformationOCodeCamelCaseMultiple() throws Exception {
		new HistoricItemUpdater(coraClientSpy);
		ClientDataGroup updatedItem = coraClientSpy.dataGroupsSentToUpdate.get(2);
		String code = updatedItem.getFirstAtomicValueWithNameInData("nameInData");
		assertEquals(code, "kingdom_of_the_north");
	}
}
