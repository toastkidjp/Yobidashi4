package jp.toastkid.yobidashi4.presentation.main.menu

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.file.ArticleFilesFinder
import jp.toastkid.yobidashi4.domain.model.file.LatestFileFinder
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.CompoundInterestCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorSettingTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.NotificationListTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.RouletteToolTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.domain.service.article.finder.AsynchronousArticleIndexerService
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainMenuViewModelTest {

    private lateinit var subject: MainMenuViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var asynchronousArticleIndexerService: AsynchronousArticleIndexerService

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var webBookmarkRepository: BookmarkRepository

    @MockK
    private lateinit var notificationEventRepository: NotificationEventRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { asynchronousArticleIndexerService } bind (AsynchronousArticleIndexerService::class)
                    single(qualifier = null) { setting } bind (Setting::class)
                    single(qualifier = null) { webBookmarkRepository } bind (BookmarkRepository::class)
                    single(qualifier = null) { notificationEventRepository } bind (NotificationEventRepository::class)
                }
            )
        }
        every { mainViewModel.tabs } returns emptyList()
        every { asynchronousArticleIndexerService.invoke(any()) } just Runs
        every { setting.userAgentName() } returns "test"

        subject = MainMenuViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun makeNewArticle() {
        every { mainViewModel.makeNewArticle() } just Runs

        subject.makeNewArticle()

        verify { mainViewModel.makeNewArticle() }
    }

    @Test
    fun switchArticleListLabel() {
        every { mainViewModel.openArticleList() } returns true

        val trueCase = subject.switchArticleListLabel()

        every { mainViewModel.openArticleList() } returns false

        val falseCase = subject.switchArticleListLabel()

        assertNotEquals(trueCase, falseCase)
    }

    @Test
    fun switchArticleListIconPath() {
        every { mainViewModel.openArticleList() } returns true

        val trueCase = subject.switchArticleListIconPath()

        every { mainViewModel.openArticleList() } returns false

        val falseCase = subject.switchArticleListIconPath()

        assertNotEquals(trueCase, falseCase)
    }

    @Test
    fun switchArticleListShortcut() {
        every { mainViewModel.openArticleList() } returns true

        val trueCase = subject.switchArticleListShortcut()

        every { mainViewModel.openArticleList() } returns false

        val falseCase = subject.switchArticleListShortcut()

        assertNotEquals(trueCase, falseCase)
    }

    @Test
    fun switchArticleList() {
        every { mainViewModel.switchArticleList() } just Runs

        subject.switchArticleList()

        verify { mainViewModel.switchArticleList() }
    }

    @Test
    fun switchFindArticle() {
        every { mainViewModel.showAggregationBox() } returns false
        every { mainViewModel.setInitialAggregationType(any()) } just Runs
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        subject.switchFindArticle()

        verify { mainViewModel.showAggregationBox() }
        verify { mainViewModel.setInitialAggregationType(7) }
        verify { mainViewModel.switchAggregationBox(true) }
    }

    @Test
    fun switchFindArticle2() {
        every { mainViewModel.showAggregationBox() } returns true
        every { mainViewModel.setInitialAggregationType(any()) } just Runs
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        subject.switchFindArticle()

        verify { mainViewModel.showAggregationBox() }
        verify(inverse = true) { mainViewModel.setInitialAggregationType(any()) }
        verify { mainViewModel.switchAggregationBox(false) }
    }

    @Test
    fun updateFinderIndex() {
        subject.updateFinderIndex()

        verify { asynchronousArticleIndexerService.invoke(any()) }
    }

    @Test
    fun dumpLatest() {
        every { setting.articleFolderPath() } returns mockk()
        mockkConstructor(ZipArchiver::class, ArticleFilesFinder::class, LatestFileFinder::class)
        every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs
        every { anyConstructed<ArticleFilesFinder>().invoke(any()) } returns mutableListOf()
        every { anyConstructed<LatestFileFinder>().invoke(any(), any()) } returns mutableListOf()
        every { mainViewModel.openFile(any()) } just Runs

        subject.dumpLatest()

        verify { anyConstructed<ZipArchiver>().invoke(any()) }
        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun dumpAll() {
        every { setting.articleFolderPath() } returns mockk()
        mockkConstructor(ZipArchiver::class, ArticleFilesFinder::class)
        every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs
        every { anyConstructed<ArticleFilesFinder>().invoke(any()) } returns mutableListOf()
        every { mainViewModel.openFile(any()) } just Runs

        subject.dumpAll()

        verify { anyConstructed<ZipArchiver>().invoke(any()) }
        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun openArticleFolder() {
        every { setting.articleFolderPath() } returns mockk()
        every { mainViewModel.openFile(any()) } just Runs

        subject.openArticleFolder()

        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun openUserFolder() {
        every { mainViewModel.openFile(any()) } just Runs

        subject.openUserFolder()

        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun openDownloadFolder() {
        every { mainViewModel.openFile(any()) } just Runs

        subject.openDownloadFolder()

        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun useEditorMenu() {
        every { mainViewModel.currentTab() } returns null

        assertFalse(subject.useEditorMenu())
    }

    @Test
    fun switchPreviewLabel() {
        val tab = mockk<EditorTab>()
        every { tab.showPreview() } returns true
        every { mainViewModel.currentTab() } returns tab

        val trueCase = subject.switchPreviewLabel()

        every { tab.showPreview() } returns false

        val falseCase = subject.switchPreviewLabel()

        assertNotEquals(trueCase, falseCase)
    }

    @Test
    fun switchPreviewLabelWhenCurrentTabIsNotEditorTab() {
        val tab = mockk<Tab>()
        every { mainViewModel.currentTab() } returns tab

        val trueCase = subject.switchPreviewLabel()

        val falseCase = subject.switchPreviewLabel()

        assertEquals(trueCase, falseCase)
    }

    @Test
    fun switchPreview() {
        val tab = mockk<EditorTab>()
        every { tab.switchPreview() } just Runs
        every { mainViewModel.currentTab() } returns tab

        subject.switchPreview()

        verify { mainViewModel.currentTab() }
        verify { tab.switchPreview() }
    }

    @Test
    fun noopSwitchPreview() {
        every { mainViewModel.currentTab() } returns null

        subject.switchPreview()

        verify { mainViewModel.currentTab() }
    }

    @Test
    fun saveCurrentEditorTab() {
        every { mainViewModel.saveCurrentEditorTab() } just Runs

        subject.saveCurrentEditorTab()

        verify { mainViewModel.saveCurrentEditorTab() }
    }

    @Test
    fun saveAllEditorTab() {
        every { mainViewModel.saveAllEditorTab() } just Runs

        subject.saveAllEditorTab()

        verify { mainViewModel.saveAllEditorTab() }
    }

    @Test
    fun switchFind() {
        every { mainViewModel.switchFind() } just Runs

        subject.switchFind()

        verify { mainViewModel.switchFind() }
    }

    @Test
    fun openEditorSetting() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openEditorSetting()

        verify { mainViewModel.openTab(any<EditorSettingTab>()) }
    }

    @Test
    fun closeCurrentTabUseIfEmptyCase() {
        val ifEmpty = mockk<() -> Unit>()
        every { ifEmpty.invoke() } just Runs

        subject.closeCurrentTab(ifEmpty)

        verify { ifEmpty.invoke() }
    }

    @Test
    fun closeCurrentTab() {
        every { mainViewModel.tabs } returns mutableListOf(mockk())
        every { mainViewModel.closeCurrent() } just Runs
        val ifEmpty = mockk<() -> Unit>()
        every { ifEmpty.invoke() } just Runs

        subject.closeCurrentTab(ifEmpty)

        verify { ifEmpty wasNot called }
    }

    @Test
    fun useAdditionalTabMenu() {
        every { mainViewModel.tabs } returns mutableListOf(mockk())

        assertTrue(subject.useAdditionalTabMenu())
    }

    @Test
    fun closeAllTabs() {
        every { mainViewModel.closeAllTabs() } just Runs

        subject.closeAllTabs()

        verify { mainViewModel.closeAllTabs() }
    }

    @Test
    fun closeOtherTabs() {
        every { mainViewModel.closeOtherTabs() } just Runs

        subject.closeOtherTabs()

        verify { mainViewModel.closeOtherTabs() }
    }

    @Test
    fun currentIsWebTabFalseCase() {
        every { mainViewModel.currentTab() } returns mockk()

        assertFalse(subject.currentIsWebTab())
    }

    @Test
    fun currentIsWebTab() {
        every { mainViewModel.currentTab() } returns mockk<WebTab>()

        assertTrue(subject.currentIsWebTab())
    }

    @Test
    fun currentIsEditableTab() {
        every { mainViewModel.currentTab() } returns mockk<MarkdownPreviewTab>()

        assertTrue(subject.currentIsEditableTab())
    }

    @Test
    fun currentIsNotEditableTab() {
        every { mainViewModel.currentTab() } returns mockk<WebTab>()

        assertFalse(subject.currentIsEditableTab())
    }

    @Test
    fun openEditorTabWithCurrentTabsPath() {
        val markdownPreviewTab = mockk<MarkdownPreviewTab>()
        every { mainViewModel.currentTab() } returns markdownPreviewTab
        val path = mockk<Path>()
        every { markdownPreviewTab.slideshowSourcePath() } returns path
        every { mainViewModel.edit(path, any()) } just Runs

        subject.openEditorTabWithCurrentTabsPath()

        verify { mainViewModel.currentTab() }
        verify { markdownPreviewTab.slideshowSourcePath() }
        verify { mainViewModel.edit(path, any()) }
    }

    @Test
    fun noopOpenEditorTabWithCurrentTabsPath() {
        every { mainViewModel.currentTab() } returns mockk()
        every { mainViewModel.edit(any(), any()) } just Runs

        subject.openEditorTabWithCurrentTabsPath()

        verify { mainViewModel.currentTab() }
        verify(inverse = true) { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun copyTabsTitle() {
        val tab = mockk<Tab>()
        every { tab.title() } returns "test"
        every { mainViewModel.currentTab() } returns tab
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject.copyTabsTitle()

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopCopyTabsTitle() {
        every { mainViewModel.currentTab() } returns null
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject.copyTabsTitle()

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun copyTabsUrl() {
        val tab = mockk<WebTab>()
        every { tab.url() } returns "https://test"
        every { mainViewModel.currentTab() } returns tab
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject.copyTabsUrl()

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopCopyTabsUrl() {
        every { mainViewModel.currentTab() } returns null
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject.copyTabsUrl()

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun copyTabsUrlAsMarkdownLink() {
        val tab = mockk<WebTab>()
        every { tab.markdownLink() } returns "[test](https://test)"
        every { mainViewModel.currentTab() } returns tab
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject.copyTabsUrlAsMarkdownLink()

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopCopyTabsUrlAsMarkdownLink() {
        every { mainViewModel.currentTab() } returns null
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject.copyTabsUrlAsMarkdownLink()

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun addWebBookmark() {
        every { webBookmarkRepository.add(any()) } just Runs
        val webTab = mockk<WebTab>()
        every { mainViewModel.currentTab() } returns webTab
        every { webTab.title() } returns "test"
        every { webTab.url() } returns "test"

        subject.addWebBookmark()

        verify { webBookmarkRepository.add(any()) }
    }

    @Test
    fun noopAddWebBookmark() {
        every { mainViewModel.currentTab() } returns null

        subject.addWebBookmark()

        verify { webBookmarkRepository wasNot called }
    }

    @Test
    fun findSlideshowPathWhenCurrentTabIsEditorTab() {
        val tab = mockk<EditorTab>()
        every { mainViewModel.currentTab() } returns tab
        every { tab.path } returns mockk()

        assertNotNull(subject.findSlideshowPath())
    }

    @Test
    fun findSlideshowPathWhenCurrentTabIsPreviewTab() {
        val tab = mockk<MarkdownPreviewTab>()
        every { mainViewModel.currentTab() } returns tab
        every { tab.slideshowSourcePath() } returns mockk()

        assertNotNull(subject.findSlideshowPath())
    }

    @Test
    fun findSlideshowPathWhenCurrentTabIsNull() {
        every { mainViewModel.currentTab() } returns null

        assertNull(subject.findSlideshowPath())
    }

    @Test
    fun slideshow() {
        every { mainViewModel.slideshow(any()) } just Runs

        subject.slideshow(mockk())

        verify { mainViewModel.slideshow(any()) }
    }

    @Test
    fun canMoveTab() {
        assertFalse(subject.canMoveTab())

        every { mainViewModel.tabs } returns mutableListOf(mockk())

        assertFalse(subject.canMoveTab())

        every { mainViewModel.tabs } returns mutableListOf(mockk(), mockk())

        assertTrue(subject.canMoveTab())
    }

    @Test
    fun moveTabIndex() {
        every { mainViewModel.moveTabIndex(any()) } just Runs

        subject.moveTabIndex(11)

        verify { mainViewModel.moveTabIndex(11) }
    }

    @Test
    fun tabCount() {
        every { mainViewModel.tabs } returns mutableListOf(mockk())

        assertEquals(1, subject.tabCount())
    }

    @Test
    fun makeTabIndexShortcut() {
        (1 .. 11).forEach {
            assertNotNull(subject.makeTabIndexShortcut(it))
        }
    }

    @Test
    fun setSelectedIndex() {
        every { mainViewModel.setSelectedIndex(any()) } just Runs

        subject.setSelectedIndex(2)

        verify { mainViewModel.setSelectedIndex(2) }
    }

    @Test
    fun openChatTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openChatTab()

        verify { mainViewModel.openTab(any<ChatTab>()) }
    }

    @Test
    fun openBookmarkTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openBookmarkTab()

        verify { mainViewModel.openTab(any<WebBookmarkTab>()) }
    }

    @Test
    fun openCalendarTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openCalendarTab()

        verify { mainViewModel.openTab(any<CalendarTab>()) }
    }

    @Test
    fun searchWithSelectedText() {
        every { mainViewModel.webSearchWithSelectedText() } just Runs

        subject.searchWithSelectedText()

        verify { mainViewModel.webSearchWithSelectedText() }
    }

    @Test
    fun openAggregationBox() {
        every { mainViewModel.showAggregationBox() } returns false
        every { mainViewModel.setInitialAggregationType(any()) } just Runs
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        subject.openAggregationBox()

        verify { mainViewModel.showAggregationBox() }
        verify { mainViewModel.setInitialAggregationType(any()) }
        verify { mainViewModel.switchAggregationBox(true) }
    }

    @Test
    fun openAggregationBox2() {
        every { mainViewModel.showAggregationBox() } returns true
        every { mainViewModel.setInitialAggregationType(any()) } just Runs
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        subject.openAggregationBox()

        verify { mainViewModel.showAggregationBox() }
        verify(inverse = true) { mainViewModel.setInitialAggregationType(any()) }
        verify { mainViewModel.switchAggregationBox(false) }
    }

    @Test
    fun openWebHistoryTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openWebHistoryTab()

        verify { mainViewModel.openTab(any<WebHistoryTab>()) }
    }

    @Test
    fun switchWebSearchBox() {
        every { mainViewModel.showWebSearch() } returns false
        every { mainViewModel.setShowWebSearch(any()) } just Runs

        subject.switchWebSearchBox()

        verify { mainViewModel.setShowWebSearch(true) }
    }

    @Test
    fun openBarcodeToolTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openBarcodeToolTab()

        verify { mainViewModel.openTab(any<BarcodeToolTab>()) }
    }

    @Test
    fun openConverterToolTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openConverterToolTab()

        verify { mainViewModel.openTab(any<ConverterToolTab>()) }
    }

    @Test
    fun openFileRenameToolTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openFileRenameToolTab()

        verify { mainViewModel.openTab(any<FileRenameToolTab>()) }
    }

    @Test
    fun openRouletteToolTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openRouletteToolTab()

        verify { mainViewModel.openTab(any<RouletteToolTab>()) }
    }

    @Test
    fun openCompoundInterestCalculatorTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openCompoundInterestCalculatorTab()

        verify { mainViewModel.openTab(any<CompoundInterestCalculatorTab>()) }
    }

    @Test
    fun openLoanCalculatorTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openLoanCalculatorTab()

        verify { mainViewModel.openTab(any<LoanCalculatorTab>()) }
    }

    @Test
    fun openNumberPlaceTab() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openNumberPlaceTab()

        verify { mainViewModel.openTab(any<NumberPlaceGameTab>()) }
    }

    @Test
    fun noopOpenMusicPlayerTab() {
        every { mainViewModel.openFileListTab(any(), any(), any()) } just Runs
        every { setting.mediaFolderPath() } returns null

        subject.openMusicPlayerTab()

        verify { setting.mediaFolderPath() }
        verify { mainViewModel wasNot called }
    }

    @Test
    fun openMusicPlayerTab() {
        every { mainViewModel.openFileListTab(any(), any(), any()) } just Runs
        every { setting.mediaFolderPath() } returns "/path/to/music"

        subject.openMusicPlayerTab()

        verify { setting.mediaFolderPath() }
        verify { mainViewModel.openFileListTab(any(), any(), any()) }
    }

    @Test
    fun toggleWorldTime() {
        every { mainViewModel.openWorldTime() } returns true
        every { mainViewModel.toggleWorldTime() } just Runs

        val shortcut = subject.toggleWorldTimeShortcut()

        subject.toggleWorldTime()
        every { mainViewModel.openWorldTime() } returns false

        assertNotEquals(subject.toggleWorldTimeShortcut(), shortcut)

        verify(exactly = 1) { mainViewModel.toggleWorldTime() }
    }

    @Test
    fun isSelectedUserAgent() {
        assertTrue(subject.isSelectedUserAgent(UserAgent.DEFAULT))
    }

    @Test
    fun chooseUserAgent() {
        every { setting.setUserAgentName(any()) } just Runs
        every { setting.save() } just Runs
        every { mainViewModel.showSnackbar(any()) } just Runs

        subject.chooseUserAgent(UserAgent.PC)

        verify { setting.setUserAgentName(any()) }
        verify { setting.save() }
        verify { mainViewModel.showSnackbar(any()) }
    }

    @Test
    fun openNotificationList() {
        every { mainViewModel.openTab(any()) } just Runs

        subject.openNotificationList()

        verify { mainViewModel.openTab(any<NotificationListTab>()) }
    }

    @Test
    fun openNotificationFile() {
        every { mainViewModel.openFile(any()) } just Runs

        subject.openNotificationFile()

        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun exportNotifications() {
        mockkStatic(Path::class, Files::class)
        every { Path.of(any<String>()) } returns mockk()
        every { Files.write(any(), any<Iterable<String>>()) } returns mockk()
        every { notificationEventRepository.readAll() } returns listOf(NotificationEvent.makeDefault())
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs
        every { mainViewModel.openFile(any()) } just Runs

        subject.exportNotifications()
        slot.captured.invoke()

        verify { mainViewModel.showSnackbar(any(), any(), any()) }
        verify { mainViewModel.openFile(any()) }
        verify { notificationEventRepository.readAll() }
    }

    @Test
    fun toggleFullscreenLabel() {
        every { mainViewModel.toggleFullscreenLabel() } returns "test"

        val label = subject.toggleFullscreenLabel()

        assertEquals("test", label)
    }

    @Test
    fun toggleFullscreen() {
        every { mainViewModel.toggleFullscreen() } just Runs

        subject.toggleFullscreen()

        verify { mainViewModel.toggleFullscreen() }
    }

    @Test
    fun toggleNarrowWindow() {
        every { mainViewModel.toggleNarrowWindow() } just Runs

        subject.toggleNarrowWindow()

        verify { mainViewModel.toggleNarrowWindow() }
    }

    @Test
    fun loadBackgroundImage() {
        every { mainViewModel.loadBackgroundImage() } just Runs

        subject.loadBackgroundImage()

        verify { mainViewModel.loadBackgroundImage() }
    }

    @Test
    fun switchUseBackground() {
        every { mainViewModel.switchUseBackground() } just Runs

        subject.switchUseBackground()

        verify { mainViewModel.switchUseBackground() }
    }

    @Test
    fun switchDarkMode() {
        every { mainViewModel.switchDarkMode() } just Runs

        subject.switchDarkMode()

        verify { mainViewModel.switchDarkMode() }
    }

    @Test
    fun openArticleTemplate() {
        every { mainViewModel.openFile(any()) } just Runs

        subject.openArticleTemplate()

        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun switchMemoryUsageBoxLabel() {
        every { mainViewModel.openMemoryUsageBox() } returns true

        val trueCase = subject.switchMemoryUsageBoxLabel()

        every { mainViewModel.openMemoryUsageBox() } returns false

        val falseCase = subject.switchMemoryUsageBoxLabel()

        assertNotEquals(trueCase, falseCase)
    }

    @Test
    fun switchMemoryUsageBox() {
        every { mainViewModel.switchMemoryUsageBox() } just Runs

        subject.switchMemoryUsageBox()

        verify { mainViewModel.switchMemoryUsageBox() }
    }

    @Test
    fun openLogViewerTab() {
        every { mainViewModel.openTextFile(any()) } just Runs

        subject.openLogViewerTab()

        verify { mainViewModel.openTextFile(any()) }
    }

    @Test
    fun toDefaultWindowSize() {
        every { mainViewModel.toDefaultWindowSize() } just Runs

        subject.toDefaultWindowSize()

        verify { mainViewModel.toDefaultWindowSize() }
    }

}