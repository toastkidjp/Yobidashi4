package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertFalse
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
        val result = specialCaseOffDayCalculatorService.invoke(2019, 1, 2)

        assertFalse(result.first)
        assertFalse(result.second)
    }

    @Test
    fun test2019_4() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2019, 4, 30)

        assertTrue(isOffDay)
        assertFalse(forceNormal)
    }

    @Test
    fun test2019_May() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2019, 5, 1)

        assertTrue(isOffDay)
        assertFalse(forceNormal)
    }

    @Test
    fun test2020_7_20() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2020, 7, 20)

        assertFalse(isOffDay)
        assertTrue(forceNormal)
    }

    @Test
    fun test2020_Jul_23() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2020, 7, 23)

        assertTrue(isOffDay)
        assertTrue(forceNormal)
    }

    @Test
    fun test2021_Jul_22() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2021, 7, 22)

        assertTrue(isOffDay)
        assertTrue(forceNormal)
    }

    @Test
    fun test2021_Jul_24() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2021, 7, 24)

        assertFalse(isOffDay)
        assertTrue(forceNormal)
    }

    @Test
    fun test2020_8_11() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2020, 8, 11)

        assertFalse(isOffDay)
        assertTrue(forceNormal)
    }

    @Test
    fun test2020_Oct() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2020, 10, 11)

        assertFalse(isOffDay)
        assertTrue(forceNormal)
    }

    @Test
    fun test2021_8_9() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2021, 8, 9)

        assertTrue(isOffDay)
        assertTrue(forceNormal)
    }

    @Test
    fun test2021_Oct() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2021, 10, 11)

        assertFalse(isOffDay)
        assertTrue(forceNormal)
    }

    @Test
    fun test2021_Apr() {
        val (isOffDay, forceNormal) = specialCaseOffDayCalculatorService.invoke(2021, 4, 26)

        assertFalse(isOffDay)
        assertFalse(forceNormal)
    }

}