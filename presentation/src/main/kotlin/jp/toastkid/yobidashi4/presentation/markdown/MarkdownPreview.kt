package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.markdown.HorizontalRule
import jp.toastkid.yobidashi4.domain.model.markdown.ListLine
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.markdown.TextBlock
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.presentation.component.VerticalDivider
import jp.toastkid.yobidashi4.presentation.slideshow.view.CodeBlockView
import jp.toastkid.yobidashi4.presentation.slideshow.view.TableLineView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MarkdownPreview(
    content: Markdown,
    scrollState: ScrollState,
    modifier: Modifier
) {
    val viewModel = remember { MarkdownPreviewViewModel(scrollState) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.onKeyEvent {
            viewModel.onKeyEvent(coroutineScope, it)
        }
    ) {
        SelectionContainer {
            Column(modifier = Modifier.verticalScroll(scrollState).padding(8.dp)) {
                content.lines().forEach { line ->
                    when (line) {
                        is TextBlock -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (line.quote) {
                                    VerticalDivider(2.dp, Color(0x88CCAAFF), Modifier.padding(start = 4.dp, end = 8.dp).height(36.dp))
                                }
                                TextLineView(
                                    line.text,
                                    TextStyle(
                                        color = if (line.quote) Color(0xFFCCAAFF) else MaterialTheme.colors.onSurface,
                                        fontSize = line.fontSize().sp,
                                        fontWeight = viewModel.makeFontWeight(line.level),
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
                                        viewModel.extractText(it, line.taskList),
                                        TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 14.sp),
                                        Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        is ImageLine -> {
                            val read = viewModel.loadBitmap(line.source)
                            if (read != null) {
                                Image(
                                    read,
                                    contentDescription = line.source,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

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
    val viewModel = remember { TextLineViewModel() }

    ClickableText(
        viewModel.annotatedString(),
        style = textStyle,
        onClick = {},
        modifier = modifier.onPointerEvent(PointerEventType.Release) {
            viewModel.onPointerReleased(it)
        },
        onTextLayout = { layoutResult ->
            viewModel.putLayoutResult(layoutResult)
        }
    )

    LaunchedEffect(text) {
        withContext(Dispatchers.IO) {
            viewModel.launch(text)
        }
    }
}
