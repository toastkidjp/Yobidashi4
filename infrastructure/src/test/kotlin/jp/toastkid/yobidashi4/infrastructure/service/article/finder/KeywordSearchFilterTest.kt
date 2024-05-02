package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KeywordSearchFilterTest {

    @Test
    fun invoke() {
        val keywordSearchFilter = KeywordSearchFilter(null)
        assertTrue(keywordSearchFilter.invoke("This text exists for test."))
        assertTrue(keywordSearchFilter.invoke("これはtestです."))
        assertTrue(keywordSearchFilter.invoke("これはtetです."))
        assertTrue(keywordSearchFilter.invoke(""))
        assertFalse(keywordSearchFilter.invoke(null))
    }

    @Test
    fun invoke1() {
        val keywordSearchFilter = KeywordSearchFilter("test")
        assertTrue(keywordSearchFilter.invoke("This text exists for test."))
        assertTrue(keywordSearchFilter.invoke("これはtestです."))
        assertFalse(keywordSearchFilter.invoke("これはtetです."))
        assertFalse(keywordSearchFilter.invoke(""))
    }

    @Test
    fun invoke2() {
        val keywordSearchFilter = KeywordSearchFilter("test これ")
        assertTrue(keywordSearchFilter.invoke("This text exists for test."))
        assertTrue(keywordSearchFilter.invoke("これはtestです."))
        assertTrue(keywordSearchFilter.invoke("これはtetです."))
        assertFalse(keywordSearchFilter.invoke(""))
    }

    @Test
    fun invoke3() {
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