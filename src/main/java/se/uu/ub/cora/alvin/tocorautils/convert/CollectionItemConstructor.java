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
	Map<String, Object> rowFromDb;

	public ClientDataGroup convert(Map<String, Object> rowFromDb2) {
		this.rowFromDb = rowFromDb2;
		ClientDataGroup item = createClientDataGroupWithAttribute();
		addChildrenToClientDataGroup(item);
		return item;
	}

	private ClientDataGroup createClientDataGroupWithAttribute() {
		ClientDataGroup item = ClientDataGroup.withNameInData("metadata");
		item.addAttributeByIdWithValue("type", "collectionItem");
		return item;
	}

	private void addChildrenToClientDataGroup(ClientDataGroup item) {
		String id = getId();
		addRecordInfo(item);
		addNameInData(item);
		addExtraData(id, item);
	}

	protected abstract String getId();

	protected abstract String getSuffix();

	protected abstract String getNameInData();

	void addRecordInfo(ClientDataGroup item) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		addId(recordInfo);
		addDataDivider(recordInfo);
		item.addChild(recordInfo);
	}

	protected void addId(ClientDataGroup recordInfo) {
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", getId() + getSuffix()));
	}

	protected void addDataDivider(ClientDataGroup recordInfo) {
		ClientDataGroup dataDivider = ClientDataGroup.withNameInData("dataDivider");
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId", "bibsys"));
		recordInfo.addChild(dataDivider);
	}

	protected void addNameInData(ClientDataGroup item) {
		item.addChild(ClientDataAtomic.withNameInDataAndValue("nameInData", getNameInData()));
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
		String value = ((String) rowFromDb.get(key)).trim();
		ClientDataGroup iso3ExtraDataPart = createExtraDataPartWithAttributeAndValue(attribute,
				value);
		extraData.addChild(iso3ExtraDataPart);
	}

	private boolean nonEmptyValueExistsForKey(String key) {
		return rowFromDb.get(key) != null && !"".equals(rowFromDb.get(key));
	}

	protected ClientDataGroup createExtraDataPartWithAttributeAndValue(String attribute,
			String value) {
		ClientDataGroup extraDataPart = ClientDataGroup.withNameInData("extraDataPart");
		extraDataPart.addAttributeByIdWithValue("type", attribute);
		extraDataPart.addChild(ClientDataAtomic.withNameInDataAndValue("value", value));
		return extraDataPart;
	}
}
