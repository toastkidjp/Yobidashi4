package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import jp.toastkid.yobidashi4.domain.service.tool.calculator.SimpleCalculator
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.BlockQuotation
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.CommaInserter
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.LinkDecoratorService
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.NumberedListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.TableFormConverter
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.math.max
import kotlin.math.min
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KeyEventConsumer(
    private val mainViewModel: MainViewModel = object : KoinComponent { val vm : MainViewModel by inject() }.vm
) {
    
    @OptIn(ExperimentalComposeUiApi::class)
    operator fun invoke(
        it: KeyEvent,
        content: TextFieldValue,
        lastParagraph: MultiParagraph?,
        scrollBy: (Float) -> Unit,
        setNewContent: (TextFieldValue) -> Unit
    ): Boolean {
        val rawSelectionStartIndex = content.selection.start
        val rauSelectionEndIndex = content.selection.end
        val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
        val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

        return when {
            it.isCtrlPressed && it.key == Key.S -> {
                mainViewModel.saveCurrentEditorTab()
                true
            }
            it.isCtrlPressed && it.key == Key.D -> {
                val startIndex = content.selection.start
                val endIndex = content.selection.end
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isNotEmpty()) {
                    val newText = StringBuilder(content.text)
                        .insert(max(startIndex, endIndex), selected)
                        .toString()
                    setNewContent(
                        TextFieldValue(
                            newText,
                            TextRange(max(startIndex, endIndex)),
                            content.composition
                        )
                    )
                    return true
                }

                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                val newText = StringBuilder(content.text)
                    .insert(lineEnd, "\n${content.text.substring(lineStart, lineEnd)}")
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        content.selection,
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.X -> {
                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                val currentLineText = content.text.substring(lineStart, lineEnd + 1)
                ClipboardPutterService().invoke(currentLineText)
                val newText = StringBuilder(content.text)
                    .delete(lineStart, lineEnd + 1)
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(lineStart),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.Minus -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = ListHeadAdder().invoke(selected, "-") ?: return false
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        converted
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + converted.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.One -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = NumberedListHeadAdder().invoke(selected) ?: return false
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        converted
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + converted.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.Two -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = selected.split("\n").map { line -> "- [ ] $line" }.joinToString("\n")
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        converted
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + converted.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.Comma -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val converted = CommaInserter().invoke(selected) ?: return false
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        converted
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + converted.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.key == Key.T -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val tableString = TableFormConverter().invoke(selected)
                val newText = StringBuilder(content.text)
                    .replace(
                        selectionStartIndex,
                        selectionEndIndex,
                        tableString
                    )
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(selectionStartIndex + tableString.length),
                        content.composition
                    )
                )
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.U -> {
                convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                    if (it.toCharArray()[0].isUpperCase()) it.lowercase() else it.uppercase()
                }?.let(setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.B -> {
                convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                    "**$it**"
                }?.let(setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.I -> {
                convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                    "```\n$it```"
                }?.let(setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.Eight -> {
                convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                    "($it)"
                }?.let(setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.RightBracket -> {
                convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                    "「$it」"
                }?.let(setNewContent)
                true
            }
            it.isCtrlPressed && it.key == Key.At -> {
                convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                    "*$it*"
                }?.let(setNewContent)
                true
            }
            it.isCtrlPressed && it.key.nativeKeyCode == java.awt.event.KeyEvent.VK_CIRCUMFLEX -> {
                convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                    "~~$it~~"
                }?.let(setNewContent)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.C -> {
                convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                    val appendLineBreakIfNeed = if (it.endsWith("\n")) "\n" else ""
                    "${SimpleCalculator().invoke(it.trimEnd())?.toString()}$appendLineBreakIfNeed"
                }?.let(setNewContent)
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.O -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                if (selected.startsWith("http://") || selected.startsWith("https://")) {
                    mainViewModel.openUrl(selected, false)
                    return true
                }
                mainViewModel.openUrl("https://search.yahoo.co.jp/search?p=${encodeUtf8(selected)}", false)
                true
            }
            it.isCtrlPressed && it.isAltPressed && it.key == Key.O -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isEmpty()) {
                    return false
                }

                val url = if (selected.startsWith("http://") || selected.startsWith("https://"))
                    selected
                else
                    "https://search.yahoo.co.jp/search?p=${encodeUtf8(selected)}"
                Desktop.getDesktop().browse(URI(url))
                true
            }
            it.isCtrlPressed && it.key == Key.Q -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.isNotEmpty()) {
                    convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                        BlockQuotation().invoke(it)
                    }?.let(setNewContent)
                    return true
                }

                val clipped = ClipboardFetcher().invoke() ?: return false
                if (clipped.isNotEmpty()) {
                    val decoratedLink = BlockQuotation().invoke(clipped) ?: return false
                    val newText = StringBuilder(content.text)
                        .insert(
                            selectionStartIndex,
                            decoratedLink
                        )
                        .toString()
                    setNewContent(
                        TextFieldValue(
                            newText,
                            TextRange(selectionStartIndex + decoratedLink.length + 1),
                            content.composition
                        )
                    )
                    return true
                }

                true
            }
            it.isCtrlPressed && it.key == Key.L -> {
                val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
                if (selected.startsWith("http://") || selected.startsWith("https://")) {
                    convertSelectedText(content, selectionStartIndex, selectionEndIndex) {
                        LinkDecoratorService().invoke(selected)
                    }?.let(setNewContent)
                    return true
                }

                val clipped = ClipboardFetcher().invoke() ?: return false
                if (clipped.startsWith("http://") || clipped.startsWith("https://")) {
                    val decoratedLink = LinkDecoratorService().invoke(clipped)
                    val newText = StringBuilder(content.text)
                        .insert(
                            selectionStartIndex,
                            decoratedLink
                        )
                        .toString()
                    setNewContent(
                        TextFieldValue(
                            newText,
                            TextRange(selectionStartIndex + decoratedLink.length + 1),
                            content.composition
                        )
                    )
                    return true
                }

                true
            }
            it.isCtrlPressed && it.key == Key.DirectionUp -> {
                scrollBy(-16.sp.value)
                true
            }
            it.isCtrlPressed && it.key == Key.DirectionDown -> {
                scrollBy(16.sp.value)
                true
            }
            it.isAltPressed && it.key == Key.DirectionRight -> {
                if (mainViewModel.openArticleList().not()) {
                    mainViewModel.switchArticleList()
                }
                true
            }
            it.isAltPressed && it.key == Key.DirectionLeft -> {
                mainViewModel.hideArticleList()
                true
            }
            else -> false
        }
    }

    private fun convertSelectedText(
        content: TextFieldValue,
        selectionStartIndex: Int,
        selectionEndIndex: Int,
        conversion: (String) -> String?
    ): TextFieldValue? {
        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return null
        }

        val converted = conversion(selected) ?: return null
        val newText = StringBuilder(content.text)
            .replace(
                selectionStartIndex,
                selectionEndIndex,
                converted
            )
            .toString()
        return TextFieldValue(
            newText,
            TextRange(selectionStartIndex + converted.length),
            content.composition
        )
    }

    private fun encodeUtf8(selectedText: String) = URLEncoder.encode(selectedText, StandardCharsets.UTF_8.name())

}