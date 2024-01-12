package jp.toastkid.yobidashi4.infrastructure.viewmodel.calendar

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import java.time.DayOfWeek
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.model.calendar.Week
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.ArticleTitleGenerator
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import jp.toastkid.yobidashi4.presentation.viewmodel.calendar.CalendarViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class CalendarViewModelImplementation : CalendarViewModel, KoinComponent {

    private val userOffDayService: UserOffDayService by inject()

    private val week = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

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

    override fun isToday(date: Int): Boolean {
        val value = localDate()
        return value.year == LocalDate.now().year && value.month == LocalDate.now().month && LocalDate.now().dayOfMonth == date
    }

    override fun openDateArticle(date: Int, onBackground: Boolean) {
        if (date == -1) {
            return
        }

        val koin = object : KoinComponent {
            val viewModel: MainViewModel by inject()
            val setting: Setting by inject()
        }
        koin.viewModel.edit(
            koin.setting.articleFolderPath().resolve(
                "${ArticleTitleGenerator().invoke(localDate().withDayOfMonth(date))}.md"
            ),
            onBackground
        )
    }

    override fun dayOfWeeks() = week

    override fun month() = makeMonth(week)

    private fun makeMonth(week: Iterable<DayOfWeek>): MutableList<Week> {
        val firstDay = localDateState.value.withDayOfMonth(1)

        val offDayFinderService = HolidayCalendar.JAPAN.getHolidays(firstDay.year, firstDay.month.value).union(userOffDayService.findBy(firstDay.monthValue))

        var hasStarted1 = false
        var current1 = firstDay
        val weeks = mutableListOf<Week>()
        for (i in 0..5) {
            val w = Week()
            week.forEach { dayOfWeek ->
                if (hasStarted1.not() && dayOfWeek != firstDay.dayOfWeek) {
                    w.addEmpty()
                    return@forEach
                }
                hasStarted1 = true

                if (firstDay.month != current1.month) {
                    w.addEmpty()
                } else {
                    w.add(current1, offDayFinderService.find { it.month == current1.month.value && it.day == current1.dayOfMonth })
                }
                current1 = current1.plusDays(1L)
            }
            if (w.anyApplicableDate()) {
                weeks.add(w)
            }
        }
        return weeks
    }

}