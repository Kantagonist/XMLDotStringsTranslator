/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
import configbuilder.Translation
import configbuilder.VirtualConfigFile
import configbuilder.readConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class YamlConfigReadingTests {

    companion object {

        @AfterAll
        @JvmStatic
        fun cleanUp() {
            val inputFile = File("${System.getProperty("user.dir")}/src/test/resources/MappingConfig.yaml")
            if (inputFile.exists()) {
                inputFile.delete()
            }
            val inputFile2 = File("${System.getProperty("user.dir")}/src/test/resources/RootPath.yaml")
            if (inputFile2.exists()) {
                inputFile2.delete()
            }
        }
    }

    @Test
    fun testYamlRootPathBuilding() {
        val inputFilePath = "${System.getProperty("user.dir")}/src/test/resources/RootPath.yaml"
        val inputFile = File(inputFilePath)
        inputFile.createNewFile()
        inputFile.writeText(
            "rootPath: \"hello/world\"\n" +
                    "translations:\n" +
                    "  - from:\n" +
                    "    to:\n"
        )

        // expected output
        val expectedConfig = VirtualConfigFile(
            rootPath = "${System.getProperty("user.dir")}/src/test/resources/hello/world",
            translations = listOf(
                Translation(
                    from = null,
                    to = null
                )
            )
        )

        // read in config
        val actualConfig = readConfig(inputFilePath)

        // checking results
        assertEquals(expectedConfig, actualConfig)
    }

    @Test
    fun testYamlReading() {

        // create input file
        val inputFilePath = "${System.getProperty("user.dir")}/src/test/resources/MappingConfig.yaml"
        val inputFile = File(inputFilePath)
        inputFile.createNewFile()
        inputFile.writeText(
            "# paths relative to this yaml file\n" +
                    "rootPath: \".\"\n" +
                    "translations:\n" +
                    "  - from:\n" +
                    "      - \"ThisFile.xml\"\n" +
                    "      - \"ThisOtherFile.xml\"\n" +
                    "    to:\n" +
                    "      - \"NewTestFile.strings\"\n" +
                    "      - \"OtherTestFile.strings\"\n" +
                    "  - from:\n" +
                    "      - \"MyFile.xml\"\n" +
                    "      - \"MyOtherFile.xml\"\n" +
                    "    to:\n" +
                    "      - \"MyTestFile.strings\"\n" +
                    "      - \"MyOtherTestFile.strings\""
        )

        // expected output
        val expectedConfig = VirtualConfigFile(
            rootPath = "${System.getProperty("user.dir")}/src/test/resources/.",
            translations = listOf(
                Translation(
                    from = listOf(
                        "ThisFile.xml",
                        "ThisOtherFile.xml"
                    ),
                    to = listOf(
                        "NewTestFile.strings",
                        "OtherTestFile.strings"
                    )
                ),
                Translation(
                    from = listOf(
                        "MyFile.xml",
                        "MyOtherFile.xml"
                    ),
                    to = listOf(
                        "MyTestFile.strings",
                        "MyOtherTestFile.strings"
                    )
                )
            )
        )

        // read in config
        val actualConfig = readConfig(inputFilePath)

        // checking results
        assertEquals(expectedConfig, actualConfig)
    }
}