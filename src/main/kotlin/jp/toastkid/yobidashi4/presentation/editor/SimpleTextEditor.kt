package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.math.max
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SimpleTextEditor(
    path: Path,
    modifier: Modifier = Modifier
) {
    val content = remember { mutableStateOf(TextFieldValue(path.readText())) }
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }

    Box {
        BasicTextField(
            value = content.value,
            onValueChange = {
                content.value = it
            },
            visualTransformation = {
                val t = codeString(content.value.text)
                TransformedText(t, OffsetMapping.Identity)
            },
            decorationBox = {
                Row {
                    Column(
                        modifier = Modifier
                            .scrollable(verticalScrollState, Orientation.Vertical)
                            .padding(end = 8.dp)
                            .wrapContentSize(unbounded = true)
                    ) {
                        val textLines = content.value.text.split("\n")
                        val max = textLines.size
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
            onTextLayout = {
                textLayoutResultState.value = it
            },
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = FontFamily.Monospace, lineHeight = 1.5.em),
            modifier = modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
                .onPointerEvent(PointerEventType.Scroll, PointerEventPass.Main) {
                    coroutineScope.launch {
                        verticalScrollState.scrollBy(it.changes.first().scrollDelta.y)
                    }
                }
                .bringIntoViewRequester(bringIntoViewRequester)
        )

        VerticalScrollbar(adapter = rememberScrollbarAdapter(verticalScrollState), modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd))
        HorizontalScrollbar(adapter = rememberScrollbarAdapter(horizontalScrollState), modifier = Modifier.fillMaxWidth().align(
            Alignment.BottomCenter))
    }

    LaunchedEffect(content.value.selection) {
        if (content.value.text.isBlank()) {
            return@LaunchedEffect
        }
        val rect = textLayoutResultState.value?.getBoundingBox(max(0, content.value.selection.end - 1))
        bringIntoViewRequester.bringIntoView(rect)
    }
}

private fun codeString(str: String) = buildAnnotatedString {
    val theme = EditorTheme.get()
    withStyle(theme.code.simple) {
        append(str)
        addStyle(theme.code.punctuation, str, ":")
        addStyle(theme.code.punctuation, str, "=")
        addStyle(theme.code.punctuation, str, "\"")
        addStyle(theme.code.punctuation, str, "[")
        addStyle(theme.code.punctuation, str, "]")
        addStyle(theme.code.punctuation, str, "{")
        addStyle(theme.code.punctuation, str, "}")
        addStyle(theme.code.punctuation, str, "(")
        addStyle(theme.code.punctuation, str, ")")
        addStyle(theme.code.punctuation, str, ",")
        addStyle(theme.code.keyword, str, "fun ")
        addStyle(theme.code.keyword, str, "---")
        addStyle(theme.code.keyword, str, "val ")
        addStyle(theme.code.keyword, str, "var ")
        addStyle(theme.code.keyword, str, "private ")
        addStyle(theme.code.keyword, str, "internal ")
        addStyle(theme.code.keyword, str, "for ")
        addStyle(theme.code.keyword, str, "expect ")
        addStyle(theme.code.keyword, str, "actual ")
        addStyle(theme.code.keyword, str, "import ")
        addStyle(theme.code.keyword, str, "package ")
        addStyle(theme.code.value, str, "true")
        addStyle(theme.code.value, str, "false")
        addStyle(theme.code.value, str, Regex("[0-9]*"))
        addStyle(theme.code.header, str, Regex("\\n#.*"))
        addStyle(theme.code.table, str, Regex("\\n\\|.*"))
        addStyle(theme.code.quote, str, Regex("\\n>.*"))
        addStyle(theme.code.quote, str, Regex("\\n-.*"))
        addStyle(theme.code.annotation, str, Regex("^@[a-zA-Z_]*"))
        addStyle(theme.code.comment, str, Regex("^\\s*//.*"))
    }
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: String) {
    addStyle(style, text, Regex.fromLiteral(regexp))
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
    for (result in regexp.findAll(text)) {
        addStyle(style, result.range.first, result.range.last + 1)
    }
}
