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

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class LanguageCollectionItemConstructor extends CollectionItemConstructor {

	@Override
	protected String getSuffix() {
		return "";
	}

	@Override
	protected String getId() {
		return rowFromDb.get("alpha3code").trim();
	}

	@Override
	protected void addId(String idPrefix, ClientDataGroup recordInfo) {
		String id = idPrefix.toLowerCase() + "LanguageItem";
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("id", id));

	}

	@Override
	protected void addExtraData(String value, ClientDataGroup item) {
		ClientDataGroup extraData = ClientDataGroup.withNameInData("extraData");
		ClientDataGroup iso3ExtraDataPart = createExtraDataPartWithAttributeAndValue("iso639Alpha3",
				value);
		extraData.addChild(iso3ExtraDataPart);
		possiblyAddExtraDataPartWithKeyAndAttribute("alpha2code", "iso639Alpha2", extraData);
		item.addChild(extraData);

	}
}
