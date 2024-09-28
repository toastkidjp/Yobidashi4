package jp.toastkid.yobidashi4.infrastructure.service.calendar

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.DayOfWeek
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.service.calendar.EquinoxDayCalculator
import jp.toastkid.yobidashi4.domain.service.calendar.MoveableHolidayCalculatorService
import jp.toastkid.yobidashi4.domain.service.calendar.SpecialCaseOffDayCalculatorService
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

internal class OffDayFinderServiceTest {

    private lateinit var offDayFinderService: OffDayFinderServiceImplementation

    @MockK
    private lateinit var userOffDayService: UserOffDayService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { userOffDayService } bind(UserOffDayService::class)
                }
            )
        }

        mockkConstructor(EquinoxDayCalculator::class)
        mockkConstructor(SpecialCaseOffDayCalculatorService::class)
        mockkConstructor(MoveableHolidayCalculatorService::class)
        every { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }.answers { Holiday("", 3, 20) }
        every { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(any()) }.answers { Holiday("", 9, 22) }
        every { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }.answers { emptySet() }
        every { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any()) } returns false
        every { userOffDayService.invoke(any(), any()) } returns false

        offDayFinderService = OffDayFinderServiceImplementation()
    }

    @Test
    fun testJune() {
        assertFalse(offDayFinderService(2020, 6, 4, DayOfWeek.THURSDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
    }

    @Test
    fun testVernalEquinoxDay() {
        assertTrue(offDayFinderService(2020, 3, 20, DayOfWeek.FRIDAY))

        verify(exactly = 1) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(2020) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(any()) }
        verify(inverse = true) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
    }

    @Test
    fun testNotVernalEquinoxDay() {
        assertFalse(offDayFinderService(2020, 3, 19, DayOfWeek.THURSDAY))

        verify(exactly = 1) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(2020) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(any()) }
    }

    @Test
    fun testAutumnalEquinoxDay() {
        assertTrue(offDayFinderService(2020, 9, 22, DayOfWeek.FRIDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(exactly = 1) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(inverse = true) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
    }

    @Test
    fun testSpecialCase2019() {
        every { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }.answers { setOf(Holiday("", 5, 1)) }

        assertTrue(offDayFinderService(2019, 5, 1, DayOfWeek.WEDNESDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
        verify(inverse = true) { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any())}
    }

    @Test
    fun testSpecialCase2019ButNotOffDayCase() {
        every { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }.answers { setOf(Holiday("", 5, 1)) }

        assertFalse(offDayFinderService(2019, 5, 11, DayOfWeek.SATURDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
        verify(exactly = 1) { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any())}
    }

    @Test
    fun testSpecialCase2020October() {
        every { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }.answers { emptySet() }

        assertFalse(offDayFinderService(2020, 10, 14, DayOfWeek.WEDNESDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
    }

    @Test
    fun testMoveableCase() {
        every { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any()) }.answers { true }

        assertTrue(offDayFinderService(2020, 9, 21, DayOfWeek.MONDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(exactly = 1) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
        verify(exactly = 1) { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any())}
        verify(inverse = true) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testUserOffDay() {
        every { userOffDayService.invoke(any(), any()) }.answers { true }

        assertTrue(offDayFinderService(2020, 12, 29, DayOfWeek.TUESDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
        verify(exactly = 1) { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any())}
        verify(exactly = 1) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testMay6() {
        assertTrue(offDayFinderService(2020, 5, 6, DayOfWeek.WEDNESDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
        verify(exactly = 1) { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any())}
        verify(exactly = 1) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testNov4() {
        assertTrue(offDayFinderService(2019, 11, 4, DayOfWeek.MONDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
        verify(exactly = 1) { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any())}
        verify(exactly = 1) { userOffDayService.invoke(any(), any())}
    }

    @Test
    fun testNormalDay() {
        assertFalse(offDayFinderService(2020, 12, 17, DayOfWeek.THURSDAY))

        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateVernalEquinoxDay(any()) }
        verify(inverse = true) { anyConstructed<EquinoxDayCalculator>().calculateAutumnalEquinoxDay(2020) }
        verify(exactly = 1) { anyConstructed<SpecialCaseOffDayCalculatorService>().invoke(any(), any()) }
        verify(exactly = 1) { anyConstructed<MoveableHolidayCalculatorService>().invoke(any(), any(), any())}
        verify(exactly = 1) { userOffDayService.invoke(any(), any())}
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

}