package jp.toastkid.yobidashi4.domain.service.calendar.japan

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
    }
}