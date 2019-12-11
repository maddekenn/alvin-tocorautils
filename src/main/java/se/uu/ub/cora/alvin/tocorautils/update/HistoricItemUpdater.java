/*
 * Copyright 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.alvin.tocorautils.update;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.cora.CoraClient;

public class HistoricItemUpdater {

	private static final String NAME_IN_DATA = "nameInData";
	private CoraClient coraClient;

	public HistoricItemUpdater(CoraClient coraClient) {
		this.coraClient = coraClient;

		ClientDataRecord itemCollection = coraClient.readAsDataRecord("metadataItemCollection",
				"historicCountryCollection");
		ClientDataGroup collectionItemReferences = itemCollection.getClientDataGroup()
				.getFirstGroupWithNameInData("collectionItemReferences");
		loopAndUpdateAllItems(coraClient, collectionItemReferences);
	}

	private void loopAndUpdateAllItems(CoraClient coraClient,
			ClientDataGroup collectionItemReferences) {
		for (ClientDataGroup itemRef : collectionItemReferences.getAllGroupsWithNameInData("ref")) {
			String linkedRecordId = itemRef.getFirstAtomicValueWithNameInData("linkedRecordId");
			updateOneItem(coraClient, linkedRecordId);
		}
	}

	private void updateOneItem(CoraClient coraClient, String linkedRecordId) {
		ClientDataGroup collectionItem = readItemFromServer(coraClient, linkedRecordId);
		updateCodeInItem(collectionItem);
		coraClient.update("genericCollectionItem", linkedRecordId, collectionItem);
	}

	private ClientDataGroup readItemFromServer(CoraClient coraClient, String linkedRecordId) {
		ClientDataRecord collectionItemRecord = coraClient.readAsDataRecord("genericCollectionItem",
				linkedRecordId);
		return collectionItemRecord.getClientDataGroup();
	}

	private void updateCodeInItem(ClientDataGroup collectionItem) {
		String code = collectionItem.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		String regEx = "([a-z])([A-Z])";
		String newCode = code.replaceAll(regEx, "$1_$2").toLowerCase();

		collectionItem.removeFirstChildWithNameInData(NAME_IN_DATA);
		collectionItem.addChild(ClientDataAtomic.withNameInDataAndValue(NAME_IN_DATA, newCode));
	}

	public CoraClient getCoraClient() {
		// needed for test
		return coraClient;
	}
}
