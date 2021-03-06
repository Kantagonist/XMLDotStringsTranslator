/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package inputmapcreators

import DotStringsTranslatorException
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Takes an XML file, which is structured like an android string resource file.
 * Creates a mapping of the strings in that file with pairings of name and content.
 *
 * @param absolutePath the absolute path of the xml file
 *
 * @return a list of name-content pairs for further use
 */
@kotlin.jvm.Throws(DotStringsTranslatorException::class)
internal fun createXmlMap(absolutePath: String): List<NameContentTuple> {

    // read in XML file
    val xmlFile = File(absolutePath)
    if (!xmlFile.exists() || !xmlFile.name.endsWith(".xml")) {
        throw DotStringsTranslatorException(
            tag = "[FILE NOT FOUND]",
            message = "Could not find .xml file under:\n$absolutePath"
        )
    }
    val dbFactoryBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val xmlInput = InputSource(StringReader(xmlFile.readText()))
    val virtualXmlDocument = dbFactoryBuilder.parse(xmlInput)

    // read contents of XML
    val result = mutableListOf<NameContentTuple>()
    val listOfStrings = virtualXmlDocument.getElementsByTagName("string")
    for (i in 0 until listOfStrings.length) {
        result.add(
            NameContentTuple(
                listOfStrings.item(i).attributes.getNamedItem("name").nodeValue,
                listOfStrings.item(i).textContent
            )
        )
    }

    // return map
    return result
}