/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package configbuilder

/**
 * Contains a key-value pair from the config file.
 * Always a list of possible sources merged together.
 */
data class VirtualConfigFile(
    var rootPath: String,
    val translations: List<Translation>?
)

data class  Translation(
    val from: List<String>?,
    val to: List<String>?
)