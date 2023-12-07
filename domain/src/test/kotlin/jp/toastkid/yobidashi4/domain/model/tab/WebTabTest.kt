package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import kotlin.io.path.pathString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
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
    fun iconPathIfUrlIsEmpty() {
        assertEquals("images/icon/ic_web.xml", WebTab().iconPath())
    }

    @Test
    fun iconPathIfNotFoundFaviconCase() {
        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().makeFolderIfNeed() } just Runs
        every { anyConstructed<WebIcon>().find(any()) } returns null

        assertEquals("images/icon/ic_web.xml", webTab.iconPath())
    }

    @Test
    fun iconPathIfNotExistsFileCase() {
        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().makeFolderIfNeed() } just Runs
        every { anyConstructed<WebIcon>().find(any()) } returns mockk()
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns false

        assertEquals("images/icon/ic_web.xml", webTab.iconPath())
    }

    @Test
    fun iconPath() {
        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().makeFolderIfNeed() } just Runs
        val path = mockk<Path>()
        every { anyConstructed<WebIcon>().find(any()) } returns path
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { path.pathString } returns "path/to/favicon"

        assertEquals("path/to/favicon", webTab.iconPath())
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
    fun reload() {
        webTab.reload()
    }

    @Test
    fun updateTitleAndUrl() {
        val countDownLatch = CountDownLatch(1)
        CoroutineScope(Dispatchers.Unconfined).launch {
            webTab.update().collect {
                assertEquals("test2", webTab.title())
                assertEquals("https://test.yahoo.co.jp/favicon.ico", webTab.url())
                countDownLatch.countDown()
            }
        }

        webTab.updateTitleAndUrl("test2", "https://test.yahoo.co.jp/favicon.ico")

        countDownLatch.await(5, TimeUnit.SECONDS)
    }

    @Test
    fun markdownLink() {
        println(webTab.markdownLink())
    }

}