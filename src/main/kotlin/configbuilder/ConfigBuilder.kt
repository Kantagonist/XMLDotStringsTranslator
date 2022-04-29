package configbuilder

import DotStringsTranslatorException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

/**
 * Reads the given file and returns it as a usable virtual object
 */
internal class ConfigBuilder {

    companion object {

        /**
         * Reads the yaml config file
         *
         * @param yamlConfigFilePath the absolute path of the config file as a String
         *
         * @return An array of FromTo pairings
         */
        @JvmStatic
        @kotlin.jvm.Throws(DotStringsTranslatorException::class)
        fun readConfig(yamlConfigFilePath: String): VirtualConfigFile {

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
            if (result.rootPath[0] == '.') {
                result.rootPath =  yamlConfigFilePath + result.rootPath.substring(1)
            }
            return result
        }
    }
}