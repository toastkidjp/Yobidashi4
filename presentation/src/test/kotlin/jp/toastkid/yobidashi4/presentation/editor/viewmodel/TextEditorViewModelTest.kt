package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.MultiParagraphIntrinsics
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrderReceiver
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
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

@OptIn(InternalComposeUiApi::class)
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
        every { setting.editorFontSize() } returns 16
        every { setting.editorLineHeight() } returns 1.5f
        every { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) } just Runs
        every { mainViewModel.darkMode() } returns true
        mockkConstructor(FindOrderReceiver::class)
        every { anyConstructed<FindOrderReceiver>().invoke(any(), any()) } just Runs
        val multiParagraphIntrinsics = mockk<MultiParagraphIntrinsics>()
        every { multiParagraphIntrinsics.annotatedString } returns AnnotatedString("test")
        every { multiParagraph.intrinsics } returns multiParagraphIntrinsics
        every { multiParagraph.getCursorRect(any()) } returns Rect(Offset.Zero, 20f)

        viewModel = TextEditorViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun setMultiParagraph() {
        assertEquals(Offset.Zero, viewModel.currentLineOffset())
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
        viewModel.content().edit { append(text) }

        viewModel.onClickLineNumber(0)

        val content = viewModel.content()
        assertEquals(text, content.text.subSequence(content.selection.start, content.selection.end))
        verify { multiParagraph.getLineHeight(0) }
        verify { multiParagraph.getLineHeight(1) }
        verify { multiParagraph.getLineHeight(2) }
    }

    @Test
    fun noopOnClickLineNumber() {
        viewModel.onClickLineNumber(0)
    }

    @Test
    fun noopOnValueChange() {
        val keyEvent = KeyEvent(
            Key.AltLeft,
            KeyEventType.KeyDown,
            isAltPressed = true
        )
        viewModel.onKeyEvent(keyEvent)

        assertTrue(viewModel.content().text.isEmpty())
    }

    @Test
    fun focusRequester() {
        assertNotNull(viewModel.focusRequester())
    }

    @Test
    fun onKeyEvent() {
        val keyEvent = KeyEvent(Key.Comma, KeyEventType.KeyDown, isCtrlPressed = true)

        val consumed = viewModel.onKeyEvent(keyEvent)

        assertFalse(consumed)
    }

    @Test
    fun onPreviewKeyEventUnconsumed() {
        val keyEvent = KeyEvent(Key.Enter, KeyEventType.KeyDown, isCtrlPressed = true)
        viewModel = TextEditorViewModel()

        val consumed = viewModel.onPreviewKeyEvent(keyEvent, CoroutineScope(Dispatchers.Unconfined))

        assertFalse(consumed)
    }

    @Test
    fun onPreviewKeyEvent() {
        val keyEvent = KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true)
        viewModel = TextEditorViewModel()

        val consumed = viewModel.onPreviewKeyEvent(keyEvent, CoroutineScope(Dispatchers.Unconfined))

        assertTrue(consumed)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun adjustLineNumberState() {
        CoroutineScope(Dispatchers.Unconfined).launch {
            viewModel.adjustLineNumberState()

            assertEquals(0, viewModel.lineNumberScrollState().value)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun initialScroll() {
        val focusRequester = mockk<FocusRequester>()
        every { focusRequester.requestFocus() } returns true
        viewModel = spyk(viewModel)
        every { viewModel.focusRequester() } returns focusRequester

        viewModel.initialScroll(CoroutineScope(Dispatchers.Unconfined), 0L)

        assertEquals(0, viewModel.verticalScrollState().value)
        verify { focusRequester.requestFocus() }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun initialScrollWhenTabsScrollOverZero() {
        val focusRequester = mockk<FocusRequester>()
        every { focusRequester.requestFocus() } returns true
        viewModel = spyk(viewModel)
        every { viewModel.focusRequester() } returns focusRequester
        val editorTab = mockk<EditorTab>()
        every { editorTab.scroll() } returns 1.0
        every { editorTab.getContent() } returns "test"
        every { editorTab.caretPosition() } returns 0
        every { editorTab.editable() } returns true
        every { editorTab.path } returns mockk()
        every { mainViewModel.finderFlow() } returns emptyFlow()
        viewModel.launchTab(editorTab)

        viewModel.initialScroll(CoroutineScope(Dispatchers.Unconfined), 0L)

        assertEquals(1, viewModel.verticalScrollState().value)
        verify { focusRequester.requestFocus() }
        verify { editorTab.scroll() }
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
        every { mainViewModel.finderFlow() } returns flowOf(FindOrder.EMPTY)

        viewModel.launchTab(tab, Dispatchers.Unconfined)

        verify { tab.getContent() }
        verify { tab.caretPosition() }
        verify { tab.editable() }
        verify { anyConstructed<FindOrderReceiver>().invoke(any(), any()) }
    }

    @Test
    fun launchTabWithNotEditableTab() {
        val tab = mockk<EditorTab>()
        every { tab.getContent() } returns "test"
        every { tab.caretPosition() } returns 3
        every { tab.editable() } returns false
        every { tab.path } returns mockk()
        every { mainViewModel.setFindStatus(any()) } just Runs
        every { mainViewModel.finderFlow() } returns flowOf(FindOrder.EMPTY)

        viewModel.launchTab(tab, Dispatchers.Unconfined)

        assertEquals("test", viewModel.content().text)
        verify { tab.getContent() }
        verify { tab.caretPosition() }
        verify { tab.editable() }
        verify { anyConstructed<FindOrderReceiver>().invoke(any(), any()) }
    }

    @Test
    fun dispose() {
        viewModel.dispose()

        //verify { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) }
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
    fun makeCharacterCountMessage() {
        assertEquals("Character: 200", viewModel.makeCharacterCountMessage(200))
        assertEquals("Character: 2,000", viewModel.makeCharacterCountMessage(2000))
    }

    @Test
    fun getHighlightSize() {
        assertNotNull(viewModel.getHighlightSize())
    }

    @Test
    fun fontSize() {
        assertEquals(16, viewModel.fontSize())
    }

    @Test
    fun lineHeight() {
        assertEquals(1.5f, viewModel.lineHeight())
    }

    @Test
    fun visualTransformation() {
        viewModel.visualTransformation()

        every { setting.editorConversionLimit() } returns -1

        viewModel = TextEditorViewModel()
        viewModel.visualTransformation()
    }

}