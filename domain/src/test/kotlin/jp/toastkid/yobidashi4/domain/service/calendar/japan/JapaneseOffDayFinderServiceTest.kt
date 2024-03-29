package jp.toastkid.yobidashi4.domain.service.calendar.japan

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JapaneseOffDayFinderServiceTest {

    private lateinit var japaneseOffDayFinderService: JapaneseOffDayFinderService

    @BeforeEach
    fun setUp() {
        japaneseOffDayFinderService = JapaneseOffDayFinderService()
    }

    @Test
    fun invoke() {
        assertNotNull(japaneseOffDayFinderService.invoke(2013, 5).firstOrNull { it.day == 6 })
        assertNotNull(japaneseOffDayFinderService.invoke(2014, 5).firstOrNull { it.day == 6 })
        assertNotNull(japaneseOffDayFinderService.invoke(2015, 5).firstOrNull { it.day == 6 })
        assertNull(japaneseOffDayFinderService.invoke(2016, 5).firstOrNull { it.day == 6 })

        assertEquals(10, japaneseOffDayFinderService.invoke(2020, 8).firstOrNull()?.day)
        assertEquals(9, japaneseOffDayFinderService.invoke(2021, 8).firstOrNull()?.day)
        assertEquals(11, japaneseOffDayFinderService.invoke(2022, 8).firstOrNull()?.day)
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
    }

}