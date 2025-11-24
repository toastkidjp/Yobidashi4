package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TrimmingServiceTest {

    private lateinit var trimmingService: TrimmingService

    @BeforeEach
    fun setUp() {
        trimmingService = TrimmingService()
    }

    @Test
    fun test() {
        assertEquals("aaa", trimmingService.invoke("  aaa   "))
        val lineSeparator = "\n"
        assertEquals(
            "john${lineSeparator}aaa${lineSeparator}trimmed",
            trimmingService.invoke(listOf("  john", " aaa   ", "trimmed  ").joinToString(lineSeparator))
        )
    }

    @ParameterizedTest
    @CsvSource(
        "null, null",
        "test \\n,test \\n",
        "'', ''",
        nullValues = ["null"],
    )
    fun testCases(input: String?, expected: String?) {
        assertEquals(expected, trimmingService.invoke(input))
    }

}