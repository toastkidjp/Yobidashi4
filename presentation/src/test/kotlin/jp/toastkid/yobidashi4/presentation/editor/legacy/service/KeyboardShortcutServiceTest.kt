package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.event.KeyEvent
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

internal class KeyboardShortcutServiceTest {

    private lateinit var keyboardShortcutService: KeyboardShortcutService

    @MockK
    private lateinit var channel: Channel<MenuCommand>

    @MockK
    private lateinit var keyEvent: KeyEvent

    @MockK
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { viewModel } bind(MainViewModel::class)
                }
            )
        }

        keyboardShortcutService = KeyboardShortcutService()

        coEvery { viewModel.emitEditorCommand(any()) }.answers { Unit }
        every { keyEvent.isAltDown() }.answers { false }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun testIsNotControlDown() {
        every { keyEvent.isControlDown() }.answers { false }

        keyboardShortcutService.invoke(keyEvent, Dispatchers.Unconfined)

        verify(exactly = 1) { keyEvent.isControlDown() }
        coVerify(exactly = 0) { viewModel.emitEditorCommand(any()) }
    }

    @Test
    fun testIsControlAndShiftDown() {
        every { keyEvent.isControlDown() }.answers { true }
        every { keyEvent.isShiftDown() }.answers { true }
        every { keyEvent.getKeyCode() }.answers { KeyEvent.VK_U }

        keyboardShortcutService.invoke(keyEvent, Dispatchers.Unconfined)

        verify(exactly = 1) { keyEvent.isControlDown() }
        verify(atLeast = 1) { keyEvent.isShiftDown() }
        verify(atLeast = 1) { keyEvent.getKeyCode() }
        coVerify(exactly = 1) { viewModel.emitEditorCommand(any()) }
    }

    @Test
    fun testIsControlAndShiftDownButNotContainU() {
        every { keyEvent.isControlDown() }.answers { true }
        every { keyEvent.isShiftDown() }.answers { true }
        every { keyEvent.getKeyCode() }.answers { KeyEvent.VK_Z }

        keyboardShortcutService.invoke(keyEvent, Dispatchers.Unconfined)

        verify(exactly = 1) { keyEvent.isControlDown() }
        verify(atLeast = 1) { keyEvent.isShiftDown() }
        verify(atLeast = 1) { keyEvent.getKeyCode() }
        coVerify(exactly = 0) { viewModel.emitEditorCommand(any()) }
    }

    @Test
    fun testIsControlAndZ() {
        every { keyEvent.isControlDown() }.answers { true }
        every { keyEvent.isShiftDown() }.answers { false }
        every { keyEvent.getKeyCode() }.answers { KeyEvent.VK_T }

        keyboardShortcutService.invoke(keyEvent, Dispatchers.Unconfined)

        verify(exactly = 1) { keyEvent.isControlDown() }
        verify(atLeast = 1) { keyEvent.isShiftDown() }
        verify(exactly = 1) { keyEvent.getKeyCode() }
        coVerify(exactly = 1) { viewModel.emitEditorCommand(any()) }
    }

    @Test
    fun testIsControlAndI() {
        every { keyEvent.isControlDown() }.answers { true }
        every { keyEvent.isShiftDown() }.answers { false }
        every { keyEvent.getKeyCode() }.answers { KeyEvent.VK_I }

        keyboardShortcutService.invoke(keyEvent, Dispatchers.Unconfined)

        verify(exactly = 1) { keyEvent.isControlDown() }
        verify(atLeast = 1) { keyEvent.isShiftDown() }
        verify(exactly = 1) { keyEvent.getKeyCode() }
        coVerify(exactly = 1) { viewModel.emitEditorCommand(MenuCommand.ITALIC) }
    }

}