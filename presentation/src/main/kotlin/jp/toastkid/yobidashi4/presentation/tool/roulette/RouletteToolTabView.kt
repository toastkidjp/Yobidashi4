package jp.toastkid.yobidashi4.presentation.tool.roulette

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.MultiLineTextField

@Composable
fun RouletteToolTabView() {
    val viewModel = remember { RouletteToolTabViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            if (viewModel.result().isNotBlank()) {
                Row(modifier = Modifier.padding(8.dp)) {
                    SelectionContainer {
                        Text(viewModel.result())
                    }

                    Icon(
                        painterResource("images/icon/ic_clipboard.xml"),
                        "Clip result",
                        Modifier
                            .clickable(onClick = viewModel::clipResult)
                            .padding(start = 8.dp)
                    )
                }
            }

            Button(
                viewModel::roulette,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Start")
            }

            MultiLineTextField(
                viewModel.input(),
                "Base file name",
                Int.MAX_VALUE,
                viewModel::onValueChange,
                viewModel::clearInput
            )
        }
    }
}