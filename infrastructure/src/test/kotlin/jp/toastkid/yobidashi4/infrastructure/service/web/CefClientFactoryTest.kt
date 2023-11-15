package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.web.ad.AdHosts
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import me.friwi.jcefmaven.CefAppBuilder
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class CefClientFactoryTest {

    @InjectMockKs
    private lateinit var subject: CefClientFactory

    @MockK
    private lateinit var findId: (CefBrowser?) -> String?

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
                    single(qualifier = null) { appSetting } bind(Setting::class)
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                }
            )
        }
        every { appSetting.darkMode() } returns true
        every { appSetting.userAgentName() } returns UserAgent.DEFAULT.name

        mockkConstructor(CefSettingsApplier::class, CefAppBuilder::class)
        every { anyConstructed<CefSettingsApplier>().invoke(any(), any()) } just Runs
        every { anyConstructed<CefAppBuilder>().setInstallDir(any()) } just Runs
        every { anyConstructed<CefAppBuilder>().setProgressHandler(any()) } just Runs
        every { anyConstructed<CefAppBuilder>().cefSettings } returns mockk()
        every { anyConstructed<CefAppBuilder>().build() } returns cefApp
        every { cefApp.createClient() } returns client
        every { client.addLoadHandler(any()) } returns client
        every { client.addLifeSpanHandler(any()) } returns client
        every { client.addRequestHandler(any()) } returns client
        every { client.addDisplayHandler(any()) } returns client
        every { client.addDownloadHandler(any()) } returns client
        every { client.addKeyboardHandler(any()) } returns client
        every { client.addContextMenuHandler(any()) } returns client

        mockkObject(AdHosts)
        every { AdHosts.make() } returns AdHosts(emptySet())

        mockkStatic(CefApp::class)
        every { CefApp.addAppHandler(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        val client = subject.invoke()

        assertNotNull(client)
        verify { anyConstructed<CefSettingsApplier>().invoke(any(), any()) }
        verify { AdHosts.make() }
        verify { CefApp.addAppHandler(any()) }
    }

}