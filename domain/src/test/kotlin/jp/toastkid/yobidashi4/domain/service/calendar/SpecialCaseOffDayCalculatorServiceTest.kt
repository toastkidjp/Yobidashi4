package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SpecialCaseOffDayCalculatorServiceTest {

    private lateinit var specialCaseOffDayCalculatorService: SpecialCaseOffDayCalculatorService

    @BeforeEach
    fun setUp() {
        specialCaseOffDayCalculatorService = SpecialCaseOffDayCalculatorService()
    }

    @Test
    fun testNormalDay() {
        val result = specialCaseOffDayCalculatorService.invoke(2019, 1)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2019_4() {
        val result = specialCaseOffDayCalculatorService.invoke(2019, 4)

        assertEquals(1, result.size)
    }

    @Test
    fun test2019_May() {
        val result = specialCaseOffDayCalculatorService.invoke(2019, 5)

        assertEquals(2, result.size)
    }

    @Test
    fun test2020_July() {
        val result = specialCaseOffDayCalculatorService.invoke(2020, 7)

        assertEquals(2, result.size)
    }

    @Test
    fun test2021_July() {
        val result = specialCaseOffDayCalculatorService.invoke(2021, 7)

        assertEquals(2, result.size)
    }

    @Test
    fun test2020_August() {
        val result = specialCaseOffDayCalculatorService.invoke(2020, 8)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2020_Oct() {
        val result = specialCaseOffDayCalculatorService.invoke(2020, 10)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2021_August() {
        val result = specialCaseOffDayCalculatorService.invoke(2021, 8)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2021_Oct() {
        val result = specialCaseOffDayCalculatorService.invoke(2021, 10)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2021_Apr() {
        val result = specialCaseOffDayCalculatorService.invoke(2021, 4)

        assertTrue(result.isEmpty())
    }

}