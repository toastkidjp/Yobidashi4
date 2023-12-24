package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FileRenameToolView() {
    val viewModel = remember { FileRenameToolViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            TextField(
                viewModel.input(),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                label = { Text("Base file name") },
                onValueChange = {
                    viewModel.onValueChange(it)
                },
                trailingIcon = {
                    Icon(
                        painterResource("images/icon/ic_clear_form.xml"),
                        contentDescription = "Clear input.",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.clickable {
                            viewModel.clearInput()
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.onKeyEvent {
                    viewModel.onKeyEvent(it)
                }
            )

            Row {
                Button(
                    onClick = {
                        viewModel.clearPaths()
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Clear files")
                }
                Button(
                    onClick = {
                        viewModel.rename()
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Rename")
                }
            }

            Box {
                val verticalScrollState = rememberLazyListState()
                LazyColumn(state = verticalScrollState) {
                    items(viewModel.items()) { path ->
                        Text(path.fileName.toString())
                    }
                }
                VerticalScrollbar(adapter = rememberScrollbarAdapter(verticalScrollState), modifier = Modifier.fillMaxHeight().align(
                    Alignment.CenterEnd))
            }
        }
    }

    LaunchedEffect(viewModel.launchedEffectKey()) {
        withContext(Dispatchers.IO) {
            viewModel.collectDroppedPaths()
        }
    }
}
