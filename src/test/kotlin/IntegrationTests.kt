/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

/**
 * Uses the example files in the integrationTestSet folder to test the main method.
 * It does not test edge cases in file formatting and such.
 * That is done in the other tests, specified to each module.
 *
 * In the other tests, the program creates semi-virtual files.
 * In these tests, the program uses real files and resets them.
 * That way, the files in integrationTestSet can be used ot exemplify the program's usage in a simple way.
 */
class IntegrationTests {

    private lateinit var targetXml1AbsolutePath: String
    private lateinit var targetXml1Content: String
    private lateinit var targetXml2AbsolutePath: String
    private lateinit var targetXml2Content: String
    private lateinit var targetDotStringsAbsolutePath: String
    private lateinit var targetDotStringsContent: String

    @BeforeEach
    fun fileInit() {
        targetXml1AbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/target1.xml"
        targetXml1Content = File(targetXml1AbsolutePath).readText()
        targetXml2AbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/target2.xml"
        targetXml2Content = File(targetXml2AbsolutePath).readText()
        targetDotStringsAbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/target.strings"
        targetDotStringsContent = File(targetDotStringsAbsolutePath).readText()
    }

    /**
     * Resets the files in the integrationTestSet folder to base settings.
     */
    @AfterEach
    fun cleanup() {
        val targetXml1File = File(targetXml1AbsolutePath)
        if (!targetXml1File.exists()) {
            targetXml1File.createNewFile()
        }
        targetXml1File.writeText(targetXml1Content)
        val targetXml2File = File(targetXml2AbsolutePath)
        if (!targetXml2File.exists()) {
            targetXml2File.createNewFile()
        }
        targetXml2File.writeText(targetXml2Content)
        val targetDotStringsFile = File(targetDotStringsAbsolutePath)
        if (!targetDotStringsFile.exists()) {
            targetDotStringsFile.createNewFile()
        }
        targetDotStringsFile.writeText(targetDotStringsContent)
    }

    @Test
    fun integrationTestForFirstTranslationInConfig() {

        // init
        val targetXmlFile1 = File(targetXml1AbsolutePath)
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
        translateViaXDST(arrayOf("--debug-mode", "--config", configFilePath))

        // evaluation
        assertEquals(expectedXmlTarget1Output, targetXmlFile1.readText())
    }

    @Test
    fun integrationTestWithSecondTranslationXmlOutput() {

        // init
        val targetXml2File = File(targetXml2AbsolutePath)
        val configFilePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/XMLDotStringsConfig.yaml"

        // create expected output
        val expectedXmlTarget2Output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<resources>\n" +
                "    <string name=\"first_test\">This should be transferred</string>\n" +
                "    <string name=\"second test\">Hello World</string>\n" +
                "    <string name=\"new_string_in_here\">an old string in here</string>\n" +
                "    <string name=\"taifun_build\">so far all is quiet</string>\n" +
                "</resources>"

        // main method run
        translateViaXDST(arrayOf("--debug-mode", "--config", configFilePath))

        // evaluation
        assertEquals(expectedXmlTarget2Output, targetXml2File.readText())
    }

    @Test
    fun integrationTestWithSecondTranslationDotStringsOutput() {

        // init
        val targetDotStringsTargetFile = File(targetDotStringsAbsolutePath)
        val configFilePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/XMLDotStringsConfig.yaml"

        // create expected output
        val expectedDotStringsTargetFileOutput = "\"first_test\" = \"This should be transferred\";\n" +
                "\"second test\" = \"Hello World\";\n" +
                "\"some_string\" = \"some Text !\";"

        // main method run
        translateViaXDST(arrayOf("--debug-mode", "--config", configFilePath))

        // evaluation
        assertEquals(expectedDotStringsTargetFileOutput, targetDotStringsTargetFile.readText())
    }
}