package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.unit.dp
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.editor.usecase.TextEditorOperationUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(InternalComposeUiApi::class)
class PreviewKeyEventHandlerTest {

    @InjectMockKs
    private lateinit var previewKeyEventHandler: PreviewKeyEventHandler

    @MockK
    private lateinit var useCase: TextEditorOperationUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { useCase.deleteLine() } just Runs
        every { useCase.cutLine() } returns true
        every { useCase.moveToTop() } just Runs
        every { useCase.moveToBottom() } just Runs
        every { useCase.scrollBy(any()) } just Runs
        every { useCase.switchLineNumber() } just Runs
        every { useCase.switchArticleList() } just Runs
        every { useCase.hideArticleList() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onKeyUp() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.A, KeyEventType.KeyUp, isCtrlPressed = true)
        )

        assertFalse(consumed)
    }

    @Test
    fun scrollUp() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertTrue(consumed)
        verify { useCase.scrollBy(-16.dp.value) }
    }

    @Test
    fun scrollDown() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.DirectionDown, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertTrue(consumed)
        verify { useCase.scrollBy(16.dp.value) }
    }

    @Test
    fun moveToTop() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true)
        )

        assertTrue(consumed)
        verify { useCase.moveToTop() }
    }

    @Test
    fun moveToBottom() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.DirectionDown, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
        )

        assertTrue(consumed)
        verify { useCase.moveToBottom() }
    }

    @Test
    fun noopCtrlShift() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.DirectionLeft, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true)
        )

        assertFalse(consumed)
    }

    @Test
    fun noopShift() {
        val setNewContent = mockk<(TextFieldState) -> Unit>()
        every { setNewContent.invoke(any()) } just Runs

        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = false, isShiftPressed = true)
        )

        assertFalse(consumed)
    }

    @Test
    fun cutLine() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.X, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertTrue(consumed)
        verify { useCase.cutLine() }
    }

    @Test
    fun deleteLine() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.Enter, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertTrue(consumed)
        verify { useCase.deleteLine() }
    }

    @Test
    fun elseCase() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.Unknown, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertFalse(consumed)
        verify(inverse = true) { useCase.deleteLine() }
    }

    @Test
    fun hideFileList() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.DirectionLeft, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
        )

        assertTrue(consumed)
        verify { useCase.hideArticleList() }
    }

    @Test
    fun openFileList() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.DirectionRight, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true)
        )

        assertTrue(consumed)
        verify { useCase.switchArticleList() }
    }

    @Test
    fun noopAltCombination() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.Unknown, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true)
        )

        assertFalse(consumed)
    }

    @Test
    fun lastLine() {
        val consumed = previewKeyEventHandler.invoke(
            KeyEvent(Key.X, KeyEventType.KeyDown, isCtrlPressed = true),
        )

        assertTrue(consumed)
        verify { useCase.cutLine() }
    }

}