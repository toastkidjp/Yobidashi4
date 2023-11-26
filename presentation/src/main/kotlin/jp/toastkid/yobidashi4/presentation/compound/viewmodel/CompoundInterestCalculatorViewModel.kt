package jp.toastkid.yobidashi4.presentation.compound.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.model.aggregation.CompoundInterestCalculationResult
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorInput
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorService

class CompoundInterestCalculatorViewModel {

    private val calculator = CompoundInterestCalculatorService()

    private val capitalInput = mutableStateOf(TextFieldValue("0"))

    fun capitalInput() = capitalInput.value

    fun setCapitalInput(value: TextFieldValue) {
        capitalInput.value = value

        calculate()
    }

    private val installmentInput = mutableStateOf(TextFieldValue("40000"))

    fun installmentInput() = installmentInput.value

    fun setInstallmentInput(value: TextFieldValue) {
        installmentInput.value = value

        calculate()
    }

    private val annualInterestInput = mutableStateOf(TextFieldValue("0.03"))

    fun annualInterestInput() = annualInterestInput.value

    fun setAnnualInterestInput(value: TextFieldValue) {
        annualInterestInput.value = value

        calculate()
    }

    private val yearInput = mutableStateOf(TextFieldValue("20"))

    fun yearInput() = yearInput.value

    fun setYearInput(value: TextFieldValue) {
        yearInput.value = value

        calculate()
    }

    private val result = mutableStateOf(CompoundInterestCalculationResult())

    fun result() = result.value

    private fun calculate() {
        CompoundInterestCalculatorInput.from(
            capitalInput.value.text,
            installmentInput.value.text,
            annualInterestInput.value.text,
            yearInput.value.text
        )?.let {
            result.value = calculator.invoke(it)
        }
    }

}