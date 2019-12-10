package se.uu.ub.cora.alvin.tocorautils.convert;

import static org.testng.Assert.assertEquals;

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class ConverterTestHelpers {

    public static void assertCorrectDataDivider(ClientDataGroup recordInfo) {
        ClientDataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
        String linkedRecordId = dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId");
        assertEquals(linkedRecordId, "bibsys");
        String linkedRecordType = dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType");
        assertEquals(linkedRecordType, "system");
    }

    public static void assertNoEnglishTextPart(ClientDataGroup text) {
        ClientDataAttribute type = ClientDataAttribute.withNameInDataAndValue("type",
                "alternative");
        ClientDataAttribute lang = ClientDataAttribute.withNameInDataAndValue("lang", "en");
        List<ClientDataGroup> textParts = (List<ClientDataGroup>) text
                .getAllGroupsWithNameInDataAndAttributes("textPart", type, lang);
        assertEquals(textParts.size(), 0);
    }

    public static void assertCorrectSwedishTextPart(ClientDataGroup text, String textValue) {
        ClientDataAttribute type = ClientDataAttribute.withNameInDataAndValue("type", "default");
        ClientDataAttribute lang = ClientDataAttribute.withNameInDataAndValue("lang", "sv");
        List<ClientDataGroup> textParts = (List<ClientDataGroup>) text
                .getAllGroupsWithNameInDataAndAttributes("textPart", type, lang);
        ClientDataGroup svTextPart = textParts.get(0);
        assertEquals(svTextPart.getFirstAtomicValueWithNameInData("text"), textValue);
    }

    public static void assertCorrectEnglishTextPart(ClientDataGroup text, String textValue) {
        ClientDataAttribute type = ClientDataAttribute.withNameInDataAndValue("type",
                "alternative");
        ClientDataAttribute lang = ClientDataAttribute.withNameInDataAndValue("lang", "en");
        List<ClientDataGroup> textParts = (List<ClientDataGroup>) text
                .getAllGroupsWithNameInDataAndAttributes("textPart", type, lang);
        ClientDataGroup svTextPart = textParts.get(0);
        assertEquals(svTextPart.getFirstAtomicValueWithNameInData("text"), textValue);
    }
}
