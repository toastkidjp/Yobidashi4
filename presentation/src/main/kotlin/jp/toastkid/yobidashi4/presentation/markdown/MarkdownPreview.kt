package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.regex.Pattern
import jp.toastkid.yobidashi4.domain.model.markdown.HorizontalRule
import jp.toastkid.yobidashi4.domain.model.markdown.ListLine
import jp.toastkid.yobidashi4.domain.model.markdown.TextBlock
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkBehaviorService
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkGenerator
import jp.toastkid.yobidashi4.presentation.slideshow.view.CodeBlockView
import jp.toastkid.yobidashi4.presentation.slideshow.view.TableLineView
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MarkdownPreview(tab: EditorTab, modifier: Modifier) {
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Box(modifier = modifier.onKeyEvent {
        when (it.key) {
            Key.DirectionUp -> {
                coroutineScope.launch {
                    scrollState.animateScrollBy(-50f)
                }
                return@onKeyEvent true
            }
            Key.DirectionDown -> {
                coroutineScope.launch {
                    scrollState.animateScrollBy(50f)
                }
                return@onKeyEvent true
            }
            Key.PageUp -> {
                coroutineScope.launch {
                    scrollState.animateScrollBy(-300f)
                }
                return@onKeyEvent true
            }
            Key.PageDown -> {
                coroutineScope.launch {
                    scrollState.animateScrollBy(300f)
                }
                return@onKeyEvent true
            }
        }
        return@onKeyEvent false
    }.focusRequester(focusRequester).focusable(true).verticalScroll(scrollState)) {
        val content = MarkdownParser().invoke(tab.path)

        SelectionContainer {
            Column(modifier = Modifier.padding(8.dp)) {
                content.lines().forEach { line ->
                    when (line) {
                        is TextBlock -> {
                            TextLineView(
                                line.text,
                                TextStyle(
                                    fontSize = line.fontSize().sp,
                                    fontWeight = if (line.level != -1) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (line.quote) FontStyle.Italic else FontStyle.Normal,
                                ),
                                Modifier.padding(bottom = 8.dp)
                            )
                        }
                        is ListLine -> Column {
                            line.list.forEachIndexed { index, it ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    when {
                                        line.ordered -> DisableSelection {
                                            Text("${index + 1}. ", fontSize = 14.sp)
                                        }
                                        line.taskList -> Checkbox(checked = it.startsWith("[x]"), enabled = false, onCheckedChange = {}, modifier = Modifier.size(32.dp))
                                        else -> DisableSelection {
                                            Text("・ ", fontSize = 14.sp)
                                        }
                                    }
                                    TextLineView(
                                        if (line.taskList) it.substring(it.indexOf("] ") + 1) else it,
                                        TextStyle(fontSize = 14.sp),
                                        Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }
                        is HorizontalRule -> Divider(modifier = Modifier.padding(vertical = 8.dp))
                        is TableLine -> TableLineView(line, 16.sp, Modifier.padding(bottom = 8.dp))
                        is CodeBlockLine -> CodeBlockView(line, 16.sp)
                        else -> Unit
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState), modifier = Modifier.fillMaxHeight().align(
                Alignment.CenterEnd
            )
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TextLineView(text: String, textStyle: TextStyle, modifier: Modifier) {
    val lastLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val annotatedString = annotate(LinkGenerator().invoke(text), MaterialTheme.colors.onSurface)
    ClickableText(
        annotatedString,
        style = textStyle,
        onClick = {},
        modifier = modifier.onPointerEvent(PointerEventType.Release) {
            val offset = lastLayoutResult.value?.getOffsetForPosition(it.changes.first().position) ?: 0
            annotatedString
                .getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()
                ?.let { annotation ->
                    if (annotation.tag == "URL") {
                        LinkBehaviorService().invoke(annotation.item)
                    }
                }
        },
        onTextLayout = { layoutResult ->
            lastLayoutResult.value = layoutResult
        }
    )
}

private fun annotate(text: String,  normalTextColor: Color) = buildAnnotatedString {
    var lastIndex = 0
    val matcher = internalLinkPattern.matcher(text)
    while (matcher.find()) {
        val title = matcher.group(1)
        val url = matcher.group(2)
        val startIndex = matcher.start()
        val endIndex = matcher.end()
        append(text.substring(lastIndex, startIndex))
        addStyle(
            style = SpanStyle(
                color = normalTextColor,
            ), start = lastIndex, end = startIndex
        )

        val annotateStart = length
        append(title)
        addStyle(
            style = SpanStyle(
                color = Color(0xff64B5F6),
                textDecoration = TextDecoration.Underline
            ), start = annotateStart, end = annotateStart + title.length
        )
        // attach a string annotation that stores a URL to the text "link"
        addStringAnnotation(
            tag = "URL",
            annotation = url,
            start = annotateStart,
            end = annotateStart + title.length
        )
        lastIndex = endIndex
    }
    val finalTextStart = length
    append(text.substring(lastIndex, text.length))
    addStyle(
        style = SpanStyle(
            color = normalTextColor,
        ), start = finalTextStart, end = length
    )
}

private val internalLinkPattern =
    Pattern.compile("\\[(.+?)\\]\\((.+?)\\)", Pattern.DOTALL)
