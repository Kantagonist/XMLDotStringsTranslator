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
 * Same as [dotStringsKeyValueCaptureGroupsPattern], but used in debugging to work on the \" problem.
 */
internal const val dotStringsKeyValueCaptureGroupsPatternExperimental = "((?<=^\\s*\").*(?:[^\\\\])(?=\")).*=.*((?<=\").*(?:[^\\\\])(?=\")).*\$"