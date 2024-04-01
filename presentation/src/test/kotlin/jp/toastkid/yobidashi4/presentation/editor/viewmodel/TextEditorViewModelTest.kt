package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.unit.em
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
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
    private lateinit var setting: Setting

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier = null) { setting } bind(Setting::class)
                }
            )
        }
        every { setting.editorConversionLimit() } returns 4500

        viewModel = TextEditorViewModel()

        every { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) } just Runs
        every { mainViewModel.darkMode() } returns true
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
        every { multiParagraph.getLineHeight(any()) } returns 31.0f
        every { multiParagraph.getLineLeft(any()) } returns 20f
        every { multiParagraph.getLineTop(any()) } returns 0f
        every { multiParagraph.lineCount } returns 11

        viewModel.setMultiParagraph(multiParagraph)

        val currentLineOffset = viewModel.currentLineOffset()
        assertEquals(20f, currentLineOffset.x)
        assertEquals(0f, currentLineOffset.y)
        assertEquals(11, viewModel.lineNumbers().size)
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
        every { multiParagraph.getLineHeight(0) } returns 25.0f
        every { multiParagraph.getLineHeight(1) } returns 31.0f
        every { multiParagraph.getLineHeight(2) } returns 30.0f
        every { multiParagraph.lineCount } returns 3
        viewModel.setMultiParagraph(multiParagraph)
        viewModel.onValueChange(TextFieldValue(text))

        viewModel.onClickLineNumber(0)

        assertEquals(text, viewModel.content().getSelectedText().text)
        assertEquals(1.55.em, viewModel.getLineHeight(-1))
        assertEquals(1.25.em, viewModel.getLineHeight(0))
        assertEquals(1.55.em, viewModel.getLineHeight(1))
        assertEquals(1.5.em, viewModel.getLineHeight(2))
        verify { multiParagraph.getLineHeight(0) }
        verify { multiParagraph.getLineHeight(1) }
        verify { multiParagraph.getLineHeight(2) }
    }

    @Test
    fun noopOnValueChange() {
        val keyEvent = KeyEvent(
            java.awt.event.KeyEvent(
                mockk(),
                java.awt.event.KeyEvent.KEY_PRESSED,
                1,
                java.awt.event.KeyEvent.ALT_DOWN_MASK,
                java.awt.event.KeyEvent.VK_ALT,
                'A'
            )
        )
        viewModel.onKeyEvent(keyEvent)

        viewModel.onValueChange(TextFieldValue("Alt pressed"))

        assertTrue(viewModel.content().text.isEmpty())
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

        val consumed = viewModel.onKeyEvent(keyEvent)

        assertFalse(consumed)
    }

    @Test
    fun onPreviewKeyEvent() {
        val keyEvent = KeyEvent(
            java.awt.event.KeyEvent(
                mockk(),
                java.awt.event.KeyEvent.KEY_PRESSED,
                1,
                java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                java.awt.event.KeyEvent.VK_ENTER,
                ','
            )
        )
        viewModel = TextEditorViewModel()

        val consumed = viewModel.onPreviewKeyEvent(keyEvent, CoroutineScope(Dispatchers.Unconfined))

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
        val focusRequester = mockk<FocusRequester>()
        every { focusRequester.requestFocus() } just Runs
        viewModel = spyk(viewModel)
        every { viewModel.focusRequester() } returns focusRequester

        viewModel.initialScroll(CoroutineScope(Dispatchers.Unconfined), 0L)

        assertEquals(0.0f, viewModel.verticalScrollState().offset)
        verify { focusRequester.requestFocus() }
    }

    @Test
    fun lineNumbers() {
        assertTrue(viewModel.lineNumbers().isEmpty())
    }

    @Test
    fun launchTab() {
        val tab = mockk<EditorTab>()
        every { tab.getContent() } returns "test"
        every { tab.caretPosition() } returns 3
        every { tab.editable() } returns true
        every { tab.path } returns mockk()
        every { mainViewModel.setFindStatus(any()) } just Runs
        every { mainViewModel.finderFlow() } returns flowOf(
            FindOrder.EMPTY,
            FindOrder("test", ""),
            FindOrder("test", "", true, true, false)
        )

        viewModel.launchTab(tab, Dispatchers.Unconfined)

        verify { tab.getContent() }
        verify { tab.caretPosition() }
        verify { tab.editable() }
        verify { mainViewModel.setFindStatus(any()) }
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
        val visualTransformation = viewModel.visualTransformation()
        assertNotEquals(VisualTransformation.None, visualTransformation)

        val transformedText = visualTransformation.filter(buildAnnotatedString { append("test") })
        assertEquals("test[EOF]", transformedText.text.text)
        assertNotEquals(OffsetMapping.Identity, transformedText.offsetMapping)
    }

    @Test
    fun visualTransformationOverLimit() {
        val text = (0..20_000).map { it.toString() }.joinToString()
        viewModel.onValueChange(TextFieldValue(text))

        val visualTransformation = viewModel.visualTransformation()
        assertEquals(VisualTransformation.None, visualTransformation)

        val transformedText = visualTransformation.filter(buildAnnotatedString { append("test") })
        assertFalse(transformedText.text.text.endsWith("[EOF]"))
        assertEquals(OffsetMapping.Identity, transformedText.offsetMapping)
    }

    @Test
    fun makeCharacterCountMessage() {
        assertEquals("Character: 200", viewModel.makeCharacterCountMessage(200))
    }
}