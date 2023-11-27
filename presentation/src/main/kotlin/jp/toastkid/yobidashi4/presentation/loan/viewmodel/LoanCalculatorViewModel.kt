package jp.toastkid.yobidashi4.presentation.loan.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

class LoanCalculatorViewModel {

    private val result = mutableStateOf("")

    fun result() = result.value

    fun setResult(value: String) {
        result.value = value
    }

    private val loanAmount = mutableStateOf("35,000,000")

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

    private val downPayment = mutableStateOf("1,000,000")

    fun downPayment() = downPayment.value

    fun setDownPayment(value: String) {
        downPayment.value = format(value)

        onChange(inputChannel, value)
    }

    private val managementFee = mutableStateOf("10,000")

    fun managementFee() = managementFee.value

    fun setManagementFee(value: String) {
        managementFee.value = format(value)

        onChange(inputChannel, value)
    }

    private val renovationReserves = mutableStateOf("10,000")

    fun renovationReserves() = renovationReserves.value

    fun setRenovationReserves(value: String) {
        renovationReserves.value = format(value)
        onChange(inputChannel, value)
    }

    private val scheduleState = mutableStateListOf<PaymentDetail>()

    fun scheduleState() = scheduleState

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
    
    private val formatter = DecimalFormat("#,###.##")

    private fun format(input: String?): String {
        if (input.isNullOrBlank()) {
            return "0"
        }

        val formatted = try {
            formatter.format(
                input.filter { it.isDigit() || it == '.' }.trim().toBigDecimalOrNull()
            )
        } catch (e: IllegalArgumentException) {
            LoggerFactory.getLogger("LoanCalculator").debug("Illegal input", e)
            input
        }
        return formatted
    }

    private fun extractLong(editText: String) =
        editText.replace(",", "").toLongOrNull() ?: 0

    private fun extractInt(editText: String) =
        editText.replace(",", "").toIntOrNull() ?: 0

    private fun extractDouble(editText: String) =
        editText.replace(",", "").toDoubleOrNull() ?: 0.0

    fun roundToIntSafely(d: Double) =
        if (d.isNaN()) "0" else d.roundToInt().toString()
    
}