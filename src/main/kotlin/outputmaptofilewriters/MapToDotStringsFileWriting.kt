package outputmaptofilewriters

import DotStringsTranslatorException
import inputmapcreators.NameContentTuple
import readSectionsOfDotStrings
import java.io.File

/**
 * Updates a given .strings file's entries with the given map.
 * In case the map contains a mapping, which is not present in the resource, the mapping is not brought into it.
 * This method only does that, if the addNewEntries parameter is configured for it.
 *
 * @param mapping a list of name-content tuples which serve as input
 * @param absolutePath the absolutePath of the target file as a String
 * @param addNewEntries adds new entries on bottom of the existing file if true, ignores them is false.
 */
internal fun writeMappingToDotStringsFile(mapping: List<NameContentTuple>, absolutePath: String, addNewEntries: Boolean = false) {
    // read in .strings file
    val dotStringsFile = File(absolutePath)
    if (!dotStringsFile.exists() || !dotStringsFile.name.endsWith(".strings")) {
        throw DotStringsTranslatorException(
            tag = "[FILE NOT FOUND]",
            message = "Could not find .strings file under:\n$absolutePath"
        )
    }

    // get sections of .strings file
    val sectionsList = readSectionsOfDotStrings(dotStringsFile.readText())
}