package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebTabTest {

    private lateinit var webTab: WebTab

    @BeforeEach
    fun setUp() {
        webTab = WebTab("test", "https://test.yahoo.co.jp")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertEquals("test", webTab.title())
    }

    @Test
    fun url() {
        assertEquals("https://test.yahoo.co.jp", webTab.url())
    }

    @Test
    fun closeable() {
        assertTrue(webTab.closeable())
    }

    @Test
    fun iconPath() {
        assertNull(webTab.iconPath())
    }

    @Test
    fun isReadableUrl() {
        assertTrue(webTab.isReadableUrl())
        assertTrue(WebTab("http", "http://a/b").isReadableUrl())
        assertFalse(WebTab("ftp", "ftp://a/b").isReadableUrl())
    }

    @Test
    fun id() {
        assertNotNull(webTab.id())
    }

    @Test
    fun markdownLink() {
        println(webTab.markdownLink())
    }

}