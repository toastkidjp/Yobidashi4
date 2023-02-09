package jp.toastkid.yobidashi4.presentation.editor.preview

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InternalLinkSchemeTest {

    private lateinit var internalLinkScheme: InternalLinkScheme

    @BeforeEach
    fun setUp() {
        internalLinkScheme = InternalLinkScheme()
    }

    @Test
    fun makeLink() {
        assertEquals(
            "[tomato](https://internal/tomato)",
            internalLinkScheme.makeLink("tomato")
        )
        assertEquals(
            "[tomato 33](https://internal/tomato%2033)",
            internalLinkScheme.makeLink("tomato 33")
        )
    }

    @Test
    fun isInternalLink() {
        assertFalse(internalLinkScheme.isInternalLink(""))
        assertFalse(internalLinkScheme.isInternalLink(" "))
        assertFalse(internalLinkScheme.isInternalLink("https://www.yahoo.co.jp"))
        assertFalse(internalLinkScheme.isInternalLink("tomato"))
        assertTrue(internalLinkScheme.isInternalLink("https://internal/tomato"))
    }

    @Test
    fun extract() {
        assertEquals(
            "",
            internalLinkScheme.extract("")
        )
        assertEquals(
            " ",
            internalLinkScheme.extract(" ")
        )
        assertEquals(
            "tomato",
            internalLinkScheme.extract("tomato")
        )
        assertEquals(
            "https://www.yahoo.co.jp",
            internalLinkScheme.extract("https://www.yahoo.co.jp")
        )
        assertEquals(
            "tomato",
            internalLinkScheme.extract("https://internal/tomato")
        )
    }
}