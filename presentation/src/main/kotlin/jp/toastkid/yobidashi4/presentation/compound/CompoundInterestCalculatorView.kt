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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.compound.viewmodel.CompoundInterestCalculatorViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CompoundInterestCalculatorView() {
    val viewModel = remember { CompoundInterestCalculatorViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Row {
            Column {
                Text("Compound Interest calculator", modifier = Modifier.padding(8.dp))

                TextField(
                    viewModel.capitalInput(),
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Installment", color = MaterialTheme.colors.secondary) },
                    onValueChange = {
                        viewModel.setCapitalInput(it)
                    },
                    trailingIcon = {
                        Icon(
                            painterResource("images/icon/ic_clear_form.xml"),
                            contentDescription = "Clear input.",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                viewModel.setCapitalInput(TextFieldValue())
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    viewModel.installmentInput(),
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Installment", color = MaterialTheme.colors.secondary) },
                    onValueChange = {
                        viewModel.setInstallmentInput(it)
                    },
                    trailingIcon = {
                        Icon(
                            painterResource("images/icon/ic_clear_form.xml"),
                            contentDescription = "Clear input.",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                viewModel.setInstallmentInput(TextFieldValue())
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    viewModel.annualInterestInput(),
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Annual interest", color = MaterialTheme.colors.secondary) },
                    onValueChange = {
                        viewModel.setAnnualInterestInput(it)
                    },
                    trailingIcon = {
                        Icon(
                            painterResource("images/icon/ic_clear_form.xml"),
                            contentDescription = "Clear input.",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                viewModel.setAnnualInterestInput(TextFieldValue())
                            }
                        )
                    }
                )

                TextField(
                    viewModel.yearInput(),
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                    label = { Text("Year", color = MaterialTheme.colors.secondary) },
                    onValueChange = { newValue ->
                        viewModel.setYearInput(TextFieldValue(newValue.text, newValue.selection, newValue.composition))
                    },
                    trailingIcon = {
                        Icon(
                            painterResource("images/icon/ic_clear_form.xml"),
                            contentDescription = "Clear input.",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                viewModel.setYearInput(TextFieldValue())
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

                    items(viewModel.result().itemArrays().toList()) { columns ->
                        SelectionContainer {
                            Row(modifier = Modifier.animateItemPlacement()) {
                                columns.forEach {
                                    Text(String.format("%,d", it), modifier = Modifier.weight(1f))
                                }
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
