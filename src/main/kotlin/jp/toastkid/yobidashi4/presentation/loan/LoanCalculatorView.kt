package jp.toastkid.yobidashi4.presentation.loan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.PaymentDetail
import jp.toastkid.yobidashi4.domain.service.loan.DebouncedCalculatorService
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun LoanCalculatorView() {
    var result by rememberSaveable {
        mutableStateOf("")
    }
    var loanAmount by remember {
        mutableStateOf("35,000,000")
    }
    var loanTerm by remember {
        mutableStateOf("35")
    }
    var interestRate by remember {
        mutableStateOf("1.0")
    }
    var downPayment by remember {
        mutableStateOf("1,000,000")
    }
    var managementFee by remember {
        mutableStateOf("10,000")
    }
    var renovationReserves by remember {
        mutableStateOf("10,000")
    }

    val scheduleState = remember { mutableStateListOf<PaymentDetail>() }

    val inputChannel: Channel<String> = Channel()

    Surface(elevation = 4.dp, color = MaterialTheme.colors.surface.copy(alpha = 0.75f)) {
        Row {
            Column(
                Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SelectionContainer {
                    Text(text = result, fontSize = 18.sp)
                }
                OutlinedTextField(
                    value = loanAmount,
                    onValueChange = {
                        loanAmount = format(it)
                        onChange(inputChannel, it)
                    },
                    label = { Text(text = "ローン総額") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                        cursorColor = MaterialTheme.colors.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = loanTerm,
                    onValueChange = {
                        loanTerm = format(it)
                        onChange(inputChannel, it)
                    },
                    label = { Text(text = "Loan term") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                        cursorColor = MaterialTheme.colors.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = interestRate,
                    onValueChange = {
                        interestRate = it
                        onChange(inputChannel, it)
                    },
                    label = { Text(text = "Interest rate") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                        cursorColor = MaterialTheme.colors.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = downPayment,
                    onValueChange = {
                        downPayment = format(it)
                        onChange(inputChannel, it)
                    },
                    label = { Text(text = "Down payment") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                        cursorColor = MaterialTheme.colors.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = managementFee,
                    onValueChange = {
                        managementFee = format(it)
                        onChange(inputChannel, it)
                    },
                    label = { Text(text = "Management fee (Monthly)") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                        cursorColor = MaterialTheme.colors.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = renovationReserves,
                    onValueChange = {
                        renovationReserves = format(it)
                        onChange(inputChannel, it)
                    },
                    label = { Text(text = "Renovation reserves (Monthly)") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        backgroundColor = Color.Transparent,
                        cursorColor = MaterialTheme.colors.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            if (scheduleState.isNotEmpty()) {
                Box (modifier = Modifier.weight(0.5f)) {
                    val scrollState = rememberLazyListState()
                    LazyColumn(state = scrollState) {
                        stickyHeader {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.animateItemPlacement()
                                    .background(if (scrollState.firstVisibleItemIndex != 0) MaterialTheme.colors.surface else Color.Transparent)
                            ) {
                                Text(
                                    "回数",
                                    modifier = Modifier.weight(0.4f)
                                )
                                Text("元本", modifier = Modifier.weight(1f))
                                Text("利息", modifier = Modifier.weight(1f))
                                Text("残金", modifier = Modifier.weight(1f))
                            }
                        }
                        itemsIndexed(scheduleState) { index, it ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.animateItemPlacement()) {
                                Text(
                                    "${(index / 12) + 1} ${(index % 12) + 1}(${index + 1})",
                                    modifier = Modifier.weight(0.4f)
                                )
                                Text(roundToIntSafely(it.principal).toString(), modifier = Modifier.weight(1f))
                                Text(roundToIntSafely(it.interest), modifier = Modifier.weight(1f))
                                Text(it.amount.toString(), modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    VerticalScrollbar(adapter = rememberScrollbarAdapter(scrollState), modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd))
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        DebouncedCalculatorService(
            inputChannel,
            {
                Factor(
                    extractLong(loanAmount),
                    extractInt(loanTerm),
                    extractDouble(interestRate),
                    extractInt(downPayment),
                    extractInt(managementFee),
                    extractInt(renovationReserves)
                )
            },
            {
                result = String.format("月々の支払額: %,d (金利総額 %,d)", it.monthlyPayment, it.paymentSchedule.map { it.interest }.sum().toLong())
                scheduleState.clear()
                scheduleState.addAll(it.paymentSchedule)
            }
        ).invoke()
    }
}

private fun roundToIntSafely(d: Double) =
    if (d.isNaN()) "0" else d.roundToInt().toString()

private fun onChange(inputChannel: Channel<String>, text: String) {
    CoroutineScope(Dispatchers.IO).launch {
        inputChannel.send(text ?: "")
    }
}

private val formatter = DecimalFormat("#,###.##")

private fun format(input: String?): String {
    if (input.isNullOrBlank()) {
        return "0"
    }

    val formatted = try {
        formatter.format(
            input.filter { it.isDigit() || it == '.' }.trim()?.toBigDecimalOrNull()
        )
    } catch (e: IllegalArgumentException) {
        LoggerFactory.getLogger("LoanCalculator").debug("Illegal input", e)
        input
    }
    return formatted
}

private fun extractLong(editText: String) =
    editText.replace(",", "")?.toLongOrNull() ?: 0

private fun extractInt(editText: String) =
    editText.replace(",", "")?.toIntOrNull() ?: 0

private fun extractDouble(editText: String) =
    editText.replace(",", "")?.toDoubleOrNull() ?: 0.0