/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package utility

/**
 * Matches a String of ONLY whitespace characters, e.g. " ", "\n"
 */
internal const val whiteSpaceOnlyPattern = "^\\s*$"

/**
 * For a string like e.g.
 *
 * "some content" = "myContent"
 *
 * this pattern will return three capture groups:
 * 0: "some content" = "myContent"
 * 1: some content
 * 2: myContent
 *
 *
 * DANGER: breaks if the key or value contain \"
 */
internal const val dotStringsKeyValueCaptureGroupsPattern = "^.*((?<=\").*(?=\")).*=.*((?<=\").*(?=\")).*\$"

/**
 * Matches everything before and also the last forward slash.
 * E.g.
 * input: /this/path/is/mine.yaml
 * match: /this/path/is/
 */
internal const val everythingBeforeAndIncludingTheLastForwardSlashPattern = "^.*(?:\\/)"

/**
 * Same as [dotStringsKeyValueCaptureGroupsPattern], but used in debugging to work on the \" problem.
 */
internal const val dotStringsKeyValueCaptureGroupsPatternExperimental = "((?<=^\\s*\").*(?:[^\\\\])(?=\")).*=.*((?<=\").*(?:[^\\\\])(?=\")).*\$"

/**
 * Gets the version number in the second capture group from the version.gradle file
 */
internal const val versionNumberExtractionPattern = "mainVersion=\"(.*)\""