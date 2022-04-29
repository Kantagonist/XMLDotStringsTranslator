/**
 * Thrown if a step is not done in accordance to configurations.
 */
internal class DotStringsTranslatorException(
    val tag: String,
    override val message: String
): Exception()