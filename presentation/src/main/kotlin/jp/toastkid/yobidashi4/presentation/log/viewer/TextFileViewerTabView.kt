package jp.toastkid.yobidashi4.presentation.log.viewer

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab

@Composable
internal fun TextFileViewerTabView(tab: TextFileViewerTab) {
    val viewModel = remember { TextFileViewerTabViewModel() }

    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.onKeyEvent {
            viewModel.keyboardScrollAction(coroutineScope, it.key, it.isCtrlPressed)
        }
            .focusRequester(viewModel.focusRequester())
    ) {
        Box() {
            SelectionContainer {
                LazyColumn(state = viewModel.listState()) {
                    itemsIndexed(viewModel.textState(), { index, line -> "$index${line.hashCode()}" }) { index, line ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DisableSelection {
                                Text(
                                    viewModel.lineNumber(index),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            Text(
                                line,
                                modifier = Modifier.padding(end = 8.dp).weight(1f)
                            )
                        }
                    }
                }
            }
            VerticalScrollbar(adapter = rememberScrollbarAdapter(viewModel.listState()), modifier = Modifier.fillMaxHeight().align(
                Alignment.CenterEnd))

            LaunchedEffect(tab.path()) {
                viewModel.launch(tab.path())
            }
        }
    }
}