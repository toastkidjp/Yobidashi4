package jp.toastkid.yobidashi4.domain.service.calendar

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.DayOfWeek
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class OffDayFinderServiceTest {

    private lateinit var offDayFinderService: OffDayFinderService

    @MockK
    private lateinit var equinoxDayCalculator: EquinoxDayCalculator

    @MockK
    private lateinit var userOffDayService: UserOffDayService

    @MockK
    private lateinit var moveableHolidayCalculatorService: MoveableHolidayCalculatorService

    @MockK
    private lateinit var specialCaseOffDayCalculator: SpecialCaseOffDayCalculatorService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        offDayFinderService = OffDayFinderService(
                equinoxDayCalculator,
                userOffDayService,
                moveableHolidayCalculatorService,
                specialCaseOffDayCalculator
        )

        every { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }.answers { 20 }
        every { equinoxDayCalculator.calculateAutumnalEquinoxDay(any()) }.answers { 22 }
        every { specialCaseOffDayCalculator.invoke(any(), any(), any()) }.answers { false to false }
        every { moveableHolidayCalculatorService.invoke(any(), any(), any()) }.answers { false }
        every { userOffDayService.invoke(any(), any()) }.answers { false }
    }

    @Test
    fun testJune() {
        assertFalse(offDayFinderService(2020, 6, 4, DayOfWeek.THURSDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
    }

    @Test
    fun testVernalEquinoxDay() {
        assertTrue(offDayFinderService(2020, 3, 20, DayOfWeek.FRIDAY))

        verify(exactly = 1) { equinoxDayCalculator.calculateVernalEquinoxDay(2020) }
        verify(exactly = 0) { equinoxDayCalculator.calculateAutumnalEquinoxDay(any()) }
        verify(exactly = 0) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
    }

    @Test
    fun testAutumnalEquinoxDay() {
        assertTrue(offDayFinderService(2020, 9, 22, DayOfWeek.FRIDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
        verify(exactly = 1) { equinoxDayCalculator.calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 0) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
    }

    @Test
    fun testSpecialCase2019() {
        every { specialCaseOffDayCalculator.invoke(any(), any(), any()) }.answers { true to true }

        assertTrue(offDayFinderService(2019, 5, 1, DayOfWeek.WEDNESDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
        verify(exactly = 0) { equinoxDayCalculator.calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
        verify(exactly = 0) { moveableHolidayCalculatorService.invoke(any(), any(), any())}
    }

    @Test
    fun testSpecialCase2020October() {
        every { specialCaseOffDayCalculator.invoke(any(), any(), any()) }.answers { false to true }

        assertFalse(offDayFinderService(2020, 10, 14, DayOfWeek.WEDNESDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
        verify(exactly = 0) { equinoxDayCalculator.calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
        verify(exactly = 0) { moveableHolidayCalculatorService.invoke(any(), any(), any())}
        verify(exactly = 0) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testMoveableCase() {
        every { moveableHolidayCalculatorService.invoke(any(), any(), any()) }.answers { true }

        assertTrue(offDayFinderService(2020, 9, 21, DayOfWeek.MONDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
        verify(exactly = 1) { equinoxDayCalculator.calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
        verify(exactly = 1) { moveableHolidayCalculatorService.invoke(any(), any(), any())}
        verify(exactly = 0) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testUserOffDay() {
        every { userOffDayService.invoke(any(), any()) }.answers { true }

        assertTrue(offDayFinderService(2020, 12, 29, DayOfWeek.TUESDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
        verify(exactly = 0) { equinoxDayCalculator.calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
        verify(exactly = 1) { moveableHolidayCalculatorService.invoke(any(), any(), any())}
        verify(exactly = 1) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testMay6() {
        assertTrue(offDayFinderService(2020, 5, 6, DayOfWeek.WEDNESDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
        verify(exactly = 0) { equinoxDayCalculator.calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
        verify(exactly = 1) { moveableHolidayCalculatorService.invoke(any(), any(), any())}
        verify(exactly = 1) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testNov4() {
        assertTrue(offDayFinderService(2019, 11, 4, DayOfWeek.MONDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
        verify(exactly = 0) { equinoxDayCalculator.calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
        verify(exactly = 1) { moveableHolidayCalculatorService.invoke(any(), any(), any())}
        verify(exactly = 1) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testNormalDay() {
        assertFalse(offDayFinderService(2020, 12, 17, DayOfWeek.THURSDAY))

        verify(exactly = 0) { equinoxDayCalculator.calculateVernalEquinoxDay(any()) }
        verify(exactly = 0) { equinoxDayCalculator.calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { specialCaseOffDayCalculator.invoke(any(), any(), any()) }
        verify(exactly = 1) { moveableHolidayCalculatorService.invoke(any(), any(), any())}
        verify(exactly = 1) { userOffDayService.invoke(any(), any())}
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

}