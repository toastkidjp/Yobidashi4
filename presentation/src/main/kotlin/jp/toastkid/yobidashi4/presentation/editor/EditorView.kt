package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.text.Regex.Companion.fromLiteral
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope

@Composable
private fun Lines(lines: Editor.Lines) = with(LocalDensity.current) {
    val maxNum = remember(lines.lineNumberDigitCount) {
        (1..lines.lineNumberDigitCount).joinToString(separator = "") { "9" }
    }

    Box(Modifier.fillMaxSize()) {
        val scrollState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            items(lines.size) { index ->
                Box(Modifier.height(16.sp.toDp() * 1.6f)) {
                    Line(Modifier.align(Alignment.CenterStart), maxNum, lines[index])
                }
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
        )
        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
        )
    }
}

// Поддержка русского языка
// دعم اللغة العربية
// 中文支持
@Composable
private fun Line(modifier: Modifier, maxNum: String, line: Editor.Line) {
    Row(modifier = modifier) {
        DisableSelection {
            Box {
                LineNumber(maxNum, Modifier.alpha(0f))
                LineNumber(line.number.toString(), Modifier.align(Alignment.CenterEnd))
            }
        }
        LineContent(
            line.content,
            modifier = Modifier
                .weight(1f)
                //.withoutWidthConstraints()
                .padding(start = 28.dp, end = 12.dp)//,
            //settings = settings
        )
    }
}

@Composable
private fun LineNumber(number: String, modifier: Modifier) = Text(
    text = number,
    fontSize = 16.sp,
    //fontFamily = Fonts.jetbrainsMono(),
    color = LocalContentColor.current,//.copy(alpha = 0.30f),
    modifier = modifier.padding(start = 12.dp)
)

@Composable
private fun LineContent(content: Editor.Content, modifier: Modifier) =
    BasicTextField(
        value = content.value.value,
        onValueChange = {
            content.value.value = it
        },
        visualTransformation = {
            val t = codeString(content.value.value)
            TransformedText(t, OffsetMapping.Identity)
        },
        singleLine = true,
        modifier = modifier
    )

private fun codeString(str: String) = buildAnnotatedString {
    val theme = EditorTheme.get()
    withStyle(theme.code.simple) {
        val strFormatted = str.replace("\t", "    ")
        append(strFormatted)
        addStyle(theme.code.punctuation, strFormatted, ":")
        addStyle(theme.code.punctuation, strFormatted, "=")
        addStyle(theme.code.punctuation, strFormatted, "\"")
        addStyle(theme.code.punctuation, strFormatted, "[")
        addStyle(theme.code.punctuation, strFormatted, "]")
        addStyle(theme.code.punctuation, strFormatted, "{")
        addStyle(theme.code.punctuation, strFormatted, "}")
        addStyle(theme.code.punctuation, strFormatted, "(")
        addStyle(theme.code.punctuation, strFormatted, ")")
        addStyle(theme.code.punctuation, strFormatted, ",")
        addStyle(theme.code.keyword, strFormatted, "fun ")
        addStyle(theme.code.keyword, strFormatted, "---")
        addStyle(theme.code.keyword, strFormatted, "val ")
        addStyle(theme.code.keyword, strFormatted, "var ")
        addStyle(theme.code.keyword, strFormatted, "private ")
        addStyle(theme.code.keyword, strFormatted, "internal ")
        addStyle(theme.code.keyword, strFormatted, "for ")
        addStyle(theme.code.keyword, strFormatted, "expect ")
        addStyle(theme.code.keyword, strFormatted, "actual ")
        addStyle(theme.code.keyword, strFormatted, "import ")
        addStyle(theme.code.keyword, strFormatted, "package ")
        addStyle(theme.code.value, strFormatted, "true")
        addStyle(theme.code.value, strFormatted, "false")
        addStyle(theme.code.value, str, Regex("[0-9]*"))
        addStyle(theme.code.header, str, Regex("^#.*"))
        addStyle(theme.code.table, str, Regex("^\\|.*"))
        addStyle(theme.code.quote, str, Regex("^>.*"))
        addStyle(theme.code.quote, str, Regex("^-.*"))
        addStyle(theme.code.value, strFormatted, Regex("[0-9]*"))
        addStyle(theme.code.annotation, strFormatted, Regex("^@[a-zA-Z_]*"))
        addStyle(theme.code.comment, strFormatted, Regex("^\\s*//.*"))
    }
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: String) {
    addStyle(style, text, fromLiteral(regexp))
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
    for (result in regexp.findAll(text)) {
        addStyle(style, result.range.first, result.range.last + 1)
    }
}

private val loadingKey = Any()

@Composable
fun <T : Any> loadableScoped(load: CoroutineScope.() -> T): MutableState<T?> {
    val state: MutableState<T?> = remember { mutableStateOf(null) }
    LaunchedEffect(loadingKey) {
        try {
            state.value = load()
        } catch (e: CancellationException) {
            // ignore
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return state
}