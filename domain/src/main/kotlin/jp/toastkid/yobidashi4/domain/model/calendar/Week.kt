package jp.toastkid.yobidashi4.domain.model.calendar

import java.time.DayOfWeek
import java.time.LocalDate

class Week {
    private val days: MutableList<CalendarDate> = mutableListOf()

    fun add(date: LocalDate, offDay: Boolean) {
        days.add(CalendarDate(date.dayOfMonth, date.dayOfWeek, offDay = offDay))
    }

    fun addEmpty() {
        days.add(CalendarDate(-1, DayOfWeek.MONDAY))
    }

    fun days() = days

    fun anyApplicableDate() = days.any { it.date != -1 }

}