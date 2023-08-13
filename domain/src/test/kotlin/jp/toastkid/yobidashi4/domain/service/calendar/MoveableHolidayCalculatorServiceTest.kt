package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MoveableHolidayCalculatorServiceTest {

    private lateinit var moveableHolidayCalculatorService: MoveableHolidayCalculatorService

    @BeforeEach
    fun setUp() {
        moveableHolidayCalculatorService = MoveableHolidayCalculatorService()
    }

    @Test
    fun test() {
        assertFalse(moveableHolidayCalculatorService.invoke(2020, 1, 12))
        assertTrue(moveableHolidayCalculatorService.invoke(2020, 1, 13))
        assertFalse(moveableHolidayCalculatorService.invoke(2020, 1, 14))
        assertFalse(moveableHolidayCalculatorService.invoke(2019, 7, 14))
        assertTrue(moveableHolidayCalculatorService.invoke(2019, 7, 15))
        assertFalse(moveableHolidayCalculatorService.invoke(2019, 7, 16))
        assertFalse(moveableHolidayCalculatorService.invoke(2020, 9, 20))
        assertTrue(moveableHolidayCalculatorService.invoke(2020, 9, 21))
        assertFalse(moveableHolidayCalculatorService.invoke(2019, 10, 13))
        assertTrue(moveableHolidayCalculatorService.invoke(2019, 10, 14))
        assertFalse(moveableHolidayCalculatorService.invoke(2019, 10, 15))
        assertFalse(moveableHolidayCalculatorService.invoke(2023, 8, 14))
    }

    @Test
    fun testFeb() {
        assertFalse(moveableHolidayCalculatorService.invoke(2020, 2, 1))
    }

}