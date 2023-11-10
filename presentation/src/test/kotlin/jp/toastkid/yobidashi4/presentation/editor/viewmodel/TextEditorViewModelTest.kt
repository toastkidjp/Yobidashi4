package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.getSelectedText
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TextEditorViewModelTest {

    private lateinit var viewModel: TextEditorViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }
        viewModel = TextEditorViewModel()

        every { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun setMultiParagraph() {
        assertEquals(Offset.Unspecified, viewModel.currentLineOffset())
        assertTrue(viewModel.lineNumbers().isEmpty())

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineLeft(any()) } returns 20f
        every { multiParagraph.getLineTop(any()) } returns 0f
        every { multiParagraph.lineCount } returns 3

        viewModel.setMultiParagraph(multiParagraph)

        val currentLineOffset = viewModel.currentLineOffset()
        assertEquals(20f, currentLineOffset.x)
        assertEquals(0f, currentLineOffset.y)
        assertEquals(3, viewModel.lineNumbers().size)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun verticalScrollState() {
        assertFalse(viewModel.verticalScrollState().isScrollInProgress)
    }

    @Test
    fun scrollbarAdapter() {
        assertEquals(0.0, viewModel.scrollbarAdapter().scrollOffset)
    }

    @Test
    fun onClickLineNumber() {
        val text = "Angel has fallen."
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(any()) } returns 0
        every { multiParagraph.getLineEnd(any()) } returns text.length
        every { multiParagraph.lineCount } returns 3
        viewModel.setMultiParagraph(multiParagraph)
        viewModel.onValueChange(TextFieldValue(text))

        viewModel.onClickLineNumber(0)

        assertEquals(text, viewModel.content().getSelectedText().text)
    }

    @Test
    fun focusRequester() {
        assertNotNull(viewModel.focusRequester())
    }

    @Test
    fun onKeyEvent() {
        val keyEvent = KeyEvent(
            java.awt.event.KeyEvent(
                mockk(),
                java.awt.event.KeyEvent.KEY_PRESSED,
                1,
                java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                java.awt.event.KeyEvent.VK_COMMA,
                ','
            )
        )
        viewModel = TextEditorViewModel()

        val consumed = viewModel.onKeyEvent(keyEvent)

        assertFalse(consumed)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun adjustLineNumberState() {
        CoroutineScope(Dispatchers.Unconfined).launch {
            viewModel.verticalScrollState().offset = 10f

            viewModel.adjustLineNumberState()

            assertEquals(10, viewModel.lineNumberScrollState().value)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun initialScroll() {
        viewModel.initialScroll(CoroutineScope(Dispatchers.Unconfined), 0L)

        assertEquals(0.0f, viewModel.verticalScrollState().offset)
    }

    @Test
    fun lineNumbers() {
        assertTrue(viewModel.lineNumbers().isEmpty())
    }

    @Test
    fun launchTab() {
    }

    @Test
    fun dispose() {
        viewModel.onValueChange(TextFieldValue("test"))

        viewModel.dispose()

        verify { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) }
        assertTrue(viewModel.content().text.isEmpty())
    }

    @Test
    fun disposeWithEmptyContent() {
        viewModel.dispose()

        verify(inverse = true) { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) }
        assertTrue(viewModel.content().text.isEmpty())
    }

    @Test
    fun currentLineHighlightColor() {
        every { mainViewModel.darkMode() } returns false
        val lineHighlightColorOnLight = viewModel.currentLineHighlightColor()
        every { mainViewModel.darkMode() } returns true
        val lineHighlightColorOnDark = viewModel.currentLineHighlightColor()

        assertNotEquals(lineHighlightColorOnLight, lineHighlightColorOnDark)
    }

    @Test
    fun visualTransformation() {
        assertNotEquals(VisualTransformation.None, viewModel.visualTransformation())
    }

    @Test
    fun makeCharacterCountMessage() {
        assertEquals("Character: 200", viewModel.makeCharacterCountMessage(200))
    }
}