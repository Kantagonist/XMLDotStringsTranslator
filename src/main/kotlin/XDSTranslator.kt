/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
import configbuilder.readConfig
import inputmapcreators.NameContentTuple
import inputmapcreators.createDotStringsMap
import inputmapcreators.createXmlMap
import outputmaptofilewriters.writeMappingToDotStringsFile
import outputmaptofilewriters.writeMappingToXmlFile
import utility.StateObserver
import utility.mergeMappings
import utility.VERSION_NUMBER_EXTRACTION_PATTERN
import java.io.File
import kotlin.system.exitProcess

/**
 * Entry function in a program, just add the arguments as strings.
 * Use the --help command to see what the program offers.
 */
fun translateViaXDST(args: Array<String>) {

    // print Greeting
    println("\nWelcome to the XMl and Dot String resource transporter.\n" +
            "-------------------------------------------------------\n")

    try {
        // evaluate args
        resetRunValues()
        var shouldContinue = true
        val iterator = args.iterator()
        while (iterator.hasNext()) {
            var externalArgument = iterator.next()
            val argument = ARGUMENTS.values().find {
                it.pureCommand == externalArgument
            }
            if (argument != null) {
                if (argument.allowsExtraString) {
                    externalArgument = iterator.next()
                }
                argument.extraRun(externalArgument)
                if (!argument.continueAfter) {
                    shouldContinue = false
                    break
                }


            // illegal argument exception
            } else {
                throw DotStringsTranslatorException("[ILLEGAL ARGUMENT]", "the argument $externalArgument is not allowed, please use the flag \n" +
                        "\n" +
                        "\t${ARGUMENTS.HELP.pureCommand}\n" +
                        "\nfor further information")
            }
        }

        // early stop
        if (shouldContinue.not()) {
            if (debugMode) return
            exitProcess(0)
        }

        // decide between relative and absolute path
        if (configFilePath.startsWith("/").not()) {
            configFilePath = "${System.getProperty("user.dir")}/$configFilePath"
        }
        val virtualConfig = readConfig(configFilePath)

        // handle translations
        var translationDebugPrintouts = ""
        var translationId = 0
        virtualConfig.translations?.forEach {

            // StateObserver init
            translationId++
            StateObserver.reset()

            // read mappings
            val inputMappingsList = mutableListOf<List<NameContentTuple>>()
            it.from?.forEach {from ->
                val map = if (from.endsWith(".xml")) {
                    createXmlMap("${virtualConfig.rootPath}/$from")
                } else if (from.endsWith(".strings")) {
                    createDotStringsMap("${virtualConfig.rootPath}/$from")
                } else {
                    throw DotStringsTranslatorException(
                        "[ILLEGAL CONFIG]",
                        "from: $from is not a valid .xml or .strings file, please check your yaml config."
                    )
                }
                for (entry in map) {
                    StateObserver.addInputData(from, entry.name)
                }
                inputMappingsList.add(map)
            }

            // merge lists
            val mergedInputs = mergeMappings(inputMappingsList)

            // write mappings into file
            it.to?.forEach {to ->
                val outputMap = if (to.endsWith(".xml")) {
                    writeMappingToXmlFile(mergedInputs, "${virtualConfig.rootPath}/$to", addNewEntries)
                } else if (to.endsWith(".strings")) {
                    writeMappingToDotStringsFile(mergedInputs, "${virtualConfig.rootPath}/$to", addNewEntries)
                } else {
                    throw DotStringsTranslatorException(
                        "[ILLEGAL CONFIG]",
                        "to: $to is not a valid .xml or .strings file, please check your yaml config."
                    )
                }
                for (entry in outputMap[0]) {
                    StateObserver.addUnmovedData(to, entry)
                }
                for (entry in outputMap[1]) {
                    StateObserver.moveData(to, entry)
                }
            }

            // Save debug printouts for this translation
            translationDebugPrintouts += "\ntranslation: $translationId\n"
            StateObserver.toString().lines().forEach { line ->
                translationDebugPrintouts += "\t$line\n"
            }

        }
        if (debugMode.not()) {
            exitProcess(0)
        } else {
            debugMessage = translationDebugPrintouts
            println(translationDebugPrintouts)
        }


    // prints custom errors
    } catch (e: DotStringsTranslatorException) {
        println("${e.tag} ${e.message}")
        if (debugMode.not()) {
            exitProcess(1)
        }
    }
}

