package jp.toastkid.yobidashi4.infrastructure.service.web

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
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
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
import org.cef.handler.CefLifeSpanHandler
import org.cef.handler.CefLoadHandler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
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
        loadHandlerSlot.captured.onBeforePopup(mockk(), mockk(), "https://www.yahoo.co.jp", "test")

        assertNotNull(client)
        verify { client.addLifeSpanHandler(any()) }
        verify { viewModel.openUrl(any(), any()) }
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
        handlerSlot.captured.onBeforeContextMenu(mockk(), mockk(), params, model)
        handlerSlot.captured.onContextMenuCommand(mockk(), mockk(), params, 1, 1)

        assertNotNull(client)
        verify { client.addContextMenuHandler(any()) }
        verify { anyConstructed<CefContextMenuFactory>().invoke(any(), any()) }
        verify { anyConstructed<CefContextMenuAction>().invoke(any(), any(), any(), any()) }
    }

}