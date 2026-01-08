package jp.toastkid.yobidashi4.presentation.compound.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.mutableStateOf
import jp.toastkid.yobidashi4.domain.model.aggregation.CompoundInterestCalculationResult
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorInput
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorService

class CompoundInterestCalculatorViewModel {

    private val calculator = CompoundInterestCalculatorService()

    private val capitalInput = TextFieldState("0")

    fun capitalInput() = capitalInput

    fun clearCapitalInput() {
        capitalInput.clearText()
    }

    private val installmentInput = TextFieldState("40000")

    fun installmentInput() = installmentInput

    fun clearInstallmentInput() {
        installmentInput.clearText()
    }

    private val annualInterestInput = TextFieldState("0.03")

    fun annualInterestInput() = annualInterestInput

    fun clearAnnualInterestInput() {
        annualInterestInput.clearText()
    }

    private val yearInput = TextFieldState("20")

    fun yearInput() = yearInput

    fun clearYearInput() {
        yearInput.clearText()
    }

    private val result = mutableStateOf(CompoundInterestCalculationResult())

    fun result() = result.value.itemArrays().toList()

    fun calculate() {
        CompoundInterestCalculatorInput.from(
            capitalInput.text.toString(),
            installmentInput.text.toString(),
            annualInterestInput.text.toString(),
            yearInput.text.toString()
        )?.let {
            result.value = calculator.invoke(it)
        }
    }

}