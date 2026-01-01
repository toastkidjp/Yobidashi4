package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.domain.service.editor.text.JsonPrettyPrint
import jp.toastkid.yobidashi4.domain.service.editor.text.TextReformat
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.BlockQuotation
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.CommaInserter
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ExpressionTextCalculatorService
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.NumberedListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.TableFormConverter
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ToHalfWidth
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max
import kotlin.math.min

class KeyEventConsumer(
    private val mainViewModel: MainViewModel = object : KoinComponent { val vm : MainViewModel by inject() }.vm,
    private val controlAndLeftBracketCase: ControlAndLeftBracketCase = ControlAndLeftBracketCase(),
    private val selectedTextConversion: SelectedTextConversion = SelectedTextConversion(),
    private val searchUrlFactory: SearchUrlFactory = SearchUrlFactory(),
    private val toHalfWidth: ToHalfWidth = ToHalfWidth(),
    private val expressionTextCalculatorService: ExpressionTextCalculatorService = ExpressionTextCalculatorService(),
    private val blockQuotation: BlockQuotation = BlockQuotation(),
    private val textReformat: TextReformat = TextReformat(),
    private val jsonPrettyPrint: JsonPrettyPrint = JsonPrettyPrint(),
) {

    private fun getSelectedText(content: TextFieldState): CharSequence {
        val selection = content.selection
        val selectionStartIndex = min(selection.start, selection.end)
        val selectionEndIndex = max(selection.start, selection.end)
        return content.text.subSequence(selectionStartIndex, selectionEndIndex)
    }

    operator fun invoke(
        it: KeyEvent,
        content: TextFieldState,
        lastParagraph: MultiParagraph?
    ): Boolean {
        if (it.type != KeyEventType.KeyDown) {
            return false
        }

        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return when {
            it.isCtrlPressed && it.key == Key.D -> {
                val selected = getSelectedText(content)
                if (selected.isNotEmpty()) {
                    content.edit {
                        insert(selection.end, selected.toString())
                    }
                    return true
                }

                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                val safeEnd = min(content.text.length, lineEnd)
                content.edit {
                    insert(safeEnd, "\n${content.text.substring(lineStart, safeEnd)}")
                    selection = content.selection
                }
                true
            }
            it.isCtrlPressed && it.key == Key.Minus -> {
                val selected = getSelectedText(content)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = ListHeadAdder().invoke(selected.toString(), "-") ?: return false
                content.edit {
                    replace(selectionStartIndex, selectionEndIndex, converted)
                    selection = TextRange(selectionStartIndex + converted.length)
                }
                true
            }
            it.isCtrlPressed && it.key == Key.One -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = NumberedListHeadAdder().invoke(selected) ?: return false
                content.edit {
                    replace(selectionStartIndex, selectionEndIndex, converted)
                    selection = TextRange(selectionStartIndex + converted.length)
                }
                true
            }
            it.isCtrlPressed && it.key == Key.Zero -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = ListHeadAdder().invoke(selected, "- [ ]") ?: return false
                content.edit {
                    replace(selectionStartIndex, selectionEndIndex, converted)
                    selection = TextRange(selectionStartIndex + converted.length)
                }
                true
            }
            it.isCtrlPressed && it.key == Key.Four -> {
                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                content.edit { selection = TextRange(lineStart) }
                true
            }
            it.isCtrlPressed && it.key == Key.E -> {
                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                content.edit { selection = TextRange(lineEnd) }
                true
            }
            it.isCtrlPressed && it.key == Key.Comma -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = CommaInserter().invoke(selected) ?: return false
                content.edit {
                    replace(selectionStartIndex, selectionEndIndex, converted)
                    selection = TextRange(selectionStartIndex + converted.length)
                }
                true
            }
            it.isCtrlPressed && it.key == Key.T -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val tableString = TableFormConverter().invoke(selected)
                content.edit {
                    replace(selectionStartIndex, selectionEndIndex, tableString)
                    selection = TextRange(selectionStartIndex + tableString.length)
                }
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.U -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, ::switchCase)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.H -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, toHalfWidth::invoke)
                true
            }
            it.isCtrlPressed && it.key == Key.B -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, ::bold)
                true
            }
            it.isCtrlPressed && it.key == Key.I -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, ::italic)
                true
            }
            it.isCtrlPressed && it.key == Key.Two -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, ::doubleQuote)
                true
            }
            it.isCtrlPressed && it.key == Key.Eight -> {
                selectedTextConversion(
                    content,
                    selectionStartIndex,
                    selectionEndIndex,
                    ::surroundBrackets
                )
                true
            }
            it.isCtrlPressed && it.key == Key.LeftBracket -> {
                return controlAndLeftBracketCase.invoke(content, selectionStartIndex)
            }
            it.isCtrlPressed && it.key == Key.RightBracket -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, ::surroundMultibyteBrackets)
                true
            }
            it.isCtrlPressed && it.key == Key.At -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, ::surroundCodeFence)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.C -> {
                selectedTextConversion(content, selectionStartIndex, selectionEndIndex, expressionTextCalculatorService::invoke)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.N -> {
                (mainViewModel.currentTab() as? EditorTab)?.switchEditable()
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.O -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                mainViewModel.openUrl(searchUrlFactory(selected), false)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.F -> {
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
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.P -> {
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
                true
            }
            it.isCtrlPressed && it.isAltPressed && it.key == Key.O -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val url = searchUrlFactory(selected)
                mainViewModel.browseUri(url)
                true
            }
            it.isCtrlPressed && it.key == Key.O -> {
                val tab = mainViewModel.currentTab() as? EditorTab ?: return false
                mainViewModel.openFile(tab.path)
                true
            }
            it.isCtrlPressed && it.key == Key.Q -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isNotEmpty()) {
                    selectedTextConversion(content, selectionStartIndex, selectionEndIndex, blockQuotation::invoke)
                    return true
                }

                val clipped = ClipboardFetcher().invoke()
                if (!clipped.isNullOrEmpty()) {
                    val decoratedLink = blockQuotation.invoke(clipped) ?: return false
                    content.edit {
                        insert(selectionStartIndex, decoratedLink)
                        selection = TextRange(min(length, selectionStartIndex + decoratedLink.length + 1))
                    }
                    return true
                }

                true
            }
            it.isCtrlPressed && it.key == Key.J -> {
                val index = content.text.indexOf("\n", selectionStartIndex)
                if (index == -1) {
                    return false
                }

                content.edit {
                    delete(index, index + 1)
                }
                true
            }
            it.isCtrlPressed && it.key == Key.L -> {
                LinkPasteCase()
                    .invoke(
                        content,
                        selectionStartIndex,
                        selectionEndIndex,
                        selectedTextConversion
                    )
            }
            else -> false
        }
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

}
