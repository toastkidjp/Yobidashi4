package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.rememberTextFieldVerticalScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.service.tool.calculator.SimpleCalculator
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.ClipboardFetcher
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.LinkDecoratorService
import jp.toastkid.yobidashi4.presentation.editor.legacy.text.CommaInserter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SimpleTextEditor(
    tab: EditorTab,
    setStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val content = remember { mutableStateOf(TextFieldValue()) }
    val verticalScrollState = rememberTextFieldVerticalScrollState()
    val lineNumberScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val lastTextLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val job = remember { Job() }

    val mainViewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }

    val theme = remember { EditorTheme() }
    var last:TransformedText? = null

    Box {
        var altPressed = false
        BasicTextField(
            value = content.value,
            onValueChange = {
                if (altPressed) {
                    return@BasicTextField
                }
                if (content.value.text.length != it.text.length) {
                    setStatus("Character: ${it.text.length}")
                    mainViewModel.updateEditorContent(
                        tab.path,
                        content.value.text,
                        -1,
                        false
                    )
                }
                content.value = it
            },
            visualTransformation = {
                if (content.value.composition != null && last != null) {
                    return@BasicTextField last!!
                }
                val start = System.nanoTime()
                val t = theme.codeString(content.value.text, mainViewModel.darkMode())
                //println("convert ${t.length} time ${System.nanoTime() - start} [ns]")
                last = TransformedText(t, OffsetMapping.Identity)
                last!!
            },
            onTextLayout = {
                lastTextLayoutResult.value = it
            },
            decorationBox = {
                Row {
                    Column(
                        modifier = Modifier
                            .verticalScroll(lineNumberScrollState)
                            .padding(horizontal = 8.dp)
                            .wrapContentSize(unbounded = true)
                    ) {
                        val max = lastTextLayoutResult.value?.lineCount ?: content.value.text.split("\n").size
                        val length = max.toString().length
                        repeat(max) {
                            val lineNumberCount = it + 1
                            val fillCount = length - lineNumberCount.toString().length
                            val lineNumberText = with(StringBuilder()) {
                                repeat(fillCount) {
                                    append(" ")
                                }
                                append(lineNumberCount)
                            }.toString()
                            Box(
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(lineNumberText, fontSize = 16.sp, fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.End, lineHeight = 1.5.em)
                            }
                        }
                    }
                    it()
                }
            },
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = FontFamily.Monospace, lineHeight = 1.5.em),
            scrollState = verticalScrollState,
            cursorBrush = SolidColor(MaterialTheme.colors.secondary),
            modifier = modifier.focusRequester(focusRequester)
                .onKeyEvent {
                    altPressed = it.isAltPressed
                    if (it.type != KeyEventType.KeyUp) {
                        return@onKeyEvent false
                    }

                    val rawSelectionStartIndex = content.value.selection.start
                    val rauSelectionEndIndex = content.value.selection.end
                    val selectionStartIndex = min(rawSelectionStartIndex, rauSelectionEndIndex)
                    val selectionEndIndex = max(rawSelectionStartIndex, rauSelectionEndIndex)

                    when {
                        it.isCtrlPressed && it.key == Key.S -> {
                            try {
                                CoroutineScope(Dispatchers.IO).launch(job) {
                                    val text = content.value.text
                                    val textArray = text.toByteArray()
                                    if (textArray.isNotEmpty()) {
                                        Files.write(tab.path, textArray)
                                    }
                                    mainViewModel.updateEditorContent(
                                        tab.path,
                                        content.value.text,
                                        -1,
                                        true
                                    )
                                }
                            } catch (e: IOException) {
                                LoggerFactory.getLogger(javaClass).warn("Storing error.", e)
                            }
                            true
                        }
                        it.isCtrlPressed && it.key == Key.D -> {
                            val startIndex = content.value.selection.start
                            val endIndex = content.value.selection.end
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.isNotEmpty()) {
                                val newText = StringBuilder(content.value.text)
                                    .insert(max(startIndex, endIndex), selected)
                                    .toString()
                                content.value = TextFieldValue(
                                    newText,
                                    TextRange(max(startIndex, endIndex)),
                                    content.value.composition
                                )
                                return@onKeyEvent true
                            }

                            val textLayoutResult = lastTextLayoutResult.value ?: return@onKeyEvent false
                            val currentLine = textLayoutResult.getLineForOffset(content.value.selection.start)
                            val lineStart = textLayoutResult.getLineStart(currentLine)
                            val lineEnd = textLayoutResult.getLineEnd(currentLine)
                            val newText = StringBuilder(content.value.text)
                                .insert(lineEnd, "\n${content.value.text.substring(lineStart, lineEnd)}")
                                .toString()
                            content.value = TextFieldValue(
                                newText,
                                content.value.selection,
                                content.value.composition
                            )
                            true
                        }
                        it.isCtrlPressed && it.isShiftPressed && it.key == Key.X -> {
                            val textLayoutResult = lastTextLayoutResult.value ?: return@onKeyEvent false
                            val currentLine = textLayoutResult.getLineForOffset(content.value.selection.start)
                            val lineStart = textLayoutResult.getLineStart(currentLine)
                            val lineEnd = textLayoutResult.getLineEnd(currentLine)
                            val currentLineText = content.value.text.substring(lineStart, lineEnd + 1)
                            ClipboardPutterService().invoke(currentLineText)
                            val newText = StringBuilder(content.value.text)
                                .delete(lineStart, lineEnd + 1)
                                .toString()
                            content.value = TextFieldValue(
                                newText,
                                TextRange(lineStart),
                                content.value.composition
                            )
                            true
                        }
                        it.isCtrlPressed && it.key == Key.Minus -> {
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.isEmpty()) {
                                return@onKeyEvent false
                            }

                            val converted = selected.split("\n").map { "- $it" }.joinToString("\n")
                            val newText = StringBuilder(content.value.text)
                                .replace(
                                    selectionStartIndex,
                                    selectionEndIndex,
                                    converted
                                )
                                .toString()
                            content.value = TextFieldValue(
                                newText,
                                TextRange(selectionStartIndex + converted.length),
                                content.value.composition
                            )
                            true
                        }
                        it.isCtrlPressed && it.key == Key.One -> {
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.isEmpty()) {
                                return@onKeyEvent false
                            }

                            val converted = selected.split("\n").mapIndexed { index, line -> "${index + 1}. $line" }.joinToString("\n")
                            val newText = StringBuilder(content.value.text)
                                .replace(
                                    selectionStartIndex,
                                    selectionEndIndex,
                                    converted
                                )
                                .toString()
                            content.value = TextFieldValue(
                                newText,
                                TextRange(selectionStartIndex + converted.length),
                                content.value.composition
                            )
                            true
                        }
                        it.isCtrlPressed && it.key == Key.Two -> {
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.isEmpty()) {
                                return@onKeyEvent false
                            }

                            val converted = selected.split("\n").map { line -> "- [ ] $line" }.joinToString("\n")
                            val newText = StringBuilder(content.value.text)
                                .replace(
                                    selectionStartIndex,
                                    selectionEndIndex,
                                    converted
                                )
                                .toString()
                            content.value = TextFieldValue(
                                newText,
                                TextRange(selectionStartIndex + converted.length),
                                content.value.composition
                            )
                            true
                        }
                        it.isCtrlPressed && it.key == Key.Comma -> {
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.isEmpty()) {
                                return@onKeyEvent false
                            }

                            val converted = CommaInserter().invoke(selected) ?: return@onKeyEvent false
                            val newText = StringBuilder(content.value.text)
                                .replace(
                                    selectionStartIndex,
                                    selectionEndIndex,
                                    converted
                                )
                                .toString()
                            content.value = TextFieldValue(
                                newText,
                                TextRange(selectionStartIndex + converted.length),
                                content.value.composition
                            )
                            true
                        }
                        it.isCtrlPressed && it.key == Key.T -> {
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.isEmpty()) {
                                return@onKeyEvent false
                            }

                            val endLineBreak = selected.endsWith("\n")
                            val tableString = selected.trimEnd().split("\n").map { "| ${it.replace(" ", " | ")}" }
                                .joinToString("\n") + if (endLineBreak) "\n" else ""
                            val newText = StringBuilder(content.value.text)
                                .replace(
                                    selectionStartIndex,
                                    selectionEndIndex,
                                    tableString
                                )
                                .toString()
                            content.value = TextFieldValue(
                                newText,
                                TextRange(selectionStartIndex + tableString.length),
                                content.value.composition
                            )
                            true
                        }
                        it.isCtrlPressed && it.isShiftPressed && it.key == Key.U -> {
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.isEmpty()) {
                                return@onKeyEvent false
                            }

                            val reversed = if (selected.toCharArray()[0].isUpperCase()) selected.lowercase() else selected.uppercase()
                            val newText = StringBuilder(content.value.text)
                                .replace(
                                    selectionStartIndex,
                                    selectionEndIndex,
                                    reversed
                                )
                                .toString()
                            content.value = TextFieldValue(
                                newText,
                                content.value.selection,
                                content.value.composition
                            )
                            true
                        }
                        it.isCtrlPressed && it.key == Key.B -> {
                            convertSelectedText(content.value, selectionStartIndex, selectionEndIndex) {
                                "**$it**"
                            }?.let { content.value = it }
                            true
                        }
                        it.isCtrlPressed && it.key == Key.I -> {
                            convertSelectedText(content.value, selectionStartIndex, selectionEndIndex) {
                                "*$it*"
                            }?.let { content.value = it }
                            true
                        }
                        it.isCtrlPressed && it.isShiftPressed && it.key == Key.C -> {
                            convertSelectedText(content.value, selectionStartIndex, selectionEndIndex) {
                                SimpleCalculator().invoke(it)?.toString()
                            }?.let { content.value = it }
                            true
                        }
                        it.isCtrlPressed && it.isShiftPressed && it.key == Key.O -> {
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.isEmpty()) {
                                return@onKeyEvent false
                            }

                            if (selected.startsWith("http://") || selected.startsWith("https://")) {
                                mainViewModel.openUrl(selected, false)
                                return@onKeyEvent true
                            }
                            mainViewModel.openUrl("https://search.yahoo.co.jp/search?p=${encodeUtf8(selected)}", false)
                            true
                        }
                        it.isCtrlPressed && it.key == Key.L -> {
                            val selected = content.value.text.substring(selectionStartIndex, selectionEndIndex)
                            if (selected.startsWith("http://") || selected.startsWith("https://")) {
                                convertSelectedText(content.value, selectionStartIndex, selectionEndIndex) {
                                    LinkDecoratorService().invoke(selected)
                                }?.let { content.value = it }
                                return@onKeyEvent true
                            }

                            val clipped = ClipboardFetcher().invoke() ?: return@onKeyEvent false
                            if (clipped.startsWith("http://") || clipped.startsWith("https://")) {
                                val decoratedLink = LinkDecoratorService().invoke(clipped)
                                val newText = StringBuilder(content.value.text)
                                    .insert(
                                        selectionStartIndex,
                                        decoratedLink
                                    )
                                    .toString()
                                content.value = TextFieldValue(
                                    newText,
                                    TextRange(selectionStartIndex + decoratedLink.length + 1),
                                    content.value.composition
                                )
                                return@onKeyEvent true
                            }

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
        )

        VerticalScrollbar(adapter = rememberScrollbarAdapter(verticalScrollState), modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd))
        HorizontalScrollbar(adapter = rememberScrollbarAdapter(horizontalScrollState), modifier = Modifier.fillMaxWidth().align(
            Alignment.BottomCenter))
    }

    LaunchedEffect(verticalScrollState.offset) {
        lineNumberScrollState.scrollTo(verticalScrollState.offset.toInt())
    }

    DisposableEffect(tab.path) {
        focusRequester.requestFocus()

        content.value = TextFieldValue(tab.getContent(), TextRange(tab.caretPosition()))
        setStatus("Character: ${content.value.text.length}")

        onDispose {
            val currentText = content.value.text
            if (currentText.isEmpty()) {
                return@onDispose
            }
            lastTextLayoutResult.value = null
            last = null
            job.cancel()
            mainViewModel.updateEditorContent(tab.path, currentText, content.value.selection.start, false)
        }
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
