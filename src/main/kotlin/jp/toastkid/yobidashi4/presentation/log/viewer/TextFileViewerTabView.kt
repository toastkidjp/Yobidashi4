package jp.toastkid.yobidashi4.presentation.log.viewer

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun TextFileViewerTabView(tab: TextFileViewerTab) {
    val scrollState = rememberLazyListState()
    val textState = remember { mutableStateListOf<String>() }

    LaunchedEffect(tab.path()) {
        if (Files.exists(tab.path()).not()) {
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            Files.readAllLines(tab.path()).forEach { textState.add(it) }
        }
    }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box() {
            SelectionContainer {
                LazyColumn(state = scrollState) {
                    itemsIndexed(textState) { index, line ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DisableSelection {
                                val length = textState.size.toString().length
                                val lineNumberCount = index + 1
                                val fillCount = length - lineNumberCount.toString().length
                                val lineNumberText = with(StringBuilder()) {
                                    repeat(fillCount) {
                                        append(" ")
                                    }
                                    append(lineNumberCount)
                                }.toString()
                                Text(
                                    "$lineNumberText",
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
            VerticalScrollbar(adapter = rememberScrollbarAdapter(scrollState), modifier = Modifier.fillMaxHeight().align(
                Alignment.CenterEnd))
        }
    }
}