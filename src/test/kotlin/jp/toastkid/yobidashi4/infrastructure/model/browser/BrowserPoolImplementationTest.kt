package jp.toastkid.yobidashi4.infrastructure.model.browser

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
import jp.toastkid.yobidashi4.infrastructure.service.CefClientFactory
import kotlin.test.assertNotNull
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BrowserPoolImplementationTest {

    private lateinit var browserPoolImplementation: BrowserPoolImplementation

    @MockK
    private lateinit var cefClient: CefClient

    @MockK
    private lateinit var cefBrowser: CefBrowser

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { cefClient.createBrowser(any(), any(), any()) }.returns(cefBrowser)
        every { cefBrowser.uiComponent }.returns(mockk())
        every { cefBrowser.close(any()) }.just(Runs)
        every { cefBrowser.reload() }.just(Runs)

        mockkConstructor(CefClientFactory::class)
        every { anyConstructed<CefClientFactory>().invoke() }.returns(cefClient)

        mockkStatic(CefApp::class)
        every { CefApp.getInstance().dispose() }.just(Runs)

        browserPoolImplementation = BrowserPoolImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onLayout() {
    }

    @Test
    fun component() {
    }

    @Test
    fun devTools() {
    }

    @Test
    fun find() {
    }

    @Test
    fun reload() {
        browserPoolImplementation.component("1", "https://www.yahoo.co.jp")

        browserPoolImplementation.reload("1")

        every { cefBrowser.reload() }
    }

    @Test
    fun disposeDoNothingIfBrowsersIsNone() {
        browserPoolImplementation.dispose("1")

        verify(inverse = true) { cefBrowser.close(any()) }
    }

    @Test
    fun dispose() {
        val component = browserPoolImplementation.component("1", "https://www.yahoo.co.jp")

        assertNotNull(component)

        browserPoolImplementation.dispose("1")

        verify { cefBrowser.close(any()) }
    }

    @Test
    fun disposeAllNoneCase() {
        browserPoolImplementation.disposeAll()

        verify(inverse = true) { cefBrowser.close(any()) }
        verify { CefApp.getInstance().dispose() }
    }

    @Test
    fun disposeAll() {
        browserPoolImplementation.component("1", "https://www.yahoo.co.jp")
        browserPoolImplementation.disposeAll()

        verify { cefBrowser.close(any()) }
        verify { CefApp.getInstance().dispose() }
    }

    @Test
    fun disposeAllPluralCase() {
        browserPoolImplementation.component("1", "https://www.yahoo.co.jp")
        browserPoolImplementation.component("2", "https://www.yahoo.co.jp")
        browserPoolImplementation.component("3", "https://www.yahoo.co.jp")

        browserPoolImplementation.disposeAll()

        verify(exactly = 3) { cefBrowser.close(any()) }
        verify { CefApp.getInstance().dispose() }
    }
}