package inputmapcreators

import DotStringsTranslatorException
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
    val sectionList = readSectionsOfDotStrings(dotStringsFile)

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
 * Sections the File into content and comment paragraphs.
 *
 * @param file The .strings file to section
 *
 * @return a non-null list of sections
 */
private fun readSectionsOfDotStrings(file: File): List<DotStringsSection> {
    val entireContent = file.readText()
    var isCurrentlyRunningMultilineComment = false
    var isCurrentlyRunningSingleLineComment = false
    var currentDotStringsSection: DotStringsSection? = null
    val result = mutableListOf<DotStringsSection>()
    var i = 0
    while (i < entireContent.length) {
        val currentChar = entireContent[i]

        // early return in case of last letter
        if (i == entireContent.length - 1) {
            if (currentDotStringsSection == null) {
                currentDotStringsSection = DotStringsSection(false, "")
            }
            currentDotStringsSection.content += currentChar
            result.add(currentDotStringsSection)
            break
        }

        val nextChar = entireContent[i+1]

        // checking for running comment
        if (isCurrentlyRunningSingleLineComment) {
            currentDotStringsSection!!.content += currentChar
            if (nextChar == '\n') {
                currentDotStringsSection!!.content += nextChar
                result.add(currentDotStringsSection!!)
                isCurrentlyRunningSingleLineComment = false
                currentDotStringsSection = null
                i++
            }
            i++
            continue
        }
        if (isCurrentlyRunningMultilineComment) {
            currentDotStringsSection!!.content += currentChar
            if (currentChar == '*' && nextChar == '/') {
                currentDotStringsSection!!.content += nextChar
                result.add(currentDotStringsSection!!)
                isCurrentlyRunningMultilineComment = false
                currentDotStringsSection = null
                i++
            }
            i++
            continue
        }

        // checking for opening comment
        if (currentChar == '/' && nextChar == '/') {
            if (currentDotStringsSection != null) {
                result.add(currentDotStringsSection)
            }
            currentDotStringsSection = DotStringsSection(true, "//")
            isCurrentlyRunningSingleLineComment = true
            i+=2
            continue
        }
        if (currentChar == '/' && nextChar == '*') {
            if (currentDotStringsSection != null) {
                result.add(currentDotStringsSection)
            }
            currentDotStringsSection = DotStringsSection(true, "/*")
            isCurrentlyRunningMultilineComment = true
            i+=2
            continue
        }

        // adding running content as last part
        if (currentDotStringsSection == null) {
            currentDotStringsSection = DotStringsSection(false, "$currentChar")
        } else {
            currentDotStringsSection.content += currentChar
        }
        i++
    }

    return result
}

private data class DotStringsSection(
    val isComment: Boolean,
    var content: String
)

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