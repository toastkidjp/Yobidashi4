package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
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
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ExpressionTextCalculatorService
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.LinkDecoratorService
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.NumberedListHeadAdder
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(InternalComposeUiApi::class)
class KeyEventConsumerTest {

    @InjectMockKs
    private lateinit var subject: KeyEventConsumer

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var searchUrlFactory: SearchUrlFactory

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = KeyEventConsumer(mainViewModel, searchUrlFactory = searchUrlFactory)
        every { searchUrlFactory.invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onKeyUp() {
        val consumed = subject.invoke(
            KeyEvent(Key.A, KeyEventType.KeyUp, isCtrlPressed = true),
            TextFieldValue(),
            mockk(),
            {}
        )

        assertFalse(consumed)
    }

    @Test
    fun duplicateSelectedText() {
        val consumed = subject.invoke(
            KeyEvent(Key.D, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.", TextRange(6, 9)),
            mockk(),
            { assertEquals("Angel hashas fallen.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun duplicateCurrentLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(Key.D, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            multiParagraph,
            { assertEquals("Angel has fallen.\nAngel has fallen.\nHe has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopListConversionIfNotSelectedAnyText() {
        val consumed = subject.invoke(
            KeyEvent(Key.Minus, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun noopListConversion() {
        mockkConstructor(ListHeadAdder::class)
        every { anyConstructed<ListHeadAdder>().invoke(any(), any()) } returns null

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(Key.Minus, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { fail() }
        )

        assertFalse(consumed)
    }

    @Test
    fun listConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(Key.Minus, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { assertEquals("- Angel has fallen.\n- He has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopOrderedListConversionIfNotSelectedAnyText() {
        val consumed = subject.invoke(
            KeyEvent(Key.One, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun noopOrderedListConversionWhenReturnsNullConversionResult() {
        mockkConstructor(NumberedListHeadAdder::class)
        every { anyConstructed<NumberedListHeadAdder>().invoke(any()) } returns null
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(Key.One, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { fail() }
        )

        assertFalse(consumed)
    }

    @Test
    fun orderedListConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(Key.One, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { assertEquals("1. Angel has fallen.\n2. He has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopTaskListConversionIfNotSelectedAnyText() {
        val consumed = subject.invoke(
            KeyEvent(Key.Zero, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun noopTaskListConversion() {
        mockkConstructor(ListHeadAdder::class)
        every { anyConstructed<ListHeadAdder>().invoke(any(), any()) } returns null

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(Key.Zero, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { fail() }
        )

        assertFalse(consumed)
    }

    @Test
    fun taskListConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(Key.Zero, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.\n", TextRange(0, 30)),
            multiParagraph,
            { assertEquals("- [ ] Angel has fallen.\n- [ ] He has gone.\n", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun moveToLineStart() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0

        val consumed = subject.invoke(
            KeyEvent(Key.Four, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(5)),
            multiParagraph,
            { assertEquals(0, it.selection.start) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopMoveToLineStart() {
        val consumed = subject.invoke(
            KeyEvent(Key.Four, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(5)),
            null,
            { fail() }
        )

        assertFalse(consumed)
    }

    @Test
    fun moveToLineEnd() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 15

        val consumed = subject.invoke(
            KeyEvent(Key.E, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(5)),
            multiParagraph,
            { assertEquals(15, it.selection.start) }
        )

        assertTrue(consumed)
    }

    @Test
    fun commaInsertion() {
        val consumed = subject.invoke(
            KeyEvent(Key.Comma, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("2000000", TextRange(0, "2000000".length)),
            mockk(),
            { assertEquals("2,000,000", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopTableConversion() {
        val consumed = subject.invoke(
            KeyEvent(Key.T, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test test"),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun tableConversion() {
        val consumed = subject.invoke(
            KeyEvent(Key.T, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test test", TextRange(0, 6)),
            mockk(),
            { assertEquals("| test | test", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun caseConversion() {
        val consumed = subject.invoke(
            KeyEvent(Key.U, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("TEST", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopCaseConversion() {
        val consumed = subject.invoke(
            KeyEvent(Key.U, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue("test"),
            mockk(),
            { assertNull(it) }
        )

        assertTrue(consumed)
    }

    @Test
    fun caseConversionToLower() {
        val consumed = subject.invoke(
            KeyEvent(Key.U, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue("TEST", TextRange(0, 4)),
            mockk(),
            { assertEquals("test", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun bolding() {
        val consumed = subject.invoke(
            KeyEvent(Key.B, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("**test**", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun italic() {
        val consumed = subject.invoke(
            KeyEvent(Key.I, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("***test***", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun doubleQuote() {
        val consumed = subject.invoke(
            KeyEvent(Key.Two, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("\"test\"", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopDoubleQuote() {
        val consumed = subject.invoke(
            KeyEvent(Key.Two, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test"),
            mockk(),
            { fail() }
        )

        assertTrue(consumed)
    }

    @Test
    fun braces() {
        val consumed = subject.invoke(
            KeyEvent(Key.Eight, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("(test)", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun surroundBraces() {
        val consumed = subject.invoke(
            KeyEvent(Key.RightBracket, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("「test」", it.getSelectedText().text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun surroundBackQuote() {
        val consumed = subject.invoke(
            KeyEvent(Key.At, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("```test```", it.getSelectedText().text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun calculate() {
        mockkConstructor(ExpressionTextCalculatorService::class)
        every { anyConstructed<ExpressionTextCalculatorService>().invoke(any()) } returns "3"

        val consumed = subject.invoke(
            KeyEvent(Key.C, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue("1+2", TextRange(0, 3)),
            mockk(),
            {
                assertEquals("3", it.getSelectedText().text)
                verify { anyConstructed<ExpressionTextCalculatorService>().invoke("1+2") }
            }
        )

        assertTrue(consumed)
    }

    @Test
    fun switchEditable() {
        val editorTab = mockk<EditorTab>()
        every { editorTab.switchEditable() } just Runs
        every { mainViewModel.currentTab() } returns editorTab

        val consumed = subject.invoke(
            KeyEvent(Key.N, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue("1+2", TextRange(0, 3)),
            mockk(),
            {  }
        )

        assertTrue(consumed)
        verify { editorTab.switchEditable() }
        verify { mainViewModel.currentTab() }
    }

    @Test
    fun openUrl() {
        every { mainViewModel.openUrl(any(), any()) } just Runs

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            {  }
        )

        assertTrue(consumed)
        verify { mainViewModel.openUrl(any(), any()) }
    }

    @Test
    fun noopOpenUrl() {
        every { mainViewModel.openUrl(any(), any()) } just Runs

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldValue("test"),
            mockk(),
            {  }
        )

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun browseUri() {
        every { mainViewModel.browseUri(any()) } just Runs

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            {  }
        )

        assertTrue(consumed)
        verify { mainViewModel.browseUri(any()) }
    }

    @Test
    fun noopBrowseUri() {
        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldValue("test"),
            mockk(),
            {  }
        )

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun noopCombineLines() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0

        val consumed = subject.invoke(
            KeyEvent(Key.J, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("nc"),
            mockk(),
            { fail() }
        )

        assertFalse(consumed)
    }

    @Test
    fun combineLines() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0

        val consumed = subject.invoke(
            KeyEvent(Key.J, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("a\nb\nc"),
            mockk(),
            { assertEquals("ab\nc", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun quoteSelectedText() {
        val text = "test\ntest2"
        val consumed = subject.invoke(
            KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue(text, TextRange(0, text.length)),
            mockk(),
            { assertEquals("> test\n> test2", it.getSelectedText().text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun quoteInsertion() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"

        val consumed = subject.invoke(
            KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("", TextRange(0)),
            mockk(),
            { assertEquals("> test", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopQuoteInsertion() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns null

        val consumed = subject.invoke(
            KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("", TextRange(0)),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun pasteDecoratedLink() {
        mockkConstructor(ClipboardFetcher::class, LinkDecoratorService::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "https://test.yahoo.com"
        val decoratedLink = "[test](https://test.yahoo.com)"
        every { anyConstructed<LinkDecoratorService>().invoke(any()) } returns decoratedLink

        val consumed = subject.invoke(
            KeyEvent(Key.L, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue("", TextRange(0)),
            mockk(),
            { assertEquals(decoratedLink, it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun toDecoratedLink() {
        val selected = "https://test.yahoo.com"
        mockkConstructor(ClipboardFetcher::class, LinkDecoratorService::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns selected
        val decoratedLink = "[test]($selected)"
        every { anyConstructed<LinkDecoratorService>().invoke(any()) } returns decoratedLink

        val consumed = subject.invoke(
            KeyEvent(Key.L, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldValue(selected, TextRange(0, selected.length)),
            mockk(),
            { assertEquals(decoratedLink, it.text) }
        )

        assertTrue(consumed)
    }

}