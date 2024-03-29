package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.input.key.KeyEvent
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

class KeyEventConsumerTest {

    @InjectMockKs
    private lateinit var subject: KeyEventConsumer

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var searchUrlFactory: SearchUrlFactory

    private lateinit var awtKeyEvent: java.awt.event.KeyEvent

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = KeyEventConsumer(mainViewModel, searchUrlFactory)
        every { searchUrlFactory.invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onKeyUp() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_A,
            'A'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue(),
            mockk(),
            {}
        )

        assertFalse(consumed)
    }

    @Test
    fun duplicateSelectedText() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_D,
            'D'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.", TextRange(6, 9)),
            mockk(),
            { assertEquals("Angel hashas fallen.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun duplicateCurrentLine() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_D,
            'D'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            multiParagraph,
            { assertEquals("Angel has fallen.\nAngel has fallen.\nHe has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopListConversionIfNotSelectedAnyText() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_MINUS,
            '-'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_MINUS,
            '-'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { fail() }
        )

        assertFalse(consumed)
    }

    @Test
    fun listConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_MINUS,
            '-'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { assertEquals("- Angel has fallen.\n- He has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopOrderedListConversionIfNotSelectedAnyText() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_1,
            '1'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun noopOrderedListConversionWhenReturnsNullConversionResult() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_1,
            '1'
        )

        mockkConstructor(NumberedListHeadAdder::class)
        every { anyConstructed<NumberedListHeadAdder>().invoke(any()) } returns null
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { fail() }
        )

        assertFalse(consumed)
    }

    @Test
    fun orderedListConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_1,
            '1'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { assertEquals("1. Angel has fallen.\n2. He has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopTaskListConversionIfNotSelectedAnyText() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_0,
            '0'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun taskListConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_0,
            '0'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_4,
            '4'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(5)),
            multiParagraph,
            { assertEquals(0, it.selection.start) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopMoveToLineStart() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_4,
            '4'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_E,
            '4'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(5)),
            multiParagraph,
            { assertEquals(15, it.selection.start) }
        )

        assertTrue(consumed)
    }

    @Test
    fun commaInsertion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_COMMA,
            ','
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("2000000", TextRange(0, "2000000".length)),
            mockk(),
            { assertEquals("2,000,000", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopTableConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_T,
            'T'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test test"),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun tableConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_T,
            'T'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test test", TextRange(0, 6)),
            mockk(),
            { assertEquals("| test | test", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun caseConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_U,
            'U'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("TEST", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopCaseConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_U,
            'U'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test"),
            mockk(),
            { assertNull(it) }
        )

        assertTrue(consumed)
    }

    @Test
    fun caseConversionToLower() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_U,
            'U'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("TEST", TextRange(0, 4)),
            mockk(),
            { assertEquals("test", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun bolding() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_B,
            'B'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("**test**", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun italic() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_I,
            'I'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("***test***", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun doubleQuote() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_2,
            '2'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("\"test\"", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopDoubleQuote() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_2,
            '2'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test"),
            mockk(),
            { fail() }
        )

        assertTrue(consumed)
    }

    @Test
    fun braces() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_8,
            '8'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("(test)", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun findBrace() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_OPEN_BRACKET,
            '{'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("{test}", TextRange(0)),
            mockk(),
            {
                assertEquals("}", it.getSelectedText().text)
                assertEquals(5, it.selection.start)
                assertEquals(6, it.selection.end)
            }
        )

        assertTrue(consumed)

        val consumed2 = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("{test}", TextRange(5, 6)),
            mockk(),
            {
                assertEquals("{", it.getSelectedText().text)
                assertEquals(0, it.selection.start)
                assertEquals(1, it.selection.end)
            }
        )

        assertTrue(consumed2)

        val consumed3 = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("「test」", TextRange(0)),
            mockk(),
            {
                assertEquals("」", it.getSelectedText().text)
                assertEquals(5, it.selection.start)
                assertEquals(6, it.selection.end)
            }
        )

        assertTrue(consumed3)

        val consumed4 = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("「test」", TextRange(5, 6)),
            mockk(),
            {
                assertEquals("「", it.getSelectedText().text)
                assertEquals(0, it.selection.start)
                assertEquals(1, it.selection.end)
            }
        )

        assertTrue(consumed4)

        assertTrue(
            subject.invoke(
                KeyEvent(awtKeyEvent),
                TextFieldValue("『test』", TextRange(0)),
                mockk(),
                {
                    assertEquals("』", it.getSelectedText().text)
                    assertEquals(5, it.selection.start)
                    assertEquals(6, it.selection.end)
                }
            )
        )

        assertTrue(
            subject.invoke(
                KeyEvent(awtKeyEvent),
                TextFieldValue("『test』", TextRange(5, 6)),
                mockk(),
                {
                    assertEquals("『", it.getSelectedText().text)
                    assertEquals(0, it.selection.start)
                    assertEquals(1, it.selection.end)
                }
            )
        )

        assertFalse(
            subject.invoke(
                KeyEvent(awtKeyEvent),
                TextFieldValue("『test』", TextRange(1)),
                mockk(),
                {
                    assertTrue(it.getSelectedText().text.isEmpty())
                    assertEquals(1, it.selection.start)
                }
            )
        )
    }

    @Test
    fun surroundBraces() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_CLOSE_BRACKET,
            '}'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("「test」", it.getSelectedText().text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun surroundBackQuote() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_AT,
            '@'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("```test```", it.getSelectedText().text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun deleteLine() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_CIRCUMFLEX,
            '~'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            { assertEquals("~~test~~", it.getSelectedText().text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun calculate() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_C,
            'C'
        )
        mockkConstructor(ExpressionTextCalculatorService::class)
        every { anyConstructed<ExpressionTextCalculatorService>().invoke(any()) } returns "3"

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_N,
            'N'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_O,
            'O'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_O,
            'O'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.ALT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_O,
            'O'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test", TextRange(0, 4)),
            mockk(),
            {  }
        )

        assertTrue(consumed)
        verify { mainViewModel.browseUri(any()) }
    }

    @Test
    fun noopBrowseUri() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.ALT_DOWN_MASK,
            java.awt.event.KeyEvent.VK_O,
            'O'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("test"),
            mockk(),
            {  }
        )

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun noopCombineLines() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_J,
            'J'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("nc"),
            mockk(),
            { fail() }
        )

        assertFalse(consumed)
    }

    @Test
    fun combineLines() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_J,
            'J'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("a\nb\nc"),
            mockk(),
            { assertEquals("ab\nc", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun quoteSelectedText() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_Q,
            'Q'
        )

        val text = "test\ntest2"
        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_Q,
            'Q'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_Q,
            'Q'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_L,
            'L'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
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

        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_L,
            'L'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue(selected, TextRange(0, selected.length)),
            mockk(),
            { assertEquals(decoratedLink, it.text) }
        )

        assertTrue(consumed)
    }

}