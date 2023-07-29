package jp.toastkid.yobidashi4.domain.service.tool

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DateArticleUrlFactoryServiceTest {

    private lateinit var dateArticleUrlFactoryService: DateArticleUrlFactoryService

    @BeforeEach
    fun setUp() {
        dateArticleUrlFactoryService = DateArticleUrlFactoryService()
    }

    @Test
    fun test() {
        assertEquals("https://ja.wikipedia.org/wiki/1月23日", dateArticleUrlFactoryService(1, 23))
        assertEquals("https://ja.wikipedia.org/wiki/12月1日", dateArticleUrlFactoryService(12, 1))
    }

    @Test
    fun testIrregularCase() {
        assertTrue(dateArticleUrlFactoryService(0, 23).isEmpty())
        assertTrue(dateArticleUrlFactoryService(12, 32).isEmpty())
        assertTrue(dateArticleUrlFactoryService(13, 1).isEmpty())
        assertTrue(dateArticleUrlFactoryService(-13, 1).isEmpty())
        assertTrue(dateArticleUrlFactoryService(3, 0).isEmpty())
        assertTrue(dateArticleUrlFactoryService(3, -1).isEmpty())
    }

}