import configbuilder.ConfigBuilder
import configbuilder.Translation
import configbuilder.VirtualConfigFile
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class YamlConfigReadingTest {

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
        val actualConfig = ConfigBuilder.readConfig(path)

        // checking results
        assertEquals(expectedConfig, actualConfig)
    }
}