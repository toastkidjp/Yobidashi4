package jp.toastkid.yobidashi4.infrastructure.viewmodel.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.invoke
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Month
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.ScrollableContentTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.domain.service.archive.TopArticleLoaderService
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import jp.toastkid.yobidashi4.domain.service.editor.EditorTabFileStore
import jp.toastkid.yobidashi4.infrastructure.service.media.MediaPlayerInvokerImplementation
import kotlin.io.path.extension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    @MockK
    private lateinit var webHistoryRepository: WebHistoryRepository

    @MockK
    private lateinit var articleFactory: ArticleFactory

    @MockK
    private lateinit var keywordSearch: FullTextArticleFinder

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
                    single(qualifier = null) { webHistoryRepository } bind(WebHistoryRepository::class)
                    single(qualifier = null) { articleFactory } bind(ArticleFactory::class)
                    single(qualifier = null) { keywordSearch } bind (FullTextArticleFinder::class)
                }
            )
        }
        every { setting.darkMode() } returns false
        every { setting.setDarkMode(any()) } just Runs
        every { setting.setUseCaseSensitiveInFinder(any()) } just Runs
        every { setting.useCaseSensitiveInFinder() } returns false
        every { topArticleLoaderService.invoke() } returns listOf(mockk(), mockk())
        every { webViewPool.dispose(any()) } just Runs
        val aggregationResult = mockk<AggregationResult>()
        every { keywordSearch.invoke(any()) } returns aggregationResult
        every { aggregationResult.isEmpty() } returns false
        every { aggregationResult.title() } returns "test"

        mockkStatic(Desktop::class)
        every { Desktop.getDesktop() } returns desktop
        every { desktop.open(any()) } just Runs
        every { desktop.browse(any()) } just Runs

        mockkConstructor(EditorTabFileStore::class)
        every { anyConstructed<EditorTabFileStore>().invoke(any(), any()) } just Runs

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
    fun loadBackgroundImageIfUseBackgroundReturnsFalse() {
        assertNotNull(subject.backgroundImage())

        every { setting.useBackground() } returns false
        every { Files.exists(any()) } returns true

        subject.loadBackgroundImage()

        verify(inverse = true) { Files.exists(any()) }
    }

    @Test
    fun showBackgroundImage() {
        assertFalse(subject.showBackgroundImage())

        subject = spyk(subject)
        every { subject.backgroundImage() } returns ImageBitmap(0, 1)

        assertTrue(subject.showBackgroundImage())
    }

    @Test
    fun switchUseBackground() {
        every { setting.switchUseBackground() } just Runs
        every { setting.useBackground() } returns true
        every { Files.exists(any()) } returns false

        subject.switchUseBackground()

        verify { setting.switchUseBackground() }
        verify { setting.useBackground() }
    }

    @Test
    fun switchUseBackgroundWithDoesNotUseBackground() {
        every { setting.switchUseBackground() } just Runs
        every { setting.useBackground() } returns false
        every { Files.exists(any()) } returns false

        subject.switchUseBackground()

        verify { setting.switchUseBackground() }
        verify { setting.useBackground() }
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
    fun noopMoveTabIndexIfTabsIsEmpty() {
        subject = spyk(subject)
        every { subject.setSelectedIndex(any()) } just Runs

        subject.moveTabIndex(1)

        verify(inverse = true) { subject.setSelectedIndex(any()) }
    }

    @Test
    fun moveTabIndexOverCase() {
        subject.openTab(mockk())
        subject.openTab(mockk())

        subject.moveTabIndex(1)

        assertEquals(0, subject.selected.value)
    }

    @Test
    fun moveTabIndexUnderCase() {
        subject.openTab(mockk())
        subject.openTab(mockk())
        subject.setSelectedIndex(0)

        subject.moveTabIndex(-1)

        assertEquals(1, subject.selected.value)
    }

    @Test
    fun moveTabIndex() {
        subject.openTab(mockk())
        subject.openTab(mockk())

        subject.moveTabIndex(-1)

        assertEquals(0, subject.selected.value)
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
    fun openFileWithMusicFile() {
        mockkStatic(Files::class, Desktop::class)
        every { Files.exists(any()) } returns true
        val desktop = mockk<Desktop>()
        every { Desktop.getDesktop() } returns desktop
        every { desktop.open(any()) } just Runs
        mockkConstructor(MediaPlayerInvokerImplementation::class)
        every { anyConstructed<MediaPlayerInvokerImplementation>().invoke(any()) } just Runs
        val path = mockk<Path>()
        every { path.extension } returns "test.m4a"

        subject.openFile(path)

        verify { Files.exists(any()) }
        verify { anyConstructed<MediaPlayerInvokerImplementation>().invoke(any()) }
        verify(inverse = true) { Desktop.getDesktop() }
    }

    @Test
    fun openFile() {
        mockkStatic(Files::class, Desktop::class)
        every { Files.exists(any()) } returns true
        val desktop = mockk<Desktop>()
        every { Desktop.getDesktop() } returns desktop
        every { desktop.open(any()) } just Runs
        mockkConstructor(MediaPlayerInvokerImplementation::class)
        every { anyConstructed<MediaPlayerInvokerImplementation>().invoke(any()) } just Runs
        val path = mockk<Path>()
        every { path.extension } returns "test.txt"
        every { path.toFile() } returns mockk()

        subject.openFile(path)

        verify { Files.exists(any()) }
        verify(inverse = true) { anyConstructed<MediaPlayerInvokerImplementation>().invoke(any()) }
        verify { Desktop.getDesktop() }
        verify { desktop.open(any()) }
    }

    @Test
    fun openFileWhenFileIsNotFound() {
        mockkStatic(Files::class, Desktop::class)
        every { Files.exists(any()) } returns false
        val desktop = mockk<Desktop>()
        every { Desktop.getDesktop() } returns desktop
        every { desktop.open(any()) } just Runs
        mockkConstructor(MediaPlayerInvokerImplementation::class)
        every { anyConstructed<MediaPlayerInvokerImplementation>().invoke(any()) } just Runs
        val path = mockk<Path>()
        every { path.extension } returns "test.txt"
        every { path.toFile() } returns mockk()

        subject.openFile(path)

        verify { Files.exists(any()) }
        verify(inverse = true) { anyConstructed<MediaPlayerInvokerImplementation>().invoke(any()) }
        verify(inverse = true) { Desktop.getDesktop() }
        verify(inverse = true) { desktop.open(any()) }
    }

    @Test
    fun openPreviewOnBackground() {
        mockkObject(MarkdownPreviewTab)
        every { MarkdownPreviewTab.with(any()) } returns mockk()

        subject.openPreview(mockk(), true)

        verify { MarkdownPreviewTab.with(any()) }
        assertEquals(0, subject.selected.value)
    }

    @Test
    fun openPreview() {
        mockkObject(MarkdownPreviewTab)
        every { MarkdownPreviewTab.with(any()) } returns mockk()

        subject.openPreview(mockk(), false)

        verify { MarkdownPreviewTab.with(any()) }
        assertEquals(0, subject.selected.value)
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
    fun webSearchWithNull() {
        mockkConstructor(SearchUrlFactory::class)
        every { anyConstructed<SearchUrlFactory>().invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"

        subject.webSearch(null, false)

        assertTrue(subject.tabs.isEmpty())
        verify(inverse = true) { anyConstructed<SearchUrlFactory>().invoke(any()) }
    }

    @Test
    fun webSearchWithBlank() {
        mockkConstructor(SearchUrlFactory::class)
        every { anyConstructed<SearchUrlFactory>().invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"

        subject.webSearch(" ", false)

        assertTrue(subject.tabs.isEmpty())
        verify(inverse = true) { anyConstructed<SearchUrlFactory>().invoke(any()) }
    }

    @Test
    fun webSearchWithSelectedText() {
        mockkConstructor(SearchUrlFactory::class)
        every { anyConstructed<SearchUrlFactory>().invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"

        subject.webSearchWithSelectedText()

        assertTrue(subject.tabs.isEmpty())
        verify(inverse = true) { anyConstructed<SearchUrlFactory>().invoke(any()) }
    }

    @Test
    fun noopBrowseUri() {
        subject.browseUri(null)

        verify(inverse = true) { desktop.browse(any()) }
    }

    @Test
    fun noopBrowseUriWithBlank() {
        subject.browseUri(" ")

        verify(inverse = true) { desktop.browse(any()) }
    }

    @Test
    fun browseUri() {
        subject.browseUri("https://www.yahoo.com")

        verify { Desktop.getDesktop() }
        verify { desktop.browse(any()) }
    }

    @Test
    fun browseUriWithHttpUrl() {
        subject.browseUri("http://www.yahoo.com")

        verify { Desktop.getDesktop() }
        verify { desktop.browse(any()) }
    }

    @Test
    fun browseUriWithCurrentTabsUrl() {
        val webTab = mockk<WebTab>()
        every { webTab.url() } returns "https://www.yahoo.com"
        subject.openTab(webTab)

        subject.browseUri("test")

        verify { webTab.url() }
        verify { Desktop.getDesktop() }
        verify { desktop.browse(any()) }
    }

    @Test
    fun browseUriWithCurrentTabIsNotWebTab() {
        subject.browseUri("test")

        verify(inverse = true) { Desktop.getDesktop() }
        verify(inverse = true) { desktop.browse(any()) }
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
        subject.openTextFile(mockk())

        assertEquals(1, subject.tabs.size)
    }

    @Test
    fun openWorldTime() {
        assertFalse(subject.openWorldTime())

        subject.toggleWorldTime()

        assertTrue(subject.openWorldTime())

        subject.toggleWorldTime()

        assertFalse(subject.openWorldTime())
    }

    @Test
    fun openUrl() {
        subject.openTab(mockk())

        subject.openUrl("https://www.yahoo.co.jp", false)

        assertTrue(subject.currentTab() is WebTab)
    }

    @Test
    fun openUrlWithHttpUrl() {
        subject.openTab(mockk())

        subject.openUrl("http://www.yahoo.co.jp", false)

        assertTrue(subject.currentTab() is WebTab)
    }

    @Test
    fun noopOpenUrl() {
        subject.openUrl("ftp://www.yahoo.co.jp", false)

        assertTrue(subject.tabs.isEmpty())
    }

    @Test
    fun openUrlOnBackground() {
        subject.openTab(mockk())

        subject.openUrl("https://www.yahoo.co.jp", true)

        assertFalse(subject.currentTab() is WebTab)
    }

    @Test
    fun openUrlOnBackgroundWithHttpUrl() {
        subject.openTab(mockk())

        subject.openUrl("http://www.yahoo.co.jp", true)

        assertFalse(subject.currentTab() is WebTab)
    }

    @Test
    fun noopEditWhenFileDoesNotExists() {
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns false

        subject.edit(mockk())

        assertTrue(subject.tabs.isEmpty())
    }

    @Test
    fun edit() {
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.readString(any()) } returns "test"

        subject.edit(mockk(), true)

        assertEquals(1, subject.tabs.size)
        verify { Files.readString(any()) }
    }

    @Test
    fun editWhenTabsHasAlreadyOpened() {
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.readString(any()) } returns "test"
        val path = mockk<Path>()
        val tab = mockk<EditorTab>()
        every { tab.path } returns path
        subject.openTab(tab)

        subject.edit(path, true)

        assertEquals(1, subject.tabs.size)
        assertEquals(0, subject.selected.value)
        verify(inverse = true) { Files.readString(any()) }
    }

    @Test
    fun editWithTitle() {
        val article = mockk<Article>()
        every { articleFactory.withTitle(any()) } returns article
        every { article.path() } returns mockk()
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.readString(any()) } returns "test"

        subject.editWithTitle("test")

        assertEquals(1, subject.tabs.size)
        verify { Files.readString(any()) }
        verify { articleFactory.withTitle(any()) }
    }

    @Test
    fun editWithTitleOnBackground() {
        val article = mockk<Article>()
        every { articleFactory.withTitle(any()) } returns article
        every { article.path() } returns mockk()
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.readString(any()) } returns "test"

        subject.editWithTitle("test", true)

        assertEquals(1, subject.tabs.size)
        verify { Files.readString(any()) }
        verify { articleFactory.withTitle(any()) }
    }

    @Test
    fun noopUpdateWebTab() {
        every { webHistoryRepository.add(any(), any()) } just Runs

        subject.updateWebTab("test", "New title", "https://www.newtitle.co.jp")

        verify { webHistoryRepository wasNot called }
    }

    @Test
    fun updateWebTab() {
        val tab = mockk<WebTab>()
        every { tab.id() } returns "test"
        every { tab.updateTitleAndUrl(any(), any()) } just Runs
        subject.openTab(tab)
        every { webHistoryRepository.add(any(), any()) } just Runs

        subject.updateWebTab("test", "New title", "https://www.newtitle.co.jp")

        verify { tab.updateTitleAndUrl(any(), any()) }
    }

    @Test
    fun updateWebTabOnlyTitle() {
        val tab = mockk<WebTab>()
        every { tab.id() } returns "test"
        every { tab.updateTitleAndUrl(any(), any()) } just Runs
        subject.openTab(tab)
        every { webHistoryRepository.add(any(), any()) } just Runs

        subject.updateWebTab("test", "New title", null)

        verify { tab.updateTitleAndUrl(any(), null) }
        verify { webHistoryRepository wasNot called }
    }

    @Test
    fun updateCalendarTab() {
        val tab = mockk<CalendarTab>()
        subject.openTab(tab)

        subject.updateCalendarTab(tab, 2024, 2)

        val updatedTab = subject.tabs[0] as CalendarTab
        assertEquals(2024, updatedTab.localDate().year)
        assertEquals(Month.FEBRUARY, updatedTab.localDate().month)
    }

    @Test
    fun noopUpdateCalendarTab() {
        subject.updateCalendarTab(mockk(), 2024, 2)

        assertTrue(subject.tabs.isEmpty())
    }

    @Test
    fun updateScrollableTab() {
        val tab = mockk<WebBookmarkTab>()
        val newTab = mockk<WebBookmarkTab>()
        every { tab.withNewPosition(any()) } returns newTab
        subject.openTab(tab)

        subject.updateScrollableTab(tab, 20)

        val updatedTab = subject.tabs[0] as ScrollableContentTab
        verify { tab.withNewPosition(any()) }
        assertSame(newTab, updatedTab)
    }

    @Test
    fun noopUpdateScrollableTab() {
        subject.updateScrollableTab(mockk(), 20)

        assertTrue(subject.tabs.isEmpty())
    }

    @Test
    fun replaceTab() {
        val tab = mockk<WebBookmarkTab>()
        val newTab = mockk<WebBookmarkTab>()
        every { tab.withNewPosition(any()) } returns newTab
        subject.openTab(mockk())
        subject.openTab(tab)

        subject.replaceTab(tab, newTab)

        val updatedTab = subject.tabs[1]
        assertSame(newTab, updatedTab)
    }

    @Test
    fun noopReplaceTab() {
        subject.replaceTab(mockk(), mockk())

        assertTrue(subject.tabs.isEmpty())
    }

    @Test
    fun closeCurrent() {
        subject.closeCurrent()

        assertTrue(subject.tabs.isEmpty())
    }

    @Test
    fun closeOtherTabs() {
        val tab = mockk<WebTab>()
        every { tab.id() } returns "test"
        subject.openTab(mockk())
        subject.openTab(tab)
        val keep = mockk<EditorTab>()
        subject.openTab(keep)
        subject.openTab(mockk())
        subject.setSelectedIndex(2)

        subject.closeOtherTabs()

        assertEquals(0, subject.selected.value)
        assertSame(keep, subject.currentTab())
        verify(exactly = 1) { webViewPool.dispose(any()) }
    }

    @Test
    fun closeOtherTabsOnEmptyCase() {
        subject.closeOtherTabs()

        assertEquals(0, subject.selected.value)
        assertNull(subject.currentTab())
        verify { webViewPool wasNot called }
    }

    @Test
    fun closeAllTabsOnEmptyCase() {
        subject.closeAllTabs()

        verify { webViewPool wasNot called }
    }

    @Test
    fun closeAllTabs() {
        subject = spyk(subject)
        val slot = slot<() -> Unit>()
        every { subject.showSnackbar(any(), any(), capture(slot)) } just Runs
        val tab = mockk<WebTab>()
        every { tab.id() } returns "test"
        subject.openTab(mockk())
        subject.openTab(tab)

        subject.closeAllTabs()
        slot.invoke()

        verify(exactly = 1) { webViewPool.dispose(any()) }
        assertEquals(2, subject.tabs.size)
        assertEquals(1, subject.selected.value)
    }

    @Test
    fun makeNewArticle() {
        every { setting.articleFolderPath() } returns mockk()
        every { Files.list(any()) } returns Stream.empty()
        subject = spyk(subject)
        every { subject.edit(any()) } just Runs
        val article = mockk<Article>()
        val slot = slot<() -> String>()
        every { article.makeFile(capture(slot)) } just Runs
        every { article.getTitle() } returns "title"
        every { article.path() } returns mockk()
        every { articleFactory.withTitle(any()) } returns article

        subject.makeNewArticle()

        assertTrue(subject.showInputBox())

        subject.invokeInputAction("test")
        slot.captured.invoke()

        verify { article.getTitle() }
        verify { subject.edit(any()) }
    }

    @Test
    fun noopMakeNewArticleIfExistsPath() {
        val extension = "png"
        val path = mockk<Path>()
        every { setting.articleFolderPath() } returns path
        every { path.extension } returns extension
        every { Files.list(any()) } returns Stream.of(path)
        subject = spyk(subject)
        every { subject.edit(any()) } just Runs

        subject.makeNewArticle()

        assertTrue(subject.showInputBox())

        subject.invokeInputAction(extension)

        verify(inverse = true) { subject.edit(any()) }
    }

    @Test
    fun saveCurrentEditorTab() {
        subject.openTab(mockk<EditorTab>())

        subject.saveCurrentEditorTab()

        verify { anyConstructed<EditorTabFileStore>().invoke(any(), any()) }
    }

    @Test
    fun noopSaveCurrentEditorTab() {
        subject.saveCurrentEditorTab()

        verify(inverse = true) { anyConstructed<EditorTabFileStore>().invoke(any(), any()) }
    }

    @Test
    fun saveAllEditorTab() {
        subject.openTab(mockk<EditorTab>())
        subject.openTab(mockk())
        subject.openTab(mockk<EditorTab>())

        subject.saveAllEditorTab()

        verify(exactly = 2) { anyConstructed<EditorTabFileStore>().invoke(any(), any()) }
    }

    @Test
    fun noopSaveAllEditorTab() {
        subject.saveAllEditorTab()

        verify(inverse = true) { anyConstructed<EditorTabFileStore>().invoke(any(), any()) }
    }

    @Test
    fun updateEditorContent() {
        val path = mockk<Path>()
        val tab = mockk<EditorTab>()
        every { tab.path } returns path
        every { tab.setContent(any(), any()) } just Runs
        every { tab.setCaretPosition(any()) } just Runs
        every { tab.setScroll(any()) } just Runs
        subject.openTab(tab)

        subject.updateEditorContent(path, "test", 1, 1.0, true)

        verify { tab.path }
        verify { tab.setContent(any(), any()) }
        verify { tab.setCaretPosition(any()) }
        verify { tab.setScroll(any()) }
    }

    @Test
    fun updateEditorContentFilterOutCase() {
        val path = mockk<Path>()
        val tab = mockk<EditorTab>()
        every { tab.path } returns mockk()
        every { tab.setContent(any(), any()) } just Runs
        every { tab.setCaretPosition(any()) } just Runs
        every { tab.setScroll(any()) } just Runs
        subject.openTab(tab)

        subject.updateEditorContent(path, "test", 1, 1.0, true)

        verify { tab.path }
        verify(inverse = true) { tab.setContent(any(), any()) }
        verify(inverse = true) { tab.setCaretPosition(any()) }
        verify(inverse = true) { tab.setScroll(any()) }
    }

    @Test
    fun updateEditorContentDefaultArgumentsCase() {
        val path = mockk<Path>()
        val tab = mockk<EditorTab>()
        every { tab.path } returns path
        every { tab.setContent(any(), any()) } just Runs
        every { tab.setCaretPosition(any()) } just Runs
        every { tab.setScroll(any()) } just Runs
        subject.openTab(tab)

        subject.updateEditorContent(path, "test", resetEditing = false)

        verify { tab.path }
        verify { tab.setContent(any(), any()) }
        verify(inverse = true) { tab.setCaretPosition(any()) }
        verify(inverse = true) { tab.setScroll(any()) }
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

        subject.setShowWebSearch(false)

        assertFalse(subject.showWebSearch())
    }

    @Test
    fun setShowWebSearchWithDefaultParameter() {
        assertFalse(subject.showWebSearch())

        subject.setShowWebSearch()

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

        subject.setShowInputBox()

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
    fun invokeInputActionWithEmpty() {
        val action = mockk<(String) -> Unit>()
        every { action.invoke(any()) } just Runs
        subject.setShowInputBox(action)

        subject.invokeInputAction("")

        verify { action wasNot called }
    }

    @Test
    fun invokeInputActionWithNull() {
        val action = mockk<(String) -> Unit>()
        every { action.invoke(any()) } just Runs
        subject.setShowInputBox(action)

        subject.invokeInputAction(null)

        verify { action wasNot called }
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

        subject.toggleNarrowWindow()

        assertEquals(1100.dp, subject.windowState().size.width)
        assertEquals(height, subject.windowState().size.height)
    }

    @Test
    fun toDefaultWindowSize() {
        val size = subject.windowState().size
        assertEquals(1100.dp, size.width)
        val height = size.height
        subject.toggleNarrowWindow()

        subject.toDefaultWindowSize()

        assertEquals(1100.dp, subject.windowState().size.width)
        assertEquals(height, subject.windowState().size.height)
    }

    @Test
    fun showingSnackbarWithDefaultParameter() {
        assertFalse(subject.showingSnackbar())
        assertNull(subject.snackbarHostState().currentSnackbarData)

        val countDownLatch = CountDownLatch(1)
        subject.showSnackbar("test")
        subject.snackbarHostState().currentSnackbarData?.performAction()
        countDownLatch.await(1, TimeUnit.SECONDS)

        assertNotNull(subject.snackbarHostState().currentSnackbarData)
        assertNull(subject.snackbarHostState().currentSnackbarData?.actionLabel)
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
    fun dismissSnackbar() {
        subject.dismissSnackbar()

        subject.showSnackbar("test")
        subject.dismissSnackbar()
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
            subject.finderFlow().collect {
                if (it == FindOrder.EMPTY) {
                    countDownLatch.countDown()
                }
            }
        }
        subject.switchFind()

        assertTrue(subject.inputValue().text.isEmpty())
        job.cancel()
        countDownLatch.await(3, TimeUnit.SECONDS)
        assertEquals(0, countDownLatch.count)
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
        val countDownLatch = CountDownLatch(1)

        val job = CoroutineScope(Dispatchers.Unconfined).launch {
            subject.finderFlow().collect {
                assertTrue(it.invokeReplace)
                countDownLatch.countDown()
            }
        }
        subject.replaceAll()

        countDownLatch.await(1, TimeUnit.SECONDS)
        job.cancel()
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

        subject.findDown()

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
    fun launchDroppedPathFlow() {
        subject = spyk(subject)
        every { subject.edit(any(), any()) } just Runs
        every { subject.openTab(any()) } just Runs

        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.launchDroppedPathFlow()
        }

        subject.emitDroppedPath(listOf("zip", "txt", "md", "log", "java", "kt", "py", "jpg", "webp", "png", "gif").map(::makePath))
        verify(exactly = 6) { subject.edit(any(), any()) }
        verify(exactly = 4) { subject.openTab(any()) }

        val countDownLatch = CountDownLatch(1)
        subject.registerDroppedPathReceiver {
            countDownLatch.countDown()
        }

        subject.emitDroppedPath(listOf(makePath("jpg")))

        countDownLatch.await(3, TimeUnit.SECONDS)

        subject.unregisterDroppedPathReceiver()
    }

    private fun makePath(extension: String): Path {
        val path = mockk<Path>()
        every { path.fileName } returns path
        every { path.toString() } returns "test.$extension"
        return path
    }

    @Test
    fun slideshow() {
        assertNull(subject.slideshowPath())

        val path = mockk<Path>()
        subject.slideshow(path)

        assertSame(path, subject.slideshowPath())

        subject.closeSlideshow()

        assertNull(subject.slideshowPath())
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun setTextManager() {
        assertNull(subject.selectedText())

        val textManager = mockk<TextContextMenu.TextManager>()
        val annotatedString = mockk<AnnotatedString>()
        every { textManager.selectedText } returns annotatedString
        every { annotatedString.text } returns "test"

        subject.setTextManager(textManager)
        assertEquals("test", subject.selectedText())
    }

    @Test
    fun caseSensitive() {
        assertFalse(subject.caseSensitive())

        subject.switchCaseSensitive()

        assertTrue(subject.caseSensitive())
        verify { setting.useCaseSensitiveInFinder() }
        verify { setting.setUseCaseSensitiveInFinder(true) }
    }

    @Test
    fun windowVisible() {
        assertTrue(subject.windowVisible())
    }

    @Test
    fun trayState() {
        subject.trayState()
    }

    @Test
    fun sendNotification() {
        subject = spyk(subject)
        val trayState = mockk<TrayState>()
        every { subject.trayState() } returns trayState
        every { trayState.sendNotification(any()) } just Runs

        subject.sendNotification(NotificationEvent.makeDefault())

        verify { trayState.sendNotification(any()) }
    }

    @Test
    fun findArticle() {
        subject.findArticle("test")

        verify { keywordSearch.invoke("test") }
        assertEquals(1, subject.tabs.size)
    }

    @Test
    fun findArticleNotFoundCase() {
        val aggregationResult = mockk<AggregationResult>()
        every { keywordSearch.invoke(any()) } returns aggregationResult
        every { aggregationResult.isEmpty() } returns true

        subject.findArticle("test")

        verify { keywordSearch.invoke("test") }
        assertTrue(subject.tabs.isEmpty())
    }

    @Test
    fun noopFindArticle() {
        subject.findArticle(null)
        subject.findArticle("")
        subject.findArticle(" ")

        verify { keywordSearch wasNot called }
    }

}