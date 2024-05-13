package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.InputTextField
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField

@Composable
internal fun FindInPageBox() {
    val viewModel = remember { FindInPageBoxViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth().onKeyEvent(viewModel::onKeyEvent)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("x", modifier = Modifier
                .padding(start = 4.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { viewModel.switchFind() }
                .padding(8.dp)
            )

            InputTextField(
                viewModel.inputValue(),
                "Please would you input web search keyword?",
                {
                    viewModel.onFindInputChange(it)
                },
                {  },
                { viewModel.onFindInputChange(TextFieldValue()) },
                viewModel.shouldShowInputHistory(),
                viewModel.inputHistories(),
                viewModel::onClickInputHistory,
                { viewModel.onClickDelete(it) },
                { viewModel.onClickClear() },
                { viewModel.onFocusChanged(it) },
                modifier = Modifier.focusRequester(viewModel.focusRequester())
                    .semantics { contentDescription = "Find input" }
            )

            if (viewModel.useReplace()) {
                SingleLineTextField(
                    viewModel.replaceInputValue(),
                    "Replacement",
                    viewModel::onReplaceInputChange,
                )
            }

            Checkbox(
                viewModel.caseSensitive(),
                onCheckedChange = {
                    viewModel.switchCaseSensitive()
                },
                modifier = Modifier.semantics { contentDescription = "Case sensitive checkbox" }
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

            Button(onClick = viewModel::replaceAll) {
                Text("All")
            }

            if (viewModel.findStatus().isNotEmpty()) {
                Text(viewModel.findStatus(), modifier = Modifier.padding(horizontal = 8.dp))
            }

            LaunchedEffect(viewModel.openFind()) {
                viewModel.launch()
            }
        }
    }
}