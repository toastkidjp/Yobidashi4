package jp.toastkid.yobidashi4.domain.model.calendar.holiday.japan

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class MoveableJapaneseHolidayTest {

    @ParameterizedTest
    @CsvSource(
        "1, true",
        "2, false"
    )
    fun testIsTargetMonth(arg: Int, expected: Boolean) {
        assertEquals(expected, MoveableJapaneseHoliday.isTargetMonth(arg))
    }

    @ParameterizedTest
    @CsvSource(
        "2023, 3, -1",
        "2013, 9, 16",
    )
    fun testFindParameterized(year: Int, month: Int, date: Int) {
        if (date == -1) {
            assertTrue(MoveableJapaneseHoliday.find(year, month).isEmpty())
            return
        }

        assertEquals(16, MoveableJapaneseHoliday.find(year, month).first().day)
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
                Assertions.assertEquals(15, agedDay2014.day)
            },
            {
                val agedDay2015 = MoveableJapaneseHoliday.find(2015, 9).first()
                assertEquals(MoveableJapaneseHoliday.RESPECT_FOR_THE_AGED_DAY.title, agedDay2015.title)
                Assertions.assertEquals(21, agedDay2015.day)
            },
            {
                val holidays = MoveableJapaneseHoliday.find(2020, 7)
                val marineDay = holidays.first()
                assertEquals(MoveableJapaneseHoliday.MARINE_DAY.title, marineDay.title)
                Assertions.assertEquals(23, marineDay.day)
                val sportsDay = holidays.last()
                assertEquals(MoveableJapaneseHoliday.SPORTS_DAY.title, sportsDay.title)
                Assertions.assertEquals(24, sportsDay.day)
            },
            {
                val holidays = MoveableJapaneseHoliday.find(2021, 7)
                val marineDay = holidays.first()
                assertEquals(MoveableJapaneseHoliday.MARINE_DAY.title, marineDay.title)
                Assertions.assertEquals(22, marineDay.day)
                val sportsDay = holidays.last()
                assertEquals(MoveableJapaneseHoliday.SPORTS_DAY.title, sportsDay.title)
                Assertions.assertEquals(23, sportsDay.day)
            },
            {
                assertEquals("体育の日", MoveableJapaneseHoliday.find(2019, 10).first().title)
            },
            {
                assertTrue(MoveableJapaneseHoliday.find(2020, 10).isEmpty())
            },
            {
                assertTrue(MoveableJapaneseHoliday.find(2021, 10).isEmpty())
            },
            {
                assertEquals("スポーツの日", MoveableJapaneseHoliday.find(2022, 10).first().title)
            },
            {
                val holidays = MoveableJapaneseHoliday.find(2022, 7)
                Assertions.assertEquals(1, holidays.size)
            },
            {
                val agedDay2023 = MoveableJapaneseHoliday.find(2023, 9).first()
                assertEquals(MoveableJapaneseHoliday.RESPECT_FOR_THE_AGED_DAY.title, agedDay2023.title)
                Assertions.assertEquals(18, agedDay2023.day)
            }
        )
    }

}