package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
internal fun FindInPageBox() {
    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }
    val focusRequester = remember { FocusRequester() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth().onKeyEvent {
            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                viewModel.switchFind()
                return@onKeyEvent true
            }
            return@onKeyEvent false
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("x", modifier = Modifier
                .padding(start = 4.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { viewModel.switchFind() }
                .padding(8.dp)
            )

            TextField(
                viewModel.inputValue(),
                onValueChange = {
                    viewModel.onFindInputChange(it)
                },
                maxLines = 1,
                label = {
                    Text(
                        "Please would you input web search keyword?",
                        color = MaterialTheme.colors.secondary
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.secondary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.focusRequester(focusRequester)
            )

            if (viewModel.currentTab() is EditorTab) {
                TextField(
                    viewModel.replaceInputValue(),
                    onValueChange = {
                        viewModel.onReplaceInputChange(it)
                    },
                    maxLines = 1,
                    label = { Text("Replacement", color = MaterialTheme.colors.secondary) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        cursorColor = MaterialTheme.colors.secondary
                        )
                )
            }

            Checkbox(
                viewModel.caseSensitive(),
                onCheckedChange = {
                    viewModel.switchCaseSensitive()
                }
            )

            Text("Case sensitive")

            Text(
                "↑",
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                    .clickable { viewModel.findUp() }
                    .padding(8.dp)
            )

            Text(
                "↓",
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                    .clickable { viewModel.findDown() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(40.dp))

            Button(onClick = { viewModel.replaceAll() }) {
                Text("All")
            }

            if (viewModel.findStatus().isNotEmpty()) {
                Text(viewModel.findStatus(), modifier = Modifier.padding(horizontal = 8.dp))
            }

            LaunchedEffect(viewModel.openFind()) {
                focusRequester.requestFocus()
            }
        }
    }
}