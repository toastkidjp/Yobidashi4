package jp.toastkid.yobidashi4.domain.service.calendar.japan

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JapaneseOffDayFinderServiceTest {

    private lateinit var japaneseOffDayFinderService: JapaneseOffDayFinderService

    @BeforeEach
    fun setUp() {
        japaneseOffDayFinderService = JapaneseOffDayFinderService()
    }

    @Test
    fun invoke() {
        assertEquals(10, japaneseOffDayFinderService.invoke(2020, 8).firstOrNull()?.day)
        assertEquals(9, japaneseOffDayFinderService.invoke(2021, 8).firstOrNull()?.day)
        assertEquals(11, japaneseOffDayFinderService.invoke(2022, 8).firstOrNull()?.day)
    }

    @ParameterizedTest
    @CsvSource(
        "2020, 10",
        "2021, 9",
        "2022, 11"
    )
    fun mountainDay(year: Int, expectedDate: Int) {
        assertEquals(expectedDate, japaneseOffDayFinderService.invoke(year, 8).firstOrNull()?.day)
    }

    @Test
    fun substituteHolidayInMay() {
        assertNotNull(japaneseOffDayFinderService.invoke(2013, 5).firstOrNull { it.day == 6 })
        assertNotNull(japaneseOffDayFinderService.invoke(2014, 5).firstOrNull { it.day == 6 })
        assertNotNull(japaneseOffDayFinderService.invoke(2015, 5).firstOrNull { it.day == 6 })
        assertNull(japaneseOffDayFinderService.invoke(2016, 5).firstOrNull { it.day == 6 })
    }

    @Test
    fun march() {
        assertEquals(20, japaneseOffDayFinderService.invoke(2024, 3).first().day)
    }

    @Test
    fun september() {
        assertEquals(23, japaneseOffDayFinderService.invoke(2024, 9).first().day)
    }

    @Test
    fun check224() {
        assertTrue(japaneseOffDayFinderService.invoke(224, 3, false).isEmpty())
        assertEquals(1, japaneseOffDayFinderService.invoke(224, 9, false).size)
    }

    @Test
    fun check20124() {
        assertTrue(japaneseOffDayFinderService.invoke(20124, 3, false).isEmpty())
        assertEquals(1, japaneseOffDayFinderService.invoke(20124, 4, false).size)
    }

}