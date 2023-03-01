package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.GridBagConstraints
import javax.swing.JComponent
import javax.swing.JOptionPane
import javax.swing.JPanel
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.setting.TestSettingImplementation
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import kotlinx.coroutines.channels.Channel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

internal class AppearanceSettingServiceTest {

    private lateinit var appearanceSettingService: AppearanceSettingService

    @MockK
    private lateinit var channel: Channel<MenuCommand>

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier=null) { TestSettingImplementation() } bind(Setting::class)
                }
            )
        }

        MockKAnnotations.init(this)
        coEvery { channel.send(any()) }.just(Runs)

        appearanceSettingService = AppearanceSettingService(channel)

        mockkConstructor(JPanel::class)
        every { anyConstructed<JPanel>().setLayout(any()) }.returns(mockk())
        every { anyConstructed<JPanel>().add(any<JComponent>(), any<GridBagConstraints>()) }.returns(mockk())

        mockkStatic(JOptionPane::class)
        every { JOptionPane.showMessageDialog(any(), any()) }.just(Runs)
    }

    @Test
    fun test() {
        appearanceSettingService.invoke()

        coVerify (atLeast = 1) { channel.send(any()) }
        verify (atLeast = 1) { anyConstructed<JPanel>().setLayout(any()) }
        verify (atLeast = 1) { anyConstructed<JPanel>().add(any<JComponent>(), any<GridBagConstraints>()) }
        verify (atLeast = 1) { JOptionPane.showMessageDialog(any(), any()) }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

}