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
import androidx.compose.ui.text.TextRange
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.CommaInserter
import jp.toastkid.yobidashi4.presentation.editor.usecase.TextEditorOperationUseCase
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(InternalComposeUiApi::class)
class KeyEventConsumerTest {

    private lateinit var subject: KeyEventConsumer

    @MockK
    private lateinit var useCase: TextEditorOperationUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { useCase.toTable() } returns true
        every { useCase.joinLines() } returns true
        every { useCase.quote() } returns true
        every { useCase.decorateLink() } returns true
        every { useCase.duplicateLine() } returns true
        every { useCase.toListLines() } returns true
        every { useCase.openFile() } returns true
        every { useCase.surroundMultibyteBrackets() } just Runs
        every { useCase.surroundCodeFence() } returns true
        every { useCase.surroundBrackets() } returns true
        every { useCase.decorateLink() } returns true
        every { useCase.reformat() } returns true
        every { useCase.insertComma() } returns true
        every { useCase.calculate() } returns true
        every { useCase.openFile() } returns true
        every { useCase.openUrl() } returns true
        every { useCase.search() } returns true
        every { useCase.moveToLineStart() } returns true
        every { useCase.moveToLineEnd() } returns true
        every { useCase.italic() } returns true
        every { useCase.bold() } returns true
        every { useCase.doubleQuote() } returns true
        every { useCase.switchEditable() } just Runs
        every { useCase.switchCase() } returns true
        every { useCase.toOrderedList() } returns true
        every { useCase.toTaskList() } returns true
        every { useCase.prettyPrint() } returns true
        every { useCase.controlAndLeftBracket() } returns true
        every { useCase.toHalfWidth() } just Runs

        subject = KeyEventConsumer(useCase)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
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
    fun duplicateCurrentLine() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.")

        val consumed = subject.invoke(
            KeyEvent(Key.D, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { useCase.duplicateLine() }
    }

    @Test
    fun listConversion() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(0, 30))

        val consumed = subject.invoke(
            KeyEvent(Key.Minus, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { useCase.toListLines() }
    }

    @Test
    fun orderedListConversion() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(0, 30))

        val consumed = subject.invoke(
            KeyEvent(Key.One, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { useCase.toOrderedList() }
    }

    @Test
    fun taskListConversion() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.\n", TextRange(0, 30))

        val consumed = subject.invoke(
            KeyEvent(Key.Zero, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { useCase.toTaskList() }
    }

    @Test
    fun moveToLineStart() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(5))
        
        val consumed = subject.invoke(
            KeyEvent(Key.Four, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        verify { useCase.moveToLineStart() }
    }

    @Test
    fun moveToLineEnd() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.", TextRange(5))

        subject.invoke(
            KeyEvent(Key.E, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        verify { useCase.moveToLineEnd() }
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
        verify { useCase.insertComma() }
    }

    @Test
    fun noopCommaInsertion() {
        mockkConstructor(CommaInserter::class)
        every { anyConstructed<CommaInserter>().invoke(any()) } returns null
        val content = TextFieldState("2000000", TextRange(0, "2000000".length))

        subject.invoke(
            KeyEvent(Key.Comma, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        verify(inverse = true) { anyConstructed<CommaInserter>().invoke(any()) }
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
    }

    @Test
    fun controlAndLeftBracket() {
        val consumed = subject.invoke(
            KeyEvent(Key.LeftBracket, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("test", TextRange(0, 4)),
            mockk(),
        )

        assertTrue(consumed)
        verify { useCase.controlAndLeftBracket() }
    }

    @Test
    fun noopControlAndLeftBracketWithoutCtrl() {
        val consumed = subject.invoke(
            KeyEvent(Key.LeftBracket, KeyEventType.KeyDown, isCtrlPressed = false),
            TextFieldState("test", TextRange(0, 4)),
            mockk()
        )

        assertFalse(consumed)
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
    }

    @Test
    fun switchEditable() {
        val consumed = subject.invoke(
            KeyEvent(Key.N, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("1+2", TextRange(0, 3)),
            mockk()
        )

        assertTrue(consumed)
        verify { useCase.switchEditable() }
    }

    @Test
    fun noopSwitchEditable() {
        val consumed = subject.invoke(
            KeyEvent(Key.N, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("1+2", TextRange(0, 3)),
            mockk(),
        )

        assertTrue(consumed)
    }

    @Test
    fun openUrl() {
        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState("test", TextRange(0, 4)),
            mockk()
        )

        assertTrue(consumed)
    }

    @Test
    fun browseUri() {
        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isAltPressed = true),
            TextFieldState("test", TextRange(0, 4)),
            mockk(),
        )

        assertTrue(consumed)
        verify { useCase.search() }
    }

    @Test
    fun openFile() {
        val editorTab = mockk<EditorTab>()
        every { editorTab.path } returns mockk()

        val consumed = subject.invoke(
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState(),
            mockk(),
        )

        assertTrue(consumed)
    }

    @Test
    fun noopCombineLines() {
        every { useCase.joinLines() } returns false

        val consumed = subject.invoke(
            KeyEvent(Key.J, KeyEventType.KeyDown, isCtrlPressed = true),
            TextFieldState("nc"),
            mockk(),
        )

        assertFalse(consumed)
    }

    @Test
    fun combineLines() {
        val content = TextFieldState("a\nb\nc", TextRange.Zero)

        val consumed = subject.invoke(
            KeyEvent(Key.J, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
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
    }

    @Test
    fun quoteInsertion() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        val content = TextFieldState("", TextRange(0))

        val consumed = subject.invoke(
            KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
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
    fun pasteDecoratedLink() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "https://test.yahoo.com"
        val content = TextFieldState("", TextRange(0))

        val consumed = subject.invoke(
            KeyEvent(Key.L, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
    }

    @Test
    fun toDecoratedLink() {
        val selected = "https://test.yahoo.com"
        val content = TextFieldState(selected, TextRange(0, selected.length))

        val consumed = subject.invoke(
            KeyEvent(Key.L, KeyEventType.KeyDown, isCtrlPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
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
        verify { useCase.toHalfWidth() }
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
        val consumed = subject.invoke(
            KeyEvent(Key.F, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            TextFieldState(),
            mockk(),
        )

        assertTrue(consumed)
        verify { useCase.reformat() }
    }

    @Test
    fun textReformatIfClipboardIsEmpty() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        val content = TextFieldState("test", TextRange(0, 1))

        val consumed = subject.invoke(
            KeyEvent(Key.F, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
        every { useCase.reformat() }
    }

    @Test
    fun jsonPrettyPrint() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        val content = TextFieldState("", TextRange(0))

        val consumed = subject.invoke(
            KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk(),
        )

        assertTrue(consumed)
    }

    @Test
    fun jsonPrettyPrintIfClipboardIsEmpty() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        val content = TextFieldState("test", TextRange(0, 1))

        val consumed = subject.invoke(
            KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true),
            content,
            mockk()
        )

        assertTrue(consumed)
    }

}