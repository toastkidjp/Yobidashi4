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

    @Test
    fun invokeWithMultipleConstructorParameter() {
        val keywordSearchFilter = KeywordSearchFilter("test これ")
        assertTrue(keywordSearchFilter.invoke("This text exists for test."))
        assertTrue(keywordSearchFilter.invoke("これはtestです."))
        assertTrue(keywordSearchFilter.invoke("これはtetです."))
        assertFalse(keywordSearchFilter.invoke(""))
    }

    @Test
    fun invokeWithMultipleConstructorParameterForExactMatch() {
        val keywordSearchFilter = KeywordSearchFilter("test*これ*です")
        assertFalse(keywordSearchFilter.invoke("This text exists for test."))
        assertTrue(keywordSearchFilter.invoke("これはtestです."))
        assertFalse(keywordSearchFilter.invoke("これはtetです."))
        assertFalse(keywordSearchFilter.invoke(""))
    }

    @Test
    fun invoke4() {
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