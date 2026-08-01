package jp.toastkid.yobidashi4.presentation.lib.keyboard

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KeyboardDrivenScrollEventHandlerTest {

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun test() {
        val subject = KeyboardDrivenScrollEventHandler()
        assertTrue(subject.invoke(KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true)).consumed)
        assertTrue(subject.invoke(KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = false)).consumed)
        assertTrue(subject.invoke(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown, isCtrlPressed = true)).consumed)
        assertTrue(subject.invoke(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown, isCtrlPressed = false)).consumed)
        assertTrue(subject.invoke(KeyEvent(Key.PageUp, KeyEventType.KeyDown)).consumed)
        assertTrue(subject.invoke(KeyEvent(Key.PageDown, KeyEventType.KeyDown)).consumed)
        assertFalse(subject.invoke(KeyEvent(Key.Z, KeyEventType.KeyDown)).consumed)
    }

}