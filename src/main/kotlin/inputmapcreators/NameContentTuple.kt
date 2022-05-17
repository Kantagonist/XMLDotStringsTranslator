/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package inputmapcreators

/**
 * A tuple which represents the key-value pair as basis for string-contents.
 */
internal data class NameContentTuple(
    val name: String,
    val content: String
)