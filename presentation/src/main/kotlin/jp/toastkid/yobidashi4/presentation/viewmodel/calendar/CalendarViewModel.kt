package jp.toastkid.yobidashi4.presentation.viewmodel.calendar

import androidx.compose.ui.text.input.TextFieldValue
import java.time.LocalDate

interface CalendarViewModel {
    fun localDate(): LocalDate
    fun plusMonths(i: Long)
    fun setYear(year: Int)
    fun moveMonth(month: Int)

    fun moveToCurrentMonth()
    fun getFirstDay(): LocalDate

    fun yearInput(): TextFieldValue

    fun setYearInput(textFieldValue: TextFieldValue)
}