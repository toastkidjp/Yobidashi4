/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.domain.service.editor.LinkDecoratorService
import jp.toastkid.yobidashi4.domain.service.editor.text.JsonPrettyPrint
import jp.toastkid.yobidashi4.domain.service.editor.text.TextReformat
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.BlockQuotation
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.CommaInserter
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ExpressionTextCalculatorService
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.NumberedListHeadAdder
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(InternalComposeUiApi::class)
class KeyEventConsumerTest {

    private lateinit var subject: KeyEventConsumer

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var controlAndLeftBracketCase: ControlAndLeftBracketCase

    @MockK
    private lateinit var selectedTextConversion: SelectedTextConversion

    @MockK
    private lateinit var searchUrlFactory: SearchUrlFactory

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @MockK
    private lateinit var linkDecoratorService: LinkDecoratorService

    @MockK
    private lateinit var expressionTextCalculatorService: ExpressionTextCalculatorService

    @MockK
    private lateinit var blockQuotation: BlockQuotation

    @MockK
    private lateinit var textReformat: TextReformat

    @MockK
    private lateinit var jsonPrettyPrint: JsonPrettyPrint

    private val conversionCapturingSlot = slot<(String) -> String?>()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { linkDecoratorService } bind(LinkDecoratorService::class)
                }
            )
        }

        subject = KeyEventConsumer(mainViewModel, controlAndLeftBracketCase, selectedTextConversion, searchUrlFactory, blockQuotation = blockQuotation, textReformat = textReformat, jsonPrettyPrint = jsonPrettyPrint)
        every { searchUrlFactory.invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"
        every { expressionTextCalculatorService.invoke(any()) } returns "3"
        every { selectedTextConversion.invoke(any(), any(), any(), capture(conversionCapturingSlot)) } returns true
        every { controlAndLeftBracketCase.invoke(any(), any()) } returns true
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
        conversionCapturingSlot.clear()
    }

    @Test
    fun onKeyUp() {
        val consumed = subject.invoke(
            KeyEvent(Key.A, KeyEventType.KeyUp, isCtrlPressed = true),
            TextFieldState(),
            mockk()
        )

        assertFalse(consumed)
    }

    @Test
    fun duplicateSelectedText() {
        val content = TextFieldState("Angel has fallen.", TextRange(6, 10))

        val consumed = subject.invoke(
            KeyEvent(Key.D, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        assertEquals("Angel has has fallen.", content.text)
    }

    @Test
    fun duplicateCurrentLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        val content = TextFieldState("Angel has fallen.\nHe has gone.")

        val consumed = subject.invoke(
            KeyEvent(Key.D, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            multiParagraph,
        )

        assertTrue(consumed)
        assertEquals("Angel has fallen.\nAngel has fallen.\nHe has gone.", content.text)
    }

    @Test
    fun noopDuplicateCurrentLine() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.")

        val consumed = subject.invoke(
            KeyEvent(Key.D, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            null
        )

        assertFalse(consumed)
    }

    @Test
    fun noopListConversionIfNotSelectedAnyText() {
        val consumed = subject.invoke(
            KeyEvent(Key.Minus, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("Angel has fallen.\nHe has gone."),
            mockk(),
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
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(0, 30))

        val consumed = subject.invoke(
            KeyEvent(Key.Minus, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            multiParagraph,
        )

        assertFalse(consumed)
    }

    @Test
    fun listConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(0, 30))

        val consumed = subject.invoke(
            KeyEvent(Key.Minus, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            multiParagraph,
        )

        assertTrue(consumed)
        assertEquals("- Angel has fallen.\n- He has gone.", content.text)
    }

    @Test
    fun noopOrderedListConversionIfNotSelectedAnyText() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.")

        val consumed = subject.invoke(
            KeyEvent(Key.One, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
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
            TextFieldState("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
        )

        assertFalse(consumed)
    }

    @Test
    fun orderedListConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(0, 30))

        val consumed = subject.invoke(
            KeyEvent(Key.One, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            multiParagraph,
        )

        assertTrue(consumed)
        assertEquals("1. Angel has fallen.\n2. He has gone.", content.text)
    }

    @Test
    fun noopTaskListConversionIfNotSelectedAnyText() {
        val consumed = subject.invoke(
            KeyEvent(Key.Zero, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("Angel has fallen.\nHe has gone."),
            mockk(),
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
            TextFieldState("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
        )

        assertFalse(consumed)
    }

    @Test
    fun taskListConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        val content = TextFieldState("Angel has fallen.\nHe has gone.\n", TextRange(0, 30))

        val consumed = subject.invoke(
            KeyEvent(Key.Zero, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            multiParagraph,
        )

        assertTrue(consumed)
        assertEquals("- [ ] Angel has fallen.\n- [ ] He has gone.\n", content.text)
    }

    @Test
    fun moveToLineStart() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(5))
        
        val consumed = subject.invoke(
            KeyEvent(Key.Four, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            multiParagraph,
        )

        assertTrue(consumed)
        assertEquals(0, content.selection.start)
    }

    @Test
    fun noopMoveToLineStart() {
        val consumed = subject.invoke(
            KeyEvent(Key.Four, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("Angel has fallen.\nHe has gone.", TextRange(5)),
            null,
        )

        assertFalse(consumed)
    }

    @Test
    fun moveToLineEnd() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 15
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(5))
        
        val consumed = subject.invoke(
            KeyEvent(Key.E, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            multiParagraph,
        )

        assertTrue(consumed)
        assertEquals(15, content.selection.start)
    }

    @Test
    fun noopMoveToLineEnd() {
        val consumed = subject.invoke(
            KeyEvent(Key.E, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("Angel has fallen.\nHe has gone.", TextRange(5)),
            null
        )

        assertFalse(consumed)
    }

    @Test
    fun commaInsertion() {
        val content = TextFieldState("2000000", TextRange(0, "2000000".length))
        
        val consumed = subject.invoke(
            KeyEvent(Key.Comma, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        assertEquals("2,000,000", content.text)
    }

    @Test
    fun noopCommaInsertion() {
        mockkConstructor(CommaInserter::class)
        every { anyConstructed<CommaInserter>().invoke(any()) } returns null
        val content = TextFieldState("2000000", TextRange(0, "2000000".length))
        
        val consumed = subject.invoke(
            KeyEvent(Key.Comma, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertFalse(consumed)
        verify { anyConstructed<CommaInserter>().invoke(any()) }
    }

    @Test
    fun noopTableConversion() {
        val consumed = subject.invoke(
            KeyEvent(Key.T, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("test test"),
            mockk()
        )

        assertFalse(consumed)
    }

    @Test
    fun tableConversion() {
        val content = TextFieldState("test test", TextRange(0, 6))

        val consumed = subject.invoke(
            KeyEvent(Key.T, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        assertEquals("| test | test", content.text)
    }

    @Test
    fun caseConversion() {
        val content = TextFieldState("test", TextRange(0, 4))

        val consumed = subject.invoke(
            KeyEvent(Key.U, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        assertEquals("TEST", conversionCapturingSlot.captured.invoke("test"))
        assertEquals("test", conversionCapturingSlot.captured.invoke("TEST"))
    }

    @Test
    fun noopCaseConversion() {
        val consumed = subject.invoke(
            KeyEvent(Key.U, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("test"),
            mockk()
        )

        assertTrue(consumed)
    }

    @Test
    fun caseConversionToLower() {
        val content = TextFieldState("TEST", TextRange(0, 4))

        val consumed = subject.invoke(
            KeyEvent(Key.U, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun bolding() {
        val content = TextFieldState("test", TextRange(0, 4))

        val consumed = subject.invoke(
            KeyEvent(Key.B, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun italic() {
        val content = TextFieldState("test", TextRange(0, 4))

        val consumed = subject.invoke(
            KeyEvent(Key.I, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun doubleQuote() {
        val content = TextFieldState("test", TextRange(0, 4))

        val consumed = subject.invoke(
            KeyEvent(Key.Two, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun noopDoubleQuote() {
        val consumed = subject.invoke(
            KeyEvent(Key.Two, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("test"),
            mockk(),
        )

        assertTrue(consumed)
    }

    @Test
    fun braces() {
        val content = TextFieldState("test", TextRange(0, 4))

        val consumed = subject.invoke(
            KeyEvent(Key.Eight, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        // TODO assertEquals("(test)", content.text)
    }

    @Test
    fun controlAndLeftBracket() {
        val consumed = subject.invoke(
            KeyEvent(Key.LeftBracket, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("test", TextRange(0, 4)),
            mockk(),
        )

        assertTrue(consumed)
        verify { controlAndLeftBracketCase.invoke(any(), any()) }
    }

    @Test
    fun noopControlAndLeftBracketWithoutCtrl() {
        val consumed = subject.invoke(
            KeyEvent(Key.LeftBracket, KeyEventType.KeyDown, isCtrlPressed = false),
            TextFieldState("test", TextRange(0, 4)),
            mockk()
        )

        assertFalse(consumed)
        verify { controlAndLeftBracketCase wasNot Called }
    }

    @Test
    fun surroundBraces() {
        val content = TextFieldState("test", TextRange(0, 4))

        val consumed = subject.invoke(
            KeyEvent(Key.RightBracket, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        assertEquals(0, content.selection.start)
        assertEquals(4, content.selection.end)
        //TODO assertEquals("「test」", content.text)
    }

    @Test
    fun surroundBackQuote() {
        val content = TextFieldState("test", TextRange(0, 4))

        val consumed = subject.invoke(
            KeyEvent(Key.At, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        assertEquals(0, content.selection.start)
        assertEquals(4, content.selection.end)
        // TODO assertEquals("```test```", content.text)
    }

    @Test
    fun calculate() {
        val content = TextFieldState("1+2", TextRange(0, 3))

        val consumed = subject.invoke(
            KeyEvent(Key.C, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        assertEquals(0, content.selection.start)
        assertEquals(3, content.selection.end)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun switchEditable() {
        val editorTab = mockk<EditorTab>()
        every { editorTab.switchEditable() } just Runs
        every { mainViewModel.currentTab() } returns editorTab

        val consumed = subject.invoke(
            KeyEvent(Key.N, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("1+2", TextRange(0, 3)),
            mockk()
        )

        assertTrue(consumed)
        verify { editorTab.switchEditable() }
        verify { mainViewModel.currentTab() }
    }

    @Test
    fun noopSwitchEditable() {
        every { mainViewModel.currentTab() } returns mockk()

        val consumed = subject.invoke(
            KeyEvent(Key.N, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("1+2", TextRange(0, 3)),
            mockk(),
        )

        assertTrue(consumed)
        verify { mainViewModel.currentTab() }
    }

    @Test
    fun openUrl() {
        every { mainViewModel.openUrl(any(), any()) } just Runs

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("test", TextRange(0, 4)),
            mockk()
        )

        assertTrue(consumed)
        verify { mainViewModel.openUrl(any(), any()) }
    }

    @Test
    fun noopOpenUrl() {
        every { mainViewModel.openUrl(any(), any()) } just Runs

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("test"),
            mockk(),
        )

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun browseUri() {
        every { mainViewModel.browseUri(any()) } just Runs

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldState("test", TextRange(0, 4)),
            mockk(),
        )

        assertTrue(consumed)
        verify { mainViewModel.browseUri(any()) }
    }

    @Test
    fun noopBrowseUri() {
        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldState("test"),
            mockk(),
        )

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun openFile() {
        val editorTab = mockk<EditorTab>()
        every { editorTab.path } returns mockk()
        every { mainViewModel.currentTab() } returns editorTab
        every { mainViewModel.openFile(any()) } just Runs

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState(),
            mockk(),
        )

        assertTrue(consumed)
        verify { mainViewModel.currentTab() }
        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun noopOpenFileIfCurrentTabIsNotEditorTab() {
        every { mainViewModel.currentTab() } returns mockk<WebTab>()
        every { mainViewModel.openFile(any()) } just Runs

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState(),
            mockk(),
        )

        assertFalse(consumed)
        verify { mainViewModel.currentTab() }
        verify(inverse = true) { mainViewModel.openFile(any()) }
    }

    @Test
    fun noopCombineLines() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0

        val consumed = subject.invoke(
            KeyEvent(Key.J, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("nc"),
            mockk(),
        )

        assertFalse(consumed)
    }

    @Test
    fun combineLines() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        val content = TextFieldState("a\nb\nc", TextRange.Zero)

        val consumed = subject.invoke(
            KeyEvent(Key.J, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        assertEquals("ab\nc", content.text)
    }

    @Test
    fun quoteSelectedText() {
        val text = "test\ntest2"
        val content = TextFieldState(text, TextRange(0, text.length))

        val consumed = subject.invoke(
            KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        assertEquals(0, content.selection.start)
        assertEquals(10, content.selection.end)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun quoteInsertion() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        every { blockQuotation.invoke(any()) } returns "> test"
        val content = TextFieldState("", TextRange(0))

        val consumed = subject.invoke(
            KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify(inverse = true) { selectedTextConversion.invoke(any(), any(), any(), any()) }
        verify { blockQuotation.invoke(any()) }
    }

    @Test
    fun noopQuoteInsertion() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns null

        val consumed = subject.invoke(
            KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("", TextRange(0)),
            mockk()
        )

        assertTrue(consumed)
    }

    @Test
    fun noopQuoteInsertionWhenReturnsNullFromConversionResult() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        every { blockQuotation.invoke(any()) } returns null

        val consumed = subject.invoke(
            KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState(""),
            mockk(),
        )

        assertFalse(consumed)
    }

    @Test
    fun pasteDecoratedLink() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "https://test.yahoo.com"
        val decoratedLink = "[test](https://test.yahoo.com)"
        every { linkDecoratorService.invoke(any()) } returns decoratedLink
        val content = TextFieldState("", TextRange(0))

        val consumed = subject.invoke(
            KeyEvent(Key.L, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        assertEquals(decoratedLink, content.text)
    }

    @Test
    fun toDecoratedLink() {
        val selected = "https://test.yahoo.com"
        mockkConstructor(ClipboardFetcher::class, LinkPasteCase::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns selected
        every { anyConstructed<LinkPasteCase>().invoke(any(), any(), any(), any()) } returns true
        val content = TextFieldState(selected, TextRange(0, selected.length))

        val consumed = subject.invoke(
            KeyEvent(Key.L, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        verify { anyConstructed<LinkPasteCase>().invoke(any(), any(), any(), any()) }
    }

    @Test
    fun toHalfWidth() {
        val selected = "１０月２１日ＡＢＣホールにて"
        val content = TextFieldState(selected, TextRange(0, selected.length))

        val consumed = subject.invoke(
            KeyEvent(Key.H, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        // TODO assertEquals("10月21日ABCホールにて", content.text)
    }

    @Test
    fun toHalfWidthWithoutCtrl() {
        val selected = "１０月２１日ＡＢＣホールにて"
        val content = TextFieldState(selected, TextRange(0, selected.length))

        val consumed = subject.invoke(
            KeyEvent(Key.H, KeyEventType.KeyDown, isCtrlPressed = false, isShiftPressed = true),
            content,
            mockk(),
        )

        assertFalse(consumed)
        verify(inverse = true) { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun toHalfWidthWithoutShift() {
        val selected = "１０月２１日ＡＢＣホールにて"
        val content = TextFieldState(selected, TextRange(0, selected.length))

        val consumed = subject.invoke(
            KeyEvent(Key.H, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = false),
            content,
            mockk()
        )

        assertFalse(consumed)
    }

    @Test
    fun noopCases() {
        assertFalse(
            subject.invoke(
                KeyEvent(Key.Y, KeyEventType.KeyDown, isCtrlPressed = true),
                TextFieldState(),
                mockk(),
            )
        )
        assertFalse(
            subject.invoke(
                KeyEvent(Key.Y, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
                TextFieldState(),
                mockk(),
            )
        )
        assertFalse(
            subject.invoke(
                KeyEvent(Key.Y, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
                TextFieldState(),
                mockk(),
            )
        )
    }

    @Test
    fun textReformat() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        every { textReformat.invoke(any()) } returns "reformat"
        val content = TextFieldState("", TextRange(0))

        val consumed = subject.invoke(
            KeyEvent(Key.F, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        assertEquals("reformat", content.text)
    }

    @Test
    fun textReformatIfClipboardIsEmpty() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        every { textReformat.invoke(any()) } returns "reformat"
        val content = TextFieldState("test", TextRange(0, 1))

        val consumed = subject.invoke(
            KeyEvent(Key.F, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        assertEquals("reformatest", content.text)
    }

    @Test
    fun noopTextReformat() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        every { textReformat.invoke(any()) } returns "reformat"

        val consumed = subject.invoke(
            KeyEvent(Key.F, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("test"),
            mockk(),
        )

        assertFalse(consumed)
    }

    @Test
    fun jsonPrettyPrint() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        every { jsonPrettyPrint.invoke(any()) } returns "{}"
        val content = TextFieldState("", TextRange(0))

        val consumed = subject.invoke(
            KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        assertEquals("{}", content.text)
    }

    @Test
    fun jsonPrettyPrintIfClipboardIsEmpty() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        every { jsonPrettyPrint.invoke(any()) } returns "{}"
        val content = TextFieldState("test", TextRange(0, 1))

        val consumed = subject.invoke(
            KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
        assertEquals("{}est", content.text)
    }

    @Test
    fun noopJsonPrettyPrint() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        every { jsonPrettyPrint.invoke(any()) } returns "{}}"

        val consumed = subject.invoke(
            KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("test"),
            mockk(),
        )

        assertFalse(consumed)
    }

    @Test
    fun noopJsonPrettyPrintWithNull() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns null
        every { jsonPrettyPrint.invoke(any()) } returns "{}}"

        val consumed = subject.invoke(
            KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("test"),
            mockk(),
        )

        assertFalse(consumed)
    }

}