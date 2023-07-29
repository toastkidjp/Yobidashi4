package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EquinoxDayCalculatorTest {

    private lateinit var equinoxDayCalculator: EquinoxDayCalculator

    @BeforeEach
    fun setUp() {
        equinoxDayCalculator = EquinoxDayCalculator()
    }

    @Test
    fun test() {
        assertEquals(20, equinoxDayCalculator.calculateVernalEquinoxDay(2020))
        assertEquals(22, equinoxDayCalculator.calculateAutumnalEquinoxDay(2020))
    }

}