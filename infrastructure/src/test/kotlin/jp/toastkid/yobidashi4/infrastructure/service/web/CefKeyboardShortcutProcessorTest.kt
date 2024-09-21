package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.event.KeyEvent
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.infrastructure.service.web.screenshot.ScreenshotExporter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.browser.CefBrowser
import org.cef.callback.CefContextMenuParams
import org.cef.handler.CefKeyboardHandler
import org.cef.misc.EventFlags
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class CefKeyboardShortcutProcessorTest {

    @InjectMockKs
    private lateinit var subject: CefKeyboardShortcutProcessor

    @MockK
    private lateinit var selectedText: () -> String

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var pool: WebViewPool

    @MockK
    private lateinit var browser: CefBrowser

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
        every { pool.switchDevTools(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun keydownCase() {
        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYDOWN, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_CIRCUMFLEX)

        assertFalse(consumed)
    }

    @Test
    fun elseCase() {
        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_CIRCUMFLEX)

        assertFalse(consumed)
    }

    @Test
    fun printToPDF() {
        every { browser.identifier } returns 2
        every { browser.printToPDF(any(), any(), any()) } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_P)

        assertTrue(consumed)
        verify { browser.identifier }
        verify { browser.printToPDF(any(), any(), any()) }
    }

    @Test
    fun noopPrintToPDF() {
        val consumed = subject.invoke(null, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_P)

        assertTrue(consumed)
    }

    @Test
    fun noopPageUp() {
        val consumed = subject.invoke(null, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_UP)

        assertTrue(consumed)
    }

    @Test
    fun pageUp() {
        every { browser.executeJavaScript(any(), any(), any()) } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_UP)

        assertTrue(consumed)
        verify { browser.executeJavaScript(any(), any(), any()) }
    }

    @Test
    fun pageDown() {
        every { browser.executeJavaScript(any(), any(), any()) } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_DOWN)

        assertTrue(consumed)
        verify { browser.executeJavaScript(any(), any(), any()) }
    }

    @Test
    fun pageDownWithNull() {
        every { browser.executeJavaScript(any(), any(), any()) } just Runs

        val consumed = subject.invoke(null, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_DOWN)

        assertTrue(consumed)
    }

    @Test
    fun bookmark() {
        mockkConstructor(BookmarkInsertion::class)
        every { anyConstructed<BookmarkInsertion>().invoke(any<CefContextMenuParams>(), any()) } just Runs
        every { browser.url } returns "https://www.yahoo.co.jp"

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_B)

        assertTrue(consumed)
        verify { browser.url }
        verify { anyConstructed<BookmarkInsertion>().invoke(any<CefContextMenuParams>(), any()) }
    }

    @Test
    fun reload() {
        every { browser.reload() } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_F5)

        assertTrue(consumed)
        verify { browser.reload() }
    }

    @Test
    fun back() {
        every { browser.canGoBack() } returns true
        every { browser.goBack() } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_ALT_DOWN, KeyEvent.VK_LEFT)

        assertTrue(consumed)
        verify { browser.canGoBack() }
        verify { browser.goBack() }
    }

    @Test
    fun cannotBack() {
        every { browser.canGoBack() } returns false
        every { browser.goBack() } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_ALT_DOWN, KeyEvent.VK_LEFT)

        assertTrue(consumed)
        verify { browser.canGoBack() }
        verify(inverse = true) { browser.goBack() }
    }

    @Test
    fun forward() {
        every { browser.canGoForward() } returns true
        every { browser.goForward() } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_ALT_DOWN, KeyEvent.VK_RIGHT)

        assertTrue(consumed)
        verify { browser.canGoForward() }
        verify { browser.goForward() }
    }

    @Test
    fun cannotForward() {
        every { browser.canGoForward() } returns false
        every { browser.goForward() } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_ALT_DOWN, KeyEvent.VK_RIGHT)

        assertTrue(consumed)
        verify { browser.canGoForward() }
        verify(inverse = true) { browser.goForward() }
    }

    @Test
    fun webSearch() {
        every { viewModel.webSearch(any()) } just Runs
        every { selectedText.invoke() } returns "text"

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_SHIFT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN,
            KeyEvent.VK_O
        )

        assertTrue(consumed)
        verify { viewModel.webSearch("text") }
        verify { selectedText.invoke() }
    }

    @Test
    fun noopShiftControl() {
        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_SHIFT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN,
            KeyEvent.VK_CIRCUMFLEX
        )

        assertFalse(consumed)
        verify { viewModel wasNot called }
        verify { selectedText wasNot called }
    }

    @Test
    fun browseUri() {
        every { viewModel.browseUri(any()) } just Runs
        every { selectedText.invoke() } returns "text"

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_ALT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN,
            KeyEvent.VK_O
        )

        assertTrue(consumed)
        verify { viewModel.browseUri(any()) }
        verify { selectedText.invoke() }
    }

    @Test
    fun printScreenshot() {
        mockkConstructor(ScreenshotExporter::class)
        every { anyConstructed<ScreenshotExporter>().invoke(any()) } just Runs
        every { browser.uiComponent } returns mockk()

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_SHIFT_DOWN,
            KeyEvent.VK_P
        )

        assertTrue(consumed)
        verify { anyConstructed<ScreenshotExporter>().invoke(any()) }
    }

    @Test
    fun noopPrintScreenshot() {
        mockkConstructor(ScreenshotExporter::class)
        every { anyConstructed<ScreenshotExporter>().invoke(any()) } just Runs

        val consumed = subject.invoke(
            null,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_SHIFT_DOWN,
            KeyEvent.VK_P
        )

        assertFalse(consumed)
        verify(inverse = true) { anyConstructed<ScreenshotExporter>().invoke(any()) }
    }

    @Test
    fun switchDevTools() {
        val webTab = mockk<WebTab>()
        every { viewModel.currentTab() } returns webTab
        every { webTab.id() } returns "test-id"

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_CONTROL_DOWN,
            KeyEvent.VK_F12
        )

        assertTrue(consumed)
        verify { pool.switchDevTools("test-id") }
        verify { webTab.id() }
    }

    @Test
    fun noopSwitchDevToolsWhenPassedOtherTab() {
        val webTab = mockk<Tab>()
        every { viewModel.currentTab() } returns webTab

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_CONTROL_DOWN,
            KeyEvent.VK_F12
        )

        assertFalse(consumed)
        verify { pool wasNot Called }
    }

    @Test
    fun reloadIgnoreCache() {
        every { browser.reloadIgnoreCache() } just Runs

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_CONTROL_DOWN,
            KeyEvent.VK_R
        )

        assertTrue(consumed)
        verify { browser.reloadIgnoreCache() }
    }

    @Test
    fun zoomIn() {
        every { browser.zoomLevel } returns 0.0
        every { browser.zoomLevel = any() } just Runs

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_CONTROL_DOWN,
            187
        )

        assertTrue(consumed)
        verify { browser.zoomLevel = 0.25 }
    }

    @Test
    fun zoomOut() {
        every { browser.zoomLevel } returns 0.25
        every { browser.zoomLevel = any() } just Runs

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_CONTROL_DOWN,
            189
        )

        assertTrue(consumed)
        verify { browser.zoomLevel = 0.0 }
    }

}