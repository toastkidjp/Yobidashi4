package jp.toastkid.yobidashi4.presentation.viewmodel.calendar

import androidx.compose.ui.text.input.TextFieldValue
import java.time.DayOfWeek
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.model.calendar.Week

interface CalendarViewModel {
    fun localDate(): LocalDate
    fun plusMonths(i: Long)
    fun setYear(year: Int)
    fun moveMonth(month: Int)

    fun moveToCurrentMonth()
    fun getFirstDay(): LocalDate

    fun yearInput(): TextFieldValue

    fun setYearInput(textFieldValue: TextFieldValue)

    fun isToday(date: Int): Boolean

    fun openDateArticle(date: Int, onBackground: Boolean = false)

    fun dayOfWeeks(): Iterable<DayOfWeek>

    fun month(): Iterable<Week>

}