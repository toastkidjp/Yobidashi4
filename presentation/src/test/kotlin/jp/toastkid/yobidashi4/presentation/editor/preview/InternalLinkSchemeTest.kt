package jp.toastkid.yobidashi4.presentation.editor.preview

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class InternalLinkSchemeTest {

    private lateinit var internalLinkScheme: InternalLinkScheme

    @BeforeEach
    fun setUp() {
        internalLinkScheme = InternalLinkScheme()
    }

    @Test
    fun makeLink() {
        assertAll(
            {
                assertEquals(
                    "[null](https://internal/null)",
                    internalLinkScheme.makeLink(null)
                )
            },
            {
                assertEquals(
                    "[tomato](https://internal/tomato)",
                    internalLinkScheme.makeLink("tomato")
                )
            },
            {
                assertEquals(
                    "[tomato 33](https://internal/tomato%2033)",
                    internalLinkScheme.makeLink("tomato 33")
                )
            }
        )
    }

    @ParameterizedTest
    @CsvSource(
        "'', false",
        "' ', false",
        "https://www.yahoo.co.jp, false",
        "tomato, false",
        "https://internal/tomato, true",
    )
    fun isInternalLink(input: String, expected: Boolean) {
        assertEquals(expected, internalLinkScheme.isInternalLink(input))
    }

    @ParameterizedTest
    @CsvSource(
        "'', ''",
        "' ', ' '",
        "tomato, tomato",
        "https://www.yahoo.co.jp, https://www.yahoo.co.jp",
        "https://internal/tomato, tomato",
        "https://internal/Clean%20Code, Clean Code"
    )
    fun extractCases(input: String, expected: String) {
        assertEquals(
            expected,
            internalLinkScheme.extract(input)
        )
    }

}