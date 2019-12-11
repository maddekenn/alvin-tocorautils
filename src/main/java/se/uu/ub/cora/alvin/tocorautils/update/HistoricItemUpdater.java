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

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.cora.CoraClient;

public class HistoricItemUpdater {

	private CoraClient coraClient;

	public HistoricItemUpdater(CoraClient coraClient) {
		this.coraClient = coraClient;

		ClientDataRecord itemCollection = coraClient.readAsDataRecord("metadataItemCollection",
				"historicCountryCollection");
		ClientDataGroup collectionItemReferences = itemCollection.getClientDataGroup()
				.getFirstGroupWithNameInData("collectionItemReferences");
		for (ClientDataGroup item : collectionItemReferences.getAllGroupsWithNameInData("ref")) {
			String itemRecordId = item.getFirstAtomicValueWithNameInData("linkedRecordId");
			ClientDataRecord collectionItemRecord = coraClient
					.readAsDataRecord("genericCollectionItem", itemRecordId);
			ClientDataGroup collectionItem = collectionItemRecord.getClientDataGroup();
			coraClient.update("genericCollectionItem", itemRecordId, collectionItem);
		}
	}
}
