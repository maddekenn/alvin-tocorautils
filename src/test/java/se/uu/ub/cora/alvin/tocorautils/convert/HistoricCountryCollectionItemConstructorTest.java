package se.uu.ub.cora.alvin.tocorautils.convert;

import static org.testng.Assert.assertEquals;
import static se.uu.ub.cora.alvin.tocorautils.convert.ConverterTestHelpers.assertCorrectDataDivider;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorautils.DbRowException;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class HistoricCountryCollectionItemConstructorTest {
	private Map<String, Object> rowFromDb;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("code", "duchy_of_saxe-coburg-meiningen");
		rowFromDb.put("svText", "Hertigdömet Sachsen-Coburg-Meiningen");
		rowFromDb.put("enText", "Duchy of Saxe-Coburg-Meiningen");
	}

	@Test
	public void testConvertHistoricCountry() {
		CollectionItemConstructor historicCountryItemConstructor = new HistoricCountryCollectionItemConstructor();
		ClientDataGroup historicCountryItem = historicCountryItemConstructor.convert(rowFromDb);
		assertEquals(historicCountryItem.getNameInData(), "metadata");
		assertEquals(historicCountryItem.getAttributes().get("type"), "collectionItem");

		ClientDataGroup recordInfo = historicCountryItem.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"),
				"duchyOfSaxeCoburgMeiningenHistoricCountryItem");
		assertCorrectDataDivider(recordInfo);
		assertEquals(historicCountryItem.getFirstAtomicValueWithNameInData("nameInData"),
				"duchyOfSaxeCoburgMeiningen");
	}

	@Test(expectedExceptions = DbRowException.class, expectedExceptionsMessageRegExp = "Could not find \"code\"")
	public void testConvertHistoricCountryWithoutCodeKey() {
		rowFromDb.remove("code");
		CollectionItemConstructor historicCountryItemConstructor = new HistoricCountryCollectionItemConstructor();
		historicCountryItemConstructor.convert(rowFromDb);
	}

	@Test(expectedExceptions = DbRowException.class)
	public void testConvertHistoricCountryWithoutCodeValue() {
		rowFromDb.replace("code", "");
		CollectionItemConstructor historicCountryItemConstructor = new HistoricCountryCollectionItemConstructor();
		historicCountryItemConstructor.convert(rowFromDb);
	}
}
