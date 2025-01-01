package jp.toastkid.yobidashi4.presentation.compound

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.DecimalVisualTransformation
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField
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

                SingleLineTextField(
                    viewModel.capitalInput(),
                    "Capital",
                    viewModel::setCapitalInput,
                    viewModel::clearCapitalInput,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = DecimalVisualTransformation()
                )

                SingleLineTextField(
                    viewModel.installmentInput(),
                    "Installment",
                    viewModel::setInstallmentInput,
                    viewModel::clearInstallmentInput,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = DecimalVisualTransformation()
                )

                SingleLineTextField(
                    viewModel.annualInterestInput(),
                    "Annual interest",
                    viewModel::setAnnualInterestInput,
                    viewModel::clearAnnualInterestInput,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                SingleLineTextField(
                    viewModel.yearInput(),
                    "Year",
                    viewModel::setYearInput,
                    viewModel::clearYearInput,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Box {
                val verticalScrollState = rememberLazyListState()
                LazyColumn(state = verticalScrollState) {
                    item {
                        Text("Result")
                    }

                    items(viewModel.result()) { columns ->
                        SelectionContainer {
                            Row(modifier = Modifier.animateItem()) {
                                columns.forEach {
                                    Text(String.format("%,d", it), modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(verticalScrollState),
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
                )
            }
        }
    }

}
