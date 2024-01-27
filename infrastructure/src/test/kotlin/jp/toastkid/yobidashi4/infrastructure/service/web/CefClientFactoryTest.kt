package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import javax.swing.SwingUtilities
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.web.ad.AdHosts
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.callback.CefBeforeDownloadCallback
import org.cef.callback.CefContextMenuParams
import org.cef.callback.CefMenuModel
import org.cef.callback.CefStringVisitor
import org.cef.handler.CefContextMenuHandler
import org.cef.handler.CefDisplayHandler
import org.cef.handler.CefDownloadHandler
import org.cef.handler.CefKeyboardHandler
import org.cef.handler.CefKeyboardHandler.CefKeyEvent
import org.cef.handler.CefKeyboardHandler.CefKeyEvent.EventType
import org.cef.handler.CefLifeSpanHandler
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefRequestHandler
import org.cef.network.CefRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class CefClientFactoryTest {

    private lateinit var subject: CefClientFactory

    @MockK
    private lateinit var cefAppFactory: CefAppFactory

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var webViewPool: WebViewPool

    @MockK
    private lateinit var cefApp: CefApp

    @MockK
    private lateinit var client: CefClient

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { cefAppFactory } bind(CefAppFactory::class)
                    single(qualifier = null) { webViewPool } bind(WebViewPool::class)
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                }
            )
        }
        every { cefAppFactory.invoke() } returns cefApp
        every { webViewPool.findId(any()) } returns "test-id"

        mockkConstructor(WebIconLoaderServiceImplementation::class)
        every { anyConstructed<WebIconLoaderServiceImplementation>().invoke(any(), any()) } just Runs
        every { cefApp.createClient() } returns client
        every { client.addLoadHandler(any()) } returns client
        every { client.addLifeSpanHandler(any()) } returns client
        every { client.addRequestHandler(any()) } returns client
        every { client.addDisplayHandler(any()) } returns client
        every { client.addDownloadHandler(any()) } returns client
        every { client.addKeyboardHandler(any()) } returns client
        every { client.addContextMenuHandler(any()) } returns client

        mockkStatic(CefApp::class)
        every { CefApp.addAppHandler(any()) } just Runs

        subject = CefClientFactory()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun checkAddLoadHandler() {
        val loadHandlerSlot = slot<CefLoadHandler>()
        every { client.addLoadHandler(capture(loadHandlerSlot)) } returns client

        val client = subject.invoke()
        val browser = mockk<CefBrowser>()
        every { browser.url } returns "https://www.yahoo.co.jp"
        val sourceSlot = slot<CefStringVisitor>()
        every { browser.getSource(capture(sourceSlot)) } just Runs
        loadHandlerSlot.captured.onLoadingStateChange(browser, false, false, false)
        sourceSlot.captured.visit("test")

        assertNotNull(client)
        verify { client.addLoadHandler(any()) }
        verify { browser.getSource(any()) }
        verify { anyConstructed<WebIconLoaderServiceImplementation>().invoke(any(), any()) }
    }

    @Test
    fun checkAddLifeSpanHandler() {
        val loadHandlerSlot = slot<CefLifeSpanHandler>()
        every { client.addLifeSpanHandler(capture(loadHandlerSlot)) } returns client
        every { viewModel.openUrl(any(), any()) } just Runs

        val client = subject.invoke()
        val result = loadHandlerSlot.captured.onBeforePopup(mockk(), mockk(), "https://www.yahoo.co.jp", "test")

        assertNotNull(client)
        assertTrue(result)
        verify { client.addLifeSpanHandler(any()) }
        verify { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun checkAddLifeSpanHandlerWithoutTargetUrl() {
        val loadHandlerSlot = slot<CefLifeSpanHandler>()
        every { client.addLifeSpanHandler(capture(loadHandlerSlot)) } returns client
        every { viewModel.openUrl(any(), any()) } just Runs

        val client = subject.invoke()
        val result = loadHandlerSlot.captured.onBeforePopup(mockk(), mockk(), null, "test")

        assertNotNull(client)
        assertTrue(result)
        verify { client.addLifeSpanHandler(any()) }
        verify { viewModel wasNot called }
    }

    @Test
    fun checkAddDisplayHandler() {
        val handlerSlot = slot<CefDisplayHandler>()
        every { client.addDisplayHandler(capture(handlerSlot)) } returns client
        every { viewModel.updateWebTab(any(), any(), any()) } just Runs
        val browser = mockk<CefBrowser>()
        every { browser.url } returns "https://www.yahoo.co.jp"

        val client = subject.invoke()
        handlerSlot.captured.onTitleChange(browser, "test")

        assertNotNull(client)
        verify { client.addDisplayHandler(any()) }
        verify { webViewPool.findId(any()) }
        verify { viewModel.updateWebTab(any(), any(), any()) }
    }

    @Test
    fun checkAddDisplayHandlerWithNullTitleCase() {
        val handlerSlot = slot<CefDisplayHandler>()
        every { client.addDisplayHandler(capture(handlerSlot)) } returns client
        every { viewModel.updateWebTab(any(), any(), any()) } just Runs
        val browser = mockk<CefBrowser>()
        every { browser.url } returns "https://www.yahoo.co.jp"

        val client = subject.invoke()
        handlerSlot.captured.onTitleChange(browser, null)

        assertNotNull(client)
        verify { client.addDisplayHandler(any()) }
        verify { webViewPool wasNot called }
        verify { viewModel wasNot called }
    }

    @Test
    fun checkAddDisplayHandlerWithNullBrowserCase() {
        val handlerSlot = slot<CefDisplayHandler>()
        every { client.addDisplayHandler(capture(handlerSlot)) } returns client
        every { viewModel.updateWebTab(any(), any(), any()) } just Runs
        val browser = mockk<CefBrowser>()
        every { browser.url } returns "https://www.yahoo.co.jp"

        val client = subject.invoke()
        handlerSlot.captured.onTitleChange(null, "test")

        assertNotNull(client)
        verify { client.addDisplayHandler(any()) }
        verify { webViewPool wasNot called }
        verify { viewModel wasNot called }
    }

    @Test
    fun checkAddDisplayHandlerNotFoundIdCase() {
        val handlerSlot = slot<CefDisplayHandler>()
        every { client.addDisplayHandler(capture(handlerSlot)) } returns client
        every { webViewPool.findId(any()) } returns null
        every { viewModel.updateWebTab(any(), any(), any()) } just Runs
        val browser = mockk<CefBrowser>()
        every { browser.url } returns "https://www.yahoo.co.jp"

        val client = subject.invoke()
        handlerSlot.captured.onTitleChange(browser, "test")

        assertNotNull(client)
        verify { client.addDisplayHandler(any()) }
        verify { webViewPool.findId(any()) }
        verify { viewModel wasNot called }
    }

    @Test
    fun checkAddDownloadHandler() {
        val handlerSlot = slot<CefDownloadHandler>()
        every { client.addDownloadHandler(capture(handlerSlot)) } returns client
        val downloadCallback = mockk<CefBeforeDownloadCallback>()
        every { downloadCallback.Continue(any(), any()) } just Runs
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns false
        every { Files.createDirectories(any()) } returns mockk()

        val client = subject.invoke()
        handlerSlot.captured.onBeforeDownload(mockk(), mockk(), "test", downloadCallback)

        assertNotNull(client)
        verify { client.addDownloadHandler(any()) }
        verify { Files.createDirectories(any()) }
        verify { downloadCallback.Continue(any(), any()) }
    }

    @Test
    fun checkAddDownloadHandlerWithoutSuggestedNameCase() {
        val handlerSlot = slot<CefDownloadHandler>()
        every { client.addDownloadHandler(capture(handlerSlot)) } returns client
        val downloadCallback = mockk<CefBeforeDownloadCallback>()
        every { downloadCallback.Continue(any(), any()) } just Runs
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.createDirectories(any()) } returns mockk()

        val client = subject.invoke()
        handlerSlot.captured.onBeforeDownload(mockk(), mockk(), null, downloadCallback)

        assertNotNull(client)
        verify { client.addDownloadHandler(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
        verify { downloadCallback wasNot called }
    }

    @Test
    fun checkAddDownloadHandlerWithoutCallback() {
        val handlerSlot = slot<CefDownloadHandler>()
        every { client.addDownloadHandler(capture(handlerSlot)) } returns client
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns false
        every { Files.createDirectories(any()) } returns mockk()

        val client = subject.invoke()
        handlerSlot.captured.onBeforeDownload(mockk(), mockk(), "test", null)

        assertNotNull(client)
        verify { client.addDownloadHandler(any()) }
        verify(inverse = true) { Files.exists(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
    }

    @Test
    fun checkAddContextMenuHandler() {
        val handlerSlot = slot<CefContextMenuHandler>()
        every { client.addContextMenuHandler(capture(handlerSlot)) } returns client
        val params = mockk<CefContextMenuParams>()
        every { params.selectionText } returns "test"
        val model = mockk<CefMenuModel>()
        mockkConstructor(CefContextMenuFactory::class, CefContextMenuAction::class)
        every { anyConstructed<CefContextMenuFactory>().invoke(any(), any()) } just Runs
        every { anyConstructed<CefContextMenuAction>().invoke(any(), any(), any(), any()) } just Runs

        val client = subject.invoke()
        handlerSlot.captured.onBeforeContextMenu(mockk(), mockk(), null, model)
        handlerSlot.captured.onBeforeContextMenu(mockk(), mockk(), params, model)
        handlerSlot.captured.onContextMenuCommand(mockk(), mockk(), params, 1, 1)

        assertNotNull(client)
        verify { client.addContextMenuHandler(any()) }
        verify { anyConstructed<CefContextMenuFactory>().invoke(any(), any()) }
        verify { anyConstructed<CefContextMenuAction>().invoke(any(), any(), any(), any()) }
    }

    @Test
    fun checkAddRequestHandler() {
        val handlerSlot = slot<CefRequestHandler>()
        every { client.addRequestHandler(capture(handlerSlot)) } returns client
        val request = mockk<CefRequest>()
        every { request.url } returns "https://www.ad.com"
        every { request.dispose() } just Runs
        every { viewModel.openUrl(any(), any()) } just Runs
        mockkObject(AdHosts)
        val adHosts = mockk<AdHosts>()
        every { AdHosts.make() } returns adHosts
        every { adHosts.contains(any()) } returns true
        subject = CefClientFactory()

        val client = subject.invoke()
        val resourceRequestHandler = handlerSlot.captured
            .getResourceRequestHandler(mockk(), mockk(), request, true, true, "test", mockk())
        resourceRequestHandler.onBeforeResourceLoad(mockk(), mockk(), request)
        val result = handlerSlot.captured.onOpenURLFromTab(mockk(), mockk(), "https://www.yahoo.co.jp", true)

        assertNotNull(client)
        assertTrue(result)
        verify { client.addRequestHandler(any()) }
        verify { request.dispose() }
        verify { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun checkAddRequestHandlerWithoutAdUrl() {
        val handlerSlot = slot<CefRequestHandler>()
        every { client.addRequestHandler(capture(handlerSlot)) } returns client
        val request = mockk<CefRequest>()
        every { request.url } returns "https://www.ad.com"
        every { request.dispose() } just Runs
        mockkObject(AdHosts)
        val adHosts = mockk<AdHosts>()
        every { AdHosts.make() } returns adHosts
        every { adHosts.contains(any()) } returns false
        subject = CefClientFactory()

        val client = subject.invoke()
        val resourceRequestHandler = handlerSlot.captured
            .getResourceRequestHandler(mockk(), mockk(), request, true, true, "test", mockk())
        val result = resourceRequestHandler.onBeforeResourceLoad(mockk(), mockk(), request)

        assertNotNull(client)
        assertFalse(result)
        verify { client.addRequestHandler(any()) }
        verify(inverse = true) { request.dispose() }
    }

    @Test
    fun checkAddKeyboardHandlerIfPassedNullEvent() {
        mockkConstructor(CefKeyboardShortcutProcessor::class)
        val handlerSlot = slot<CefKeyboardHandler>()
        every { client.addKeyboardHandler(capture(handlerSlot)) } returns client
        every { anyConstructed<CefKeyboardShortcutProcessor >().invoke(any(), any(), any(), any()) } returns true

        val client = subject.invoke()
        handlerSlot.captured.onKeyEvent(mockk(), null)

        assertNotNull(client)
        verify { client.addKeyboardHandler(any()) }
        verify(inverse = true) { anyConstructed<CefKeyboardShortcutProcessor >().invoke(any(), any(), any(), any()) }
    }

    @Test
    fun checkAddKeyboardHandlerIfNotKeyDown() {
        mockkConstructor(CefKeyboardShortcutProcessor::class)
        val handlerSlot = slot<CefKeyboardHandler>()
        every { client.addKeyboardHandler(capture(handlerSlot)) } returns client
        every { anyConstructed<CefKeyboardShortcutProcessor >().invoke(any(), any(), any(), any()) } returns false
        mockkStatic(SwingUtilities::class)
        every { SwingUtilities.windowForComponent(any()) } returns mockk()
        val kClass = CefKeyEvent::class.java
        kClass.declaredFields.forEach { p ->
            try {
                p.isAccessible = true
            } catch (e:Throwable) {
                e.printStackTrace()
            }
        }
        val constructor = kClass.declaredConstructors[0]
        constructor.isAccessible = true
        val event = constructor.newInstance(EventType.KEYEVENT_CHAR, 1, 1, 1, false, 'A', 'A', false) as CefKeyEvent

        val client = subject.invoke()
        val consumed = handlerSlot.captured.onKeyEvent(mockk(), event)

        assertNotNull(client)
        assertFalse(consumed)
        verify { client.addKeyboardHandler(any()) }
        verify { anyConstructed<CefKeyboardShortcutProcessor >().invoke(any(), any(), any(), any()) }
        verify(inverse = true) { SwingUtilities.windowForComponent(any()) }
    }

}