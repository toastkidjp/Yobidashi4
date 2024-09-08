package jp.toastkid.yobidashi4.presentation.loan.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoanCalculatorViewModelTest {

    private lateinit var subject: LoanCalculatorViewModel

    @BeforeEach
    fun setUp() {
        subject = LoanCalculatorViewModel()

        subject.launch()
    }

    @Test
    fun result() {
        assertTrue(subject.result().isEmpty())
    }

    @Test
    fun useZeroInsteadOfEmpty() {
        subject.setLoanAmount(TextFieldValue())

        assertEquals("0", subject.loanAmount().text)
    }

    @Test
    fun useZeroInsteadOfBlank() {
        subject.setLoanAmount(TextFieldValue(" "))

        assertEquals("0", subject.loanAmount().text)
    }

    @Test
    fun loanAmount() {
        subject.setLoanAmount(TextFieldValue("20000000"))

        assertEquals("20000000", subject.loanAmount().text)
    }

    @Test
    fun loanTerm() {
        subject.setLoanTerm(TextFieldValue("5"))

        assertEquals("5", subject.loanTerm().text)
    }

    @Test
    fun interestRate() {
        subject.setInterestRate(TextFieldValue("0.5"))

        assertEquals("0.5", subject.interestRate().text)
    }

    @Test
    fun interestRateWithPlainText() {
        subject.setInterestRate(TextFieldValue("az"))

        assertEquals("", subject.interestRate().text)
    }

    @Test
    fun downPayment() {
        subject.setDownPayment(TextFieldValue("2000000"))

        assertEquals("2000000", subject.downPayment().text)
    }

    @Test
    fun managementFee() {
        subject.setManagementFee(TextFieldValue("30000"))

        assertEquals("30000", subject.managementFee().text)
    }

    @Test
    fun renovationReserves() {
        subject.setRenovationReserves(TextFieldValue("30001"))

        assertEquals("30001", subject.renovationReserves().text)
    }

    @Test
    fun roundToIntSafely() {
        assertEquals("0", subject.roundToIntSafely(Double.NaN))
        assertEquals("2", subject.roundToIntSafely(2.2))
        assertEquals("5", subject.roundToIntSafely(4.5))
    }

    @Test
    fun inputChannel() {
        assertNotNull(subject.inputChannel())
    }

    @Test
    fun setEmpty() {
        subject.setRenovationReserves(TextFieldValue())

        assertEquals("0", subject.renovationReserves().text)
    }

    @Test
    fun setIncludingComma() {
        subject.setRenovationReserves(TextFieldValue("10,000"))

        assertEquals("10000", subject.renovationReserves().text)
    }

    @Test
    fun visualTransformation() {
        val visualTransformation = subject.visualTransformation()

        assertNotSame(VisualTransformation.None, visualTransformation)
    }

    @Test
    fun listState() {
        assertEquals(0, subject.listState().firstVisibleItemIndex)
    }

}