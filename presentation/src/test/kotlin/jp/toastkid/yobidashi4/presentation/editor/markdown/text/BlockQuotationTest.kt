package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BlockQuotationTest {

    /**
     * Line separator.
     */
    private val lineSeparator = System.getProperty("line.separator")

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

    @Test
    fun testInvoke() {
        assertEquals("> tomato", quotation("tomato"))
        assertEquals(
            "> 1. tomato$lineSeparator> 2. orange$lineSeparator> 3. apple",
            quotation("1. tomato${lineSeparator}2. orange${lineSeparator}3. apple")
        )
    }

    @Test
    fun testInvokeWithEmptyInput() {
        assertEquals("", quotation(""))
    }

}