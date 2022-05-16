import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import java.io.File

class IntegrationTests {

    companion object {
        lateinit var targetXmlAbsolutePath: String
        lateinit var targetXmlContent: String
        lateinit var targetDotStringsAbsolutePath: String
        lateinit var targetDotStringsContent: String

        /**
         * Resets the files in the integrationTestSet folder to base settings.
         * In the other tests, the program creates semi-virtual files.
         * In this test, the program uses real files and resets them.
         * That way, the files in integrationTestSet can be used ot exemplify the program's usage in a simple way.
         */
        @AfterAll
        @JvmStatic
        fun cleanup() {
            val targetXmlFile = File(targetXmlAbsolutePath)
            if (!targetXmlFile.exists()) {
                targetXmlFile.createNewFile()
            }
            targetXmlFile.writeText(targetXmlContent)
            val targetDotStringsFile = File(targetDotStringsAbsolutePath)
            if (!targetDotStringsFile.exists()) {
                targetDotStringsFile.createNewFile()
            }
            targetDotStringsFile.writeText(targetDotStringsContent)
        }
    }

    /**
     * Uses the example files in the integrationTestSet folder to test the main method.
     * It does not test edge cases in file formatting and such.
     * That is done in the other tests, specified to each module.
     */
    @Test
    fun integrationTestWithPresetFiles() {

        // init for reset
        targetXmlAbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/target.xml"
        targetXmlContent = File(targetXmlAbsolutePath).readText()
        targetDotStringsAbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/target.strings"
        targetDotStringsContent = File(targetDotStringsAbsolutePath).readText()

        // config setup
        val configFilePath = "${System.getProperty("user.dir")}/src/test/resources/integrationTestSet/XMLDotStringsConfig.yaml"

        // main method run
        main(arrayOf("--config", configFilePath))

        // evaluation
    }
}