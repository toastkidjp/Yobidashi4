package jp.toastkid.yobidashi4.domain.service.loan

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoanCalculatorTest {

    @InjectMockKs
    private lateinit var calculator: LoanCalculator

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun test() {
        assertEquals(
            87748,
            calculator.invoke(Factor(25_000_000, 35, 1.0, 1_000_000, 10000, 10000)).monthlyPayment
        )
    }

    @Test
    fun testOverDownPayment() {
        assertEquals(
            20000,
            calculator.invoke(Factor(25_000_000, 35, 1.0, 27_000_000, 10000, 10000)).monthlyPayment
        )
    }

    @Test
    fun testOverIntegerRange() {
        assertEquals(
            70515207,
            calculator.invoke(Factor(25_000_000_000L, 35, 1.0, 27_000_000, 10000, 10000)).monthlyPayment
        )
    }

}