package jp.toastkid.yobidashi4.domain.model.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class MoveableJapaneseHolidayTest {

    @Test
    fun testIsTargetMonth() {
        assertTrue(MoveableJapaneseHoliday.isTargetMonth(1))
        assertFalse(MoveableJapaneseHoliday.isTargetMonth(2))
    }

    @Test
    fun testFind() {
        assertTrue(MoveableJapaneseHoliday.find(2023, 3).isEmpty())
        assertAll(
            {
                val agedDay2013 = MoveableJapaneseHoliday.find(2013, 9).first()
                assertEquals(MoveableJapaneseHoliday.RESPECT_FOR_THE_AGED_DAY.title, agedDay2013.title)
                assertEquals(16, agedDay2013.day)
            },
            {
                val agedDay2014 = MoveableJapaneseHoliday.find(2014, 9).first()
                assertEquals(MoveableJapaneseHoliday.RESPECT_FOR_THE_AGED_DAY.title, agedDay2014.title)
                assertEquals(15, agedDay2014.day)
            },
            {
                val agedDay2015 = MoveableJapaneseHoliday.find(2015, 9).first()
                assertEquals(MoveableJapaneseHoliday.RESPECT_FOR_THE_AGED_DAY.title, agedDay2015.title)
                assertEquals(21, agedDay2015.day)
            },
            {
                val holidays = MoveableJapaneseHoliday.find(2020, 7)
                val marineDay = holidays.first()
                assertEquals(MoveableJapaneseHoliday.MARINE_DAY.title, marineDay.title)
                assertEquals(23, marineDay.day)
                val sportsDay = holidays.last()
                assertEquals(MoveableJapaneseHoliday.SPORTS_DAY.title, sportsDay.title)
                assertEquals(24, sportsDay.day)
            },
            {
                val holidays = MoveableJapaneseHoliday.find(2021, 7)
                val marineDay = holidays.first()
                assertEquals(MoveableJapaneseHoliday.MARINE_DAY.title, marineDay.title)
                assertEquals(22, marineDay.day)
                val sportsDay = holidays.last()
                assertEquals(MoveableJapaneseHoliday.SPORTS_DAY.title, sportsDay.title)
                assertEquals(23, sportsDay.day)
            },
            {
                assertTrue(MoveableJapaneseHoliday.find(2020, 10).isEmpty())
            },
            {
                assertTrue(MoveableJapaneseHoliday.find(2021, 10).isEmpty())
            },
            {
                val holidays = MoveableJapaneseHoliday.find(2022, 7)
                assertEquals(1, holidays.size)
            },
            {
                val agedDay2023 = MoveableJapaneseHoliday.find(2023, 9).first()
                assertEquals(MoveableJapaneseHoliday.RESPECT_FOR_THE_AGED_DAY.title, agedDay2023.title)
                assertEquals(18, agedDay2023.day)
            }
        )
    }

}