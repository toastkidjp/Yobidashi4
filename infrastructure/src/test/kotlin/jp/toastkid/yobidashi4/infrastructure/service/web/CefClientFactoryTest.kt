package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import me.friwi.jcefmaven.CefAppBuilder
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.callback.CefStringVisitor
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
    private lateinit var findId: (CefBrowser?) -> String?

    @MockK
    private lateinit var cefAppFactory: CefAppFactory

    @MockK
    private lateinit var appSetting : Setting

    @MockK
    private lateinit var viewModel: MainViewModel

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
                    single(qualifier = null) { appSetting } bind(Setting::class)
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                }
            )
        }
        every { appSetting.darkMode() } returns true
        every { appSetting.userAgentName() } returns UserAgent.DEFAULT.name
        every { cefAppFactory.invoke() } returns cefApp

        mockkConstructor(CefSettingsApplier::class, CefAppBuilder::class, WebIconLoaderServiceImplementation::class)
        every { anyConstructed<CefSettingsApplier>().invoke(any(), any()) } just Runs
        every { anyConstructed<CefAppBuilder>().setInstallDir(any()) } just Runs
        every { anyConstructed<CefAppBuilder>().setProgressHandler(any()) } just Runs
        every { anyConstructed<CefAppBuilder>().cefSettings } returns mockk()
        every { anyConstructed<CefAppBuilder>().build() } returns cefApp
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

}