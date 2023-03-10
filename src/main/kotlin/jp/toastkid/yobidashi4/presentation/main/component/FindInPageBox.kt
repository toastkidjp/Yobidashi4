package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel

@Composable
internal fun FindInPageBox(viewModel: MainViewModel) {
    val focusRequester = remember { FocusRequester() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
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
                label = { Text("Please would you input web search keyword?", color = MaterialTheme.colors.secondary) },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
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
                    label = { Text("Replacement") },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
                )
            }

            Text("↑", modifier = Modifier
                .padding(start = 8.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { viewModel.findUp() }
                .padding(8.dp)
            )

            Text("↓", modifier = Modifier
                .padding(start = 8.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { viewModel.findDown() }
                .padding(8.dp)
            )

            Button(onClick = { viewModel.replaceAll() }) {
                Text("All")
            }

            LaunchedEffect(viewModel.openFind()) {
                focusRequester.requestFocus()
            }
        }
    }
}