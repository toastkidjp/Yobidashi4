package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_clear_form
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun InputTextField(
    textFieldValue: TextFieldState,
    labelText: String,
    onSearch:  () -> Unit,
    clearButton: () -> Unit,
    openSuggestion: Boolean,
    suggestions: List<String>,
    suggestionConsumer: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onClickClear: () -> Unit,
    onFocusChanged: (FocusState) -> Unit = {},
    modifier: Modifier
) {
    Box {
        MultiLineTextField(
            textFieldValue,
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent, cursorColor = MaterialTheme.colors.secondary),
            labelText = labelText,
            onClearInput = clearButton,
            keyboardActions = KeyboardActionHandler {
                onSearch.invoke()
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            inputTransformation = SingleLineTransformation(),
            modifier = modifier.onFocusChanged(onFocusChanged)
        )

        DropdownMenu(
            openSuggestion,
            properties = PopupProperties(clippingEnabled = false),
            onDismissRequest = {  }
        ) {
            suggestions.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clickable { onClickDelete(it) }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text("x")
                    }

                    HoverHighlightDropdownMenuItem(it) {
                        suggestionConsumer(it)
                    }
                }
            }

            HoverHighlightDropdownMenuItem("Clear history", onClick = onClickClear)
        }
    }
}

private class SingleLineTransformation : InputTransformation {

    override fun TextFieldBuffer.transformInput() {
        var indexOf = asCharSequence().indexOf("\n")
        while (indexOf != -1) {
            replace(indexOf, indexOf + 1, "")
            indexOf = asCharSequence().indexOf("\n")
        }
    }

}

private val emptyClearInputAction = {}

@Composable
fun SingleLineTextField(
    textFieldValue: TextFieldState,
    labelText: String,
    onClearInput: () -> Unit = emptyClearInputAction,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActionHandler = KeyboardActionHandler { },
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent, cursorColor = MaterialTheme.colors.secondary),
    inputTransformation: InputTransformation = SingleLineTransformation(),
    visualTransformation: OutputTransformation = OutputTransformation { },
    modifier: Modifier = Modifier
) {
    MultiLineTextField(
        textFieldValue,
        labelText,
        1,
        onClearInput,
        keyboardOptions,
        keyboardActions,
        colors,
        inputTransformation,
        visualTransformation,
        modifier
    )
}

@Composable
fun MultiLineTextField(
    textFieldValue: TextFieldState,
    labelText: String,
    maxLines: Int,
    onClearInput: () -> Unit = emptyClearInputAction,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActionHandler = KeyboardActionHandler { },
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent, cursorColor = MaterialTheme.colors.secondary),
    inputTransformation: InputTransformation = InputTransformation { },
    visualTransformation: OutputTransformation = OutputTransformation {  },
    modifier: Modifier = Modifier
) {
    TextField(
        textFieldValue,
        lineLimits = if (maxLines == 1) TextFieldLineLimits.SingleLine else TextFieldLineLimits.MultiLine(maxLines),
        colors = colors,
        label = { Text(labelText, color = MaterialTheme.colors.secondary) },
        trailingIcon = {
            if (onClearInput !== emptyClearInputAction) {
                Icon(
                    painterResource(Res.drawable.ic_clear_form),
                    contentDescription = "Clear input.",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.clickable(onClick = onClearInput)
                )
            }
        },
        keyboardOptions = keyboardOptions,
        onKeyboardAction = keyboardActions,
        inputTransformation = inputTransformation,
        outputTransformation = visualTransformation,
        modifier = modifier
    )
}