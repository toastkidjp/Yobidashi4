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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
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
import java.io.IOException
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    val mainViewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }

    val textColor = remember {
        if (mainViewModel.darkMode())
            Color(0xFFF0F0F0)
        else
            Color(0xFF000B00)
    }

    Box {
        var last:TransformedText? = null
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
                val t = codeString(content.value.text, textColor)
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
            cursorBrush = SolidColor(MaterialTheme.colors.secondary),
            modifier = modifier.focusRequester(focusRequester)
                .onKeyEvent {
                    altPressed = it.isAltPressed
                    if (it.type != KeyEventType.KeyUp) {
                        return@onKeyEvent false
                    }
                    when {
                        it.isCtrlPressed && it.key == Key.S -> {
                            try {
                                CoroutineScope(Dispatchers.IO).launch {
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
            mainViewModel.updateEditorContent(tab.path, currentText, content.value.selection.start, false)
        }
    }
}

private fun codeString(str: String, textColor: Color) = buildAnnotatedString {
    val theme = EditorTheme.get()
    withStyle(SpanStyle(textColor)) {
        append(str)
        addStyle(theme.code.keyword, str, "---")
        addStyle(theme.code.value, str, Regex("[0-9]*"))
        addStyle(theme.code.header, str, Regex("\\n#.*"))
        addStyle(theme.code.table, str, Regex("\\n\\|.*"))
        addStyle(theme.code.quote, str, Regex("\\n>.*"))
        addStyle(theme.code.quote, str, Regex("\\n-.*"))
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
