package jp.toastkid.yobidashi4.domain.model.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import java.time.DayOfWeek
import java.time.LocalDate

class Week {
    private val days: MutableList<CalendarDate> = mutableListOf()

    fun add(date: LocalDate, holidays: List<Holiday> = emptyList()) {
        days.add(
            CalendarDate(
                date.dayOfMonth,
                date.dayOfWeek,
                holidays.map(Holiday::title),
                offDay = holidays.any { it.flag == HolidayCalendar.JAPAN.flag }
            )
        )
    }

    fun add(date: LocalDate, holiday: Holiday? = null) {
        days.add(
            CalendarDate(
                date.dayOfMonth,
                date.dayOfWeek,
                if (holiday != null) listOf(holiday.title) else emptyList(),
                offDay = holiday != null && holiday.flag == HolidayCalendar.JAPAN.flag
            )
        )
    }

    fun addEmpty() {
        days.add(CalendarDate(-1, DayOfWeek.MONDAY))
    }

    fun days() = days

    fun anyApplicableDate() = days.any { it.date != -1 }

}