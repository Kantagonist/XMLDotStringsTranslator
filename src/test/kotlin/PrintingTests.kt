/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue


/**
 * Uses the example files in the debugPrintingTestSet folder.
 * Tests the debug printing utility.
 */
class PrintingTests {

    private val targetXmlAbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/debugPrintingTestSet/target.xml"
    private lateinit var targetXmlContent: String
    private val targetDotStringsAbsolutePath = "${System.getProperty("user.dir")}/src/test/resources/debugPrintingTestSet/target.strings"
    private lateinit var targetDotStringsContent: String

    @BeforeEach
    fun fileInit() {
        targetXmlContent = File(targetXmlAbsolutePath).readText()
        targetDotStringsContent = File(targetDotStringsAbsolutePath).readText()
    }

    /**
     * Resets the files in the debugPrintingTestSet folder to base settings.
     */
    @AfterEach
    fun cleanup() {
        val targetXmlFile = File(targetXmlAbsolutePath)
        if (!targetXmlFile.exists()) {
            targetXmlFile.createNewFile()
        }
        targetXmlFile.writeText(targetXmlContent)
        val targetDotStringFile = File(targetDotStringsAbsolutePath)
        if (!targetDotStringFile.exists()) {
            targetDotStringFile.createNewFile()
        }
        targetDotStringFile.writeText(targetDotStringsContent)
    }

    @Test
    fun printingTestWithoutAddingOfNewLines() {

        // init
        val configFilePath = "${System.getProperty("user.dir")}/src/test/resources/debugPrintingTestSet/XMLDotStringsConfig.yaml"

        // create expected output
        val expectedPrintOutput = "translation: 1\n" +
                "\tInput Data:\n" +
                "\t\tinput.xml\n" +
                "\t\t\tmove_to_standard\n" +
                "\t\t\tsecond test\n" +
                "\t\t\tfirst_test\n" +
                "\t\t\tsome_string\n" +
                "\t\n" +
                "\t\tinput.strings\n" +
                "\t\t\tnew_string_in_here\n" +
                "\t\t\tfirst_test\n" +
                "\t\t\ttaifun_build\n" +
                "\t\n" +
                "\tUnmoved Data:\n" +
                "\t\ttarget.xml\n" +
                "\t\t\tmy_non_moved_string\n" +
                "\t\n" +
                "\tMoved Data:\n" +
                "\t\ttarget.xml\n" +
                "\t\t\tmove_to_standard\n" +
                "\t\t\tsecond test\n" +
                "\t\t\tfirst_test"

        // main method run
        main(arrayOf("--debug-mode", "--config", configFilePath))

        // evaluates if the system's console prints
        assertTrue(getDebugMessage().contains(expectedPrintOutput))
    }

    @Test
    fun printingTestWithAddingOfNewLines() {

        // init
        val configFilePath = "${System.getProperty("user.dir")}/src/test/resources/debugPrintingTestSet/CustomConfig.yaml"

        // create expected output
        val expectedPrintOutput = "translation: 1\n" +
                "\tInput Data:\n" +
                "\t\tinput.xml\n" +
                "\t\t\tmove_to_standard\n" +
                "\t\t\tsecond test\n" +
                "\t\t\tfirst_test\n" +
                "\t\t\tsome_string\n" +
                "\t\n" +
                "\t\tinput.strings\n" +
                "\t\t\tnew_string_in_here\n" +
                "\t\t\tfirst_test\n" +
                "\t\t\ttaifun_build\n" +
                "\t\n" +
                "\tUnmoved Data:\n" +
                "\t\ttarget.strings\n" +
                "\t\n" +
                "\tMoved Data:\n" +
                "\t\ttarget.strings\n" +
                "\t\t\tmove_to_standard\n" +
                "\t\t\tsecond test\n" +
                "\t\t\tnew_string_in_here\n" +
                "\t\t\tfirst_test\n" +
                "\t\t\ttaifun_build\n" +
                "\t\t\tsome_string"

        // main method run
        main(arrayOf("--debug-mode", "--add-new-entries", "--config", configFilePath))

        // evaluates if the system's console prints
        assertTrue(getDebugMessage().contains(expectedPrintOutput))
    }
}