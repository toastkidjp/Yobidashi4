package jp.toastkid.yobidashi4.domain.model.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday

class Week {
    private val days: MutableList<CalendarDate> = mutableListOf()

    fun add(date: LocalDate, holiday: Holiday? = null) {
        days.add(CalendarDate(date.dayOfMonth, date.dayOfWeek, holiday?.title ?: "", offDay = holiday != null))
    }

    fun addEmpty() {
        days.add(CalendarDate(-1, DayOfWeek.MONDAY))
    }

    fun days() = days

    fun anyApplicableDate() = days.any { it.date != -1 }

}