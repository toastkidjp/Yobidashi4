package jp.toastkid.yobidashi4.domain.model.web.search

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchUrlFactoryTest {

    private lateinit var searchUrlFactory: SearchUrlFactory

    @BeforeEach
    fun setUp() {
        searchUrlFactory = SearchUrlFactory()
    }

    @Test
    fun invoke() {
        assertTrue(searchUrlFactory.invoke("ラーメン").endsWith("=%E3%83%A9%E3%83%BC%E3%83%A1%E3%83%B3"))
        assertEquals("http://test.www.yahoo.co.jp", searchUrlFactory.invoke("http://test.www.yahoo.co.jp"))
        assertEquals("https://www.yahoo.co.jp", searchUrlFactory.invoke("https://www.yahoo.co.jp"))
    }
}