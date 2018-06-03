/*
 * Copyright 2018 Uppsala University Library
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
package se.uu.ub.cora.alvin.tocorautils.convert;

import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public abstract class CollectionItemConstructor {
	Map<String, String> rowFromDb;

	public ClientDataGroup convert(Map<String, String> rowFromDb) {
		this.rowFromDb = rowFromDb;
		ClientDataGroup item = createClientDataGroupWithAttribute();
		addChildrenToClientDataGroup(item);
		return item;
	}

	private ClientDataGroup createClientDataGroupWithAttribute() {
		ClientDataGroup item = ClientDataGroup.withNameInData("metadata");
		item.addAttributeByIdWithValue("type", "collectionItem");
		return item;
	}

	void addChildrenToClientDataGroup(ClientDataGroup item) {
		String id = getId();
		addRecordInfo(id, item);
		addNameInData(id, item);
		addExtraData(id, item);
	}

	protected abstract String getId();

	void addRecordInfo(String id, ClientDataGroup item) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		addId(id, recordInfo);
		addDataDivider(recordInfo);
		item.addChild(recordInfo);
	}

	protected abstract void addId(String id, ClientDataGroup recordInfo);

	protected void addDataDivider(ClientDataGroup recordInfo) {
		ClientDataGroup dataDivider = ClientDataGroup.withNameInData("dataDivider");
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId", "bibsys"));
		recordInfo.addChild(dataDivider);
	}

	protected void addNameInData(String nameInData, ClientDataGroup item) {
		item.addChild(ClientDataAtomic.withNameInDataAndValue("nameInData", nameInData));
	}

	protected abstract void addExtraData(String value, ClientDataGroup item);

	protected void possiblyAddExtraDataPartWithKeyAndAttribute(String key, String attribute,
			ClientDataGroup extraData) {
		if (nonEmptyValueExistsForKey(key)) {
			addExtraDataPartWithAttributeAndValue(key, attribute, extraData);
		}
	}

	private void addExtraDataPartWithAttributeAndValue(String key, String attribute,
			ClientDataGroup extraData) {
		String value = rowFromDb.get(key).trim();
		ClientDataGroup iso3ExtraDataPart = createExtraDataPartWithAttributeAndValue(attribute,
				value);
		extraData.addChild(iso3ExtraDataPart);
	}

	private boolean nonEmptyValueExistsForKey(String key) {
		return rowFromDb.containsKey(key) && rowFromDb.get(key) != null
				&& !"".equals(rowFromDb.get(key));
	}

	protected ClientDataGroup createExtraDataPartWithAttributeAndValue(String attribute,
			String value) {
		ClientDataGroup extraDataPart = ClientDataGroup.withNameInData("extraDataPart");
		extraDataPart.addAttributeByIdWithValue("type", attribute);
		extraDataPart.addChild(ClientDataAtomic.withNameInDataAndValue("value", value));
		return extraDataPart;
	}

}
