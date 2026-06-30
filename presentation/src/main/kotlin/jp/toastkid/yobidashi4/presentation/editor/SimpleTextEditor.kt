/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.component.collectCommittedInput
import jp.toastkid.yobidashi4.presentation.editor.data.LineNumber
import jp.toastkid.yobidashi4.presentation.editor.viewmodel.TextEditorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalTextApi::class, FlowPreview::class)
@Composable
fun SimpleTextEditor(
    tab: EditorTab,
    setStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember(tab.path) { TextEditorViewModel() }
    val coroutineScope = rememberCoroutineScope()

    Box {
        BasicTextField(
            state = viewModel.content(),
            onTextLayout = {
                val multiParagraph = it.invoke()?.multiParagraph ?: return@BasicTextField
                viewModel.setMultiParagraph(multiParagraph)
            },
            inputTransformation = viewModel.inputTransformation(),
            outputTransformation = viewModel.visualTransformation(),
            decorator = {
                Row(modifier = Modifier
                    .drawBehind {
                        val currentLineOffset = viewModel.currentLineOffset()
                        drawRect(
                            viewModel.currentLineHighlightColor(),
                            topLeft = currentLineOffset,
                            size = viewModel.getHighlightSize()
                        )
                    }
                ) {
                    if (viewModel.showLineNumber()) {
                        LineNumber(
                            viewModel::lineNumbers,
                            viewModel.lineNumberScrollState(),
                            viewModel.fontSize(),
                            viewModel.lineHeight(),
                            viewModel::onClickLineNumber
                        )
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))

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
                .onPreviewKeyEvent(viewModel::onPreviewKeyEvent)
                .onKeyEvent(viewModel::onKeyEvent)
                .semantics { contentDescription = "Editor input area" }
        )

        val focusManager = LocalFocusManager.current

        DisposableEffect(tab.path) {
            viewModel.launchTab(tab)
            viewModel.initialScroll(coroutineScope)

            setStatus(viewModel.makeCharacterCountMessage(tab.getContent().length))

            onDispose {
                focusManager.clearFocus(true)
                viewModel.dispose()
            }
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
        snapshotFlow(viewModel::calculateConversionTrigger)
            .distinctUntilChanged()
            .filter { it.inComposition }
            .debounce(100)
            .flowOn(Dispatchers.Default)
            .collect { _ ->
                viewModel.parseContent()
            }
    }

    LaunchedEffect(tab.path) {
        coroutineScope.launch {
            viewModel.scrollEventFlow()
                .collect {
                    viewModel.verticalScrollState().scrollBy(it)
                }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun LineNumber(
    lineNumbers: () -> List<LineNumber>,
    scrollState: ScrollState,
    fontSize: Int,
    lineHeight: Float,
    onClickLineNumber: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(start = 8.dp)
            .wrapContentSize(unbounded = true)
    ) {
        lineNumbers().forEach { (lineNumber, lineNumberText) ->
            LineNumberBox(onClickLineNumber, lineNumber, lineNumberText, fontSize, lineHeight)
        }
    }
}

@Composable
@OptIn(ExperimentalTextApi::class)
private fun LineNumberBox(
    onClickLineNumber: (Int) -> Unit,
    lineNumber: Int,
    lineNumberText: String,
    fontSize: Int,
    lineHeight: Float
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.clickable {
            onClickLineNumber(lineNumber)
        }
            .semantics { contentDescription = "Line number $lineNumberText" }
    ) {
        Text(
            lineNumberText,
            fontSize = fontSize.sp,
            fontFamily = FontFamily("MS Gothic"),
            textAlign = TextAlign.End,
            style = TextStyle(
                lineHeight = lineHeight.em,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            )
        )
    }
}
