package jp.toastkid.yobidashi4.domain.model.calendar.holiday

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HolidayCalendarTest {

    @Test
    fun getHolidays() {
        assertEquals(3, HolidayCalendar.JAPAN.getHolidays(2023, 5).size)
    }

    @Test
    fun getFlag() {
        assertEquals("\uD83C\uDDEF\uD83C\uDDF5", HolidayCalendar.JAPAN.flag)
    }

}