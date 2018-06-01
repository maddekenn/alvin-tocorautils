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
package se.uu.ub.cora.alvin.tocorautils.country;

import static org.testng.Assert.assertEquals;

import javax.naming.NamingException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.ImportResult;
import se.uu.ub.cora.alvin.tocorautils.doubles.FromDbToCoraConverterSpy;
import se.uu.ub.cora.alvin.tocorautils.doubles.ListImporterSpy;
import se.uu.ub.cora.alvin.tocorautils.doubles.RecordReaderFactorySpy;
import se.uu.ub.cora.alvin.tocorautils.doubles.RecordReaderSpy;

public class FromDbToCoraTest {

	private FromDbToCoraConverterSpy toCoraConverter;
	private RecordReaderFactorySpy recordReaderFactory;
	private ListImporterSpy importer;
	private FromDbToCoraImp countryToCora;

	@BeforeMethod
	public void beforeMethod() {
		toCoraConverter = new FromDbToCoraConverterSpy();
		recordReaderFactory = new RecordReaderFactorySpy();
		importer = new ListImporterSpy();
		countryToCora = (FromDbToCoraImp) FromDbToCoraImp
				.usingRecordReaderFactoryAndDbToCoraConverterAndImporter(recordReaderFactory,
						toCoraConverter, importer);
	}

	@Test
	public void testCountryToCora() throws NamingException {
		ImportResult importResult = countryToCora.importFromTable("someTableName");

		RecordReaderSpy factoredReader = recordReaderFactory.factored;
		assertEquals(factoredReader.usedTableName, "someTableName");

		assertEquals(factoredReader.returnedList, toCoraConverter.rowsFromDb);
		assertEquals(toCoraConverter.returnedList2, importer.listOfConvertedRows);

		assertEquals(importResult.listOfFails.get(0), "failure from ListImporterSpy");
	}
}