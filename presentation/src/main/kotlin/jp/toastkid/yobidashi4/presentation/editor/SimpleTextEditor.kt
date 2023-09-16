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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
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
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.readText
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SimpleTextEditor(
    path: Path,
    modifier: Modifier = Modifier
) {
    val content = remember { mutableStateOf(TextFieldValue(path.readText())) }
    val verticalScrollState = rememberTextFieldVerticalScrollState()
    val lineNumberScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    val textColor = remember {
        if (object : KoinComponent { val vm : MainViewModel by inject() }.vm.darkMode())
            Color(0xFFF0F0F0)
        else
            Color(0xFF000B00)
    }

    Box {
        BasicTextField(
            value = content.value,
            onValueChange = {
                content.value = it
            },
            visualTransformation = {
                val start = System.currentTimeMillis()
                val t = codeString(content.value.text, textColor)
                println("convert ${t.length} time ${System.currentTimeMillis() - start} [ms]")
                TransformedText(t, OffsetMapping.Identity)
            },
            decorationBox = {
                Row {
                    Column(
                        modifier = Modifier
                            .verticalScroll(lineNumberScrollState)
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
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = FontFamily.Monospace, lineHeight = 1.5.em),
            scrollState = verticalScrollState,
            modifier = modifier.focusRequester(focusRequester)
        )

        VerticalScrollbar(adapter = rememberScrollbarAdapter(verticalScrollState), modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd))
        HorizontalScrollbar(adapter = rememberScrollbarAdapter(horizontalScrollState), modifier = Modifier.fillMaxWidth().align(
            Alignment.BottomCenter))
    }

    LaunchedEffect(verticalScrollState.offset) {
        lineNumberScrollState.scrollTo(verticalScrollState.offset.toInt())
    }

    LaunchedEffect(path) {
        focusRequester.requestFocus()
    }
}

private fun codeString(str: String, textColor: Color) = buildAnnotatedString {
    val theme = EditorTheme.get()
    withStyle(SpanStyle(textColor)) {
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
        addStyle(theme.code.keyword, str, "---")
        addStyle(theme.code.keyword, str, "fun ")
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
