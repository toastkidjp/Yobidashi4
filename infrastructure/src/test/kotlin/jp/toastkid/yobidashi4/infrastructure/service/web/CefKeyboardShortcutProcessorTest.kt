package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.event.KeyEvent
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import org.cef.browser.CefBrowser
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
    private lateinit var webTabViewModel: WebTabViewModel

    @MockK
    private lateinit var browser: CefBrowser

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                    single(qualifier = null) { webTabViewModel } bind(WebTabViewModel::class)
                }
            )
        }
        MockKAnnotations.init(this)

        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.webSearch(any(), any()) } just Runs
        every { viewModel.browseUri(any()) } just Runs
        every { webTabViewModel.switchDevTools(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun notConsumeOnDown() {
        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYDOWN, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_F)

        assertFalse(consumed)
    }

    @Test
    fun passBrowserNull() {
        val consumed = subject.invoke(null, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYDOWN, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_F)

        assertFalse(consumed)
    }

    @Test
    fun elseCase() {
        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_CIRCUMFLEX)

        assertFalse(consumed)
    }

    @Test
    fun find() {
        every { webTabViewModel.switchFind() } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_F)

        assertTrue(consumed)
        verify { webTabViewModel.switchFind() }
    }

    @Test
    fun closeCurrent() {
        every { viewModel.closeCurrent() } just Runs

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_W)

        assertTrue(consumed)
        verify { viewModel.closeCurrent() }
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
    fun bookmark() {
        mockkConstructor(BookmarkInsertion::class)
        every { anyConstructed<BookmarkInsertion>().invoke(any(), any()) } just Runs
        every { browser.url } returns "https://www.yahoo.co.jp"

        val consumed = subject.invoke(browser, CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP, EventFlags.EVENTFLAG_CONTROL_DOWN, KeyEvent.VK_B)

        assertTrue(consumed)
        verify { browser.url }
        verify { anyConstructed<BookmarkInsertion>().invoke(any(), any()) }
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
    fun switchDevTools() {
        val webTab = mockk<WebTab>()
        every { viewModel.currentTab() } returns webTab
        every { webTab.id() } returns "test-id"

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_CONTROL_DOWN,
            KeyEvent.VK_K
        )

        assertTrue(consumed)
        verify { webTabViewModel.switchDevTools("test-id") }
        verify { webTab.id() }
    }

    @Test
    fun noopSwitchDevTools() {
        val webTab = mockk<Tab>()
        every { viewModel.currentTab() } returns webTab

        val consumed = subject.invoke(
            browser,
            CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP,
            EventFlags.EVENTFLAG_CONTROL_DOWN,
            KeyEvent.VK_K
        )

        assertTrue(consumed)
        verify { webTabViewModel wasNot Called }
    }

}