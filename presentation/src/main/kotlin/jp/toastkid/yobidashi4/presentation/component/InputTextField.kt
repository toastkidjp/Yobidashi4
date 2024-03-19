package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
internal fun InputTextField(
    textFieldValue: TextFieldValue,
    labelText: String,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch:  (KeyboardActionScope.() -> Unit),
    clearButton: () -> Unit,
    openSuggestion: Boolean,
    suggestions: List<String>,
    suggestionConsumer: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onClickClear: () -> Unit,
    modifier: Modifier
) {
    Box {
        TextField(
            textFieldValue,
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent, cursorColor = MaterialTheme.colors.secondary),
            label = { Text(labelText, color = MaterialTheme.colors.secondary) },
            onValueChange = onValueChange,
            keyboardActions = KeyboardActions(
                onSearch = onSearch
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            visualTransformation = {
                TransformedText(AnnotatedString(it.replace("\n".toRegex(), " ")), OffsetMapping.Identity)
            },
            trailingIcon = {
                Icon(
                    painterResource("images/icon/ic_clear_form.xml"),
                    contentDescription = "Clear input.",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.clickable(onClick = clearButton)
                )
            },
            modifier = modifier
        )

        DropdownMenu(
            openSuggestion,
            properties = PopupProperties(clippingEnabled = false),
            onDismissRequest = {  }
        ) {
            suggestions.forEach {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "x",
                        Modifier.clickable { onClickDelete(it) }.padding(vertical = 8.dp, horizontal = 4.dp)
                    )

                    DropdownMenuItem({
                        suggestionConsumer(it)
                    }) {
                        Text(it)
                    }
                }
            }

            DropdownMenuItem(onClickClear) {
                Text("Clear history")
            }
        }
    }
}