package inputmapcreators

import DotStringsTranslatorException
import readSectionsOfDotStrings
import java.io.File

/**
 * Takes a .strings file.
 * Creates a mapping of the strings in that file with pairings of name and content.
 *
 * @param absolutePath the absolute path of the .strings file
 *
 * @return a list of name-content pairs for further use
 */
@kotlin.jvm.Throws(DotStringsTranslatorException::class)
internal fun createDotStringsMap(absolutePath: String): List<NameContentTuple> {
    // file init
    val dotStringsFile = File(absolutePath)
    if (!dotStringsFile.exists() || !dotStringsFile.name.endsWith(".strings")) {
        throw DotStringsTranslatorException(
            tag = "[FILE NOT FOUND]",
            message = "Could not find .strings file under:\t\n$absolutePath"
        )
    }

    // get sections
    val sectionList = readSectionsOfDotStrings(dotStringsFile.readText())

    // create results
    val result = mutableListOf<NameContentTuple>()
    for (section in sectionList) {
        if (!section.isComment) {
            /*
             * TODO find a better way to separate string resources.
             *
             * Because this will break if a semicolon is used in a string resource.
             * Create a regex pattern to match only true (".*" = ".*")
             */
            for (stringResource in section.content.split(";")) {
                if (!stringResource.contains(Regex("^\\s*$"))) { // matches a whitespace-only string
                    result.add(getNameContentTupleFrom(stringResource))
                }
            }
        }
    }
    return result
}

/**
 * Takes a string resource in style of the .string resource file in form of a [String].
 * Creates a virtual representation of that file.
 *
 * @param stringResource the String representation of the string resource.
 *
 * @return virtual representation of the string resource
 */
private fun getNameContentTupleFrom(stringResource: String): NameContentTuple {
    val nameRegex = Regex("^.*((?<=\").*(?=\")).*=.*((?<=\").*(?=\")).*\$", RegexOption.DOT_MATCHES_ALL)
    val captureGroupValues= nameRegex.find(stringResource, 0)?.groupValues
        ?: throw DotStringsTranslatorException("[FORMAT ERROR]", "Tried to extract name from:\n\t$stringResource")

    return NameContentTuple(
        name = captureGroupValues[1],
        content = captureGroupValues[2]
    )
}