package jp.toastkid.yobidashi4.infrastructure.viewmodel.main

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
import java.util.stream.Stream
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.domain.service.archive.TopArticleLoaderService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
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

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Files::class, ImageIO::class)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { setting } bind(Setting::class)
                    single(qualifier = null) { topArticleLoaderService } bind(TopArticleLoaderService::class)
                }
            )
        }
        every { setting.darkMode() } returns false
        every { setting.setDarkMode(any()) } just Runs

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
    }

    @Test
    fun showWebSearch() {
    }

    @Test
    fun setShowWebSearch() {
    }

    @Test
    fun showAggregationBox() {
    }

    @Test
    fun switchAggregationBox() {
    }

    @Test
    fun initialAggregationType() {
    }

    @Test
    fun setInitialAggregationType() {
    }

    @Test
    fun showInputBox() {
    }

    @Test
    fun setShowInputBox() {
    }

    @Test
    fun invokeInputAction() {
    }

    @Test
    fun windowState() {
    }

    @Test
    fun toggleFullscreen() {
    }

    @Test
    fun toggleFullscreenLabel() {
    }

    @Test
    fun toggleNarrowWindow() {
    }

    @Test
    fun openTab() {
    }

    @Test
    fun snackbarHostState() {
    }

    @Test
    fun showingSnackbar() {
    }

    @Test
    fun showSnackbar() {
    }

    @Test
    fun openArticleList() {
    }

    @Test
    fun switchArticleList() {
    }

    @Test
    fun hideArticleList() {
    }

    @Test
    fun articles() {
    }

    @Test
    fun reloadAllArticle() {
    }

    @Test
    fun openMemoryUsageBox() {
    }

    @Test
    fun switchMemoryUsageBox() {
    }

    @Test
    fun openFind() {
    }

    @Test
    fun switchFind() {
    }

    @Test
    fun inputValue() {
    }

    @Test
    fun replaceInputValue() {
    }

    @Test
    fun finderFlow() {
    }

    @Test
    fun onFindInputChange() {
    }

    @Test
    fun onReplaceInputChange() {
    }

    @Test
    fun findUp() {
    }

    @Test
    fun replaceAll() {
    }

    @Test
    fun findDown() {
    }

    @Test
    fun setFindStatus() {
    }

    @Test
    fun findStatus() {
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