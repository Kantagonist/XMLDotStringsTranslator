/*
 * This software is provided without any warranties.
 * It is licensed under the GNU GPL v3.
 * The full license is available in the file LICENSE.txt
 */
import inputmapcreators.NameContentTuple
import org.junit.jupiter.api.Test
import utility.mergeMappings
import kotlin.test.assertEquals

class MapMergeTests {

    @Test
    fun mergeSmallExclusiveMaps() {

        // create inputs
        val topInput = listOf(
            NameContentTuple("hello","world"),
            NameContentTuple("12", "34")
        )
        val bottomInput = listOf(
            NameContentTuple("this", "test is"),
            NameContentTuple("an_easy_one", "TO do in my IDE")
        )

        // create expected output
        val expectedOutput = listOf(
            NameContentTuple("hello","world"),
            NameContentTuple("12", "34"),
            NameContentTuple("this", "test is"),
            NameContentTuple("an_easy_one", "TO do in my IDE")
        )

        // merge input lists
        val actualOutput = mergeMappings(listOf(topInput, bottomInput))

        // compare results
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun mergeTwoInclusiveMaps() {

        // create inputs
        val topInput = listOf(
            NameContentTuple("hello","world"),
            NameContentTuple("12", "34"),
            NameContentTuple("hello_my_freedom", "cry_out_for_a_hero") ,
            NameContentTuple("1444442", "34"),
            NameContentTuple("this_test_is_top", "45820582")
        )
        val bottomInput = listOf(
            NameContentTuple("this", "test is"),
            NameContentTuple("this_test_is_top", "00000000000000"),
            NameContentTuple("an_easy_one", "TO do in my IDE"),
            NameContentTuple("hello_my_freedom", "this should not be part of it")
        )

        // create expected output
        val expectedOutput = listOf(
            NameContentTuple("hello","world"),
            NameContentTuple("12", "34"),
            NameContentTuple("hello_my_freedom", "cry_out_for_a_hero") ,
            NameContentTuple("1444442", "34"),
            NameContentTuple("this_test_is_top", "45820582"),
            NameContentTuple("this", "test is"),
            NameContentTuple("an_easy_one", "TO do in my IDE")
        )

        // merge input lists
        val actualOutput = mergeMappings(listOf(topInput, bottomInput))

        // compare results
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun mergeMultipleInclusiveMaps() {

        // create inputs
        val input1 = listOf(
            NameContentTuple("hello","world"),
            NameContentTuple("12", "34"),
            NameContentTuple("hello_my_freedom", "cry_out_for_a_hero") ,
            NameContentTuple("1444442", "34"),
            NameContentTuple("this_test_is_top", "45820582")
        )
        val input2 = listOf(
            NameContentTuple("this", "test is"),
            NameContentTuple("an_easy_one", "TO do in my IDE"),
            NameContentTuple("hello_my_freedom", "this should not be part of it")
        )
        val input3 = listOf(
            NameContentTuple("this_test_is_top", "22222222"),
            NameContentTuple("new entry", "ranging vows"),
            NameContentTuple("duplicate_of_4", "dominant text")
        )
        val input4 = listOf(
            NameContentTuple("duplicate_of_4", "subservient text"),
            NameContentTuple("something_else", "backpack, y soy la hora"),
            NameContentTuple("The smartest clipper you will find", "its ho hey ho"),
            NameContentTuple("Shes the Margaret Evans of", "of the blue star line")
        )

        // create expected output
        val expectedOutput = listOf(
            NameContentTuple("hello","world"),
            NameContentTuple("12", "34"),
            NameContentTuple("hello_my_freedom", "cry_out_for_a_hero") ,
            NameContentTuple("1444442", "34"),
            NameContentTuple("this_test_is_top", "45820582"),
            NameContentTuple("this", "test is"),
            NameContentTuple("an_easy_one", "TO do in my IDE"),
            NameContentTuple("new entry", "ranging vows"),
            NameContentTuple("duplicate_of_4", "dominant text"),
            NameContentTuple("something_else", "backpack, y soy la hora"),
            NameContentTuple("The smartest clipper you will find", "its ho hey ho"),
            NameContentTuple("Shes the Margaret Evans of", "of the blue star line")
        )

        // merge input lists
        val actualOutput = mergeMappings(listOf(input1, input2, input3, input4))

        // compare results
        assertEquals(expectedOutput, actualOutput)
    }
}