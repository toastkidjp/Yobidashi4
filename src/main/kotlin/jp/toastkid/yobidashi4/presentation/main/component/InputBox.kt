package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun InputBox(viewModel: MainViewModel) {
    val focusRequester = remember { FocusRequester() }
    Surface(
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        val query = remember { mutableStateOf(TextFieldValue()) }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentSize()
        ) {
            Text("x", modifier = Modifier
                .padding(start = 4.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { viewModel.setShowInputBox(false) }
                .padding(8.dp)
            )

            TextField(
                query.value,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.secondary
                ),
                label = { Text("Please would you input web search keyword?", color = MaterialTheme.colors.secondary) },
                onValueChange = {
                    query.value = TextFieldValue(it.text, it.selection, it.composition)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                trailingIcon = {
                    Icon(
                        painterResource("images/icon/ic_clear_form.xml"),
                        contentDescription = "Clear input.",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.clickable {
                            query.value = TextFieldValue()
                        }
                    )
                },
                modifier = Modifier.focusRequester(focusRequester)
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter
                            && query.value.composition == null
                        ) {
                            viewModel.invokeInputAction(query.value.text)
                            viewModel.setShowInputBox(false)
                            viewModel.setInputBoxAction()
                        }
                        true
                    }
            )

            Button(
                onClick = {
                    viewModel.invokeInputAction(query.value.text)
                    viewModel.setShowInputBox(false)
                    viewModel.setInputBoxAction()
                }
            ) {
                Text("Done")
            }

            LaunchedEffect(viewModel.showWebSearch()) {
                if (viewModel.showWebSearch()) {
                    focusRequester.requestFocus()
                }
                query.value = TextFieldValue(
                    (viewModel.currentTab() as? WebTab)?.url() ?: ""
                )
            }
        }
    }
}