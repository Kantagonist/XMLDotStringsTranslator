/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package configbuilder

import DotStringsTranslatorException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import utility.EVERYTHING_BEFORE_AND_INCLUDING_THE_LAST_FORWARD_SLASH_PATTERN
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

/**
 * Reads the yaml config file and creates a virtual representation.
 *
 * @param yamlConfigFilePath the absolute path of the config file as a String
 *
 * @return An array of FromTo pairings
 */
@kotlin.jvm.Throws(DotStringsTranslatorException::class)
internal fun readConfig(yamlConfigFilePath: String): VirtualConfigFile {

    // check yaml file
    val yamlFile = File(yamlConfigFilePath)
    if (!yamlFile.exists() || !yamlFile.isFile) {
        throw DotStringsTranslatorException(
            tag = "[FILE NOT FOUND]",
            message = "Could not find config file under:\n$yamlConfigFilePath"
        )
    }

    // use SnakeYaml to get the contents
    val mapper = ObjectMapper(YAMLFactory())
    mapper.registerModule(KotlinModule.Builder().build())
    val result = Files.newBufferedReader(Path(yamlConfigFilePath)).use {
        mapper.readValue(yamlFile, VirtualConfigFile::class.java)
    }

    // fix given rootPath to account for relative and absolute path
    if (!result.rootPath.startsWith("/")) {
        val regex = Regex(EVERYTHING_BEFORE_AND_INCLUDING_THE_LAST_FORWARD_SLASH_PATTERN)
        val match = regex.find(yamlConfigFilePath)
        match?.let {
            result.rootPath =  "${it.value}${result.rootPath}"
        }
    }
    return result
}