package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BlockQuotationTest {

    /**
     * Line separator.
     */
    private val lineSeparator = System.lineSeparator()

    /**
     * Test object.
     */
    private lateinit var quotation: BlockQuotation

    /**
     * Initialize object.
     */
    @BeforeEach
    fun setUp() {
        quotation = BlockQuotation()
    }


    @ParameterizedTest
    @CsvSource(
        "tomato, > tomato",
        "1. tomato\\n2. orange\\n3. apple, > 1. tomato\\n> 2. orange\\n> 3. apple",
        "test\\n, > test\\n"
    )
    fun test(input: String, expected: String) {
        assertEquals(expected.replace("\\n", "\n"), quotation(input.replace("\\n", "\n")))
    }

    @Test
    fun testInvokeWithEmptyInput() {
        assertEquals("", quotation(""))
    }

    @Test
    fun testInvokeWithNullInput() {
        assertNull(quotation(null))
    }

}