/**
 * Used to reset the values for the arguments before evaluation.
 */
private fun resetRunValues() {
    configFilePath = "XMLDotStringConfig.yaml"
    addNewEntries = false
    debugMode = false
    debugMessage = ""
}

private var configFilePath = "XMLDotStringConfig.yaml"
private var addNewEntries = false
private var debugMode = false
private var debugMessage = ""

/**
 * Public custom getter for debugging and tests.
 */
fun getDebugMessage(): String {
    return debugMessage
}

/**
 * Keeps track of legal arguments and contains a lambda which is supposed to be fired everytime this is called.
 * Illegal Arguments are ignored.
 */
private enum class ARGUMENTS(
    val pureCommand: String,
    val continueAfter: Boolean,
    val allowsExtraString: Boolean,
    val quickDescription: String,
    val extraRun: (String?) -> Unit
) {
    HELP(
        "--help",
        false,
        false,
        "a quick help guide",
        {
            println(
                "For an in-depth tutorial on how to use this software, visit\n\n" +
                "\thttps://github.com/Kantagonist/XMLDotStringsTranslator/README.md\n\n" +
                "This software depends on an input .yaml file, which should be in your current directory and named XMLDotStringConfig.yaml.\n" +
                "To set a different config Yaml file for this run, add the flag \n\n\t${CONFIG.pureCommand} /path/to/YourFileName.yaml\n\n" +
                "The program allows for a certain set of extra config flags\n"
            )

            /*
             * calculates distance between command names to
             * display the description all on the same height for a cleaner look.
             * Tab use is too discordant for this
             */
            var offset = 0
            for (entry in ARGUMENTS.values()) {
               if (entry.pureCommand.length > offset) offset = entry.pureCommand.length
            }
            offset += 5
            var print = "The following flags are allowed:\n"
            for (entry in ARGUMENTS.values()) {
                print += "\n\t${entry.pureCommand}"
                for (i in 1..(offset - entry.pureCommand.length)) {
                    print += " "
                }
                print += "[${entry.quickDescription}]"
            }
            println("$print\n")
        }
    ),
    ADD_NEW_ENTRIES(
        "--add-new-entries",
        true,
        false,
        "Add entries into target files, which did not exist before",
        {
            addNewEntries = true
            println("\n[ADDITIONAL MODE] elected to add new entries in each output file\n")
        }
    ),
    CONFIG(
        "--config",
    true,
        true,
        "Set a different config Yaml file for this translation",
        {
            it?.let {
                configFilePath = it
            }
        }
    ),
    DEBUG_MODE(
        "--debug-mode",
        true,
        false,
        "Enable debug console messages and print which changes were applied",
        {
            debugMode = true
            println("Debug Mode selected.")
        }
    ),
    VERSION(
        "--version",
        false,
        false,
        "Print the current version number to the console",
        {
            println("XmlDotStringsTranslator\n\nVersion: ${getVersion()}")
        }
    )
}

/**
 * Reads the version as a String for debugging purposes.
 *
 * @return the version number as a String
 */
private fun getVersion(): String {

    // read file
    val versionFilePath = "${System.getProperty("user.dir")}/src/main/resources/version.gradle"
    val versionFileContent = File(versionFilePath).readText()

    // extract number
    val versionRegex = Regex(VERSION_NUMBER_EXTRACTION_PATTERN, RegexOption.DOT_MATCHES_ALL)
    val captureGroupValues = versionRegex.find(versionFileContent, 0)?.groupValues
        ?: throw DotStringsTranslatorException("[FORMAT ERROR]", "Tried and failed to extract version number from resources/version.gradle")

    return captureGroupValues[1]
}