package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField

@Composable
fun FileRenameToolView() {
    val viewModel = remember { FileRenameToolViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            SingleLineTextField(
                viewModel.input(),
                "Base file name",
                viewModel::onValueChange,
                viewModel::clearInput,
                modifier = Modifier.onKeyEvent(viewModel::onKeyEvent)
            )

            Row {
                Button(
                    onClick = viewModel::clearPaths,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Clear files")
                }
                Button(
                    onClick = viewModel::rename,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Rename")
                }
            }

            Box {
                LazyColumn(state = viewModel.listState()) {
                    items(viewModel.items()) { path ->
                        Text(path.fileName.toString())
                    }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(viewModel.listState()),
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
                )
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.collectDroppedPaths()

        onDispose {
            viewModel.dispose()
        }
    }
}
