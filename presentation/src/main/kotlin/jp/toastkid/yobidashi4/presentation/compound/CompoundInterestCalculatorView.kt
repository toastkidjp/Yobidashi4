package jp.toastkid.yobidashi4.presentation.compound

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.aggregation.CompoundInterestCalculationResult
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorService

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CompoundInterestCalculatorView() {
    val calculator = remember { CompoundInterestCalculatorService() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        val firstInput = remember { mutableStateOf(TextFieldValue("40000")) }
        val secondInput = remember { mutableStateOf(TextFieldValue("0.03")) }
        val thirdInput = remember { mutableStateOf(TextFieldValue("20")) }
        val result = remember { mutableStateOf(CompoundInterestCalculationResult()) }
        Row {
            Column {
                Text("Compound Interest calculator", modifier = Modifier.padding(8.dp))
                TextField(
                    firstInput.value,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Installment") },
                    onValueChange = {
                        firstInput.value = TextFieldValue(it.text, it.selection, it.composition)

                        convert(firstInput.value.text, secondInput.value.text, thirdInput.value.text)?.let { converted ->
                            val (installment, annualRate, year) = converted
                            result.value = calculator.invoke(installment, annualRate, year)
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
                    label = { Text("Annual interest") },
                    onValueChange = {
                        secondInput.value = TextFieldValue(it.text, it.selection, it.composition)

                        convert(firstInput.value.text, secondInput.value.text, thirdInput.value.text)?.let {
                            val (installment, annualRate, year) = it
                            result.value = calculator.invoke(installment, annualRate, year)
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

                TextField(
                    thirdInput.value,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Year") },
                    onValueChange = { newValue ->
                        thirdInput.value = TextFieldValue(newValue.text, newValue.selection, newValue.composition)

                        convert(firstInput.value.text, secondInput.value.text, thirdInput.value.text)?.let {
                            val (installment, annualRate, year) = it
                            result.value = calculator.invoke(installment, annualRate, year)
                        }
                    },
                    trailingIcon = {
                        Icon(
                            painterResource("images/icon/ic_clear_form.xml"),
                            contentDescription = "Clear input.",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                thirdInput.value = TextFieldValue()
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Box {
                val verticalScrollState = rememberLazyListState()
                LazyColumn(state = verticalScrollState) {
                    item {
                        Text("Result")
                    }

                    items(result.value.itemArrays().toList()) { columns ->
                        Row(modifier = Modifier.animateItemPlacement()) {
                            columns.forEach {
                                Text(it.toString(), modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                VerticalScrollbar(adapter = rememberScrollbarAdapter(verticalScrollState), modifier = Modifier.fillMaxHeight().align(
                    Alignment.CenterEnd))
            }
        }
    }

}

private fun convert(
    first: String?,
    second: String?,
    third: String?,
): Triple<Int, Double, Int>? {
    val installment = first?.replace(REPLACE_TARGET, "")?.toIntOrNull() ?: return null
    val annualInterest = second?.replace(REPLACE_TARGET, "")?.toDoubleOrNull() ?: return null
    val year = third?.replace(REPLACE_TARGET, "")?.toIntOrNull() ?: return null

    return Triple(installment, annualInterest, year)
}

private const val REPLACE_TARGET = ","