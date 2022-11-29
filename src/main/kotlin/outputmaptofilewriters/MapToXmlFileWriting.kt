/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package outputmaptofilewriters

import DotStringsTranslatorException
import inputmapcreators.NameContentTuple
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.File
import java.io.FileWriter
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

// REGEX: matches standalone hint in XML declaration
private val standaloneHintRegex = Regex("\\sstandalone=\".*\"")

/**
 * Updates a given xml file's entries with the given map.
 * In case the map contains a mapping, which is not present in the xml resource, the mapping is not brought into it.
 * This method only does that, if the addNewEntries parameter is configured for it.
 *
 * @param mapping a list of name-content tuples which serve as input
 * @param absolutePath the absolutePath of the target file as a String
 * @param addNewEntries adds new entries on bottom of the existing file if true, ignores them is false.
 *
 * @return 2 non-null lists, the first transmits the original strings ids in the file, the second shows all the manipulated ids.
 */
@kotlin.jvm.Throws(DotStringsTranslatorException::class)
internal fun writeMappingToXmlFile(
    mapping: List<NameContentTuple>,
    absolutePath: String,
    addNewEntries: Boolean = false): List<List<String>> {

    // read in XML file
    val xmlFile = File(absolutePath)
    if (!xmlFile.exists() || !xmlFile.name.endsWith(".xml")) {
        throw DotStringsTranslatorException(
            tag = "[FILE NOT FOUND]",
            message = "Could not find .xml file under:\n$absolutePath"
        )
    }

    // initiate debug result
    val debuggingResult = listOf(mutableListOf(), mutableListOf<String>())

    // read in XML
    val dbFactoryBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val xmlText = xmlFile.readText()
    val hasStandaloneDeclaration = standaloneHintRegex.containsMatchIn(xmlText.lines()[0])
    val xmlTarget = InputSource(StringReader(xmlText))
    val virtualXmlDocument = dbFactoryBuilder.parse(xmlTarget)

    // manipulate strings objects
    val listOfStrings = virtualXmlDocument.getElementsByTagName("string")
    val listOfManipulatedMappingElements = mutableListOf<NameContentTuple>()
    for (i in 0 until listOfStrings.length) {
        debuggingResult[0].add(listOfStrings.item(i).attributes.getNamedItem("name").nodeValue)
        val nameContentTuple = mapping.find {
            it.name == listOfStrings.item(i).attributes.getNamedItem("name").nodeValue
        }
        if (nameContentTuple != null) {
            debuggingResult[1].add(nameContentTuple.name)
            listOfManipulatedMappingElements.add(nameContentTuple)
            listOfStrings.item(i).textContent = nameContentTuple.content
        }
    }

    // add new strings
    if (addNewEntries) {
        mapping.forEach { mainMapping ->
            if (listOfManipulatedMappingElements.find { mainMapping.name == it.name } == null) {
                debuggingResult[0].add(mainMapping.name)
                debuggingResult[1].add(mainMapping.name)
                addToXmlDocument(virtualXmlDocument, mainMapping)
            }
        }
    }

    // write manipulated virtual document into file
    val transformer = TransformerFactory.newInstance().newTransformer()
    val source = DOMSource(virtualXmlDocument)
    val resultWriter = StreamResult(FileWriter(xmlFile))
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    transformer.transform(source, resultWriter)

    // fix file errors, which sadly occur due to Oracle's incompetence
    xmlFileCleanup(xmlFile, !hasStandaloneDeclaration)

    // return ids for debugging usage in the main function
    return debuggingResult
}

/**
 * Adds a new string resource to the bottom of the virtual xml file.
 *
 * @param xmlDocument The virtual DOM representation of the xml file
 * @param nameContentTuple the new node to add at the bottom
 */
private fun addToXmlDocument(xmlDocument: Document, nameContentTuple: NameContentTuple) {
    val firstResourceElement = xmlDocument.getElementsByTagName("resources").item(0)
    val newElement = xmlDocument.createElement("string")

    // set naming attribute
    val attribute = xmlDocument.createAttribute("name")
    attribute.nodeValue = nameContentTuple.name
    newElement.setAttributeNode(attribute)

    // set text content of node
    val textContent = xmlDocument.createTextNode(nameContentTuple.content)
    newElement.appendChild(textContent)

    // add node to document
    firstResourceElement.appendChild(newElement)
}

/**
 * Sadly, the XML transformer, provided by Java is not sensible.
 * It adds blank lines were there weren't any before.
 * Not by accident, but because they genuinely believe that it's a sensible choice.
 * In order to correct this, you need to use an XSLT stylesheet, but for simplicity's sake, just fix the file manually.
 *
 * @param file the XML file to be cleaned.
 * @param standaloneHintDeletion if true, delete the standalone hint in the xml declaration, if false not.
 */
private fun xmlFileCleanup(file: File, standaloneHintDeletion: Boolean) {
    var result = ""

    // REGEX: matches every non-whitespace
    val noWhiteSpaceRegex = Regex("\\S")
    for (line in file.readLines()) {
        if (noWhiteSpaceRegex.containsMatchIn(line)) {
            result = "$result$line\n"
        }
    }

    // eliminate standalone hint
    if (standaloneHintDeletion) {
        if (standaloneHintRegex.containsMatchIn(result)) {
            result = standaloneHintRegex.replace(result, "")
        }
    }

    // write result
    file.writeText(result.trim())
}