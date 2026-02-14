package jp.toastkid.yobidashi4.presentation.loan.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.LoanPayment
import jp.toastkid.yobidashi4.domain.model.loan.PaymentDetail
import jp.toastkid.yobidashi4.domain.service.loan.DebouncedCalculatorService
import jp.toastkid.yobidashi4.domain.service.loan.LoanPaymentExporter
import jp.toastkid.yobidashi4.presentation.component.DecimalInputTransformation
import jp.toastkid.yobidashi4.presentation.component.DecimalVisualTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import java.text.DecimalFormat
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt

class LoanCalculatorViewModel {

    private val scrollState = LazyListState(0)

    private val result = mutableStateOf("")

    fun result() = result.value

    private val brokerageFee = AtomicReference("")

    private val totalInterest = mutableStateOf("")

    fun totalInterest() = totalInterest.value

    private val loanAmount = TextFieldState("35000000")

    fun loanAmount() = loanAmount

    fun setLoanAmount() {
/*        loanAmount.edit {
            replace(0, length, formatDecimalString(loanAmount.text.toString()))
        }*/

        brokerageFee.set(calculateBrokerageFee())

        onChange(inputChannel)
    }

    private val loanTerm = TextFieldState("35")

    fun loanTerm() = loanTerm

    fun setLoanTerm() {
        /*loanTerm.edit {
            replace(0, length, formatDecimalString(loanTerm.text.toString()))
        }*/
        onChange(inputChannel)
    }

    private val interestRate = TextFieldState("1.0")

    fun interestRate() = interestRate

    fun setInterestRate() {
        onChange(inputChannel)
    }

    private val downPayment = TextFieldState("1000000")

    fun downPayment() = downPayment

    fun setDownPayment() {
/*        downPayment.edit {
            replace(0, length, formatDecimalString(downPayment.text.toString()))
        }*/
        onChange(inputChannel)
    }

    private val managementFee = TextFieldState("10000")

    fun managementFee() = managementFee

    fun setManagementFee() {
/*        managementFee.edit {
            replace(0, length, formatDecimalString(managementFee.text.toString()))
        }*/
        onChange(inputChannel)
    }

    private val renovationReserves = TextFieldState("10000")

    fun renovationReserves() = renovationReserves

    fun setRenovationReserves() {
/*        renovationReserves.edit {
            replace(0, length, formatDecimalString(renovationReserves.text.toString()))
        }*/
        onChange(inputChannel)
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
            ::makeFactor,
            {
                lastPaymentResult.set(it)

                result.value = String.format("Monthly payment: %,d", it.monthlyPayment)

                totalInterest.value = String.format("Amount of interest: %,d", it.totalInterestAmount())

                scheduleState.clear()
                scheduleState.addAll(it.paymentSchedule)
            }
        ).invoke()
    }

    private fun onChange(inputChannel: Channel<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            inputChannel.send(UUID.randomUUID().toString())
        }
    }

    private val visualTransformation = DecimalVisualTransformation()

    fun visualTransformation() = visualTransformation

    private fun formatDecimalString(input: String): String {
        if (input.isBlank()) {
            return "0"
        }

        return input.filter { it.isDigit() || it == '.' }.trim()
    }

    private fun extractLong(editText: CharSequence) = editText.toString().toLongOrNull() ?: 0

    private fun extractInt(editText: CharSequence) = editText.toString().toIntOrNull() ?: 0

    private fun extractDouble(editText: CharSequence) =
        editText.toString().toDoubleOrNull() ?: 0.0

    fun format(l: Long): String = decimalFormat.format(l)

    fun roundToIntSafely(d: Double): String =
        if (d.isNaN()) "0" else decimalFormat.format(d.roundToInt())

    fun listState() = scrollState

    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyDown) {
            return false
        }

        if (keyEvent.isCtrlPressed && keyEvent.key == Key.P) {
            val loanPayment = lastPaymentResult.get() ?: return true
            LoanPaymentExporter().invoke(
                makeFactor(),
                loanPayment
            )
            return true
        }

        return false
    }

    private fun makeFactor() = Factor(
        extractLong(loanAmount.text),
        extractInt(loanTerm.text),
        extractDouble(interestRate.text),
        extractInt(downPayment.text),
        extractInt(managementFee.text),
        extractInt(renovationReserves.text)
    )

    private val decimalFormat = DecimalFormat("#,###.##")

    /**
     * <= 2_000_000 5.5％
     * <= 4_000_000 4.4％＋22,000
     * else 3.3％＋66,000
     */
    private fun calculateBrokerageFee(): String {
        val amount = extractLong(loanAmount.text)
        val brokerageFee = when {
            amount <= 2_000_000 -> (amount * 0.055)
            amount <= 4_000_000 -> (amount * 0.044) + 22_000
            else -> (amount * 0.033) + 66_000
        }.toLong()
        return String.format("Brokerage fee: %,d", brokerageFee)
    }

    fun brokerageFee(): String = brokerageFee.get()

    private val inputTransformation = DecimalInputTransformation()

    fun inputTransformation() = inputTransformation

}