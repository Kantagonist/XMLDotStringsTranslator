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