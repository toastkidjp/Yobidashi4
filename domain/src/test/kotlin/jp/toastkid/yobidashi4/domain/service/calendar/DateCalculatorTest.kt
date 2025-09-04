package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DateCalculatorTest {

    private lateinit var subject: MondayDateCalculator

    @BeforeEach
    fun setUp() {
        subject = MondayDateCalculator()
    }

    @Test
    fun test() {
        assertEquals(5, subject.invoke(2025, 5, 1))
        assertEquals(26, subject.invoke(2025, 5, -1))
        assertEquals(8, subject.invoke(2025, 9, 2))
        assertEquals(15, subject.invoke(2025, 9, 3))
        assertEquals(24, subject.invoke(2025, 11, 4))
    }

}
