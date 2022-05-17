/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */

/**
 * Thrown if a step is not done in accordance to configurations.
 */
internal class DotStringsTranslatorException(
    val tag: String,
    override val message: String
): Exception()