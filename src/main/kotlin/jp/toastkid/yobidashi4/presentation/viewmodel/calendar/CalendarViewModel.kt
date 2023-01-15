package jp.toastkid.yobidashi4.presentation.viewmodel.calendar

import java.time.LocalDate

interface CalendarViewModel {
    fun localDate(): LocalDate
    fun plusMonths(i: Long)
    fun setYear(year: Int)
    fun moveMonth(month: Int)
    fun getFirstDay(): LocalDate
}