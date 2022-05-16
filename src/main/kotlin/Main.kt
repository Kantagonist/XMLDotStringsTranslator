import configbuilder.readConfig
import inputmapcreators.NameContentTuple
import inputmapcreators.createDotStringsMap
import inputmapcreators.createXmlMap
import outputmaptofilewriters.writeMappingToDotStringsFile
import outputmaptofilewriters.writeMappingToXmlFile
import utility.mergeMappings
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    try {
        // evaluate args
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
                throw DotStringsTranslatorException("[ILLEGAL ARGUMENT]", "the argument $externalArgument is not allowed, please use \n" +
                        "\n" +
                        "\t${ARGUMENTS.LIST_ARGUMENTS.pureCommand}\n" +
                        "\nfor further information")
            }
        }

        // early stop
        if (shouldContinue.not()) {
            exitProcess(0)
        }

        // decide between relative and absolute path
        if (configFilePath.startsWith("/").not()) {
            configFilePath = "${System.getProperty("user.dir")}/$configFilePath"
        }
        val virtualConfig = readConfig(configFilePath)

        // handle translations
        virtualConfig.translations?.forEach {

            // read mappings
            val inputMappingsList = mutableListOf<List<NameContentTuple>>()
            it.from?.forEach {from ->
                if (from.endsWith(".xml")) {
                    inputMappingsList.add(createXmlMap("${virtualConfig.rootPath}/$from"))
                } else if (from.endsWith(".strings")) {
                    inputMappingsList.add(createDotStringsMap("${virtualConfig.rootPath}/$from"))
                } else {
                    throw DotStringsTranslatorException(
                        "[ILLEGAL CONFIG]",
                        "from: $from is not a valid .xml or .strings file, please check your yaml config."
                    )
                }
            }

            // merge lists
            val mergedInputs = mergeMappings(inputMappingsList)

            // write mappings into file
            it.to?.forEach {to ->
                if (to.endsWith(".xml")) {
                    writeMappingToXmlFile(mergedInputs, "${virtualConfig.rootPath}/$to", addNewEntries)
                } else if (to.endsWith(".strings")) {
                    writeMappingToDotStringsFile(mergedInputs, "${virtualConfig.rootPath}/$to", addNewEntries)
                } else {
                    throw DotStringsTranslatorException(
                        "[ILLEGAL CONFIG]",
                        "to: $to is not a valid .xml or .strings file, please check your yaml config."
                    )
                }
            }
        }
        exitProcess(0)


    // prints custom errors
    } catch (e: DotStringsTranslatorException) {
        println("${e.tag} ${e.message}")
        exitProcess(1)
    }
}

private var configFilePath = "XMLDotStringConfig.yaml"
private var addNewEntries = false

/**
 * Keeps track of legal arguments and contains a lambda which is supposed to be fired everytime this is called.
 * Illegal Arguments are ignored.
 */
private enum class ARGUMENTS(
    val pureCommand: String,
    val continueAfter: Boolean,
    val allowsExtraString: Boolean,
    val extraRun: (String?) -> Unit
) {
    HELP(
        "--help",
        false,
        false,
        {
            println(
                "Welcome to the XMl and Dot String resource transporter.\n" +
                        "For an in-depth tutorial on how to use this software, visit\n\n" +
                        "\thttps://github.com/Kantagonist/XMLDotStringsTranslator/README.md" +
                        "\n\nTo list all available commands, please call\n\n\t${LIST_ARGUMENTS.pureCommand}\n\n" +
                        "This software depends on an input .yaml file, which should be in your current directory and named XMLDotStringConfig.yaml.\n" +
                        "To set a different one, call \n\n\t${CONFIG.pureCommand} /path/to/YourFileName.config\n\n"
            )
        }
    ),
    ADD_NEW_ENTRIES(
        "--add-new-entries",
        true,
        false,
        {
            addNewEntries = true
            println("\n[ADDITIONAL MODE] elected to add new entries in each output file\n")
        }
    ),
    LIST_ARGUMENTS(
        "--list-arguments",
        false,
        false,
        {
            var print = "The following comments are allowed:\n"
            for (entry in ARGUMENTS.values()) {
                print += "\n\t${entry.pureCommand}"
            }
            println("$print\n")
        }
    ),
    CONFIG(
        "--config",
    true,
        true,
        {
            it?.let {
                configFilePath = it
            }
        }
    )
}