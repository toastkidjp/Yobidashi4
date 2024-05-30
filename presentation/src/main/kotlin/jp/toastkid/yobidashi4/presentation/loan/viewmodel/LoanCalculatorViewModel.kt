package jp.toastkid.yobidashi4.presentation.loan.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.VisualTransformation
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.PaymentDetail
import jp.toastkid.yobidashi4.domain.service.loan.DebouncedCalculatorService
import jp.toastkid.yobidashi4.presentation.component.DecimalVisualTransformation
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class LoanCalculatorViewModel {

    private val scrollState = LazyListState(0)

    private val result = mutableStateOf("")

    fun result() = result.value

    private val loanAmount = mutableStateOf("35000000")

    fun loanAmount() = loanAmount.value

    fun setLoanAmount(value: String) {
        loanAmount.value = format(value)

        onChange(inputChannel, value)
    }

    private val loanTerm = mutableStateOf("35")

    fun loanTerm() = loanTerm.value

    fun setLoanTerm(value: String) {
        loanTerm.value = format(value)

        onChange(inputChannel, value)
    }

    private val interestRate = mutableStateOf("1.0")

    fun interestRate() = interestRate.value

    fun setInterestRate(value: String) {
        interestRate.value = format(value)

        onChange(inputChannel, value)
    }

    private val downPayment = mutableStateOf("1000000")

    fun downPayment() = downPayment.value

    fun setDownPayment(value: String) {
        downPayment.value = format(value)

        onChange(inputChannel, value)
    }

    private val managementFee = mutableStateOf("10000")

    fun managementFee() = managementFee.value

    fun setManagementFee(value: String) {
        managementFee.value = format(value)

        onChange(inputChannel, value)
    }

    private val renovationReserves = mutableStateOf("10000")

    fun renovationReserves() = renovationReserves.value

    fun setRenovationReserves(value: String) {
        renovationReserves.value = format(value)
        onChange(inputChannel, value)
    }

    private val scheduleState = mutableStateListOf<PaymentDetail>()

    fun scheduleState(): List<PaymentDetail> = scheduleState

    private val inputChannel: Channel<String> = Channel()

    fun inputChannel() = inputChannel
    
    fun launch() {
        DebouncedCalculatorService(
            inputChannel,
            {
                Factor(
                    extractLong(loanAmount.value),
                    extractInt(loanTerm.value),
                    extractDouble(interestRate.value),
                    extractInt(downPayment.value),
                    extractInt(managementFee.value),
                    extractInt(renovationReserves.value)
                )
            },
            {
                result.value = String.format("月々の支払額: %,d (金利総額 %,d)", it.monthlyPayment,
                    it.paymentSchedule.sumOf { paymentDetail -> paymentDetail.interest }.toLong()
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

    private fun format(input: String): String {
        if (input.isBlank()) {
            return "0"
        }

        return input.filter { it.isDigit() || it == '.' }.trim()
    }

    private fun extractLong(editText: String) = editText.toLong()

    private fun extractInt(editText: String) = editText.toInt()

    private fun extractDouble(editText: String) =
        editText.toDoubleOrNull() ?: 0.0

    fun roundToIntSafely(d: Double) =
        if (d.isNaN()) "0" else d.roundToInt().toString()

    fun listState() = scrollState

}