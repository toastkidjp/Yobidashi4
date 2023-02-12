package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.service.converter.TwoStringConverterService

@Composable
fun TwoValueConverterBox(unixTimeConverterService: TwoStringConverterService) {
    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        val firstInput = remember { mutableStateOf(TextFieldValue(unixTimeConverterService.defaultFirstInputValue())) }
        val secondInput = remember { mutableStateOf(TextFieldValue(unixTimeConverterService.defaultSecondInputValue())) }
        val result = remember { mutableStateOf("") }
        Row {
            Column {
                TextField(
                    firstInput.value,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Keyword") },
                    onValueChange = {
                        firstInput.value = TextFieldValue(it.text, it.selection, it.composition)

                        unixTimeConverterService.firstInputAction(firstInput.value.text)?.let { newValue ->
                            secondInput.value = TextFieldValue(newValue)
                        }
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    secondInput.value,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Keyword") },
                    onValueChange = {
                        secondInput.value = TextFieldValue(it.text, it.selection, it.composition)

                        unixTimeConverterService.secondInputAction(secondInput.value.text)?.let {
                            firstInput.value = TextFieldValue(it)
                        }
                    },
                    trailingIcon = {
                        Icon(
                            painterResource("images/icon/ic_clear_form.xml"),
                            contentDescription = "Clear input.",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                secondInput.value = TextFieldValue()
                            }
                        )
                    }
                )
            }
            if (result.value.isNotBlank()) {
                Text(
                    result.value
                )
            }
        }
    }
}