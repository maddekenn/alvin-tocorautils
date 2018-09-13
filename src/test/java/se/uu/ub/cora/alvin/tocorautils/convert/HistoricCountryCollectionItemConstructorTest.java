package se.uu.ub.cora.alvin.tocorautils.convert;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static se.uu.ub.cora.alvin.tocorautils.convert.ConverterTestHelpers.assertCorrectDataDivider;

public class HistoricCountryCollectionItemConstructorTest {
    private Map<String, String> rowFromDb;

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
        assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "duchyOfSaxeCoburgMeiningenHistoricCountryItem");
        assertCorrectDataDivider(recordInfo);
        assertEquals(historicCountryItem.getFirstAtomicValueWithNameInData("nameInData"), "duchyOfSaxeCoburgMeiningen");
    }

}
