/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
import inputmapcreators.NameContentTuple
import inputmapcreators.createDotStringsMap
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
            if (xmlInputFile1.exists()) {
                xmlInputFile1.delete()
            }

            val dotStringsInputFile1 = File( "${System.getProperty("user.dir")}/src/test/resources/mapCreationTest.strings")
            if (dotStringsInputFile1.exists()) {
                dotStringsInputFile1.delete()
            }
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

    @Test
    fun dotStringsMapCreation() {
        // set up XML resource file
        val inputFile = File( "${System.getProperty("user.dir")}/src/test/resources/mapCreationTest.strings")
        inputFile.createNewFile()
        inputFile.writeText(
            "\"testing_a_string\" = \"fellow_human_build\"; \"testing\" = \"lorem Ipsum lorem\";\n" +
                 "// hello world, this is ignored\n" +
                 "\"urban build.\" = \"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed di\";\n" +
                 "/* Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed\n" +
                 "diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\n" +
                 " sed diam voluptua. At vero eos et accusam et justo duo dolore \"cameraguy_is_one\" = \"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed di\";\n" +
                 "*/\n" +
                 "\"my_testing\" = \"o eos et accusam et justo duo dolores et ea rebum.\n" +
                 "Stet clita kasd gubergren, no sea ta\";\n" +
                 "\"of_this_them-helllo\" = \"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed di\";\n" +
                 "// test comment again\n" +
                 "\"_amet_consetetur_sad\" = \"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed di\";" +
                 "// \"should be ignored\" = \"Lorem ipsum dolor sit amet, consetetur sa\";"
        )

        // set up expected output
        val expectedOutput = listOf(
            NameContentTuple("testing_a_string", "fellow_human_build"),
            NameContentTuple("testing", "lorem Ipsum lorem"),
            NameContentTuple("urban build.", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed di"),
            NameContentTuple("my_testing", "o eos et accusam et justo duo dolores et ea rebum.\nStet clita kasd gubergren, no sea ta"),
            NameContentTuple("of_this_them-helllo", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed di"),
            NameContentTuple("_amet_consetetur_sad", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed di")
        )

        // run mapping operation
        val actualOutput = createDotStringsMap(inputFile.absolutePath)

        // evaluate results
        assertEquals(expectedOutput, actualOutput)
    }
}