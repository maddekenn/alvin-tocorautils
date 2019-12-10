package se.uu.ub.cora.alvin.tocorautils.update;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.Action;
import se.uu.ub.cora.clientdata.ActionLink;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.cora.CoraClient;

public class CoraClientSpy implements CoraClient {

	boolean isCalled = false;
	List<String> readRecordType = new ArrayList<>();
	List<String> readRecordId = new ArrayList<>();
	List<String> readAsRecordRecordType = new ArrayList<>();
	List<String> readAsRecordRecordId = new ArrayList<>();
	boolean extraLastItem = false;

	@Override
	public String create(String recordType, String recordId1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String delete(String recordType, String recordId1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String read(String recordType, String recordId) {
		readRecordType.add(recordType);
		readRecordId.add(recordId);
		try {
			if (recordType.equals("metadataItemCollection")
					&& recordId.equals("historicCountryCollection")) {
				Path path = Path.of("src/test/resources/readHistoricCountryCollection.json");
				return Files.readString(path, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ClientDataRecord readAsDataRecord(String recordType, String recordId) {
		readAsRecordRecordType.add(recordType);
		readAsRecordRecordId.add(recordId);
		if (recordType.equals("metadataItemCollection")
				&& recordId.equals("historicCountryCollection")) {

			ClientDataGroup clientDataGroup = ClientDataGroup.withNameInData("metadata");
			ClientDataRecord clientDataRecord = ClientDataRecord
					.withClientDataGroup(clientDataGroup);
			clientDataGroup.addAttributeByIdWithValue("type", "itemCollection");

			ClientDataGroup collectionItemReferences = ClientDataGroup
					.withNameInData("collectionItemReferences");
			clientDataGroup.addChild(collectionItemReferences);

			collectionItemReferences.addChild(createRefWithItemName("gaulHistoricCountryItem"));
			collectionItemReferences.addChild(createRefWithItemName("britainHistoricCountryItem"));
			if (extraLastItem) {
				collectionItemReferences
						.addChild(createRefWithItemName("extraLastItemHistoricCountryItem"));
			}

			return clientDataRecord;
		}
		return null;
	}
	// {
	// "children": [
	// {
	// "name": "linkedRecordType",
	// "value": "coraText"
	// },
	// {
	// "name": "linkedRecordId",
	// "value": "historicCountryCollectionDefText"
	// }
	// ],
	// "actionLinks": {
	// "read": {
	// "requestMethod": "GET",
	// "rel": "read",
	// "url":
	// "https://cora.epc.ub.uu.se/alvin/rest/record/coraText/historicCountryCollectionDefText",
	// "accept": "application/vnd.uub.record+json"
	// }
	// },
	// "name": "defTextId"
	// }

	private ClientDataRecordLink createRefWithItemName(String itemName) {
		ClientDataRecordLink firstRef = ClientDataRecordLink.withNameInDataAndTypeAndId("ref",
				"genericCollectionItem", itemName);
		firstRef.setRepeatId("0");

		ActionLink actionLink = ActionLink.withAction(Action.READ);
		firstRef.addActionLink("read", actionLink);
		actionLink.setRequestMethod("GET");
		actionLink.setURL(
				"https://cora.epc.ub.uu.se/alvin/rest/record/genericCollectionItem/" + itemName);
		actionLink.setAccept("application/vnd.uub.record+json");
		return firstRef;
	}

	@Override
	public String readIncomingLinks(String recordType, String recordId1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readList(String recordType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(String recordType, String recordId1, String json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String create(String recordType, ClientDataGroup dataGroup) {
		// TODO Auto-generated method stub
		return null;
	}

}
