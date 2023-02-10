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
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun UnixTimeConverterView() {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") }
    val offset = remember { OffsetDateTime.now().offset }
    val defaultFirstInputValue = remember {
        LocalDateTime.now().toInstant(offset).toEpochMilli().toString()
    }
    val defaultSecondInputValue = remember {
        LocalDateTime.now().format(dateFormatter).toString()
    }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        val firstInput = remember { mutableStateOf(TextFieldValue(defaultFirstInputValue)) }
        val secondInput = remember { mutableStateOf(TextFieldValue(defaultSecondInputValue)) }
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

                        secondInput.value = TextFieldValue(
                            LocalDateTime
                                .ofInstant(Instant.ofEpochMilli(firstInput.value.text.toLong()), ZoneId.systemDefault())
                                .format(dateFormatter)
                        )
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

                        try {
                            firstInput.value = TextFieldValue(
                                LocalDateTime.parse(secondInput.value.text, dateFormatter)
                                    .toInstant(offset)
                                    .toEpochMilli()
                                    .toString()
                            )
                        } catch (e: DateTimeException) {
                            // > /dev/null
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