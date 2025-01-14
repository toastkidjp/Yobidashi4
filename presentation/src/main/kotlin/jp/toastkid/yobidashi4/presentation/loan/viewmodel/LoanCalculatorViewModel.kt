package jp.toastkid.yobidashi4.presentation.loan.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.TestOnly
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.LoanPayment
import jp.toastkid.yobidashi4.domain.model.loan.PaymentDetail
import jp.toastkid.yobidashi4.domain.service.loan.DebouncedCalculatorService
import jp.toastkid.yobidashi4.domain.service.loan.LoanPaymentExporter
import jp.toastkid.yobidashi4.presentation.component.DecimalVisualTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt

class LoanCalculatorViewModel {

    private val scrollState = LazyListState(0)

    private val result = mutableStateOf("")

    fun result() = result.value

    private val loanAmount = mutableStateOf(TextFieldValue("35000000"))

    fun loanAmount() = loanAmount.value

    fun setLoanAmount(value: TextFieldValue) {
        loanAmount.value = value.copy(text = formatDecimalString(value.text))

        onChange(inputChannel, value.text)
    }

    private val loanTerm = mutableStateOf(TextFieldValue("35"))

    fun loanTerm() = loanTerm.value

    fun setLoanTerm(value: TextFieldValue) {
        loanTerm.value = value.copy(text = formatDecimalString(value.text))

        onChange(inputChannel, value.text)
    }

    private val interestRate = mutableStateOf(TextFieldValue("1.0"))

    fun interestRate() = interestRate.value

    fun setInterestRate(value: TextFieldValue) {
        interestRate.value = value.copy(formatDecimalString(value.text))

        onChange(inputChannel, value.text)
    }

    private val downPayment = mutableStateOf(TextFieldValue("1000000"))

    fun downPayment() = downPayment.value

    fun setDownPayment(value: TextFieldValue) {
        downPayment.value = value.copy(formatDecimalString(value.text))

        onChange(inputChannel, value.text)
    }

    private val managementFee = mutableStateOf(TextFieldValue("10000"))

    fun managementFee() = managementFee.value

    fun setManagementFee(value: TextFieldValue) {
        managementFee.value = value.copy(formatDecimalString(value.text))

        onChange(inputChannel, value.text)
    }

    private val renovationReserves = mutableStateOf(TextFieldValue("10000"))

    fun renovationReserves() = renovationReserves.value

    fun setRenovationReserves(value: TextFieldValue) {
        renovationReserves.value = value.copy(formatDecimalString(value.text))
        onChange(inputChannel, value.text)
    }

    private val lastPaymentResult = AtomicReference<LoanPayment?>(null)

    @TestOnly
    fun setLastPaymentResult(payment: LoanPayment) {
        lastPaymentResult.set(payment)
    }

    private val scheduleState = mutableStateListOf<PaymentDetail>()

    fun scheduleState(): List<PaymentDetail> = scheduleState

    private val inputChannel: Channel<String> = Channel()

    fun inputChannel() = inputChannel

    fun launch() {
        DebouncedCalculatorService(
            inputChannel,
            {
                makeFactor()
            },
            {
                lastPaymentResult.set(it)

                result.value = String.format("月々の支払額: %,d (金利総額 %,d)", it.monthlyPayment,
                    it.totalInterestAmount()
                )
                scheduleState.clear()
                scheduleState.addAll(it.paymentSchedule)
            }
        ).invoke()
    }

    private fun onChange(inputChannel: Channel<String>, text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            inputChannel.send(text)
        }
    }

    private val visualTransformation: VisualTransformation = DecimalVisualTransformation()

    fun visualTransformation() = visualTransformation

    private fun formatDecimalString(input: String): String {
        if (input.isBlank()) {
            return "0"
        }

        return input.filter { it.isDigit() || it == '.' }.trim()
    }

    private fun extractLong(editText: String) = editText.toLong()

    private fun extractInt(editText: String) = editText.toInt()

    private fun extractDouble(editText: String) =
        editText.toDoubleOrNull() ?: 0.0

    fun format(l: Long) = decimalFormat.format(l)

    fun roundToIntSafely(d: Double) =
        if (d.isNaN()) "0" else decimalFormat.format(d.roundToInt())

    fun listState() = scrollState

    fun onKeyEvent(coroutineScope: CoroutineScope, keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyDown) {
            return false
        }

        if (keyEvent.isCtrlPressed && keyEvent.key == Key.P) {
            val loanPayment = lastPaymentResult.get() ?: return true
            LoanPaymentExporter().invoke(
                makeFactor(),
                loanPayment
            )
            return@onKeyEvent true
        }

        return false
    }

    private fun makeFactor() = Factor(
        extractLong(loanAmount.value.text),
        extractInt(loanTerm.value.text),
        extractDouble(interestRate.value.text),
        extractInt(downPayment.value.text),
        extractInt(managementFee.value.text),
        extractInt(renovationReserves.value.text)
    )

    private val decimalFormat = DecimalFormat("#,###.##")

}