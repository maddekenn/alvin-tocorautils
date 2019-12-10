package se.uu.ub.cora.alvin.tocorautils.update;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.cora.CoraClient;

public class HistoricItemUpdater {

	private CoraClient coraClient;

	public HistoricItemUpdater(CoraClient coraClient) {
		this.coraClient = coraClient;

		ClientDataRecord itemCollection = coraClient.readAsRecord("metadataItemCollection",
				"historicCountryCollection");
		ClientDataGroup collectionItemReferences = itemCollection.getClientDataGroup()
				.getFirstGroupWithNameInData("collectionItemReferences");
		for (ClientDataGroup item : collectionItemReferences.getAllGroupsWithNameInData("ref")) {
			String firstAtomicValueWithNameInData = item
					.getFirstAtomicValueWithNameInData("linkedRecordId");
			coraClient.readAsRecord("genericCollectionItem", firstAtomicValueWithNameInData);
		}
	}
}
