package jp.toastkid.yobidashi4.presentation.tool.roulette

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
                            .clickable {
                                viewModel.clipResult()
                            }
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

            TextField(
                viewModel.input(),
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
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.roulette()
                    }
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}