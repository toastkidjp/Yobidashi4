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
import kotlinx.coroutines.channels.Channel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class KeyboardShortcutServiceTest {

    private lateinit var keyboardShortcutService: KeyboardShortcutService

    @MockK
    private lateinit var channel: Channel<MenuCommand>

    @MockK
    private lateinit var keyEvent: KeyEvent

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        keyboardShortcutService = KeyboardShortcutService(channel)

        coEvery { channel.send(any()) }.answers { Unit }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testIsNotControlDown() {
        every { keyEvent.isControlDown() }.answers { false }

        keyboardShortcutService.invoke(keyEvent)

        verify(exactly = 1) { keyEvent.isControlDown() }
        coVerify(exactly = 0) { channel.send(any()) }
    }

    @Test
    fun testIsControlAndShiftDown() {
        every { keyEvent.isControlDown() }.answers { true }
        every { keyEvent.isShiftDown() }.answers { true }
        every { keyEvent.getKeyCode() }.answers { KeyEvent.VK_U }

        keyboardShortcutService.invoke(keyEvent)

        verify(exactly = 1) { keyEvent.isControlDown() }
        verify(exactly = 1) { keyEvent.isShiftDown() }
        verify(exactly = 1) { keyEvent.getKeyCode() }
        coVerify(exactly = 1) { channel.send(any()) }
    }

    @Test
    fun testIsControlAndShiftDownButNotContainU() {
        every { keyEvent.isControlDown() }.answers { true }
        every { keyEvent.isShiftDown() }.answers { true }
        every { keyEvent.getKeyCode() }.answers { KeyEvent.VK_Z }

        keyboardShortcutService.invoke(keyEvent)

        verify(exactly = 1) { keyEvent.isControlDown() }
        verify(exactly = 1) { keyEvent.isShiftDown() }
        verify(exactly = 2) { keyEvent.getKeyCode() }
        coVerify(exactly = 0) { channel.send(any()) }
    }

    @Test
    fun testIsControlAndZ() {
        every { keyEvent.isControlDown() }.answers { true }
        every { keyEvent.isShiftDown() }.answers { false }
        every { keyEvent.getKeyCode() }.answers { KeyEvent.VK_T }

        keyboardShortcutService.invoke(keyEvent)

        verify(exactly = 1) { keyEvent.isControlDown() }
        verify(exactly = 1) { keyEvent.isShiftDown() }
        verify(exactly = 1) { keyEvent.getKeyCode() }
        coVerify(exactly = 1) { channel.send(any()) }
    }

    @Test
    fun testIsControlAndI() {
        every { keyEvent.isControlDown() }.answers { true }
        every { keyEvent.isShiftDown() }.answers { false }
        every { keyEvent.getKeyCode() }.answers { KeyEvent.VK_I }

        keyboardShortcutService.invoke(keyEvent)

        verify(exactly = 1) { keyEvent.isControlDown() }
        verify(exactly = 1) { keyEvent.isShiftDown() }
        verify(exactly = 1) { keyEvent.getKeyCode() }
        coVerify(exactly = 1) { channel.send(MenuCommand.ITALIC) }
    }

}