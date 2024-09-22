package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(InternalComposeUiApi::class)
class PreviewKeyEventConsumerTest {

    @InjectMockKs
    private lateinit var previewKeyEventConsumer: PreviewKeyEventConsumer

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @MockK
    private lateinit var scrollBy: (Float) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { scrollBy.invoke(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onKeyUp() {
        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.A, KeyEventType.KeyUp, isCtrlPressed = true),
            TextFieldValue(),
            mockk(),
            {},
            scrollBy
        )

        assertFalse(consumed)
    }

    @Test
    fun scrollUp() {
        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue(),
            mockk(),
            {},
            scrollBy
        )

        assertTrue(consumed)
        verify { scrollBy(-16.dp.value) }
    }

    @Test
    fun scrollDown() {
        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.DirectionDown, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue(),
            mockk(),
            {},
            scrollBy
        )

        assertTrue(consumed)
        verify { scrollBy(16.dp.value) }
    }

    @Test
    fun moveToTop() {
        val setNewContent = mockk<(TextFieldValue) -> Unit>()
        every { setNewContent.invoke(any()) } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue(),
            mockk(),
            setNewContent,
            scrollBy
        )

        assertTrue(consumed)
        verify { setNewContent.invoke(any()) }
    }

    @Test
    fun moveToBottom() {
        val setNewContent = mockk<(TextFieldValue) -> Unit>()
        every { setNewContent.invoke(any()) } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.DirectionDown, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue(),
            mockk(),
            setNewContent,
            scrollBy
        )

        assertTrue(consumed)
        verify { setNewContent.invoke(any()) }
    }

    @Test
    fun noopCtrlShift() {
        val setNewContent = mockk<(TextFieldValue) -> Unit>()
        every { setNewContent.invoke(any()) } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.DirectionLeft, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue(),
            mockk(),
            setNewContent,
            scrollBy
        )

        assertFalse(consumed)
        verify { setNewContent wasNot Called }
    }

    @Test
    fun cutLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.X, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test\ntest2\ntest3"),
            multiParagraph,
            {},
            scrollBy
        )

        assertTrue(consumed)
        verify { scrollBy wasNot called }
        verify { multiParagraph.getLineForOffset(any()) }
        verify { multiParagraph.getLineStart(0) }
        verify { multiParagraph.getLineEnd(0) }
        verify { anyConstructed<ClipboardPutterService>().invoke("test\n") }
    }

    @Test
    fun noopCutLine() {
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.X, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test\ntest2\ntest3"),
            null,
            {},
            scrollBy
        )

        assertFalse(consumed)
        verify { scrollBy wasNot called }
        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke("test\n") }
    }

    @Test
    fun cutLineOnSelectedTextCase() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.X, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test\ntest2\ntest3", TextRange(1, 3)),
            multiParagraph,
            {},
            scrollBy
        )

        assertFalse(consumed)
        verify { scrollBy wasNot called }
        verify { multiParagraph wasNot called }
        verify { anyConstructed<ClipboardPutterService>() wasNot called }
    }

    @Test
    fun deleteLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.Enter, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test\ntest2\ntest3"),
            multiParagraph,
            { assertEquals("test2\ntest3", it.text) },
            scrollBy
        )

        assertTrue(consumed)
        verify { scrollBy wasNot called }
        verify { multiParagraph.getLineForOffset(any()) }
        verify { multiParagraph.getLineStart(0) }
        verify { multiParagraph.getLineEnd(0) }
    }

    @Test
    fun deleteLineOnSelectedText() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.Enter, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test\ntest2\ntest3", TextRange(0, 3)),
            multiParagraph,
            {},
            scrollBy
        )

        assertTrue(consumed)
        verify { scrollBy wasNot called }
        verify { multiParagraph wasNot called }
    }

    @Test
    fun deleteLineOnParagraphIsNull() {
        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.Enter, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test\ntest2\ntest3"),
            null,
            {},
            scrollBy
        )

        assertFalse(consumed)
    }

    @Test
    fun elseCase() {
        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.Unknown, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue(),
            mockk(),
            {},
            scrollBy
        )

        assertFalse(consumed)
    }

    @Test
    fun hideFileList() {
        every { mainViewModel.switchArticleList() } just Runs
        every { mainViewModel.hideArticleList() } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.DirectionLeft, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldValue(),
            mockk(),
            {},
            mockk()
        )

        assertTrue(consumed)

        verify(inverse = true) { mainViewModel.switchArticleList() }
        verify { mainViewModel.hideArticleList() }
    }

    @Test
    fun openFileList() {
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.switchArticleList() } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.DirectionRight, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldValue(),
            mockk(),
            {},
            scrollBy
        )

        assertTrue(consumed)

        verify { mainViewModel.openArticleList() }
        verify { mainViewModel.switchArticleList() }
        verify { scrollBy wasNot called }
    }

    @Test
    fun noopOpenFileList() {
        every { mainViewModel.openArticleList() } returns true
        every { mainViewModel.switchArticleList() } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.DirectionRight, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldValue(),
            mockk(),
            {},
            scrollBy
        )

        assertTrue(consumed)

        verify { mainViewModel.openArticleList() }
        verify(inverse = true) { mainViewModel.switchArticleList() }
        verify { scrollBy wasNot called }
    }

    @Test
    fun noopAltCombination() {
        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.Unknown, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldValue(),
            mockk(),
            {},
            mockk()
        )

        assertFalse(consumed)
    }

    @Test
    fun lastLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 2
        every { multiParagraph.getLineStart(2) } returns 11
        every { multiParagraph.getLineEnd(2) } returns 16
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(Key.X, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test\ntest2\ntest3"),
            multiParagraph,
            {},
            scrollBy
        )

        assertTrue(consumed)
        verify { scrollBy wasNot called }
        verify { multiParagraph.getLineForOffset(any()) }
        verify { multiParagraph.getLineStart(2) }
        verify { multiParagraph.getLineEnd(2) }
        verify { anyConstructed<ClipboardPutterService>().invoke("test3") }
    }

}