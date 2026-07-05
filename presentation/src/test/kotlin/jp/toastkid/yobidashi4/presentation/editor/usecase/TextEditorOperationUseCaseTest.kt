/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.usecase

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.selectAll
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
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
import jp.toastkid.yobidashi4.presentation.editor.keyboard.ControlAndLeftBracketCase
import jp.toastkid.yobidashi4.presentation.editor.keyboard.LinkPasteCase
import jp.toastkid.yobidashi4.presentation.editor.keyboard.SelectedTextConversion
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.BlockQuotation
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.CommaInserter
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ExpressionTextCalculatorService
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.NumberedListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ToHalfWidth
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
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

class TextEditorOperationUseCaseTest {

    private lateinit var subject: TextEditorOperationUseCase

    @MockK
    private lateinit var mainViewModel: MainViewModel

    private val content = TextFieldState()

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @MockK
    private lateinit var multiParagraphState: () -> MultiParagraph?

    @MockK
    private lateinit var scrollBy: (Float) -> Unit

    @MockK
    private lateinit var switchLineNumber: () -> Unit

    @MockK
    private lateinit var controlAndLeftBracketCase: ControlAndLeftBracketCase

    @MockK
    private lateinit var selectedTextConversion: SelectedTextConversion

    @MockK
    private lateinit var searchUrlFactory: SearchUrlFactory

    @MockK
    private lateinit var toHalfWidth: ToHalfWidth

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

        every { scrollBy.invoke(any()) } just Runs
        every { multiParagraphState.invoke() } returns multiParagraph
        every { switchLineNumber.invoke() } just Runs
        every { searchUrlFactory.invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"
        every { expressionTextCalculatorService.invoke(any()) } returns "3"
        every { selectedTextConversion.invoke(any(), any(), any(), capture(conversionCapturingSlot)) } returns true
        every { controlAndLeftBracketCase.invoke(any(), any()) } returns true
        every { toHalfWidth.invoke(any()) } returns ""
        content.clearText()

        subject = TextEditorOperationUseCase(
            mainViewModel, content, multiParagraphState, scrollBy, switchLineNumber,
            controlAndLeftBracketCase,
            selectedTextConversion,
            searchUrlFactory,
            toHalfWidth,
            expressionTextCalculatorService,
            blockQuotation,
            textReformat,
            jsonPrettyPrint
        )
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun scrollBy() {
        subject.scrollBy(1f)

        every { scrollBy.invoke(1f) }
    }

    @Test
    fun cutLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
        every { multiParagraphState.invoke() } returns multiParagraph

        content.edit {
            append("test\ntest2\ntest3")
        }

        val consumed = subject.cutLine()

        assertTrue(consumed)
        verify { scrollBy wasNot called }
        verify { multiParagraph.getLineForOffset(any()) }
        verify { multiParagraph.getLineStart(0) }
        verify { multiParagraph.getLineEnd(0) }
        verify { anyConstructed<ClipboardPutterService>().invoke("test\n") }
    }

    @Test
    fun cutLineOnSelectedTextCase() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        every { multiParagraphState.invoke() } returns multiParagraph

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
        content.edit {
            append("test\ntest2\ntest3")
            selection = TextRange(1, 3)
        }

        val consumed = subject.cutLine()

        assertFalse(consumed)
        verify { scrollBy wasNot called }
        verify { multiParagraph wasNot called }
        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun deleteLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        every { multiParagraphState.invoke() } returns multiParagraph
        content.edit {
            append("test\ntest2\ntest3")
        }

        subject.deleteLine()

