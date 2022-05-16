import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class IntegrationTests {

    lateinit var targetXml1AbsolutePath: String
    lateinit var targetXmlContent: String
    lateinit var targetDotStringsAbsolutePath: String
    lateinit var targetDotStringsContent: String

    /**
     * Resets the files in the integrationTestSet folder to base settings.
     * In the other tests, the program creates semi-virtual files.
     * In this test, the program uses real files and resets them.
     * That way, the files in integrationTestSet can be used ot exemplify the program's usage in a simple way.
     */
    @AfterEach
    fun cleanup() {
        val targetXml1File = File(targetXml1AbsolutePath)
        if (!targetXml1File.exists()) {
            targetXml1File.createNewFile()
        }
        targetXml1File.writeText(targetXmlContent)
        val targetDotStringsFile = File(targetDotStringsAbsolutePath)
        if (!targetDotStringsFile.exists()) {
            targetDotStringsFile.createNewFile()
        }
        targetDotStringsFile.writeText(targetDotStringsContent)
    }

    /**
     * Uses the example files in the integrationTestSet folder to test the main method.
     * It does not test edge cases in file formatting and such.
     * That is done in the other tests, specified to each module.
     */
    @Test
    fun integrationTestForFirstTranslationInConfig() {

        // init for reset
        targetXml1AbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/target1.xml"
        val targetXmlFile1 = File(targetXml1AbsolutePath)
        targetXmlContent = targetXmlFile1.readText()
        targetDotStringsAbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/target.strings"
        targetDotStringsContent = File(targetDotStringsAbsolutePath).readText()

        // config setup
        val configFilePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/XMLDotStringsConfig.yaml"

        // create expected output
        val expectedXmlTarget1Output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<resources>\n" +
                "    <string name=\"first_test\">This should be transferred</string>\n" +
                "    <string name=\"second test\">Hello World</string>\n" +
                "    <string name=\"new_string_in_here\">a new string in here</string>\n" +
                "    <string name=\"taifun_build\">a build-in storm</string>\n" +
                "</resources>"

        // main method run
        main(arrayOf("--debug-mode", "--config", configFilePath))

        // evaluation
        assertEquals(expectedXmlTarget1Output, targetXmlFile1.readText())
    }
}