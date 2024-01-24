package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Image
import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.infrastructure.model.web.ContextMenu
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.browser.CefBrowser
import org.cef.callback.CefContextMenuParams
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class CefContextMenuActionTest {

    @InjectMockKs
    private lateinit var subject: CefContextMenuAction

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var pool: WebViewPool

    @MockK
    private lateinit var browser: CefBrowser

    @MockK
    private lateinit var param: CefContextMenuParams

    @MockK
    private lateinit var currentWebTab: WebTab

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                    single(qualifier = null) { pool } bind(WebViewPool::class)
                }
            )
        }
        MockKAnnotations.init(this)

        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.webSearch(any(), any()) } just Runs
        every { viewModel.browseUri(any()) } just Runs
        every { viewModel.currentTab() } returns currentWebTab
        every { currentWebTab.title() } returns "title"
        every { currentWebTab.id() } returns "test1"
        every { param.sourceUrl } returns "https://www.yahoo.com"
        every { param.linkUrl } returns "https://www.yahoo.com"
        every { param.pageUrl } returns "https://www.yahoo.com"
        every { param.selectionText } returns "selectionText"
        every { browser.url } returns "https://www.yahoo.com"
        every { pool.switchDevTools(any()) } just Runs

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
        every { anyConstructed<ClipboardPutterService>().invoke(any<Image>()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun elseCase() {
        subject.invoke(browser, param, "test", Int.MIN_VALUE)

        verify { browser wasNot called }
        verify { param wasNot called }
    }

    @Test
    fun reload() {
        every { browser.reload() } just Runs

        subject.invoke(browser, param, "test", ContextMenu.RELOAD.id)

        verify { browser.reload() }
    }

    @Test
    fun noopReload() {
        subject.invoke(null, param, "test", ContextMenu.RELOAD.id)
    }

    @Test
    fun noopOpenOtherTabWhenPassedParamsNull() {
        subject.invoke(browser, null, "test", ContextMenu.OPEN_OTHER_TAB.id)

        verify(inverse = true) { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun noopOpenOtherTab() {
        every { param.linkUrl } returns null

        subject.invoke(browser, param, "test", ContextMenu.OPEN_OTHER_TAB.id)

        verify { param.linkUrl }
        verify(inverse = true) { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun openOtherTab() {
        subject.invoke(browser, param, "test", ContextMenu.OPEN_OTHER_TAB.id)

        verify { param.linkUrl }
        verify { viewModel.openUrl("https://www.yahoo.com", false) }
    }

    @Test
    fun noopOpenOtherTabOnBackgroundWhenPassedNull() {
        subject.invoke(browser, null, "test", ContextMenu.OPEN_BACKGROUND.id)

        verify(inverse = true) { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun noopOpenOtherTabOnBackground() {
        every { param.linkUrl } returns null

        subject.invoke(browser, param, "test", ContextMenu.OPEN_BACKGROUND.id)

        verify { param.linkUrl }
        verify(inverse = true) { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun openOtherTabOnBackground() {
        subject.invoke(browser, param, "test", ContextMenu.OPEN_BACKGROUND.id)

        verify { param.linkUrl }
        verify { viewModel.openUrl("https://www.yahoo.com", true) }
    }

    @Test
    fun noopSearchWithImage() {
        every { param.sourceUrl } returns null

        subject.invoke(browser, param, "test", ContextMenu.SEARCH_WITH_IMAGE.id)

        verify { param.sourceUrl }
        verify(inverse = true) { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun noopSearchWithImageWithNullParam() {
        every { param.sourceUrl } returns null

        subject.invoke(browser, null, "test", ContextMenu.SEARCH_WITH_IMAGE.id)

        verify { param.sourceUrl }
        verify(inverse = true) { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun searchWithImage() {
        subject.invoke(browser, param, "test", ContextMenu.SEARCH_WITH_IMAGE.id)

        verify { param.sourceUrl }
        verify { viewModel.openUrl(any(), false) }
    }

    @Test
    fun noopClipTextWithNullParam() {
        subject.invoke(browser, null, "test", ContextMenu.CLIP_TEXT.id)

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopClipText() {
        every { param.selectionText } returns null

        subject.invoke(browser, param, "test", ContextMenu.CLIP_TEXT.id)

        verify { param.selectionText }
        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun clipText() {
        subject.invoke(browser, param, "test", ContextMenu.CLIP_TEXT.id)

        verify { param.selectionText }
        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopClipLinkWhenPassedNull() {
        subject.invoke(browser, null, "test", ContextMenu.CLIP_LINK.id)

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopClipLink() {
        every { param.linkUrl } returns null

        subject.invoke(browser, param, "test", ContextMenu.CLIP_LINK.id)

        verify { param.linkUrl }
        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun clipLink() {
        subject.invoke(browser, param, "test", ContextMenu.CLIP_LINK.id)

        verify { param.linkUrl }
        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun clipMarkdownLink() {
        subject.invoke(browser, param, "test", ContextMenu.CLIP_AS_MARKDOWN_LINK.id)

        verify { param.pageUrl }
        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopClipMarkdownLinkWithParamIsNull() {
        subject.invoke(browser, null, "test", ContextMenu.CLIP_AS_MARKDOWN_LINK.id)

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopClipLinkWithNullTab() {
        every { viewModel.currentTab() } returns null

        subject.invoke(browser, param, "test", ContextMenu.CLIP_AS_MARKDOWN_LINK.id)

        verify { viewModel.currentTab() }
        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopClipPageLink() {
        every { param.linkUrl } returns null
        every { param.sourceUrl } returns null
        every { param.pageUrl } returns null

        subject.invoke(browser, param, "test", ContextMenu.CLIP_PAGE_LINK.id)

        verify { param.linkUrl }
        verify { param.sourceUrl }
        verify { param.pageUrl }
        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun clipPageLinkWithPageUrl() {
        every { param.linkUrl } returns null
        every { param.sourceUrl } returns null

        subject.invoke(browser, param, "test", ContextMenu.CLIP_PAGE_LINK.id)

        verify { param.linkUrl }
        verify { param.sourceUrl }
        verify { param.pageUrl }
        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun clipPageLink() {
        subject.invoke(browser, param, "test", ContextMenu.CLIP_PAGE_LINK.id)

        verify { param.linkUrl }
        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun printToPDF() {
        every { browser.printToPDF(any(), any(), any()) } just Runs
        every { browser.identifier } returns 123

        subject.invoke(browser, param, "test", ContextMenu.SAVE_AS_PDF.id)

        verify { browser.printToPDF(any(), any(), any()) }
        verify { browser.identifier }
    }

    @Test
    fun search() {
        subject.invoke(browser, param, "test", ContextMenu.SEARCH_WITH_SELECTED_TEXT.id)

        verify { viewModel.webSearch(any(), any()) }
    }

    @Test
    fun resetZoom() {
        every { browser.zoomLevel = any() } just Runs

        subject.invoke(browser, param, "test", ContextMenu.RESET_ZOOM.id)

        verify { browser.zoomLevel = any() }
    }

    @Test
    fun noopResetZoom() {
        subject.invoke(null, param, "test", ContextMenu.RESET_ZOOM.id)
    }

    @Test
    fun download() {
        every { browser.startDownload(any()) } just Runs

        subject.invoke(browser, param, "test", ContextMenu.DOWNLOAD.id)

        verify { browser.startDownload(any()) }
    }

    @Test
    fun noopDownload() {
        subject.invoke(null, param, "test", ContextMenu.DOWNLOAD.id)
    }

    @Test
    fun noopDownloadWithNullParam() {
        every { browser.startDownload(any()) } just Runs

        subject.invoke(browser, null, "test", ContextMenu.DOWNLOAD.id)

        verify(inverse = true) { browser.startDownload(any()) }
    }

    @Test
    fun noopDownloadWithNullSourceUrl() {
        every { browser.startDownload(any()) } just Runs
        every { param.sourceUrl } returns null

        subject.invoke(browser, param, "test", ContextMenu.DOWNLOAD.id)

        verify(inverse = true) { browser.startDownload(any()) }
        verify { param.sourceUrl }
    }

    @Test
    fun addBookmark() {
        mockkConstructor(BookmarkInsertion::class)
        every { anyConstructed<BookmarkInsertion>().invoke(any<CefContextMenuParams>(), any()) } just Runs

        subject.invoke(browser, param, "test", ContextMenu.ADD_BOOKMARK.id)

        verify { anyConstructed<BookmarkInsertion>().invoke(any<CefContextMenuParams>(), any()) }
    }

    @Test
    fun addBookmarkPassedNull() {
        mockkConstructor(BookmarkInsertion::class)
        every { anyConstructed<BookmarkInsertion>().invoke(any<CefContextMenuParams>(), any()) } just Runs

        subject.invoke(null, param, "test", ContextMenu.ADD_BOOKMARK.id)

        verify { anyConstructed<BookmarkInsertion>().invoke(any<CefContextMenuParams>(), any()) }
    }

    @Test
    fun noopClipImage() {
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } returns null

        subject.invoke(browser, param, "test", ContextMenu.CLIP_IMAGE.id)

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<Image>()) }
    }

    @Test
    fun noopClipImageWithSourceUrlIsNull() {
        every { param.sourceUrl } returns null
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } returns null

        subject.invoke(browser, param, "test", ContextMenu.CLIP_IMAGE.id)

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<Image>()) }
    }

    @Test
    fun noopClipImageWithNullParam() {
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } returns null

        subject.invoke(browser, null, "test", ContextMenu.CLIP_IMAGE.id)

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<Image>()) }
    }

    @Test
    fun clipImage() {
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } returns mockk()

        subject.invoke(browser, param, "test", ContextMenu.CLIP_IMAGE.id)

        verify { anyConstructed<ClipboardPutterService>().invoke(any<Image>()) }
    }

    @Test
    fun openWithOtherBrowser() {
        subject.invoke(browser, param, "test", ContextMenu.OPEN_WITH_OTHER_BROWSER.id)

        verify { viewModel.browseUri(any()) }
    }

    @Test
    fun openWithOtherBrowserWithSourceUrl() {
        every { param.linkUrl } returns null
        every { param.sourceUrl } returns "https://source.com"

        subject.invoke(browser, param, "test", ContextMenu.OPEN_WITH_OTHER_BROWSER.id)

        verify { viewModel.browseUri(any()) }
    }

    @Test
    fun openWithOtherBrowserWithParamWhichDoesNotHasLinks() {
        every { param.linkUrl } returns null
        every { param.sourceUrl } returns null

        subject.invoke(browser, param, "test", ContextMenu.OPEN_WITH_OTHER_BROWSER.id)

        verify { viewModel.browseUri(any()) }
    }

    @Test
    fun openWithOtherBrowserUseSelectedText() {
        subject.invoke(browser, null, "test", ContextMenu.OPEN_WITH_OTHER_BROWSER.id)

        verify { viewModel.browseUri(any()) }
    }

    @Test
    fun switchDevTools() {
        subject.invoke(browser, param, "test", ContextMenu.DEVELOPER_TOOL.id)

        verify { pool.switchDevTools(any()) }
    }

    @Test
    fun noopSwitchDevTools() {
        every { viewModel.currentTab() } returns null

        subject.invoke(browser, param, "test", ContextMenu.DEVELOPER_TOOL.id)

        verify { pool wasNot called }
    }

}