        verify { scrollBy wasNot called }
        verify { multiParagraph.getLineForOffset(any()) }
        verify { multiParagraph.getLineStart(0) }
        verify { multiParagraph.getLineEnd(0) }
        assertEquals("test2\ntest3", content.text)
    }

    @Test
    fun deleteLineOnSelectedText() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        every { multiParagraphState.invoke() } returns multiParagraph
        content.edit {
            append("test\ntest2\ntest3")
            selection = TextRange(0, 3)
        }

        subject.deleteLine()

        verify { scrollBy wasNot called }
        verify { multiParagraph wasNot called }
    }

    @Test
    fun deleteLineOnParagraphIsNull() {
        every { multiParagraphState.invoke() } returns null

        subject.deleteLine()
    }

    @Test
    fun hideFileList() {
        every { mainViewModel.switchArticleList() } just Runs
        every { mainViewModel.hideArticleList() } just Runs

        subject.hideArticleList()

        verify(inverse = true) { mainViewModel.switchArticleList() }
        verify { mainViewModel.hideArticleList() }
    }

    @Test
    fun openFileList() {
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.switchArticleList() } just Runs

        subject.switchArticleList()

        verify { mainViewModel.openArticleList() }
        verify { mainViewModel.switchArticleList() }
        verify { scrollBy wasNot called }
    }

    @Test
    fun switchLineNumber() {
        subject.switchLineNumber()

        verify { switchLineNumber.invoke() }
    }

    // TODO
    @Test
    fun duplicateSelectedText() {
        content.clearText()
        content.edit {
            append("Angel has fallen.")
            selection = TextRange(6, 10)
        }

        val consumed = subject.duplicateLine()

        assertTrue(consumed)
        assertEquals("Angel has has fallen.", content.text)
    }

    @Test
    fun duplicateCurrentLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
        }

        val consumed = subject.duplicateLine()

        assertTrue(consumed)
        assertEquals("Angel has fallen.\nAngel has fallen.\nHe has gone.", content.text)
    }

    @Test
    fun noopDuplicateCurrentLine() {
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
        }
        every { multiParagraphState.invoke() } returns null

        val consumed = subject.duplicateLine()

        assertFalse(consumed)
    }

    @Test
    fun noopListConversionIfNotSelectedAnyText() {
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
        }

        val consumed = subject.toListLines()

        assertFalse(consumed)
    }

    @Test
    fun noopListConversion() {
        mockkConstructor(ListHeadAdder::class)
        every { anyConstructed<ListHeadAdder>().invoke(any(), any()) } returns null

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(0, 30)
        }

        val consumed = subject.toListLines()

        assertFalse(consumed)
    }

    @Test
    fun listConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(0, 30)
        }

        val consumed = subject.toListLines()

        assertTrue(consumed)
        assertEquals("- Angel has fallen.\n- He has gone.", content.text)
    }

    @Test
    fun noopOrderedListConversionIfNotSelectedAnyText() {
        val content = TextFieldState("Angel has fallen.\nHe has gone.")
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
        }

        val consumed = subject.toOrderedList()

        assertFalse(consumed)
    }

    @Test
    fun noopOrderedListConversionWhenReturnsNullConversionResult() {
        mockkConstructor(NumberedListHeadAdder::class)
        every { anyConstructed<NumberedListHeadAdder>().invoke(any()) } returns null
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(0, 30)
        }

        val consumed = subject.toOrderedList()

        assertFalse(consumed)
    }

    @Test
    fun orderedListConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(0, 30)
        }

        val consumed = subject.toOrderedList()

        assertTrue(consumed)
        assertEquals("1. Angel has fallen.\n2. He has gone.", content.text)
    }

    @Test
    fun noopTaskListConversionIfNotSelectedAnyText() {
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
        }

        val consumed = subject.toTaskList()

        assertFalse(consumed)
    }

    @Test
    fun noopTaskListConversion() {
        mockkConstructor(ListHeadAdder::class)
        every { anyConstructed<ListHeadAdder>().invoke(any(), any()) } returns null

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(0, 30)
        }

        val consumed = subject.toTaskList()

        assertFalse(consumed)
    }

    @Test
    fun taskListConversion() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(0, 30)
        }

        val consumed = subject.toListLines()

        assertTrue(consumed)
    }

    @Test
    fun moveToLineStart() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(5)
        }

        val consumed = subject.moveToLineStart()

        assertTrue(consumed)
        assertEquals(0, content.selection.start)
    }

    @Test
    fun noopMoveToLineStart() {
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(5)
        }
        every { multiParagraphState.invoke() } returns null

        val consumed = subject.moveToLineStart()

        assertFalse(consumed)
    }

    @Test
    fun moveToLineEnd() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 15
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(5)
        }

        val consumed = subject.moveToLineEnd()

        assertTrue(consumed)
        assertEquals(15, content.selection.start)
    }

    @Test
    fun noopMoveToLineEnd() {
        content.clearText()
        content.edit {
            append("Angel has fallen.\nHe has gone.")
            selection = TextRange(5)
        }
        every { multiParagraphState.invoke() } returns null

        val consumed = subject.moveToLineEnd()

        assertFalse(consumed)
    }

    @Test
    fun commaInsertion() {
        content.clearText()
        content.edit {
            append("2000000")
            selectAll()
        }

        val consumed = subject.insertComma()

        assertTrue(consumed)
        assertEquals("2,000,000", content.text)
    }

    @Test
    fun noopCommaInsertion() {
        mockkConstructor(CommaInserter::class)
        every { anyConstructed<CommaInserter>().invoke(any()) } returns null
        content.clearText()
        content.edit {
            append("2000000")
            selectAll()
        }

        val consumed = subject.insertComma()

        assertFalse(consumed)
        verify { anyConstructed<CommaInserter>().invoke(any()) }
    }

    @Test
    fun noopTableConversion() {
        content.clearText()
        content.edit {
            append("test test")
        }

        val consumed = subject.toTable()

        assertFalse(consumed)
    }

    @Test
    fun tableConversion() {
        content.clearText()
        content.edit {
            append("test test")
            selection = TextRange(0, 6)
        }

        val consumed = subject.toTable()

        assertTrue(consumed)
        assertEquals("| test | test", content.text)
    }

    @Test
    fun caseConversion() {
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.switchCase()

        assertTrue(consumed)
        assertEquals("TEST", conversionCapturingSlot.captured.invoke("test"))
        assertEquals("test", conversionCapturingSlot.captured.invoke("TEST"))
    }

    @Test
    fun noopCaseConversion() {
        content.clearText()
        content.edit {
            append("test")
        }

        val consumed = subject.switchCase()

        assertTrue(consumed)
    }

    @Test
    fun caseConversionToLower() {
        content.clearText()
        content.edit {
            append("TEST")
            selection = TextRange(0, 4)
        }

        val consumed = subject.switchCase()

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun bolding() {
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.bold()

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun italic() {
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.italic()

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun doubleQuote() {
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.doubleQuote()

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun noopDoubleQuote() {
        content.clearText()
        content.edit {
            append("test")
        }

        val consumed = subject.doubleQuote()

        assertTrue(consumed)
    }

    @Test
    fun braces() {
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.surroundBrackets()

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun controlAndLeftBracket() {
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.controlAndLeftBracket()

        assertTrue(consumed)
        verify { controlAndLeftBracketCase.invoke(any(), any()) }
    }

    @Test
    fun surroundBraces() {
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.surroundBrackets()

        assertTrue(consumed)
        assertEquals(0, content.selection.start)
        assertEquals(4, content.selection.end)
    }

    @Test
    fun surroundBackQuote() {
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.surroundCodeFence()

        assertTrue(consumed)
        assertEquals(0, content.selection.start)
        assertEquals(4, content.selection.end)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun calculate() {
        content.clearText()
        content.edit {
            append("1+2")
            selectAll()
        }

        val consumed = subject.calculate()

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
        content.clearText()
        content.edit {
            append("1+2")
            selectAll()
        }

        subject.switchEditable()

        verify { editorTab.switchEditable() }
        verify { mainViewModel.currentTab() }
    }

    @Test
    fun openUrl() {
        every { mainViewModel.openUrl(any(), any()) } just Runs
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.openUrl()

        assertTrue(consumed)
        verify { mainViewModel.openUrl(any(), any()) }
    }

    @Test
    fun noopOpenUrl() {
        every { mainViewModel.openUrl(any(), any()) } just Runs
        content.clearText()
        content.edit {
            append("test")
        }

        val consumed = subject.search()

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun browseUri() {
        every { mainViewModel.browseUri(any()) } just Runs
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 4)
        }

        val consumed = subject.search()

        assertTrue(consumed)
        verify { mainViewModel.browseUri(any()) }
    }

    @Test
    fun noopBrowseUri() {
        content.clearText()
        content.edit {
            append("test")
        }
        val consumed = subject.search()

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun openFile() {
        val editorTab = mockk<EditorTab>()
        every { editorTab.path } returns mockk()
        every { mainViewModel.currentTab() } returns editorTab
        every { mainViewModel.openFile(any()) } just Runs

        val consumed = subject.openFile()

        assertTrue(consumed)
        verify { mainViewModel.currentTab() }
        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun noopOpenFileIfCurrentTabIsNotEditorTab() {
        every { mainViewModel.currentTab() } returns mockk<WebTab>()
        every { mainViewModel.openFile(any()) } just Runs

        val consumed = subject.openFile()

        assertFalse(consumed)
        verify { mainViewModel.currentTab() }
        verify(inverse = true) { mainViewModel.openFile(any()) }
    }

    @Test
    fun noopCombineLines() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        content.clearText()
        content.edit {
            append("nc")
        }

        val consumed = subject.joinLines()

        assertFalse(consumed)
    }

    @Test
    fun combineLines() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()
        content.edit {
            append("a\nb\nc")
            selection = TextRange.Zero
        }

        val consumed = subject.joinLines()

        assertTrue(consumed)
        assertEquals("ab\nc", content.text)
    }

    @Test
    fun quoteSelectedText() {
        val text = "test\ntest2"
        content.clearText()
        content.edit {
            append(text)
            selectAll()
        }

        val consumed = subject.quote()

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
        content.clearText()

        val consumed = subject.quote()

        assertTrue(consumed)
        verify(inverse = true) { selectedTextConversion.invoke(any(), any(), any(), any()) }
        verify { blockQuotation.invoke(any()) }
    }

    @Test
    fun noopQuoteInsertion() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns null
        content.clearText()

        val consumed = subject.quote()

        assertTrue(consumed)
    }

    @Test
    fun noopQuoteInsertionWhenReturnsNullFromConversionResult() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        every { blockQuotation.invoke(any()) } returns null
        content.clearText()

        val consumed = subject.quote()

        assertFalse(consumed)
    }

    @Test
    fun pasteDecoratedLink() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "https://test.yahoo.com"
        val decoratedLink = "[test](https://test.yahoo.com)"
        every { linkDecoratorService.invoke(any()) } returns decoratedLink
        content.clearText()

        val consumed = subject.decorateLink()

        assertTrue(consumed)
        assertEquals(decoratedLink, content.text)
    }

    @Test
    fun toDecoratedLink() {
        val selected = "https://test.yahoo.com"
        mockkConstructor(ClipboardFetcher::class, LinkPasteCase::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns selected
        every { anyConstructed<LinkPasteCase>().invoke(any(), any(), any(), any()) } returns true
        content.clearText()
        content.edit {
            append(selected)
            selectAll()
        }

        val consumed = subject.decorateLink()

        assertTrue(consumed)
        verify { anyConstructed<LinkPasteCase>().invoke(any(), any(), any(), any()) }
    }

    @Test
    fun toHalfWidth() {
        content.clearText()
        content.edit {
            append("１０月２１日ＡＢＣホールにて")
            selectAll()
        }

        subject.toHalfWidth()

        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun textReformat() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        every { textReformat.invoke(any()) } returns "reformat"
        content.clearText()

        val consumed = subject.reformat()

        assertTrue(consumed)
        assertEquals("reformat", content.text)
    }

    @Test
    fun textReformatIfClipboardIsEmpty() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        every { textReformat.invoke(any()) } returns "reformat"
        content.clearText()
        content.edit {
            append("test")
            selection = TextRange(0, 1)
        }

        val consumed = subject.reformat()

        assertTrue(consumed)
        assertEquals("reformatest", content.text)
    }

    @Test
    fun noopTextReformat() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        every { textReformat.invoke(any()) } returns "reformat"
        content.clearText()
        content.edit {
            append("test")
        }

        val consumed = subject.reformat()

        assertFalse(consumed)
    }

    @Test
    fun jsonPrettyPrint() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "test"
        every { jsonPrettyPrint.invoke(any()) } returns "{}"
        content.clearText()

        val consumed = subject.prettyPrint()

        assertTrue(consumed)
        assertEquals("{}", content.text)
    }

    @Test
    fun jsonPrettyPrintIfClipboardIsEmpty() {
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns ""
        every { jsonPrettyPrint.invoke(any()) } returns "{}"
        val content = TextFieldState("test", TextRange(0, 1))

        val consumed = subject.prettyPrint()

        assertFalse(consumed)
    }

}