package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DateCalculatorTest {

    @Test
    fun test() {
        val dateCalculator = MondayDateCalculator()
        assertEquals(5, dateCalculator.invoke(2025, 5, 1))
        assertEquals(26, dateCalculator.invoke(2025, 5, -1))
        assertEquals(8, dateCalculator.invoke(2025, 9, 2))
        assertEquals(15, dateCalculator.invoke(2025, 9, 3))
        assertEquals(24, dateCalculator.invoke(2025, 11, 4))
    }

}
