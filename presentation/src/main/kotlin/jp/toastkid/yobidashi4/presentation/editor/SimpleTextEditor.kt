package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.component.collectCommittedInput
import jp.toastkid.yobidashi4.presentation.editor.viewmodel.TextEditorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import java.util.regex.Pattern

@Immutable
private data class EditorStylePattern(
    val regex: Pattern,
    val lightStyle: SpanStyle,
    val darkStyle: SpanStyle
)

// スタイル計算結果を保持するデータクラス
data class ParseResult(
    val text: String,
    val styles: List<Triple<Int, Int, SpanStyle>>
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalTextApi::class)
@Composable
fun SimpleTextEditor(
    tab: EditorTab,
    setStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { TextEditorViewModel() }
    val coroutineScope = rememberCoroutineScope()
    val parseResult = remember { mutableStateOf(ParseResult("", emptyList())) }
    val transformation = remember(parseResult.value) {
        TextEditorOutputTransformation(viewModel.content(), parseResult.value)
    }

    Box {
        BasicTextField(
            state = viewModel.content(),
            onTextLayout = {
                val multiParagraph = it.invoke()?.multiParagraph ?: return@BasicTextField
                viewModel.setMultiParagraph(multiParagraph)
            },
            inputTransformation = viewModel.inputTransformation(),
            outputTransformation = transformation,
            decorator = {
                Row {
                    Column(
                        modifier = Modifier
                            .verticalScroll(viewModel.lineNumberScrollState())
                            .padding(horizontal = 8.dp)
                            .wrapContentSize(unbounded = true)
                    ) {
                        viewModel.lineNumbers().forEach { (lineNumber, lineNumberText) ->
                            Box(
                                contentAlignment = Alignment.CenterEnd,
                                modifier = Modifier.clickable {
                                    viewModel.onClickLineNumber(lineNumber)
                                }
                                    .semantics { contentDescription = "Line number $lineNumberText" }
                            ) {
                                Text(
                                    lineNumberText,
                                    fontSize = viewModel.fontSize().sp,
                                    fontFamily = FontFamily("MS Gothic"),
                                    textAlign = TextAlign.End,
                                    style = TextStyle(
                                        lineHeight = viewModel.lineHeight().em,
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.None
                                        )
                                    )
                                )
                            }
                        }
                    }
                    it()
                }
            },
            textStyle = TextStyle(
                color = MaterialTheme.colors.onSurface,
                fontSize = viewModel.fontSize().sp,
                fontFamily = FontFamily("MS Gothic"),
                lineHeight = viewModel.lineHeight().em,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            scrollState = viewModel.verticalScrollState(),
            cursorBrush = SolidColor(MaterialTheme.colors.secondary),
            modifier = modifier.focusRequester(viewModel.focusRequester())
                .fillMaxWidth()
                .onPreviewKeyEvent {
                    viewModel.onPreviewKeyEvent(it, coroutineScope)
                }
                .onKeyEvent(viewModel::onKeyEvent)
                .drawBehind {
                    val currentLineOffset = viewModel.currentLineOffset()
                    drawRect(
                        viewModel.currentLineHighlightColor(),
                        topLeft = currentLineOffset,
                        size = viewModel.getHighlightSize()
                    )
                }
                .semantics { contentDescription = "Editor input area" }
        )

        DisposableEffect(tab.path) {
            viewModel.launchTab(tab)
            viewModel.initialScroll(coroutineScope)

            setStatus(viewModel.makeCharacterCountMessage(tab.getContent().length))

            onDispose(viewModel::dispose)
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(viewModel.verticalScrollState()),
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
        )
    }

    LaunchedEffect(viewModel.verticalScrollState().value) {
        viewModel.adjustLineNumberState()
    }

    LaunchedEffect(viewModel.content()) {
        collectCommittedInput(viewModel.content()) {
            viewModel.update()
            setStatus(viewModel.makeCharacterCountMessage(viewModel.content().text.length))
        }
    }

    LaunchedEffect(viewModel.content()) {
        val textFieldState = viewModel.content()

        snapshotFlow {
                val text = textFieldState.text

                val lineCount = viewModel.lineNumbers().size

                val lineStarts = text.splitToSequence('\n')
                    .map { if (it.isNotEmpty()) it[0] else '\n' }
                    .joinToString("")

                Triple(lineCount, lineStarts, textFieldState.composition == null)
            }
            .distinctUntilChanged()
            .filter { it.third }
            .debounce(100)
            .flowOn(Dispatchers.Default)
            .collect { _ ->
                val currentText = viewModel.content().text.toString()
                val styles = calculateStyleAsync(true, currentText)

                parseResult.value = ParseResult(currentText, styles)
            }
    }
}

