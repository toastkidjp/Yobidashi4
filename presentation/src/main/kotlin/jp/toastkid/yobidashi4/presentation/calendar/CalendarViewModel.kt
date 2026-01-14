package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.mutableStateOf
import jp.toastkid.yobidashi4.domain.model.calendar.Week
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.service.article.ArticleTitleGenerator
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import jp.toastkid.yobidashi4.domain.service.calendar.label.CalendarLabelFinderService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.chrono.JapaneseDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicReference

class CalendarViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val setting: Setting by inject()

    private val userOffDayService: UserOffDayService by inject()

    private val calendarLabelFinderService = CalendarLabelFinderService()

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

    fun localDate(): LocalDate = localDateState.value

    private fun setNewLocalDate(newDate: LocalDate) {
        val year = newDate.year
        japaneseYear.set(makeJapaneseYearLabel(year, newDate.month.value))
        localDateState.value = newDate
    }

    private fun makeJapaneseYearLabel(year: Int, month: Int): String =
        if (year >= 1873)
            JapaneseDate.of(year, month, 1)
                .format(DateTimeFormatter.ofPattern("Gy"))
        else
            ""

    fun plusMonths(i: Long) {
        val year = localDateState.value.year

        setNewLocalDate(localDateState.value.plusMonths(i))

        val nextYear = localDateState.value.year
        if (year != nextYear) {
            yearInput.setTextAndPlaceCursorAtEnd("$nextYear")
        }
    }

    fun setYear(year: Int) {
        setNewLocalDate(localDateState.value.withYear(year))
    }

    fun moveMonth(month: Int) {
        setNewLocalDate(localDateState.value.withMonth(month))
    }

    fun moveToCurrentMonth() {
        val year = localDateState.value.year

        setNewLocalDate(LocalDate.now())

        val nextYear = localDateState.value.year
        if (year != nextYear) {
            yearInput.setTextAndPlaceCursorAtEnd("$nextYear")
        }
    }

    fun getFirstDay(): LocalDate {
        return localDateState.value.withDayOfMonth(1)
    }

    private val yearInput = TextFieldState()

    fun yearInput() = yearInput

    fun setYearInput() {
        yearInput().text.toString().toIntOrNull()?.let {
            setYear(it)
        }
    }

    private val japaneseYear = AtomicReference("")

    fun japaneseYear() = japaneseYear.get()

    fun isToday(date: Int): Boolean {
        val value = localDate()
        return value.year == LocalDate.now().year && value.month == LocalDate.now().month && LocalDate.now().dayOfMonth == date
    }

    fun openDateArticle(date: Int, onBackground: Boolean = false) {
        if (date == -1) {
            return
        }

        mainViewModel.edit(
            setting.articleFolderPath().resolve(
                "${ArticleTitleGenerator().invoke(localDate().withDayOfMonth(date))}.md"
            ),
            onBackground
        )
    }

    fun dayOfWeeks() = week

    fun month() = makeMonth(week)

    private fun makeMonth(week: Iterable<DayOfWeek>): MutableList<Week> {
        val firstDay = localDateState.value.withDayOfMonth(1)

        val jpHolidays = HolidayCalendar.JAPAN.getHolidays(firstDay.year, firstDay.month.value).union(userOffDayService.findBy(firstDay.monthValue))
        val ukHolidays = HolidayCalendar.UK.getHolidays(firstDay.year, firstDay.monthValue)
        val usHolidays = HolidayCalendar.US.getHolidays(firstDay.year, firstDay.monthValue)
        val calendarLabels = calendarLabelFinderService.invoke(firstDay.year, firstDay.monthValue)

        var hasStarted1 = false
        var current1 = firstDay
        val weeks = mutableListOf<Week>()
        (0..5).forEach { i ->
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
                    val holidays = calculateHolidays(current1, jpHolidays, ukHolidays, usHolidays)
                    w.add(current1, holidays, calendarLabels.filter { it.day == current1.dayOfMonth })
                }
                current1 = current1.plusDays(1L)
            }
            if (w.anyApplicableDate()) {
                weeks.add(w)
            }
        }
        return weeks
    }

    private fun calculateHolidays(
        current1: LocalDate,
        jpHolidays: Set<Holiday>,
        ukHolidays: List<Holiday>,
        usHolidays: List<Holiday>
    ): List<Holiday> {
        val holiday = jpHolidays.find { it.day == current1.dayOfMonth }
        val ukHolidayCandidate = ukHolidays.find { it.day == current1.dayOfMonth }
        val usHolidayCandidate = usHolidays.find { it.day == current1.dayOfMonth }
        val holidays = listOfNotNull(holiday, ukHolidayCandidate, usHolidayCandidate)
        return holidays
    }

    private val openMonthChooser = mutableStateOf(false)

    fun openingMonthChooser(): Boolean {
        return openMonthChooser.value
    }

    fun openMonthChooser() {
        openMonthChooser.value = true
    }

    fun closeMonthChooser() {
        openMonthChooser.value = false
    }

    fun launch(date: LocalDate) {
        setNewLocalDate(date)
        yearInput.setTextAndPlaceCursorAtEnd("${localDate().year}")
    }

    fun onDispose(tab: CalendarTab) {
        mainViewModel.updateCalendarTab(tab, localDate().year, localDate().month.value)
    }

}