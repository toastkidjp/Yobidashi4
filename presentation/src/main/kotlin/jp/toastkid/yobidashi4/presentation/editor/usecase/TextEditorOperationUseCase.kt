/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.usecase

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
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
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.TableFormConverter
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ToHalfWidth
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.math.max
import kotlin.math.min

private data class SelectionIndices(
    val start: Int,
    val end: Int
)

class TextEditorOperationUseCase(
    private val mainViewModel: MainViewModel,
    private val content: TextFieldState,
    private val lastParagraph: () -> MultiParagraph?,
    private val scrollBy: (Float) -> Unit,
    private val switchLineNumber: () -> Unit,
    private val controlAndLeftBracketCase: ControlAndLeftBracketCase = ControlAndLeftBracketCase(),
    private val selectedTextConversion: SelectedTextConversion = SelectedTextConversion(),
    private val searchUrlFactory: SearchUrlFactory = SearchUrlFactory(),
    private val toHalfWidth: ToHalfWidth = ToHalfWidth(),
    private val expressionTextCalculatorService: ExpressionTextCalculatorService = ExpressionTextCalculatorService(),
    private val blockQuotation: BlockQuotation = BlockQuotation(),
    private val textReformat: TextReformat = TextReformat(),
    private val jsonPrettyPrint: JsonPrettyPrint = JsonPrettyPrint(),
) {

    fun moveToTop() {
        content.edit {
            selection = TextRange.Zero
        }
    }

    fun moveToBottom() {
        content.edit {
            selection = TextRange(length)
        }
    }

    fun scrollBy(spValue: Float) {
        scrollBy.invoke(spValue)
    }

    fun cutLine(): Boolean {
        if (content.selection.start != content.selection.end) {
            return false
        }

        val textLayoutResult = lastParagraph() ?: return false
        val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
        val lineStart = textLayoutResult.getLineStart(currentLine)
        val lineEnd = textLayoutResult.getLineEnd(currentLine)
        val targetEnd = min(content.text.length, lineEnd + 1)
        val currentLineText = content.text.substring(lineStart, targetEnd)
        ClipboardPutterService().invoke(currentLineText)
        content.edit { delete(lineStart, targetEnd) }
        return true
    }

    fun deleteLine() {
        if (content.selection.start != content.selection.end) {
            return
        }

        val textLayoutResult = lastParagraph() ?: return
        val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
        val lineStart = textLayoutResult.getLineStart(currentLine)
        val lineEnd = textLayoutResult.getLineEnd(currentLine)
        content.edit {
            delete(lineStart, min(length, lineEnd + 1))
        }
    }

    fun switchArticleList() {
        if (mainViewModel.openArticleList().not()) {
            mainViewModel.switchArticleList()
        }
    }

    fun hideArticleList() {
        mainViewModel.hideArticleList()
    }

    fun switchLineNumber() {
        switchLineNumber.invoke()
    }

    // it.isCtrlPressed && it.key == Key.D
    fun duplicateLine(): Boolean {
        val selected = getSelectedText(content)
        if (selected.isNotEmpty()) {
            content.edit {
                insert(selection.end, selected.toString())
            }
            return true
        }

        val textLayoutResult = lastParagraph.invoke() ?: return false
        val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
        val lineStart = textLayoutResult.getLineStart(currentLine)
        val lineEnd = textLayoutResult.getLineEnd(currentLine)
        val safeEnd = min(content.text.length, lineEnd)
        content.edit {
            insert(safeEnd, "\n${content.text.substring(lineStart, safeEnd)}")
            selection = content.selection
        }
        return true
    }

    // it.isCtrlPressed && it.key == Key.Minus
    fun toListLines(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        val selected = getSelectedText(content)
        if (selected.isEmpty()) {
            return false
        }

        val converted = ListHeadAdder().invoke(selected.toString(), "-") ?: return false
        content.edit {
            replace(selectionStartIndex, selectionEndIndex, converted)
            selection = TextRange(selectionStartIndex + converted.length)
        }
        return true
    }

    //it.isCtrlPressed && it.key == Key.One
    fun toOrderedList(): Boolean {
        val (selectionStartIndex, selectionEndIndex) = calculateSelectionIndices()

        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return false
        }

        val converted = NumberedListHeadAdder().invoke(selected) ?: return false
        content.edit {
            replace(selectionStartIndex, selectionEndIndex, converted)
            selection = TextRange(selectionStartIndex + converted.length)
        }
        return true
    }

    // it.isCtrlPressed && it.key == Key.Zero
    fun toTaskList(): Boolean {
        val (selectionStartIndex, selectionEndIndex) = calculateSelectionIndices()

        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return false
        }

        val converted = ListHeadAdder().invoke(selected, "- [ ]") ?: return false
        content.edit {
            replace(selectionStartIndex, selectionEndIndex, converted)
            selection = TextRange(selectionStartIndex + converted.length)
        }
        return true
    }

    fun moveToLineStart(): Boolean {
        val textLayoutResult = lastParagraph.invoke() ?: return false
        val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
        val lineStart = textLayoutResult.getLineStart(currentLine)
        content.edit { selection = TextRange(lineStart) }
        return true
    }

    // it.isCtrlPressed && it.key == Key.E
    fun moveToLineEnd(): Boolean {
        val textLayoutResult = lastParagraph.invoke() ?: return false
        val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
        val lineEnd = textLayoutResult.getLineEnd(currentLine)
        content.edit { selection = TextRange(lineEnd) }
        return true
    }

    // it.isCtrlPressed && it.key == Key.Comma
    fun insertComma(): Boolean {
        val (selectionStartIndex, selectionEndIndex) = calculateSelectionIndices()

        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return false
        }

        val converted = CommaInserter().invoke(selected) ?: return false
        content.edit {
            replace(selectionStartIndex, selectionEndIndex, converted)
            selection = TextRange(selectionStartIndex + converted.length)
        }
        return true
    }

    // it.isCtrlPressed && it.key == Key.T
    fun toTable(): Boolean {
        val (selectionStartIndex, selectionEndIndex) = calculateSelectionIndices()

        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return false
        }

        val tableString = TableFormConverter().invoke(selected)
        content.edit {
            replace(selectionStartIndex, selectionEndIndex, tableString)
            selection = TextRange(selectionStartIndex + tableString.length)
        }
        return true
    }

    // it.isCtrlPressed && it.isShiftPressed && it.key == Key.U
    fun switchCase(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return selectedTextConversion(
            content,
            selectionStartIndex,
            selectionEndIndex,
            ::switchCase
        )
    }

    // it.isCtrlPressed && it.isShiftPressed && it.key == Key.H
    fun toHalfWidth() {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        selectedTextConversion(
            content,
            selectionStartIndex,
            selectionEndIndex,
            toHalfWidth::invoke
        )
    }

    // it.isCtrlPressed && it.key == Key.B
    fun bold(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return selectedTextConversion(
            content,
            selectionStartIndex,
            selectionEndIndex,
            ::bold
        )
    }

    // it.isCtrlPressed && it.key == Key.I
    fun italic(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return selectedTextConversion(
            content,
            selectionStartIndex,
            selectionEndIndex,
            ::italic
        )
    }

    // it.isCtrlPressed && it.key == Key.Two
    fun doubleQuote(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return selectedTextConversion(
            content,
            selectionStartIndex,
            selectionEndIndex,
            ::doubleQuote
        )
    }

    // it.isCtrlPressed && it.key == Key.Eight
    fun surroundBrackets(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return selectedTextConversion(
            content,
            selectionStartIndex,
            selectionEndIndex,
            ::surroundBrackets
        )
    }

    // it.isCtrlPressed && it.key == Key.LeftBracket
    fun controlAndLeftBracket(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)

        return controlAndLeftBracketCase.invoke(content, selectionStartIndex)
    }

    // it.isCtrlPressed && it.key == Key.RightBracket
    fun surroundMultibyteBrackets() {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        selectedTextConversion(
            content,
            selectionStartIndex,
            selectionEndIndex,
            ::surroundMultibyteBrackets
        )
    }

    // it.isCtrlPressed && it.key == Key.At
    fun surroundCodeFence(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return selectedTextConversion.invoke(content, selectionStartIndex, selectionEndIndex, ::surroundCodeFence)
    }

    // it.isCtrlPressed && it.isShiftPressed && it.key == Key.C
    fun calculate(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return selectedTextConversion(
            content,
            selectionStartIndex,
            selectionEndIndex,
            expressionTextCalculatorService::invoke
        )
    }

    // it.isCtrlPressed && it.isShiftPressed && it.key == Key.N
    fun switchEditable() {
        (mainViewModel.currentTab() as? EditorTab)?.switchEditable()
    }

    // it.isCtrlPressed && it.isShiftPressed && it.key == Key.O
    fun openUrl(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return false
        }

        mainViewModel.openUrl(searchUrlFactory(selected), false)
        return true
    }

    // it.isCtrlPressed && it.isShiftPressed && it.key == Key.F
    fun reformat(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        val selected = StringBuilder(content.text.substring(selectionStartIndex, selectionEndIndex))
        if (selected.isEmpty()) {
            val clipped = ClipboardFetcher().invoke()
            if (clipped.isNullOrBlank().not()) {
                selected.append(clipped)
            }
        }

        if (selected.isEmpty()) {
            return false
        }

        val converted = textReformat.invoke(selected.toString())
        content.edit {
            replace(selectionStartIndex, selectionEndIndex, converted)
            selection = TextRange(selectionStartIndex + converted.length)
        }

        selected.setLength(0)
        return true
    }

    // it.isCtrlPressed && it.isShiftPressed && it.key == Key.P
    fun prettyPrint(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        val selected = StringBuilder(content.text.substring(selectionStartIndex, selectionEndIndex))
        if (selected.isEmpty()) {
            val clipped = ClipboardFetcher().invoke()
            if (clipped.isNullOrBlank().not()) {
                selected.append(clipped)
            }
        }

        if (selected.isEmpty()) {
            return false
        }

        val converted = jsonPrettyPrint.invoke(selected.toString())
        content.edit {
            replace(selectionStartIndex, selectionEndIndex, converted)
            selection = TextRange(selectionStartIndex + converted.length)
        }
        selected.setLength(0)
        return true
    }

    // it.isCtrlPressed && it.isAltPressed && it.key == Key.O
    fun search(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return false
        }

        val url = searchUrlFactory(selected)
        mainViewModel.browseUri(url)
        return true
    }

    // it.isCtrlPressed && it.key == Key.O
    fun openFile(): Boolean {
        val tab = mainViewModel.currentTab() as? EditorTab ?: return false
        mainViewModel.openFile(tab.path)
        return true
    }

    // it.isCtrlPressed && it.key == Key.Q
    fun quote(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isNotEmpty()) {
            selectedTextConversion(
                content,
                selectionStartIndex,
                selectionEndIndex,
                blockQuotation::invoke
            )
            return true
        }

        val clipped = ClipboardFetcher().invoke()
        if (!clipped.isNullOrEmpty()) {
            val decoratedLink = blockQuotation.invoke(clipped) ?: return false
            content.edit {
                insert(selectionStartIndex, decoratedLink)
                selection =
                    TextRange(
                        min(length, selectionStartIndex + decoratedLink.length + 1)
                    )
            }
            return true
        }

        return true
    }

    // it.isCtrlPressed && it.key == Key.J
    fun joinLines(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)

        val index = content.text.indexOf("\n", selectionStartIndex)
        if (index == -1) {
            return false
        }

        content.edit {
            delete(index, index + 1)
        }
        return true
    }

    // it.isCtrlPressed && it.key == Key.L
    fun decorateLink(): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)
        return LinkPasteCase()
            .invoke(
                content,
                selectionStartIndex,
                selectionEndIndex,
                selectedTextConversion
            )
    }

    private fun getSelectedText(content: TextFieldState): CharSequence {
        val selection = content.selection
        val selectionStartIndex = min(selection.start, selection.end)
        val selectionEndIndex = max(selection.start, selection.end)
        return content.text.subSequence(selectionStartIndex, selectionEndIndex)
    }

    private fun surroundCodeFence(it: String) = "```$it```"

    private fun surroundMultibyteBrackets(it: String) = "「$it」"

    private fun surroundBrackets(it: String) = "($it)"

    private fun doubleQuote(it: String) = "\"$it\""

    private fun italic(it: String) = "***$it***"

    private fun bold(it: String) = "**$it**"

    private fun switchCase(it: String) =
        if (it.toCharArray()[0].isUpperCase()) it.lowercase()
        else it.uppercase()

    private fun calculateSelectionIndices(): SelectionIndices {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)
        return SelectionIndices(selectionStartIndex, selectionEndIndex)
    }
}