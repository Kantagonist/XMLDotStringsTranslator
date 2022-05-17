/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
package utility

import inputmapcreators.NameContentTuple

/**
 * Merges the given Mappings via FULL OUTER JOINS.
 * In case of duplicates, the first occurrence in the list of mappings is used.
 * The latter ones are discarded.
 *
 * @param mappings the list of mappings to merge
 *
 * @return a merged mapping with first come, first served for duplicate entries
 */
internal fun mergeMappings(mappings: List<List<NameContentTuple>>): List<NameContentTuple> {
    var mergedMapping = listOf<NameContentTuple>()
    for (currentMapping in mappings) {
        mergedMapping = mergeMappings(mergedMapping, currentMapping)
    }
    return mergedMapping
}

/**
 * Merges the given top mapping with the given bottom mapping.
 * The merge rule is TOP FIRST.
 * This is similar to a FULL OUTER JOIN.
 * The resources, which exist in both are taken from the top input.
 *
 * @param topMapping the dominant mapping (its duplicates are used)
 * @param bottomMapping the subservient mapping (its duplicates are overwritten by the instances in the top mapping)
 *
 * @return the merged mapping
 */
private fun mergeMappings(topMapping: List<NameContentTuple>, bottomMapping: List<NameContentTuple>): List<NameContentTuple> {
    val result = mutableListOf<NameContentTuple>()
    val mutableBottomMapping = bottomMapping.toMutableList()
    topMapping.forEach { topTuple ->
        result.add(topTuple)
        mutableBottomMapping.find { bottomTuple ->
            topTuple.name == bottomTuple.name
        }?.let {
            mutableBottomMapping.remove(it)
        }
    }
    mutableBottomMapping.forEach {
        result.add(it)
    }
    return result
}