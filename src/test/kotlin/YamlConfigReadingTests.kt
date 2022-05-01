import configbuilder.Translation
import configbuilder.VirtualConfigFile
import configbuilder.readConfig
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class YamlConfigReadingTests {

    @Test
    fun testFileExistence() {
        val path = "${System.getProperty("user.dir")}/src/test/resources/XmlDotStringConfig.yaml"
        assert(File(path).exists())
    }

    @Test
    fun testYamlReading() {
        // expected output
        val expectedConfig = VirtualConfigFile(
            rootPath = "${System.getProperty("user.dir")}/src/test/resources/XmlDotStringConfig.yaml",
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

        // testing
        val path = "${System.getProperty("user.dir")}/src/test/resources/XmlDotStringConfig.yaml"
        val actualConfig = readConfig(path)

        // checking results
        assertEquals(expectedConfig, actualConfig)
    }
}