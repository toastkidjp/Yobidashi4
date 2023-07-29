package jp.toastkid.yobidashi4.domain.model.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService

class Week {
    private val days: MutableList<CalendarDate> = mutableListOf()

    private val offDayFinderService = OffDayFinderService()

    fun add(date: LocalDate) {
        days.add(CalendarDate(date.dayOfMonth, date.dayOfWeek,
            offDay = offDayFinderService.invoke(date.year, date.month.value, date.dayOfMonth, date.dayOfWeek)))
    }

    fun addEmpty() {
        days.add(CalendarDate(-1, DayOfWeek.MONDAY))
    }

    fun days() = days

    fun anyApplicableDate() = days.any { it.date != -1 }

}