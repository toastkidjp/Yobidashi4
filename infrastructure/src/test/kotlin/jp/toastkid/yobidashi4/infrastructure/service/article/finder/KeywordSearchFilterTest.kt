package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class KeywordSearchFilterTest {

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., true",
        "これはtestです., true",
        "これはtetです., true",
        "'', true",
        "null, false",
        nullValues = ["null"]
    )
    fun invoke(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter(null)
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., true",
        "これはtestです., true",
        "これはtetです., false",
        "'', false",
        "null, false",
        nullValues = ["null"]
    )
    fun invokeWithSingleConstructorParameter(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter("test")
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., true",
        "これはtestです., true",
        "これはtetです., true",
        "'', false",
        "null, false",
        nullValues = ["null"]
    )
    fun invokeWithMultipleConstructorParameter(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter("test これ")
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., false",
        "これはtestです., true",
        "これはtetです., false",
        "'', false",
        "null, false",
        nullValues = ["null"]
    )
    fun invokeWithMultipleConstructorParameterForExactMatch(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter("test*これ*です")
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @Test
    fun invokeForExactMatch() {
        val keywordSearchFilter = KeywordSearchFilter("\"for test\"")
        assertTrue(keywordSearchFilter.invoke("This text exists for test."))
        assertFalse(keywordSearchFilter.invoke("これはtestです."))
        assertFalse(keywordSearchFilter.invoke("これはtetです."))
        assertFalse(keywordSearchFilter.invoke(""))
    }

    @Test
    fun init() {
        assertFalse(KeywordSearchFilter("input ").invoke("test"))
    }

}