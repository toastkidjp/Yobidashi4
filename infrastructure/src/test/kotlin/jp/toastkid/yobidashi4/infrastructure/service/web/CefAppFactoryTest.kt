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
import me.friwi.jcefmaven.CefAppBuilder
import org.cef.CefApp
import org.cef.callback.CefCommandLine
import org.cef.handler.CefAppHandlerAdapter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class CefAppFactoryTest {

    private lateinit var subject: CefAppFactory

    @MockK
    private lateinit var appSetting : Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { appSetting } bind(Setting::class)
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
        every { anyConstructed<CefAppBuilder>().build() } returns mockk()

        subject = CefAppFactory()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        val slot = slot<CefAppHandlerAdapter>()
        mockkStatic(CefApp::class)
        every { CefApp.addAppHandler(capture(slot)) } just Runs
        val cefCommandLine = mockk<CefCommandLine>()
        every { cefCommandLine.appendSwitch(any()) } just Runs
        every { cefCommandLine.appendSwitchWithValue(any(), any()) } just Runs

        val cefApp = subject.invoke()
        slot.captured.onBeforeCommandLineProcessing("", cefCommandLine)

        assertNotNull(cefApp)
        verify(exactly = 2) { cefCommandLine.appendSwitchWithValue(any(), any()) }
        verify { CefApp.addAppHandler(any()) }
    }

    @Test
    fun invokeWithProcessType() {
        val slot = slot<CefAppHandlerAdapter>()
        mockkStatic(CefApp::class)
        every { CefApp.addAppHandler(capture(slot)) } just Runs
        val cefCommandLine = mockk<CefCommandLine>()
        every { cefCommandLine.appendSwitch(any()) } just Runs
        every { cefCommandLine.appendSwitchWithValue(any(), any()) } just Runs

        val cefApp = subject.invoke()
        slot.captured.onBeforeCommandLineProcessing("test", cefCommandLine)

        assertNotNull(cefApp)
        verify(inverse = true) { cefCommandLine.appendSwitchWithValue(any(), any()) }
        verify { CefApp.addAppHandler(any()) }
    }

}