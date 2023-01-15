package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebTabView(tab: WebTab) {
    val viewModel = remember {
        object : KoinComponent {
            val webTabViewModel: WebTabViewModel by inject()
        }.webTabViewModel
    }

    if (tab.isReadableUrl().not()) {
        return
    }

    Column {
        if (viewModel.openFind()) {
            FinderView(viewModel, tab.id())
        }

        viewModel.view(tab.id(), tab.url())
    }
}

@Composable
private fun FinderView(
    viewModel: WebTabViewModel,
    tabId: String
) {
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
                    viewModel.onFindInputChange(tabId, it)
                },
                maxLines = 1,
                label = { Text("Please would you input web search keyword?") },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.focusRequester(focusRequester)
            )

            Text("↑", modifier = Modifier
                .padding(start = 8.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { viewModel.findUp(tabId) }
                .padding(8.dp)
            )

            Text("↓", modifier = Modifier
                .padding(start = 8.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { viewModel.findDown(tabId) }
                .padding(8.dp)
            )

            LaunchedEffect(viewModel.openFind()) {
                focusRequester.requestFocus()
            }
        }
    }
}