import inputmapcreators.NameContentTuple
import inputmapcreators.createXmlMap
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class MapCreationTests {

    companion object {
        @AfterAll
        @JvmStatic
        fun cleanup() {
            val xmlInputFile1 = File("${System.getProperty("user.dir")}/src/test/resources/mapCreationTest.xml")
            xmlInputFile1.delete()
        }
    }

    @Test
    fun xmlMapCreation() {
        // set up XML resource file
        val inputFile = File( "${System.getProperty("user.dir")}/src/test/resources/mapCreationTest.xml")
        inputFile.createNewFile()
        inputFile.writeText(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<resources>\n" +
                    "\t<string name=\"test_string_1\">This into the map</string>\n" +
                    "\t<string name=\"test_string_2\">Hello World</string>\n" +
                    "\t<string name=\"test_string_3\">A world free of problems</string>\n" +
            "</resources>"
        )

        // set up expected output
        val expectedOutput = listOf(
            NameContentTuple("test_string_1", "This into the map"),
            NameContentTuple("test_string_2", "Hello World"),
            NameContentTuple("test_string_3", "A world free of problems")
        )

        // run mapping operation
        val actualOutput = createXmlMap(inputFile.absolutePath)

        // evaluate results
        assertEquals(expectedOutput, actualOutput)
    }
}