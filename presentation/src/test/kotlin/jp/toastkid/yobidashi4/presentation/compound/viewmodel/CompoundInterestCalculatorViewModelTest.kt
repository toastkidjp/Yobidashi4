package jp.toastkid.yobidashi4.presentation.compound.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CompoundInterestCalculatorViewModelTest {

    private lateinit var subject: CompoundInterestCalculatorViewModel

    @BeforeEach
    fun setUp() {
        subject = CompoundInterestCalculatorViewModel()
    }

    @Test
    fun setCapitalInput() {
        assertEquals("0", subject.capitalInput().text)

        subject.setCapitalInput(TextFieldValue("10000"))

        assertEquals("10000", subject.capitalInput().text)
        assertEquals(20, subject.result().itemArrays().size)
    }

    @Test
    fun setInstallmentInput() {
        assertEquals("40000", subject.installmentInput().text)

        subject.setInstallmentInput(TextFieldValue("10000"))

        assertEquals("10000", subject.installmentInput().text)
        assertEquals(20, subject.result().itemArrays().size)
    }

    @Test
    fun setAnnualInterestInput() {
        assertEquals("0.03", subject.annualInterestInput().text)

        subject.setAnnualInterestInput(TextFieldValue("0.1"))

        assertEquals("0.1", subject.annualInterestInput().text)
        assertEquals(20, subject.result().itemArrays().size)
    }

    @Test
    fun setYearInput() {
        assertEquals("20", subject.yearInput().text)

        subject.setYearInput(TextFieldValue("10"))

        assertEquals("10", subject.yearInput().text)
        assertEquals(10, subject.result().itemArrays().size)
    }

}