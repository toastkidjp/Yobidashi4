package jp.toastkid.yobidashi4.domain.service.calendar

import java.time.DayOfWeek
import java.time.LocalDate

class DateCalculator {

    private val DAYS_OF_WEEK_FOR_LAST_WEEK =
        setOf(DayOfWeek.MONDAY, DayOfWeek.SUNDAY, DayOfWeek.SATURDAY)

    operator fun invoke(year: Int, month: Int, week: Int): Int {
        val localDate = LocalDate.of(year, month, 1)
        val firstDayOfWeek = localDate.dayOfWeek

        val d = if (firstDayOfWeek == DayOfWeek.MONDAY) {
            8
        } else {
            (firstDayOfWeek.ordinal - (DayOfWeek.MONDAY.ordinal + 1))
        }

        val targetWeek = when {
            (week != -1) -> week
            DAYS_OF_WEEK_FOR_LAST_WEEK.contains(firstDayOfWeek) -> 5
            else -> 4
        }

        val o = if (firstDayOfWeek == DayOfWeek.MONDAY) 6 else (firstDayOfWeek.ordinal - (DayOfWeek.MONDAY.ordinal + 1))
        return 7 * targetWeek - (o)
    }
}
