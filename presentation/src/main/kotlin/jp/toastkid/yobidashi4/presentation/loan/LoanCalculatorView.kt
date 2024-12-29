package jp.toastkid.yobidashi4.presentation.loan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField
import jp.toastkid.yobidashi4.presentation.loan.viewmodel.LoanCalculatorViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoanCalculatorView() {
    val viewModel = remember { LoanCalculatorViewModel() }
    val coroutineScope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    Surface(
        elevation = 4.dp,
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        modifier = Modifier.onKeyEvent { viewModel.onKeyEvent(coroutineScope, it) }
    ) {
        Row {
            Column(
                Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SelectionContainer {
                    Text(text = viewModel.result(), fontSize = 18.sp)
                }

                LoanCalculatorInput(
                    viewModel.loanAmount(),
                    viewModel::setLoanAmount,
                    "Loan amount",
                    viewModel.visualTransformation()
                )

                LoanCalculatorInput(
                    viewModel.loanTerm(),
                    viewModel::setLoanTerm,
                    "Loan term",
                    viewModel.visualTransformation()
                )

                LoanCalculatorInput(
                    viewModel.interestRate(),
                    viewModel::setInterestRate,
                    "Interest rate",
                    viewModel.visualTransformation()
                )

                LoanCalculatorInput(
                    viewModel.downPayment(),
                    viewModel::setDownPayment,
                    "Down payment",
                    viewModel.visualTransformation()
                )

                LoanCalculatorInput(
                    viewModel.managementFee(),
                    viewModel::setManagementFee,
                    "Management fee (Monthly)",
                    viewModel.visualTransformation()
                )

                LoanCalculatorInput(
                    viewModel.renovationReserves(),
                    viewModel::setRenovationReserves,
                    "Renovation reserves (Monthly)",
                    viewModel.visualTransformation()
                )
            }

            if (viewModel.scheduleState().isNotEmpty()) {
                Box (modifier = Modifier.weight(0.5f)) {
                    val color =
                        if (viewModel.listState().firstVisibleItemIndex != 0) MaterialTheme.colors.surface else Color.Transparent
                    LazyColumn(state = viewModel.listState()) {
                        stickyHeader {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.animateItem()
                                    .drawBehind { drawRect(color) }
                            ) {
                                Text(
                                    "Count",
                                    modifier = Modifier.weight(0.4f)
                                )
                                Text("Amount", modifier = Modifier.weight(1f))
                                Text("Interest", modifier = Modifier.weight(1f))
                                Text("Remaining", modifier = Modifier.weight(1f))
                            }
                        }
                        itemsIndexed(viewModel.scheduleState()) { index, it ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.animateItem()) {
                                Text(
                                    "${index + 1} (${(index / 12) + 1}/${(index % 12) + 1})",
                                    modifier = Modifier.weight(0.4f)
                                )
                                Text(viewModel.roundToIntSafely(it.principal), modifier = Modifier.weight(1f))
                                Text(viewModel.roundToIntSafely(it.interest), modifier = Modifier.weight(1f))
                                Text(viewModel.format(it.amount), modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(viewModel.listState()),
                        modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.launch()
    }
}

@Composable
private fun LoanCalculatorInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    labelText: String,
    visualTransformation: VisualTransformation
) {
    SingleLineTextField(
        textFieldValue = value,
        onValueChange = onValueChange,
        labelText = labelText,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onSurface,
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.onSurface
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}
