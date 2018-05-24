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
package se.uu.ub.cora.alvin.tocorautils;

import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class CountryCollectionItemConstructor {

	private Map<String, String> rowFromDb;

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

	private void addChildrenToClientDataGroup(ClientDataGroup item) {
		String alpha2 = rowFromDb.get("alpha2code");
		addRecordInfo(alpha2, item);
		addNameInData(alpha2, item);
		addExtraData(alpha2, item);
	}

	private void addRecordInfo(String alpha2code, ClientDataGroup item) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		addId(alpha2code, recordInfo);
		addDataDivider(recordInfo);
		item.addChild(recordInfo);
	}

	private void addId(String alpha2code, ClientDataGroup recordInfo) {
		String id = alpha2code.toLowerCase() + "CountryItem";
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", id));
	}

	private void addDataDivider(ClientDataGroup recordInfo) {
		ClientDataGroup dataDivider = ClientDataGroup.withNameInData("dataDivider");
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId", "bibsys"));
		recordInfo.addChild(dataDivider);
	}

	private void addNameInData(String alpha2, ClientDataGroup item) {
		item.addChild(ClientDataAtomic.withNameInDataAndValue("nameInData", alpha2));
	}

	private void addExtraData(String value, ClientDataGroup item) {
		ClientDataGroup extraData = ClientDataGroup.withNameInData("extraData");
		ClientDataGroup iso2ExtraDataPart = createExtraDataPartWithAttributeAndValue(
				"iso31661Alpha2", value);
		extraData.addChild(iso2ExtraDataPart);
		possiblyAddExtraDataPartWithKeyAndAttribute("alpha3code", "iso31661Alpha3", extraData);
		possiblyAddExtraDataPartWithKeyAndAttribute("numericalcode", "numericalcode", extraData);
		possiblyAddExtraDataPartWithKeyAndAttribute("marccode", "marccode", extraData);
		item.addChild(extraData);
	}

	private void possiblyAddExtraDataPartWithKeyAndAttribute(String key, String attribute,
			ClientDataGroup extraData) {
		if (valueExistsForKey(key)) {
			addExtraDataPartWithAttributeAndValue(key, attribute, extraData);
		}
	}

	private void addExtraDataPartWithAttributeAndValue(String key, String attribute,
			ClientDataGroup extraData) {
		String alpha3 = rowFromDb.get(key);
		ClientDataGroup iso3ExtraDataPart = createExtraDataPartWithAttributeAndValue(attribute,
				alpha3);
		extraData.addChild(iso3ExtraDataPart);
	}

	private boolean valueExistsForKey(String key) {
		return rowFromDb.containsKey(key);
	}

	private ClientDataGroup createExtraDataPartWithAttributeAndValue(String attribute,
			String value) {
		ClientDataGroup extraDataPart = ClientDataGroup.withNameInData("extraDataPart");
		extraDataPart.addAttributeByIdWithValue("type", attribute);
		extraDataPart.addChild(ClientDataAtomic.withNameInDataAndValue("value", value));
		return extraDataPart;
	}

}
