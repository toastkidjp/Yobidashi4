package jp.toastkid.yobidashi4.infrastructure.viewmodel.calendar

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import java.time.LocalDate
import jp.toastkid.yobidashi4.presentation.viewmodel.calendar.CalendarViewModel
import org.koin.core.annotation.Single

@Single
class CalendarViewModelImplementation : CalendarViewModel {

    private val localDateState =  mutableStateOf(LocalDate.now())

    override fun localDate(): LocalDate = localDateState.value

    override fun plusMonths(i: Long) {
        val year = localDateState.value.year

        localDateState.value = localDateState.value.plusMonths(i)

        val nextYear = localDateState.value.year
        if (year != nextYear) {
            setYearInput(TextFieldValue("$nextYear"))
        }
    }

    override fun setYear(year: Int) {
        localDateState.value = localDateState.value.withYear(year)
    }

    override fun moveMonth(month: Int) {
        localDateState.value = localDateState.value.withMonth(month)
    }

    override fun moveToCurrentMonth() {
        val year = localDateState.value.year

        localDateState.value = LocalDate.now()

        val nextYear = localDateState.value.year
        if (year != nextYear) {
            setYearInput(TextFieldValue("$nextYear"))
        }
    }

    override fun getFirstDay(): LocalDate {
        return localDateState.value.withDayOfMonth(1)
    }

    private val yearInput = mutableStateOf(TextFieldValue("${localDate().year}"))

    override fun yearInput() = yearInput.value

    override fun setYearInput(textFieldValue: TextFieldValue) {
        yearInput.value = textFieldValue
        textFieldValue.text.toIntOrNull()?.let {
            setYear(it)
        }
    }

}