private fun calculateStyleAsync(darkTheme: Boolean, str: String): List<Triple<Int, Int, SpanStyle>> {
    val list = mutableListOf<Triple<Int, Int, SpanStyle>>()

    patterns.forEach { pattern ->
        val find = pattern.regex.matcher(str)
        while (find.find()) {
            val spanStyle = if (darkTheme) pattern.darkStyle else pattern.lightStyle
            list.add(Triple(find.start(), find.end(), spanStyle))
        }
    }
    return list
}

class TextEditorOutputTransformation(
    private val content: TextFieldState,
    private val currentParseResult: ParseResult
) : OutputTransformation {

    override fun TextFieldBuffer.transformOutput() {
        val currentText = this.asCharSequence()
        val parsedText = currentParseResult.text

        if (currentText == parsedText) {
            currentParseResult.styles.forEach { (start, end, style) ->
                if (start <= length && end <= length) {
                    addStyle(style, start, end)
                }
            }
            append("[EOF]")
            return
        }

        val diffIndex = findDiffIndexFast(currentText, parsedText)
        val realDiffLength = currentText.length - parsedText.length

        currentParseResult.styles.forEach { (start, end, style) ->
            var newStart = start
            var newEnd = end

            if (start >= diffIndex) {
                newStart = (start + realDiffLength).coerceAtLeast(0)
                newEnd = (end + realDiffLength).coerceAtLeast(0)
            } else if (end > diffIndex) {
                newEnd = (end + realDiffLength).coerceAtLeast(0)
            }

            if (newStart < newEnd && newStart <= length && newEnd <= length) {
                addStyle(style, newStart, newEnd)
            }
        }

        val selectionStart = content.selection.start
        if (selectionStart <= currentText.length) {
            // 現在の行の「始まり」と「終わり」のインデックスを取得
            val (lineStart, lineEnd) = findCurrentLineRange(currentText, selectionStart)
            val currentLineText = currentText.substring(lineStart, lineEnd)

            // その1行だけに対して、定義済みの正規表現を回す（数十文字なので 0ms で終わる）
            patterns.forEach { pattern ->
                val matcher = pattern.regex.matcher(currentLineText)
                while (matcher.find()) {
                    val style = if (true) pattern.darkStyle else pattern.lightStyle // 必要に応じてdarkMode判定を

                    // 全体のインデックス（lineStartからの相対位置）に直して上書き適用
                    val globalStart = lineStart + matcher.start()
                    val globalEnd = lineStart + matcher.end()

                    if (globalStart <= length && globalEnd <= length) {
                        addStyle(style, globalStart, globalEnd)
                    }
                }
            }
        }

        append("[EOF]")
    }

    private fun findDiffIndexFast(current: CharSequence, parsed: String): Int {
        val minLen = minOf(current.length, parsed.length)
        for (i in 0 until minLen) {
            if (current[i] != parsed[i]) {
                return i
            }
        }
        return minLen
    }

    private fun findCurrentLineRange(text: CharSequence, selectionStart: Int): Pair<Int, Int> {
        val start = text.lastIndexOf('\n', selectionStart - 1).coerceAtKeyAtLeast(0)
        val end = text.indexOf('\n', selectionStart).let { if (it == -1) text.length else it }
        return Pair(start, end)
    }

    private fun Int.coerceAtKeyAtLeast(value: Int): Int = if (this < value) value else this

}

private val patterns = listOf(
    EditorStylePattern(
        Pattern.compile("^[0-9]+\\.\\s", Pattern.MULTILINE),
        SpanStyle(Color(0xFF6897BB)),
        SpanStyle(Color(0xFFA8B7EE))
    ),
    EditorStylePattern(
        Pattern.compile("^#.*?$", Pattern.MULTILINE),
        SpanStyle(Color(0xFF008800), fontWeight = FontWeight.Bold),
        SpanStyle(Color(0xFF00DD00), fontWeight = FontWeight.Bold)
    ),
    EditorStylePattern(
        Pattern.compile("^\\|.*?$", Pattern.MULTILINE),
        SpanStyle(Color(0xFF8800CC)),
        SpanStyle(Color(0xFF86EEC7))
    ),
    EditorStylePattern(
        Pattern.compile("^>.*?$", Pattern.MULTILINE),
        SpanStyle(Color(0xFF7744AA)),
        SpanStyle(Color(0xFFCCAAFF))
    ),
    EditorStylePattern(
        Pattern.compile("^-.*?$", Pattern.MULTILINE),
        SpanStyle(Color(0xFF666239)),
        SpanStyle(Color(0xFFFFD54F))
    ),
    EditorStylePattern(
        Pattern.compile("^\\*.*?$", Pattern.MULTILINE),
        SpanStyle(Color(0xFF666239)),
        SpanStyle(Color(0xFFFFD54F))
    )
)