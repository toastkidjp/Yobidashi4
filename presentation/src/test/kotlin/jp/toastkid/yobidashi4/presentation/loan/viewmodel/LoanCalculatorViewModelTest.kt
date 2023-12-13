package jp.toastkid.yobidashi4.presentation.loan.viewmodel

import org.junit.jupiter.api.Assertions.assertEquals
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
    fun loanAmount() {
        subject.setLoanAmount("20000000")

        assertEquals("20,000,000", subject.loanAmount())
    }

    @Test
    fun loanTerm() {
        subject.setLoanTerm("5")

        assertEquals("5", subject.loanTerm())
    }

    @Test
    fun interestRate() {
        subject.setInterestRate("0.5")

        assertEquals("0.5", subject.interestRate())
    }

    @Test
    fun downPayment() {
        subject.setDownPayment("2000000")

        assertEquals("2,000,000", subject.downPayment())
    }

    @Test
    fun managementFee() {
        subject.setManagementFee("30000")

        assertEquals("30,000", subject.managementFee())
    }

    @Test
    fun renovationReserves() {
        subject.setRenovationReserves("30001")

        assertEquals("30,001", subject.renovationReserves())
    }

    @Test
    fun roundToIntSafely() {
        assertEquals("0", subject.roundToIntSafely(Double.NaN))
        assertEquals("2", subject.roundToIntSafely(2.2))
        assertEquals("5", subject.roundToIntSafely(4.5))
    }
}