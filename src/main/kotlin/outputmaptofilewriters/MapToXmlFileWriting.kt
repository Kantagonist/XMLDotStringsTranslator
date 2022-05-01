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

/**
 * Updates a given resource files entries with the given map.
 * In case the map contains a mapping, which is not present in the xml resource, the mapping is not brought into it.
 * This method only does that, if the addNewEntries parameter is configured for it.
 *
 * @param mapping a list of name-content tuples which serve as input
 * @param absolutePath the absolutePath of the target file as a String
 * @param addNewEntries adds new entries on bottom of the existing file if true, ignores them is false.
 */
@kotlin.jvm.Throws(DotStringsTranslatorException::class)
internal fun writeMappingToFile(mapping: List<NameContentTuple>, absolutePath: String, addNewEntries: Boolean = false) {
    // read in XML file
    val xmlFile = File(absolutePath)
    if (!xmlFile.exists() || !xmlFile.name.endsWith(".xml")) {
        throw DotStringsTranslatorException(
            tag = "[FILE NOT FOUND]",
            message = "Could not find .xml file under:\n$absolutePath"
        )
    }
    val dbFactoryBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val xmlTarget = InputSource(StringReader(xmlFile.readText()))
    val virtualXmlDocument = dbFactoryBuilder.parse(xmlTarget)

    // manipulate strings objects
    val listOfStrings = virtualXmlDocument.getElementsByTagName("string")
    val listOfManipulatedMappingElements = mutableListOf<NameContentTuple>()
    for (i in 0 until listOfStrings.length) {
        val nameContentTuple = mapping.find {
            it.name == listOfStrings.item(i).attributes.getNamedItem("name").nodeValue
        }
        if (nameContentTuple != null) {
            listOfManipulatedMappingElements.add(nameContentTuple)
            listOfStrings.item(i).textContent = nameContentTuple.content
        }
    }

    // add new strings
    if (addNewEntries) {
        mapping.forEach { mainMapping ->
            if (listOfManipulatedMappingElements.find { mainMapping.name == it.name } == null) {
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
}


/**
 * Adds a new string resource to the bottom of the virtual xml file.
 *
 * @param xmlDocument The virtual DOM representation of the xml file
 * @param nameContentTuple the new node to add at the bottom
 */
private fun addToXmlDocument(xmlDocument: Document, nameContentTuple: NameContentTuple) {
    val firstResourceElement = xmlDocument.getElementsByTagName("resources").item(0)
    val attribute = xmlDocument.createAttribute("name")
    attribute.nodeValue = nameContentTuple.name
    val newStringNode = firstResourceElement.appendChild(xmlDocument.createElement("string").setAttributeNode(attribute))
    newStringNode.nodeValue = nameContentTuple.content
}