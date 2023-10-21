package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.URL
import java.util.regex.Pattern
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.markdown.HorizontalRule
import jp.toastkid.yobidashi4.domain.model.markdown.ListLine
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.markdown.TextBlock
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.presentation.component.VerticalDivider
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrder
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkBehaviorService
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkGenerator
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.slideshow.view.CodeBlockView
import jp.toastkid.yobidashi4.presentation.slideshow.view.TableLineView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MarkdownTabView(tab: MarkdownPreviewTab, modifier: Modifier) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.5f),
        modifier = modifier.focusRequester(focusRequester)
    ) {
        MarkdownContent(tab.markdown(), scrollState, modifier)

        LaunchedEffect(tab) {
            scrollState.scrollTo(tab.scrollPosition())

            focusRequester.requestFocus()
        }

        DisposableEffect(tab) {
            onDispose {
                val mainViewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm

                val indexOf = mainViewModel.tabs.indexOf(tab)
                if (indexOf == -1) {
                    return@onDispose
                }
                tab.setScrollPosition(scrollState.value)
                mainViewModel.tabs.set(indexOf, tab)
            }
        }
    }
}

@Composable
fun MarkdownContent(
    content: Markdown,
    scrollState: ScrollState,
    modifier: Modifier
) {
    val mainViewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }
    val coroutineScope = rememberCoroutineScope()
    val scrollAction = remember { KeyboardScrollAction(scrollState) }

    Box(
        modifier = modifier.onKeyEvent {
            val scrollActionConsumed = scrollAction(coroutineScope, it.key, it.isCtrlPressed)
            if (scrollActionConsumed) {
                return@onKeyEvent true
            }

            if (it.type != KeyEventType.KeyUp) {
                return@onKeyEvent false
            }

            val selectedText = mainViewModel.selectedText()
            if (!selectedText.isNullOrBlank() && it.isCtrlPressed && it.isShiftPressed && it.key == Key.O) {
                mainViewModel
                    .openUrl("https://search.yahoo.co.jp/search?p=$selectedText", false)
                return@onKeyEvent true
            }

            false
        }
    ) {
        SelectionContainer {
            Column(modifier = Modifier.verticalScroll(scrollState).padding(8.dp)) {
                content.lines().forEach { line ->
                    when (line) {
                        is TextBlock -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (line.quote) {
                                    VerticalDivider(2.dp, Color(0x88CCAAFF), Modifier.padding(start = 4.dp, end = 8.dp))
                                }
                                TextLineView(
                                    line.text,
                                    TextStyle(
                                        color = if (line.quote) Color(0xFFCCAAFF) else MaterialTheme.colors.onSurface,
                                        fontSize = line.fontSize().sp,
                                        fontWeight = if (line.level != -1) FontWeight.Bold else FontWeight.Normal,
                                    ),
                                    Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        is ListLine -> Column {
                            line.list.forEachIndexed { index, it ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    when {
                                        line.ordered -> DisableSelection {
                                            Text("${index + 1}. ", fontSize = 14.sp)
                                        }

                                        line.taskList -> Checkbox(
                                            checked = it.startsWith("[x]"),
                                            enabled = false,
                                            onCheckedChange = {},
                                            modifier = Modifier.size(32.dp)
                                        )

                                        else -> DisableSelection {
                                            Text("・ ", fontSize = 14.sp)
                                        }
                                    }
                                    TextLineView(
                                        if (line.taskList) it.substring(it.indexOf("] ") + 1) else it,
                                        TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 14.sp),
                                        Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        is ImageLine -> Image(
                            ImageIO.read(URL(line.source)).toComposeImageBitmap(),
                            contentDescription = line.source,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        is HorizontalRule -> Divider(modifier = Modifier.padding(vertical = 8.dp))
                        is TableLine -> TableLineView(line, 16.sp, Modifier.padding(bottom = 8.dp))
                        is CodeBlockLine -> CodeBlockView(line, 16.sp, Modifier.padding(bottom = 8.dp))
                        else -> Unit
                    }
                }
            }
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
    val finderTarget = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm.finderFlow() }
    val annotatedString = annotate(
        LinkGenerator().invoke(text), finderTarget.collectAsState(
            FindOrder.EMPTY).value.target
    )
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

private fun annotate(text: String, finderTarget: String?) = buildAnnotatedString {
    var lastIndex = 0
    val matcher = internalLinkPattern.matcher(text)
    while (matcher.find()) {
        val title = matcher.group(1)
        val url = matcher.group(2)
        val startIndex = matcher.start()
        val endIndex = matcher.end()

        val extracted = text.substring(lastIndex, startIndex)
        if (extracted.isNotEmpty()) {
            append(extracted)
        }

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

    if (lastIndex >= text.length) {
        return@buildAnnotatedString
    }

    append(text.substring(lastIndex, text.length))

    if (text.contains("~~")) {
        val m = Pattern.compile("~~(.+?)~~", Pattern.DOTALL).matcher(text)
        while (m.find()) {
            addStyle(
                SpanStyle(textDecoration = TextDecoration.LineThrough),
                m.start(),
                m.end()
            )
        }
    }

    if (finderTarget.isNullOrBlank().not()) {
        val finderMatcher = Pattern.compile(finderTarget).matcher(text)
        while (finderMatcher.find()) {
            addStyle(
                style = SpanStyle(
                    color = Color(0xFF00AAFF),
                    background = Color(0xFFFFFFFF)
                ), start = finderMatcher.start(), end = finderMatcher.end()
            )
        }
    }
}

private val internalLinkPattern =
    Pattern.compile("\\[(.+?)\\]\\((.+?)\\)", Pattern.DOTALL)
