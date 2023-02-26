package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FileRenameToolView() {
    val viewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm
    val paths = remember { mutableStateListOf<Path>() }
    val firstInput = remember { mutableStateOf(TextFieldValue("img_")) }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        LazyColumn {
            item {
                TextField(
                    firstInput.value,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Base file name") },
                    onValueChange = {
                        firstInput.value = TextFieldValue(it.text, it.selection, it.composition)
                    },
                    trailingIcon = {
                        Icon(
                            painterResource("images/icon/ic_clear_form.xml"),
                            contentDescription = "Clear input.",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                firstInput.value = TextFieldValue()
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter
                            && firstInput.value.composition == null
                            && firstInput.value.text.isNotBlank()
                        ) {
                            rename(paths, firstInput.value.text)
                        }
                        true
                    }
                )
            }

            item {
                Row {
                    Button(onClick = {
                        paths.clear()
                    },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Clear files")
                    }
                    Button(onClick = {
                        rename(paths, firstInput.value.text)
                    },
                        modifier = Modifier.padding(8.dp)
                        ) {
                        Text("Rename")
                    }
                }
            }

            items(paths) { path ->
                Text(path.fileName.toString())
            }
        }
    }

    LaunchedEffect(viewModel.droppedPathFlow()) {
        withContext(Dispatchers.IO) {
            viewModel.droppedPathFlow().collect {
                paths.add(it)
            }
        }
    }
}

private fun rename(
    paths: SnapshotStateList<Path>,
    baseName: String
) {
    paths.forEachIndexed { i, p ->
        Files.copy(p, p.resolveSibling("${baseName}_${i + 1}.${p.extension}"))
    }
}
