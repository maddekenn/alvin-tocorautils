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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.log.LoggerFactorySpy;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.logger.LoggerProvider;

public class HistoricItemUpdaterTest {
	String appTokenVerifierURL = "http://appTokenVerifierURL";
	String baseURL = "http://baseURL";
	String userId = "someUserId";
	String appToken = "someApptoken";
	private CoraClientSpy coraClientSpy;
	private ClientUpdater historicItemUpdater;
	private LoggerFactorySpy loggerFactory;
	private String testedClassname = "HistoricItemUpdater";

	@BeforeMethod
	public void beforeMethod() {
		loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
		coraClientSpy = new CoraClientSpy();
		historicItemUpdater = HistoricItemUpdater.usingCoraClient(coraClientSpy);
	}

	@Test
	public void testGivenCoraClientIsUsed() throws Exception {
		historicItemUpdater.update();
		assertCorrectRead();
		assertEquals(coraClientSpy.readAsRecordRecordId.size(), 4);
	}

	@Test
	public void testLogging() throws Exception {
		assertEquals(loggerFactory.getNoOfInfoLogMessagesUsingClassname(testedClassname), 0);
		historicItemUpdater.update();
		assertEquals(loggerFactory.getNoOfInfoLogMessagesUsingClassname(testedClassname), 5);
		assertEquals(loggerFactory.getInfoLogMessageUsingClassNameAndNo(testedClassname, 1),
				"Changing: gaul to:gaul");
		assertEquals(loggerFactory.getInfoLogMessageUsingClassNameAndNo(testedClassname, 2),
				"Changing: romanRepublic to:roman_republic");
		assertEquals(loggerFactory.getInfoLogMessageUsingClassNameAndNo(testedClassname, 3),
				"Changing: kingdomOfTheNorth to:kingdom_of_the_north");

		assertEquals(loggerFactory.getInfoLogMessageUsingClassNameAndNo(testedClassname,
				calculateLastInfoLogNumber()), "....finished update of historicItems!");
	}

	private int calculateLastInfoLogNumber() {
		return loggerFactory.getNoOfInfoLogMessagesUsingClassname(testedClassname) - 1;
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
		historicItemUpdater.update();
		assertCorrectRead();
		assertEquals(coraClientSpy.readAsRecordRecordId.get(4), "extraLastItemHistoricCountryItem");
		assertEquals(coraClientSpy.readAsRecordRecordId.size(), 5);
	}

	@Test
	public void testUpdate() {
		historicItemUpdater.update();
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
		historicItemUpdater.update();
		ClientDataGroup updatedItem = coraClientSpy.dataGroupsSentToUpdate.get(0);
		String code = updatedItem.getFirstAtomicValueWithNameInData("nameInData");
		assertEquals(code, "gaul");
	}

	@Test
	public void testTransformationOCodeCamelCase() throws Exception {
		historicItemUpdater.update();
		ClientDataGroup updatedItem = coraClientSpy.dataGroupsSentToUpdate.get(1);
		String code = updatedItem.getFirstAtomicValueWithNameInData("nameInData");
		assertEquals(code, "roman_republic");
	}

	@Test
	public void testTransformationOCodeCamelCaseMultiple() throws Exception {
		historicItemUpdater.update();
		ClientDataGroup updatedItem = coraClientSpy.dataGroupsSentToUpdate.get(2);
		String code = updatedItem.getFirstAtomicValueWithNameInData("nameInData");
		assertEquals(code, "kingdom_of_the_north");
	}
}
