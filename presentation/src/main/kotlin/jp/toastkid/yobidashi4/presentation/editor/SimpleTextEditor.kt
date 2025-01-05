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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.viewmodel.TextEditorViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalTextApi::class)
@Composable
fun SimpleTextEditor(
    tab: EditorTab,
    setStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { TextEditorViewModel() }
    val coroutineScope = rememberCoroutineScope()

    Box {
        BasicTextField(
            value = viewModel.content(),
            onValueChange = {
                viewModel.onValueChange(it)
                setStatus(viewModel.makeCharacterCountMessage(it.text.length))
            },
            onTextLayout = {
                viewModel.setMultiParagraph(it.multiParagraph)
            },
            visualTransformation = viewModel.visualTransformation(),
            decorationBox = {
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
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily("MS Gothic"),
                                    textAlign = TextAlign.End,
                                    lineHeight = viewModel.getLineHeight(lineNumber)
                                )
                            }
                        }
                    }
                    it()
                }
            },
            textStyle = TextStyle(
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
                fontFamily = FontFamily("MS Gothic"),
                lineHeight = 1.5.em
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

    LaunchedEffect(viewModel.verticalScrollState().offset) {
        viewModel.adjustLineNumberState()
    }
}