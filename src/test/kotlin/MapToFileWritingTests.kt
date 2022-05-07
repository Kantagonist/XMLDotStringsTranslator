import inputmapcreators.NameContentTuple
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import outputmaptofilewriters.writeMappingToFile
import java.io.File
import kotlin.test.assertEquals

class MapToFileWritingTests {

    companion object {
        @AfterAll
        @JvmStatic
        fun cleanup() {
            val targetXmlFile1 = File("${System.getProperty("user.dir")}/src/test/resources/writeToFileTest.xml")
            val targetXmlFile2 = File( "${System.getProperty("user.dir")}/src/test/resources/writeNewElements.xml")
            if (targetXmlFile1.exists()) {
                targetXmlFile1.delete()
            }
            if (targetXmlFile2.exists()) {
                targetXmlFile2.delete()
            }
        }
    }

    @Test
    fun writeMapToXmlFile() {

        // create input map
        val inputListOfNameContentTuples = listOf(
            NameContentTuple("Lorem_ipsum_dolor", "Lorem ipsum dolor sit "),
            NameContentTuple("agag_agaga_agagag", "aöognaong;agna"),
            NameContentTuple("gren, no sea takimat", "luptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takim"),
            NameContentTuple("do_not_add", "This text should not be transmitted on standard settings")
        )

        // create target file
        val targetFile = File( "${System.getProperty("user.dir")}/src/test/resources/writeToFileTest.xml")
        targetFile.createNewFile()
        targetFile.writeText(
               "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<resources>\n" +
                    "    <string name=\"gren, no sea takimat\">This into the map</string>\n" +
                    "    <string name=\"Lorem_ipsum_dolor\">Hello World</string>\n" +
                    "    <string name=\"agag_agaga_agagag\"> At vero eos et accusam et justo du</string>\n" +
                    "    <string name=\"a_random_string_resource\">Unfrozen pizza shell</string>\n" +
                    "</resources>"
        )

        // create expected file content
        val expectedFileContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "    <string name=\"gren, no sea takimat\">luptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takim</string>\n" +
                "    <string name=\"Lorem_ipsum_dolor\">Lorem ipsum dolor sit </string>\n" +
                "    <string name=\"agag_agaga_agagag\">aöognaong;agna</string>\n" +
                "    <string name=\"a_random_string_resource\">Unfrozen pizza shell</string>\n" +
                "</resources>"

        // generate actual output
        writeMappingToFile(inputListOfNameContentTuples, targetFile.absolutePath)
        val actualFileContent = targetFile.readText()

        // evaluate result
        assertEquals(expectedFileContent, actualFileContent)
    }

    @Test
    fun writeNewElementsToXml() {

        // create input map
        val inputListOfNameContentTuples = listOf(
            NameContentTuple("Lorem_ipsum_dolor", "12346393593"),
            NameContentTuple("the_test_file", "1095185ß51 2424")
        )

        // create target file
        val targetFile = File( "${System.getProperty("user.dir")}/src/test/resources/writeNewElements.xml")
        targetFile.createNewFile()
        targetFile.writeText(
               "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<resources>\n" +
                    "    <string name=\"test_map_to_stay\">This into the map</string>\n" +
                    "    <string name=\"the_string_to_stay\">Hello World</string>\n" +
                    "</resources>"
        )

        // create expected file content
        val expectedFileContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "    <string name=\"test_map_to_stay\">This into the map</string>\n" +
                "    <string name=\"the_string_to_stay\">Hello World</string>\n" +
                "    <string name=\"Lorem_ipsum_dolor\">12346393593</string>\n" +
                "    <string name=\"the_test_file\">1095185ß51 2424</string>\n" +
                "</resources>"

        // generate actual output
        writeMappingToFile(inputListOfNameContentTuples, targetFile.absolutePath, addNewEntries = true)
        val actualFileContent = targetFile.readText()

        // evaluate result
        assertEquals(expectedFileContent, actualFileContent)
    }
}