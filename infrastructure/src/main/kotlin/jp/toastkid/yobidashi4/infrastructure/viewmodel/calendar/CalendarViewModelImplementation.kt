package jp.toastkid.yobidashi4.infrastructure.viewmodel.calendar

import androidx.compose.runtime.mutableStateOf
import java.time.LocalDate
import jp.toastkid.yobidashi4.presentation.viewmodel.calendar.CalendarViewModel
import org.koin.core.annotation.Single

@Single
class CalendarViewModelImplementation : CalendarViewModel {

    private val localDateState =  mutableStateOf(LocalDate.now())

    override fun localDate(): LocalDate = localDateState.value

    override fun plusMonths(i: Long) {
        localDateState.value = localDateState.value.plusMonths(i)
    }

    override fun setYear(year: Int) {
        localDateState.value = localDateState.value.withYear(year)
    }

    override fun moveMonth(month: Int) {
        localDateState.value = localDateState.value.withMonth(month)
    }

    override fun moveToCurrentMonth() {
        localDateState.value = LocalDate.now()
    }

    override fun getFirstDay(): LocalDate {
        return localDateState.value.withDayOfMonth(1)
    }

}