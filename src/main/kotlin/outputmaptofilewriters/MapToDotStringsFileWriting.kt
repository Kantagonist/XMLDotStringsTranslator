/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package outputmaptofilewriters

import DotStringsTranslatorException
import inputmapcreators.NameContentTuple
import utility.DOT_STRINGS_KEY_VALUE_CAPTURE_GROUPS_PATTERN
import utility.readSectionsOfDotStrings
import utility.WHITE_SPACE_ONLY_PATTERN
import java.io.File
import kotlin.jvm.Throws

/**
 * Updates a given .strings file's entries with the given map.
 * In case the map contains a mapping, which is not present in the resource, the mapping is not brought into it.
 * This method only does that, if the addNewEntries parameter is configured for it.
 *
 * @param mapping a list of name-content tuples which serve as input
 * @param absolutePath the absolutePath of the target file as a String
 * @param addNewEntries adds new entries on bottom of the existing file if true, ignores them is false.
 *
 *  @return 2 non-null lists, the first transmits the original strings ids in the file, the second shows all the manipulated ids.
 */
@Throws(DotStringsTranslatorException::class)
internal fun writeMappingToDotStringsFile(
    mapping: List<NameContentTuple>,
    absolutePath: String,
    addNewEntries: Boolean = false): List<List<String>> {
    // read in .strings file
    val dotStringsFile = File(absolutePath)
    if (!dotStringsFile.exists() || !dotStringsFile.name.endsWith(".strings")) {
        throw DotStringsTranslatorException(
            tag = "[FILE NOT FOUND]",
            message = "Could not find .strings file under:\n$absolutePath"
        )
    }

    // initiate debug result
    val debuggingResult = listOf(mutableListOf(), mutableListOf<String>())

    // get sections of .strings file
    val sectionsList = readSectionsOfDotStrings(dotStringsFile.readText())

    // change each section if they are not a comment
    var result = ""
    val mutableMapping = mapping.toMutableList()
    for (section in sectionsList) {
        if (!section.isComment) {
            /*
             * TODO find a better way to separate string resources.
             *
             * Because this will break if a semicolon is used in a string resource.
             * Create a regex pattern to match only true (".*" = ".*")
             */
            for (stringResource in section.content.split(";")) {
                result += if (!stringResource.contains(Regex(WHITE_SPACE_ONLY_PATTERN))) {
                    "${manipulateSectionContent(stringResource, mutableMapping, debuggingResult)};"
                } else {
                    stringResource // re-adds line breaks and trailing spaces of a section
                }
            }
        } else {
            result += section.content
        }
    }

    // add new lines to bottom of the file
    if (addNewEntries) {
        for (entry in mutableMapping) {
            debuggingResult[0].add(entry.name)
            debuggingResult[1].add(entry.name)
            result += "\n\"${entry.name}\" = \"${entry.content}\";"
        }
    }

    // write result into file
    dotStringsFile.writeText(result)

    // return ids for debugging usage in the main function
    return debuggingResult
}

/**
 * Manipulates the given pre-formatted resource string with the first matching mapping if it exists.
 * If not, simply returns the original String.
 * Also removes the mapped resource, so it can't be reused.
 *
 * @param content the String to manipulate
 * @param mapping the mapping with a possible key match
 *
 * @return the given content String, manipulated or not
 *
 * @throws DotStringsTranslatorException if the regex matching process for key-value did not succeed.
 */
@Throws(DotStringsTranslatorException::class)
private fun manipulateSectionContent(
    content: String,
    mapping: MutableList<NameContentTuple>,
    debuggingList: List<MutableList<String>>): String {

    // match key-value pair
    val nameRegex = Regex(DOT_STRINGS_KEY_VALUE_CAPTURE_GROUPS_PATTERN, RegexOption.DOT_MATCHES_ALL)
    val captureGroups = nameRegex.find(content, 0)?.groups
    if (captureGroups == null || captureGroups.size < 3) {
        throw DotStringsTranslatorException(
            tag = "[MATCHING ERROR]",
            message = "matching failed for key-value pair in:\t\n$content"
        )
    }
    val key = captureGroups[1]!!
    val value = captureGroups[2]!!

    debuggingList[0].add(key.value)

    // find mapping
    val tuple = mapping.find {
       it.name == key.value
    }
    mapping.remove(tuple)

    // apply mapping
    var result = content
    tuple?.let {
        debuggingList[1].add(it.name)
        result = content.replaceRange(key.range, it.name)
        /*
         * by replacing the first part, the ranges of the content might be changed.
         * As a result, the offset needs to be calculated
         */
        val offset = key.range.last - key.range.first - it.name.length + 1
        val offsetRange = IntRange(value.range.first + offset, value.range.last + offset)
        result = result.replaceRange(offsetRange, it.content)
    }
    return result
}