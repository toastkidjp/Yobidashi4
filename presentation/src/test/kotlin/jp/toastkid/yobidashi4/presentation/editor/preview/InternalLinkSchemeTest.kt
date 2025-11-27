package jp.toastkid.yobidashi4.presentation.editor.preview

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class InternalLinkSchemeTest {

    private lateinit var internalLinkScheme: InternalLinkScheme

    @BeforeEach
    fun setUp() {
        internalLinkScheme = InternalLinkScheme()
    }

    @ParameterizedTest
    @CsvSource(
        "null, [null](https://internal/null)",
        "tomato, [tomato](https://internal/tomato)",
        "tomato 33, [tomato 33](https://internal/tomato%2033)",
        nullValues = ["null"],
    )
    fun makeLink(input: String?, expected: String) {
        assertEquals(expected, internalLinkScheme.makeLink(input))
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