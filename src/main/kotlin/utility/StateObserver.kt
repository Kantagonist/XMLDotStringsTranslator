/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */

package utility

import DotStringsTranslatorException

/**
 * An internal Singleton.
 * Keeps track of the state of the current translation.
 * Has to be reset manually each run.
 * Used for later printouts if the program is set up to print unmoved and moved contents.
 */
internal object StateObserver {

    private var movedData: HashMap<String, HashSet<String>> = HashMap()
    private var unmovedData: HashMap<String, HashSet<String>> = HashMap()

    /**
     * Resets the state of the observer machine.
     */
    internal fun reset() {
        movedData = HashMap()
        unmovedData = HashMap()
    }

    /**
     * Adds the unmoved data entry and sorts based on BucketSort.
     * Buckets are the filepath names.
     *
     * @param filePath The filePath, set in the config file, functions as key
     * @param id The id of the entry, serves as bucket filler
     */
    internal fun addUnMovedData(filePath: String, id: String) {
        unmovedData[filePath]?.add(id) ?: run {
            val newSet = HashSet<String>()
            newSet.add(id)
            unmovedData.put(filePath, newSet)
        }
    }

    /**
     * Moves an entry from unmovedData to movedData.
     * Throws an exception if the entry does not exist.
     * Uses the same BucketSort as its unmoved twin.
     *
     * @param filePath The filePath, set in the config file, functions as key
     * @param id The id of the entry, serves as bucket filler
     */
    @kotlin.jvm.Throws(DotStringsTranslatorException::class)
    internal fun moveData(filePath: String, id: String) {

        // fail check
        if (unmovedData[filePath] == null) {
            throw DotStringsTranslatorException("[OBSERVATION ERROR]", " Tried to move $filePath, but it doesn't exist")
        }
        if (unmovedData[filePath]?.contains(id) == false) {
            throw DotStringsTranslatorException("[OBSERVATION ERROR]", " Tried to move $filePath with id: $, but it doesn't exist")
        }

        // data move
        unmovedData[filePath]?.remove(id)
        movedData[filePath]?.add(id) ?: run {
            val newSet = HashSet<String>()
            newSet.add(id)
            movedData.put(filePath, newSet)
        }
    }

    /**
     * Sorts the current data by file and preformats an easily readable format
     */
    override fun toString(): String {
        val resultBuilder = StringBuilder("Unmoved Data:")
        for (entry in unmovedData) {
            resultBuilder.append("\n\t${entry.key}\n")
            for (id in entry.value) {
                resultBuilder.append("\t\t$id\n")
            }
        }

        resultBuilder.append("\nMoved Data:")
        for (entry in movedData) {
            resultBuilder.append("\n\t${entry.key}\n")
            for (id in entry.value) {
                resultBuilder.append("\t\t$id\n")
            }
        }

        return resultBuilder.toString()
    }
}