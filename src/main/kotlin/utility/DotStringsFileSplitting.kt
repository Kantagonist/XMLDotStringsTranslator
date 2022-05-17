/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package utility

/**
 * Sections the File into content and comment paragraphs.
 *
 * @param entireContent The .strings file content to section as a [String]
 *
 * @return a non-null list of sections
 */
internal fun readSectionsOfDotStrings(entireContent: String): List<DotStringsSection> {
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

internal data class DotStringsSection(
    val isComment: Boolean,
    var content: String
)