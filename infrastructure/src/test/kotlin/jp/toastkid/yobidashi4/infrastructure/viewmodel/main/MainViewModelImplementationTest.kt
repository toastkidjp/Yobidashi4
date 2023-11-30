package jp.toastkid.yobidashi4.infrastructure.viewmodel.main

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.attribute.FileTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.domain.service.archive.TopArticleLoaderService
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainViewModelImplementationTest {

    private lateinit var subject: MainViewModelImplementation

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var topArticleLoaderService: TopArticleLoaderService

    @MockK
    private lateinit var desktop: Desktop

    @MockK
    private lateinit var webViewPool: WebViewPool

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Files::class, ImageIO::class)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { setting } bind(Setting::class)
                    single(qualifier = null) { topArticleLoaderService } bind(TopArticleLoaderService::class)
                    single(qualifier = null) { webViewPool } bind(WebViewPool::class)
                }
            )
        }
        every { setting.darkMode() } returns false
        every { setting.setDarkMode(any()) } just Runs
        every { topArticleLoaderService.invoke() } returns listOf(mockk(), mockk())
        every { webViewPool.dispose(any()) } just Runs

        mockkStatic(Desktop::class)
        every { Desktop.getDesktop() } returns desktop
        every { desktop.open(any()) } just Runs
        every { desktop.browse(any()) } just Runs

        subject = MainViewModelImplementation()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun switchDarkMode() {
        assertFalse(subject.darkMode())

        subject.switchDarkMode()

        assertTrue(subject.darkMode())
    }

    @Test
    fun loadBackgroundImageIfFileDoesNotExists() {
        assertNotNull(subject.backgroundImage())

        every { setting.useBackground() } returns true
        mockkStatic(Files::class, ImageIO::class)
        every { Files.exists(any()) } returns false
        every { Files.list(any()) } returns Stream.empty()
        every { ImageIO.read(any<InputStream>()) } returns BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB_PRE)

        subject.loadBackgroundImage()

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.list(any()) }
        verify(inverse = true) { ImageIO.read(any<InputStream>()) }
    }


    @Test
    fun loadBackgroundImageIfListIsEmpty() {
        assertNotNull(subject.backgroundImage())

        every { setting.useBackground() } returns true
        mockkStatic(Files::class, ImageIO::class)
        every { Files.exists(any()) } returns true
        every { Files.list(any()) } returns Stream.empty()
        every { ImageIO.read(any<InputStream>()) } returns BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB_PRE)

        subject.loadBackgroundImage()

        verify { Files.exists(any()) }
        verify { Files.list(any()) }
        verify(inverse = true) { ImageIO.read(any<InputStream>()) }
    }

    @Test
    fun loadBackgroundImage() {
        assertNotNull(subject.backgroundImage())

        every { setting.useBackground() } returns true
        every { Files.exists(any()) } returns true
        every { Files.list(any()) } returns Stream.of(mockk())
        val inputStream = mockk<InputStream>()
        every { Files.newInputStream(any()) } returns inputStream
        every { inputStream.close() } just Runs
        every { ImageIO.read(any<InputStream>()) } returns BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB_PRE)

        subject.loadBackgroundImage()

        verify { Files.exists(any()) }
        verify { Files.list(any()) }
        verify { ImageIO.read(any<InputStream>()) }
        verify { inputStream.close() }
    }

    @Test
    fun setSelectedIndex() {
        subject.openTab(LoanCalculatorTab())
        subject.openTab(LoanCalculatorTab())

        assertEquals(1, subject.selected.value)
        subject.setSelectedIndex(-1)
        assertEquals(1, subject.selected.value)
        subject.setSelectedIndex(2)
        assertEquals(1, subject.selected.value)

        subject.setSelectedIndex(0)
        assertEquals(0, subject.selected.value)

        assertEquals(0, subject.tabs.indexOf(subject.currentTab()))
    }

    @Test
    fun getTabs() {
        subject.openTab(LoanCalculatorTab())
        subject.openTab(LoanCalculatorTab())

        assertEquals(2, subject.tabs.size)
    }

    @Test
    fun openFileListTab() {
        every { Files.getLastModifiedTime(any()) } returns FileTime.fromMillis(System.currentTimeMillis())

        subject.openFileListTab("test", listOf(mockk(), mockk()), true, FileTab.Type.FIND)

        verify { Files.getLastModifiedTime(any()) }
        assertEquals(1, subject.tabs.size)
    }

    @Test
    fun openFile() {
    }

    @Test
    fun openPreview() {
    }

    @Test
    fun noopWebSearch() {
        mockkConstructor(SearchUrlFactory::class)
        every { anyConstructed<SearchUrlFactory>().invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"

        subject.webSearch(null, false)

        assertTrue(subject.tabs.isEmpty())
        verify(inverse = true) { anyConstructed<SearchUrlFactory>().invoke(any()) }
    }

    @Test
    fun webSearch() {
        mockkConstructor(SearchUrlFactory::class)
        every { anyConstructed<SearchUrlFactory>().invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"

        subject.webSearch("test", false)

        val tab = subject.tabs.first() as WebTab
        assertEquals("https://search.yahoo.co.jp/search?p=test", tab.url())
        verify { anyConstructed<SearchUrlFactory>().invoke(any()) }
    }

    @Test
    fun noopBrowseUri() {
        subject.browseUri(null)

        verify(inverse = true) { desktop.browse(any()) }
    }

    @Test
    fun browseUri() {
        subject.browseUri("https://www.yahoo.com")

        verify { Desktop.getDesktop() }
        verify { desktop.browse(any()) }
    }

    @Test
    fun removeTabAt() {
        val tab = mockk<Tab>()
        every { tab.closeable() } returns false
        subject.openTab(tab)
        val webTab = mockk<WebTab>()
        every { webTab.closeable() } returns true
        every { webTab.id() } returns "test"
        subject.openTab(webTab)
        subject.openTab(mockk<EditorTab>())

        subject.removeTabAt(0)

        assertEquals(3, subject.tabs.size)

        subject.setSelectedIndex(2)
        subject.removeTabAt(1)

        assertEquals(2, subject.tabs.size)
        assertEquals(1, subject.selected.value)
        assertTrue(subject.currentTab() is EditorTab)
        verify { tab.closeable() }
        verify { webTab.closeable() }
        verify { webViewPool.dispose("test") }
    }

    @Test
    fun openTextFile() {
    }

    @Test
    fun openUrl() {
    }

    @Test
    fun edit() {
    }

    @Test
    fun updateWebTab() {
    }

    @Test
    fun closeCurrent() {
    }

    @Test
    fun closeOtherTabs() {
    }

    @Test
    fun closeAllTabs() {
    }

    @Test
    fun addNewArticle() {
    }

    @Test
    fun saveCurrentEditorTab() {
    }

    @Test
    fun saveAllEditorTab() {
    }

    @Test
    fun updateEditorContent() {
    }

    @Test
    fun openingEditor() {
        assertFalse(subject.openingEditor())

        subject.openTab(mockk<EditorTab>())

        assertTrue(subject.openingEditor())
    }

    @Test
    fun setShowWebSearch() {
        assertFalse(subject.showWebSearch())

        subject.setShowWebSearch(true)

        assertTrue(subject.showWebSearch())
    }

    @Test
    fun switchAggregationBox() {
        assertFalse(subject.showAggregationBox())

        subject.switchAggregationBox(true)

        assertTrue(subject.showAggregationBox())
    }

    @Test
    fun initialAggregationType() {
        assertEquals(0, subject.initialAggregationType())

        subject.setInitialAggregationType(2)

        assertEquals(2, subject.initialAggregationType())
    }

    @Test
    fun setShowInputBox() {
        assertFalse(subject.showInputBox())

        subject.setShowInputBox(null)

        assertFalse(subject.showInputBox())
        subject.invokeInputAction("test")

        val action = mockk<(String) -> Unit>()
        every { action.invoke(any()) } just Runs
        subject.setShowInputBox(action)

        assertTrue(subject.showInputBox())
        subject.invokeInputAction("test")
        verify { action.invoke("test") }
    }

    @Test
    fun windowState() {
        val initialWindowState = subject.windowState()
        assertTrue(initialWindowState.size.width > 600.dp)
        assertTrue(initialWindowState.size.height > 600.dp)
        assertEquals(WindowPosition(Alignment.Center), initialWindowState.position)
        assertEquals(WindowPlacement.Floating, initialWindowState.placement)
        assertEquals("Full screen", subject.toggleFullscreenLabel())

        subject.toggleFullscreen()

        assertEquals(WindowPlacement.Maximized, initialWindowState.placement)
        assertEquals("Exit full screen", subject.toggleFullscreenLabel())

        subject.toggleFullscreen()

        assertEquals(WindowPlacement.Floating, initialWindowState.placement)
        assertEquals("Full screen", subject.toggleFullscreenLabel())
    }

    @Test
    fun toggleNarrowWindow() {
        val size = subject.windowState().size
        assertEquals(1100.dp, size.width)
        val height = size.height

        subject.toggleNarrowWindow()

        assertEquals(520.dp, subject.windowState().size.width)
        assertEquals(height, subject.windowState().size.height)
    }

    @Test
    fun showingSnackbar() {
        assertFalse(subject.showingSnackbar())
        assertNull(subject.snackbarHostState().currentSnackbarData)

        val countDownLatch = CountDownLatch(1)
        subject.showSnackbar("test", "Test", { countDownLatch.countDown() })
        subject.snackbarHostState().currentSnackbarData?.performAction()
        countDownLatch.await(1, TimeUnit.SECONDS)

        assertTrue(subject.showingSnackbar())
        assertNotNull(subject.snackbarHostState().currentSnackbarData)
    }

    @Test
    fun switchArticleList() {
        assertFalse(subject.openArticleList())

        subject.switchArticleList()

        assertTrue(subject.openArticleList())

        subject.hideArticleList()

        assertFalse(subject.openArticleList())

        subject.hideArticleList()

        assertFalse(subject.openArticleList())
    }

    @Test
    fun articles() {
        val articles = subject.articles()

        assertTrue(articles.isEmpty())
    }

    @Test
    fun reloadAllArticle() {
        subject.reloadAllArticle()

        val articles = subject.articles()
        assertEquals(2, articles.size)
    }

    @Test
    fun switchMemoryUsageBox() {
        assertFalse(subject.openMemoryUsageBox())

        subject.switchMemoryUsageBox()

        assertTrue(subject.openMemoryUsageBox())
    }

    @Test
    fun switchFind() {
        assertFalse(subject.openFind())

        subject.switchFind()

        assertTrue(subject.openFind())

        subject.onFindInputChange(TextFieldValue("test"))
        val countDownLatch = CountDownLatch(1)
        val job = CoroutineScope(Dispatchers.Unconfined).launch {
            subject.finderFlow().collectLatest {
                assertSame(it, FindOrder.EMPTY)
                countDownLatch.countDown()
            }
        }
        subject.switchFind()

        assertTrue(subject.inputValue().text.isEmpty())
        job.cancel()
        countDownLatch.await(1, TimeUnit.SECONDS)
    }

    @Test
    fun onFindInputChange() {
        assertTrue(subject.inputValue().text.isEmpty())

        subject.onFindInputChange(TextFieldValue("test"))

        assertEquals("test", subject.inputValue().text)
    }

    @Test
    fun onReplaceInputChange() {
        assertTrue(subject.replaceInputValue().text.isEmpty())

        subject.onReplaceInputChange(TextFieldValue("test"))

        assertEquals("test", subject.replaceInputValue().text)
    }

    @Test
    fun findUp() {
        val countDownLatch = CountDownLatch(1)

        val job = CoroutineScope(Dispatchers.Unconfined).launch {
            subject.finderFlow().collect {
                assertTrue(it.upper)
                countDownLatch.countDown()
            }
        }

        subject.findUp()

        countDownLatch.await(1, TimeUnit.SECONDS)
        job.cancel()
    }

    @Test
    fun replaceAll() {
    }

    @Test
    fun findDown() {
        val countDownLatch = CountDownLatch(1)

        val job = CoroutineScope(Dispatchers.Unconfined).launch {
            subject.finderFlow().collect {
                assertFalse(it.upper)
                countDownLatch.countDown()
            }
        }

        subject.findUp()

        countDownLatch.await(1, TimeUnit.SECONDS)
        job.cancel()
    }

    @Test
    fun findStatus() {
        assertTrue(subject.findStatus().isEmpty())

        subject.setFindStatus("test")

        assertEquals("test", subject.findStatus())
    }

    @Test
    fun droppedPathFlow() {
    }

    @Test
    fun emitDroppedPath() {
    }

    @Test
    fun slideshowPath() {
    }

    @Test
    fun slideshow() {
    }

    @Test
    fun closeSlideshow() {
    }

    @Test
    fun setTextManager() {
    }

    @Test
    fun selectedText() {
    }

}